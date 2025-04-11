package com.minecolonies.api.configuration.builders.values;

import com.ldtteam.structurize.util.LanguageHandler;
import com.minecolonies.api.util.constant.Constants;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

/**
 * Basic class that holds a reference to a configuration value.
 *
 * @param <T> the type of the value.
 */
abstract class AbstractConfigValue<T> implements IConfigValueWithBinder<T>
{
    /**
     * The key for the config value.
     */
    protected final String key;

    /**
     * The default value for the config.
     */
    protected final T defaultValue;

    /**
     * The current Forge bound config option.
     */
    private ForgeConfigSpec.ConfigValue<T> binding;

    /**
     * Create a config value given the key and the default value.
     *
     * @param key          the config key.
     * @param defaultValue the default value.
     */
    protected AbstractConfigValue(final String key, final T defaultValue)
    {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    @Override
    public final void setBinding(final ForgeConfigSpec.Builder builder)
    {
        this.binding = createBinding(builder);
    }

    /**
     * Method to create the Forge config spec binding value.
     *
     * @param builder the Forge config spec builder.
     * @return the Forge config value.
     */
    protected abstract ForgeConfigSpec.ConfigValue<T> createBinding(final ForgeConfigSpec.Builder builder);

    @Override
    public String getKey()
    {
        return key;
    }

    @Override
    public T getDefault()
    {
        return defaultValue;
    }

    /**
     * Util method to attach a comment and translation key to your Forge config value.
     *
     * @param builder     the Forge config spec builder.
     * @param key         the config key.
     * @param defaultDesc an optional default description to add at the end of the comment.
     * @return the Forge config spec builder configured with the context comment and translation values.
     */
    protected final ForgeConfigSpec.Builder withTranslation(final ForgeConfigSpec.Builder builder, final String key, final @Nullable String defaultDesc)
    {
        final String name = LanguageHandler.translateKey(Constants.MOD_ID + ".config." + key);
        String comment = LanguageHandler.translateKey(Constants.MOD_ID + ".config." + key + ".comment");
        if (!StringUtils.isBlank(defaultDesc))
        {
            comment += " " + defaultDesc;
        }
        return builder.comment(comment).translation(name);
    }

    @Override
    public final T get()
    {
        if (binding == null)
        {
            throw new IllegalStateException("Configuration value has not been bound to underlying configuration system, cannot load value.");
        }
        return binding.get();
    }
}
