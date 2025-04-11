package com.minecolonies.api.configuration.builders.builder;

import com.minecolonies.api.configuration.builders.values.IConfigValue;

import java.util.List;
import java.util.function.Predicate;

public interface IConfigCategoryBuilder
{
    /**
     * Define an integer value. With a given key and a default value.
     * <p>Minimum and maximum values range from {@link Integer#MIN_VALUE} and {@link Integer#MAX_VALUE}.</p>
     *
     * @param key          the key for the config option.
     * @param defaultValue the default value the config option will utilize.
     * @return the supplier for the value.
     */
    default IConfigValue<Integer> defineInteger(final String key, final int defaultValue)
    {
        return defineInteger(key, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Define an integer value. With a given key and a default value.
     * <p>Also provides a minimum and a maximum value.</p>
     *
     * @param key          the key for the config option.
     * @param defaultValue the default value the config option will utilize.
     * @param minValue     the minimum value the config can have.
     * @param maxValue     the maximum value the config can have.
     * @return the supplier for the value.
     */
    IConfigValue<Integer> defineInteger(final String key, final int defaultValue, final int minValue, final int maxValue);

    /**
     * Define a double value. With a given key and a default value.
     * <p>Minimum and maximum values range from {@link Double#MIN_VALUE} and {@link Double#MAX_VALUE}.</p>
     *
     * @param key          the key for the config option.
     * @param defaultValue the default value the config option will utilize.
     * @return the supplier for the value.
     */
    default IConfigValue<Double> defineDouble(final String key, final double defaultValue)
    {
        return defineDouble(key, defaultValue, Double.MIN_VALUE, Double.MAX_VALUE);
    }

    /**
     * Define a double value. With a given key and a default value.
     * <p>Also provides a minimum and a maximum value.</p>
     *
     * @param key          the key for the config option.
     * @param defaultValue the default value the config option will utilize.
     * @param minValue     the minimum value the config can have.
     * @param maxValue     the maximum value the config can have.
     * @return the supplier for the value.
     */
    IConfigValue<Double> defineDouble(final String key, final double defaultValue, final double minValue, final double maxValue);

    /**
     * Define a long value. With a given key and a default value.
     * <p>Minimum and maximum values range from {@link Long#MIN_VALUE} and {@link Long#MAX_VALUE}.</p>
     *
     * @param key          the key for the config option.
     * @param defaultValue the default value the config option will utilize.
     * @return the supplier for the value.
     */
    default IConfigValue<Long> defineLong(final String key, final long defaultValue)
    {
        return defineLong(key, defaultValue, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    /**
     * Define a double value. With a given key and a default value.
     * <p>Also provides a minimum and a maximum value.</p>
     *
     * @param key          the key for the config option.
     * @param defaultValue the default value the config option will utilize.
     * @param minValue     the minimum value the config can have.
     * @param maxValue     the maximum value the config can have.
     * @return the supplier for the value.
     */
    IConfigValue<Long> defineLong(final String key, final long defaultValue, final long minValue, final long maxValue);

    /**
     * Define a boolean value. With a given key and a default value.
     *
     * @param key          the key for the config option.
     * @param defaultValue the default value the config option will utilize.
     * @return the supplier for the value.
     */
    IConfigValue<Boolean> defineBoolean(final String key, final boolean defaultValue);

    /**
     * Define an enum value. With a given key and a default value.
     * <p>All possible values must equal one of to the enum keys.</p>
     *
     * @param key          the key for the config option.
     * @param defaultValue the default value the config option will utilize.
     * @return the supplier for the value.
     */
    <T extends Enum<T>> IConfigValue<T> defineEnum(final String key, final T defaultValue);

    /**
     * Define a list value. With a given key and a default value.
     * <p>All possible values must equal one of to the enum keys.</p>
     *
     * @param key              the key for the config option.
     * @param defaultValue     the default value the config option will utilize.
     * @param elementValidator a validator predicate that will check if each parsed item is a proper value.
     * @return the supplier for the value.
     */
    <T> IConfigValue<List<? extends T>> defineList(final String key, final List<? extends T> defaultValue, final Predicate<Object> elementValidator);
}
