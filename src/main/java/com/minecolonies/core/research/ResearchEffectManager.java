package com.minecolonies.core.research;

import com.minecolonies.api.research.IResearchEffect;
import com.minecolonies.api.research.IResearchEffectManager;
import com.minecolonies.api.colony.IColony;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

/**
 * The map of unlocked research effects of a given colony.
 */
public class ResearchEffectManager implements IResearchEffectManager
{
    /**
     * The map of the research effects, from a string identifier to the effect.
     */
    private final Map<ResourceLocation, IResearchEffect> effectMap = new HashMap<>();

    /**
     * Set of active AStages stages for this colony.
     */
    private final Set<String> activeStages = new HashSet<>();

    /**
     * The colony this manager belongs to.
     */
    private final IColony colony;

    /**
     * Create a new research effect manager.
     *
     * @param colony the colony this manager belongs to.
     */
    public ResearchEffectManager(final IColony colony)
    {
        this.colony = colony;
    }

    @Override
    public double getEffectStrength(final ResourceLocation id)
    {
        final IResearchEffect effect = effectMap.get(id);
        if (effect instanceof GlobalResearchEffect)
        {
            return effect.getEffect();
        }
        return 0;
    }

    @Override
    public void applyEffect(final IResearchEffect effect)
    {
        final IResearchEffect effectInMap = effectMap.get(effect.getId());
        if (effectInMap != null)
        {
            if (effect.overrides(effectInMap))
            {
                effectMap.put(effect.getId(), effect);
            }
        }
        else
        {
            effectMap.put(effect.getId(), effect);
        }

        // Handle AStages effects specifically
        if (effect instanceof AStagesResearchEffect astagesEffect)
        {
            final String stage = astagesEffect.getStage();
            if (!activeStages.contains(stage))
            {
                activeStages.add(stage);
                AStagesHelper.grantStageToColonyManagers(colony, stage);
            }
        }
    }

    @Override
    public void removeAllEffects()
    {
        // Revoke all AStages stages before clearing effects
        for (String stage : activeStages)
        {
            AStagesHelper.revokeStageFromColonyManagers(colony, stage);
        }
        
        effectMap.clear();
        activeStages.clear();
    }

    /**
     * Get the set of active AStages stages.
     *
     * @return the set of active stage names.
     */
    public Set<String> getActiveStages()
    {
        return new HashSet<>(activeStages);
    }
}
