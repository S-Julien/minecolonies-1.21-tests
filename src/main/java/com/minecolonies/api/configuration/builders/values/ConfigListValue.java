package com.minecolonies.api.configuration.builders.values;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;
import java.util.function.Predicate;

/**
 * Class that holds any double configuration value.
 */
public class ConfigListValue<T> extends AbstractConfigValue<List<? extends T>>
{
    /**
     * The validator predicate for the individual items in the list.
     */
    private final Predicate<Object> elementValidator;

    /**
     * Create a bool config value given the key and default value.
     *
     * @param key              the config key.
     * @param defaultValue     the default value.
     * @param elementValidator the validator predicate for the individual items in the list.
     */
    public ConfigListValue(final String key, final List<? extends T> defaultValue, final Predicate<Object> elementValidator)
    {
        super(key, defaultValue);
        this.elementValidator = elementValidator;
    }

    @Override
    public ForgeConfigSpec.ConfigValue<List<? extends T>> createBinding(final ForgeConfigSpec.Builder builder)
    {
        return withTranslation(builder, key, null).defineList(key, defaultValue, elementValidator);
    }
}
