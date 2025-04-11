package com.minecolonies.api.configuration.builders.values;

import com.ldtteam.structurize.util.LanguageHandler;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Class that holds any integer configuration value.
 */
public class ConfigIntegerValue extends AbstractConfigValue<Integer>
{
    private final static String DESC_DEFAULT_KEY = "minecolonies.config.default.int";

    /**
     * The minimum allowed value.
     */
    private final int minValue;

    /**
     * The maximum allowed value.
     */
    private final int maxValue;

    /**
     * Create a numeric config value given the key, default value, minimum and maximum value.
     *
     * @param key          the config key.
     * @param defaultValue the default value.
     * @param minValue     the minimum value.
     * @param maxValue     the maximum value.
     */
    public ConfigIntegerValue(final String key, final int defaultValue, final int minValue, final int maxValue)
    {
        super(key, defaultValue);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public ForgeConfigSpec.ConfigValue<Integer> createBinding(final ForgeConfigSpec.Builder builder)
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
    public int getMinValue()
    {
        return minValue;
    }

    /**
     * Get the maximum allowed value.
     *
     * @return the numeric maximum value.
     */
    public int getMaxValue()
    {
        return maxValue;
    }
}
