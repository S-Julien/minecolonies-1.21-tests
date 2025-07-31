package com.minecolonies.core.research;

import com.minecolonies.api.research.ModResearchEffects;
import com.minecolonies.api.research.IResearchEffect;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A research effect that grants or revokes AStages stages to colony managers.
 */
public class AStagesResearchEffect implements IResearchEffect
{
    /**
     * The NBT tag for the effect's identifier.
     */
    private static final String TAG_ID = "id";

    /**
     * The NBT tag for the effect's description.
     */
    private static final String TAG_DESC = "desc";

    /**
     * The NBT tag for the effect's subtitle.
     */
    private static final String TAG_SUBTITLE = "subtitle";

    /**
     * The NBT tag for the AStages stage name.
     */
    private static final String TAG_STAGE = "stage";

    /**
     * The unique effect Id.
     */
    private final ResourceLocation id;

    /**
     * The effect's description.
     */
    private final TranslatableContents name;

    /**
     * The effect's subtitle description.
     */
    private final TranslatableContents subtitle;

    /**
     * The AStages stage name to grant/revoke.
     */
    private final String stage;

    /**
     * Constructor to create a new AStages research effect.
     *
     * @param id       the effect id.
     * @param stage    the AStages stage name.
     */
    public AStagesResearchEffect(final ResourceLocation id, final String stage)
    {
        this.id = id;
        this.stage = stage;
        
        // Generate translation keys based on the effect id
        String descKey = String.format("com.%s.research.%s.description", 
            id.getNamespace(), id.getPath().replaceAll("[ /]", "."));
        String subtitleKey = String.format("com.%s.research.%s.subtitle", 
            id.getNamespace(), id.getPath().replaceAll("[ /]", "."));
        
        this.name = new TranslatableContents(descKey, null, List.of(stage).toArray());
        this.subtitle = new TranslatableContents(subtitleKey, null, TranslatableContents.NO_ARGS);
    }

    /**
     * The constructor to build a new AStages research effect from NBT.
     *
     * @param nbt the nbt containing the traits for the effect.
     */
    public AStagesResearchEffect(final CompoundTag nbt)
    {
        this.id = ResourceLocation.parse(nbt.getString(TAG_ID));
        this.stage = nbt.getString(TAG_STAGE);
        this.name = new TranslatableContents(nbt.getString(TAG_DESC), null, List.of(stage).toArray());
        this.subtitle = new TranslatableContents(nbt.getString(TAG_SUBTITLE), null, TranslatableContents.NO_ARGS);
    }

    @Override
    public ModResearchEffects.ResearchEffectEntry getRegistryEntry()
    {
        return ModResearchEffects.astagesResearchEffect.get();
    }

    @Override
    public ResourceLocation getId()
    {
        return this.id;
    }

    @Override
    public TranslatableContents getName()
    {
        return this.name;
    }

    @Override
    public TranslatableContents getSubtitle()
    {
        return this.subtitle;
    }

    @Override
    public double getEffect()
    {
        // AStages effects don't have numeric values
        return 1.0;
    }

    @Override
    public boolean overrides(@NotNull final IResearchEffect other)
    {
        // AStages effects don't override each other based on magnitude
        return false;
    }

    @Override
    public CompoundTag writeToNBT()
    {
        final CompoundTag nbt = new CompoundTag();
        nbt.putString(TAG_ID, id.toString());
        nbt.putString(TAG_DESC, name.getKey());
        nbt.putString(TAG_SUBTITLE, subtitle.getKey());
        nbt.putString(TAG_STAGE, stage);
        return nbt;
    }

    /**
     * Get the AStages stage name.
     *
     * @return the stage name.
     */
    public String getStage()
    {
        return stage;
    }
}