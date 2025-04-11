package com.minecolonies.api.configuration.builders;

import com.ldtteam.structurize.util.LanguageHandler;
import com.minecolonies.api.configuration.builders.values.IConfigValueWithBinder;
import com.minecolonies.api.util.constant.Constants;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Collection;
import java.util.Map;

public class ForgeConfigWriter implements IConfigWriter
{
    private final ForgeConfigSpec.Builder builder;

    public ForgeConfigWriter(final ForgeConfigSpec.Builder builder)
    {
        this.builder = builder;
    }

    @Override
    public void write(final Map<String, Collection<IConfigValueWithBinder<?>>> configuration)
    {
        for (final Map.Entry<String, Collection<IConfigValueWithBinder<?>>> categoryEntry : configuration.entrySet())
        {
            final String categoryKey = categoryEntry.getKey();

            builder.comment(LanguageHandler.translateKey(Constants.MOD_ID + ".config." + categoryKey + ".comment")).push(categoryKey);

            for (final IConfigValueWithBinder<?> optionValue : categoryEntry.getValue())
            {
                optionValue.setBinding(builder);
            }

            builder.pop();
        }
    }
}
