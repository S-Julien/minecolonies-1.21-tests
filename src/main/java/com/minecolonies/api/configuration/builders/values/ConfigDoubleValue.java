package com.minecolonies.api.configuration.builders.values;

import com.ldtteam.structurize.util.LanguageHandler;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Class that holds any double configuration value.
 */
public class ConfigDoubleValue extends AbstractConfigValue<Double>
{
    private final static String DESC_DEFAULT_KEY = "minecolonies.config.default.double";

    /**
     * The minimum allowed value.
     */
    private final double minValue;

    /**
     * The maximum allowed value.
     */
    private final double maxValue;

    /**
     * Create a numeric config value given the key, default value, minimum and maximum value.
     *
     * @param key          the config key.
     * @param defaultValue the default value.
     * @param minValue     the minimum value.
     * @param maxValue     the maximum value.
     */
    public ConfigDoubleValue(final String key, final double defaultValue, final double minValue, final double maxValue)
    {
        super(key, defaultValue);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public ForgeConfigSpec.ConfigValue<Double> createBinding(final ForgeConfigSpec.Builder builder)
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
    public double getMinValue()
    {
        return minValue;
    }

    /**
     * Get the maximum allowed value.
     *
     * @return the numeric maximum value.
     */
    public double getMaxValue()
    {
        return maxValue;
    }
}
