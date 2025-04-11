package com.minecolonies.api.configuration.builders.values;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Class that holds any double configuration value.
 */
public class ConfigEnumValue<T extends Enum<T>> extends AbstractConfigValue<T>
{
    /**
     * Create a bool config value given the key and default value.
     *
     * @param key          the config key.
     * @param defaultValue the default value.
     */
    public ConfigEnumValue(final String key, final T defaultValue)
    {
        super(key, defaultValue);
    }

    @Override
    public ForgeConfigSpec.ConfigValue<T> createBinding(final ForgeConfigSpec.Builder builder)
    {
        return withTranslation(builder, key, null).defineEnum(key, defaultValue);
    }
}
