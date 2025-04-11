package com.minecolonies.api.configuration.builders.values;

import java.util.function.Supplier;

public interface IConfigValue<T> extends Supplier<T>
{
    String getKey();

    T getDefault();
}
