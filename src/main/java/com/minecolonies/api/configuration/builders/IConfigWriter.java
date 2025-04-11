package com.minecolonies.api.configuration.builders;

import com.minecolonies.api.configuration.builders.values.IConfigValueWithBinder;

import java.util.Collection;
import java.util.Map;

public interface IConfigWriter
{
    void write(final Map<String, Collection<IConfigValueWithBinder<?>>> configuration);
}
