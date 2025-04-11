package com.minecolonies.api.configuration.builders.builder;

/**
 * Interface for managing write-only configuration builders.
 */
public interface IConfigBuilder
{
    /**
     * Create a category configuration instance.
     * <p>Allows you to define configuration values listed under a given category.</p>
     *
     * @param key the category key to use, will determine the translation string.
     * @return the category builder.
     */
    ConfigBuilder.ConfigCategoryBuilder createCategory(final String key);
}
