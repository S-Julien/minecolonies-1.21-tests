package com.minecolonies.api.configuration.builders.values;

import com.ldtteam.structurize.util.LanguageHandler;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Class that holds any long configuration value.
 */
public class ConfigLongValue extends AbstractConfigValue<Long>
{
    private final static String DESC_DEFAULT_KEY = "minecolonies.config.default.long";

    /**
     * The minimum allowed value.
     */
    private final long minValue;

    /**
     * The maximum allowed value.
     */
    private final long maxValue;

    /**
     * Create a numeric config value given the key, default value, minimum and maximum value.
     *
     * @param key          the config key.
     * @param defaultValue the default value.
     * @param minValue     the minimum value.
     * @param maxValue     the maximum value.
     */
    public ConfigLongValue(final String key, final long defaultValue, final long minValue, final long maxValue)
    {
        super(key, defaultValue);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public ForgeConfigSpec.ConfigValue<Long> createBinding(final ForgeConfigSpec.Builder builder)
    {
        return withTranslation(builder, key, LanguageHandler.translateKeyWithFormat(DESC_DEFAULT_KEY, defaultValue, minValue, maxValue)).defineInRange(key,
            defaultValue,
            minValue,
            maxValue);
    }

    /**
     * Get the minimum allowed value.
     *
     * @return the numeric minimum value.
     */
    public long getMinValue()
    {
        return minValue;
    }

    /**
     * Get the maximum allowed value.
     *
     * @return the numeric maximum value.
     */
    public long getMaxValue()
    {
        return maxValue;
    }
}
