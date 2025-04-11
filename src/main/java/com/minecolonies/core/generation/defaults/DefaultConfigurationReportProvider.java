package com.minecolonies.core.generation.defaults;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ldtteam.structurize.util.LanguageHandler;
import com.minecolonies.api.configuration.ClientConfiguration;
import com.minecolonies.api.configuration.CommonConfiguration;
import com.minecolonies.api.configuration.ServerConfiguration;
import com.minecolonies.api.configuration.builders.IConfigWriter;
import com.minecolonies.api.configuration.builders.builder.ConfigBuilder;
import com.minecolonies.api.configuration.builders.builder.IConfigBuilder;
import com.minecolonies.api.configuration.builders.values.*;
import com.minecolonies.api.util.constant.Constants;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DefaultConfigurationReportProvider implements DataProvider
{
    protected final PackOutput packOutput;

    public DefaultConfigurationReportProvider(@NotNull final PackOutput packOutput)
    {
        this.packOutput = packOutput;
    }

    @Override
    @NotNull
    public CompletableFuture<?> run(final @NotNull CachedOutput cache)
    {
        final JsonObject root = new JsonObject();
        root.add("common", writeConfigurationInstance(CommonConfiguration::new));
        root.add("server", writeConfigurationInstance(ServerConfiguration::new));
        root.add("client", writeConfigurationInstance(ClientConfiguration::new));

        return DataProvider.saveStable(cache, root, this.packOutput.getOutputFolder(PackOutput.Target.REPORTS).resolve(Constants.MOD_ID).resolve("config.json"));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Default Config Report Provider";
    }

    private JsonObject writeConfigurationInstance(final Consumer<IConfigBuilder> consumer)
    {
        final Map<String, JsonArray> optionsPerCategory = new HashMap<>();

        final ConfigBuilder configBuilder = new ConfigBuilder();
        consumer.accept(configBuilder);

        configBuilder.write(new JsonConfigWriter((category, key, type, option) -> {
            option.addProperty("key", key);
            option.addProperty("name", LanguageHandler.translateKey(Constants.MOD_ID + ".config." + key));
            option.addProperty("description", LanguageHandler.translateKey(Constants.MOD_ID + ".config." + key + ".comment"));
            optionsPerCategory.computeIfAbsent(category, (k) -> new JsonArray()).add(option);
        }));

        final JsonArray categories = new JsonArray();
        for (final Map.Entry<String, JsonArray> entry : optionsPerCategory.entrySet())
        {
            final JsonObject category = new JsonObject();
            category.addProperty("key", entry.getKey());
            category.addProperty("name", LanguageHandler.translateKey(Constants.MOD_ID + ".config." + entry.getKey()));
            category.addProperty("description", LanguageHandler.translateKey(Constants.MOD_ID + ".config." + entry.getKey() + ".comment"));
            category.add("options", entry.getValue());
            categories.add(category);
        }

        final JsonObject root = new JsonObject();
        root.add("categories", categories);
        return root;
    }

    @FunctionalInterface
    private interface ConfigOptionConsumer
    {
        void accept(final String category, final String key, final String type, final JsonObject object);
    }

    private record JsonConfigWriter(ConfigOptionConsumer optionAdder) implements IConfigWriter
    {
        @Override
        public void write(final Map<String, Collection<IConfigValueWithBinder<?>>> configuration)
        {
            for (final Map.Entry<String, Collection<IConfigValueWithBinder<?>>> categoryEntry : configuration.entrySet())
            {
                final String categoryKey = categoryEntry.getKey();

                for (final IConfigValueWithBinder<?> optionValue : categoryEntry.getValue())
                {
                    final String optionKey = optionValue.getKey();

                    final JsonObject configOption = new JsonObject();
                    if (optionValue instanceof ConfigIntegerValue integerValue)
                    {
                        configOption.addProperty("default", integerValue.getDefault());
                        configOption.addProperty("min", integerValue.getMinValue());
                        configOption.addProperty("max", integerValue.getMaxValue());
                        optionAdder.accept(categoryKey, optionKey, "integer", configOption);
                    }
                    else if (optionValue instanceof ConfigDoubleValue doubleValue)
                    {
                        configOption.addProperty("default", doubleValue.getDefault());
                        configOption.addProperty("min", doubleValue.getMinValue());
                        configOption.addProperty("max", doubleValue.getMaxValue());
                        optionAdder.accept(categoryKey, optionKey, "double", configOption);
                    }
                    else if (optionValue instanceof ConfigLongValue longValue)
                    {
                        configOption.addProperty("default", longValue.getDefault());
                        configOption.addProperty("min", longValue.getMinValue());
                        configOption.addProperty("max", longValue.getMaxValue());
                        optionAdder.accept(categoryKey, optionKey, "long", configOption);
                    }
                    else if (optionValue instanceof ConfigBoolValue boolValue)
                    {
                        configOption.addProperty("default", boolValue.getDefault());
                        optionAdder.accept(categoryKey, optionKey, "boolean", configOption);
                    }
                    else if (optionValue instanceof ConfigEnumValue<?> enumValue)
                    {
                        configOption.addProperty("default", enumValue.getDefault().name());
                        final JsonArray enumValues = new JsonArray();
                        for (final Enum value : enumValue.getDefault().getClass().getEnumConstants())
                        {
                            enumValues.add(value.name());
                        }
                        configOption.add("values", enumValues);
                        optionAdder.accept(categoryKey, optionKey, "enum", configOption);
                    }
                    else if (optionValue instanceof ConfigListValue<?> listValue)
                    {
                        configOption.addProperty("default", listValue.getDefault().stream().map(Object::toString).collect(Collectors.joining(", ")));
                        optionAdder.accept(categoryKey, optionKey, "list", configOption);
                    }
                }
            }
        }
    }
}
