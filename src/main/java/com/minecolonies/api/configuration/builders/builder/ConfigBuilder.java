package com.minecolonies.api.configuration.builders.builder;

import com.minecolonies.api.configuration.builders.IConfigWriter;
import com.minecolonies.api.configuration.builders.values.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Class for managing configuration builders.
 */
public final class ConfigBuilder implements IConfigBuilder
{
    /**
     * Class for managing configuration builders for a specific category.
     */
    public static final class ConfigCategoryBuilder implements IConfigCategoryBuilder
    {
        private final Map<String, IConfigValueWithBinder<?>> options = new HashMap<>();

        @Override
        public IConfigValue<Integer> defineInteger(final String key, final int defaultValue, final int minValue, final int maxValue)
        {
            final ConfigIntegerValue configValue = new ConfigIntegerValue(key, defaultValue, minValue, maxValue);
            options.put(key, configValue);
            return configValue;
        }

        @Override
        public IConfigValue<Double> defineDouble(final String key, final double defaultValue, final double minValue, final double maxValue)
        {
            final ConfigDoubleValue configValue = new ConfigDoubleValue(key, defaultValue, minValue, maxValue);
            options.put(key, configValue);
            return configValue;
        }

        @Override
        public IConfigValue<Long> defineLong(final String key, final long defaultValue, final long minValue, final long maxValue)
        {
            final ConfigLongValue configValue = new ConfigLongValue(key, defaultValue, minValue, maxValue);
            options.put(key, configValue);
            return configValue;
        }

        @Override
        public IConfigValue<Boolean> defineBoolean(final String key, final boolean defaultValue)
        {
            final ConfigBoolValue configValue = new ConfigBoolValue(key, defaultValue);
            options.put(key, configValue);
            return configValue;
        }

        @Override
        public <T extends Enum<T>> IConfigValue<T> defineEnum(final String key, final T defaultValue)
        {
            final ConfigEnumValue<T> configValue = new ConfigEnumValue<>(key, defaultValue);
            options.put(key, configValue);
            return configValue;
        }

        @Override
        public <T> IConfigValue<List<? extends T>> defineList(final String key, final List<? extends T> defaultValue, final Predicate<Object> elementValidator)
        {
            final ConfigListValue<T> configValue = new ConfigListValue<>(key, defaultValue, elementValidator);
            options.put(key, configValue);
            return configValue;
        }
    }

    /**
     * The current registered builders.
     */
    private final Map<String, ConfigCategoryBuilder> categoryBuilders = new HashMap<>();

    @Override
    public ConfigCategoryBuilder createCategory(final String key)
    {
        return categoryBuilders.computeIfAbsent(key, (k) -> new ConfigCategoryBuilder());
    }

    /**
     * Write the config to a permanent location.
     *
     * @param writer the config writer instance.
     */
    public void write(final IConfigWriter writer)
    {
        writer.write(categoryBuilders.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().options.values())));
    }
}
