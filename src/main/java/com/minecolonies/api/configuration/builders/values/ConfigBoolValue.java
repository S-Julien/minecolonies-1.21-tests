package com.minecolonies.api.configuration.builders.values;

import com.ldtteam.structurize.util.LanguageHandler;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Class that holds any double configuration value.
 */
public class ConfigBoolValue extends AbstractConfigValue<Boolean>
{
    private final static String DESC_DEFAULT_KEY = "minecolonies.config.default.boolean";

    /**
     * Create a bool config value given the key and default value.
     *
     * @param key          the config key.
     * @param defaultValue the default value.
     */
    public ConfigBoolValue(final String key, final boolean defaultValue)
    {
        super(key, defaultValue);
    }

    @Override
    public ForgeConfigSpec.ConfigValue<Boolean> createBinding(final ForgeConfigSpec.Builder builder)
    {
        return withTranslation(builder, key, LanguageHandler.translateKeyWithFormat(DESC_DEFAULT_KEY, defaultValue)).define(key, defaultValue);
    }
}
