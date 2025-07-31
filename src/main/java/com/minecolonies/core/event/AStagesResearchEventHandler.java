package com.minecolonies.core.event;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.core.research.AStagesHelper;
import com.minecolonies.core.research.ResearchEffectManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/**
 * Event handler for AStages research effect integration.
 */
@EventBusSubscriber
public class AStagesResearchEventHandler
{
    /**
     * Handle player login events to grant any missed AStages stages.
     *
     * @param event the player login event.
     */
    @SubscribeEvent
    public static void onPlayerLogin(final PlayerEvent.PlayerLoggedInEvent event)
    {
        if (!(event.getEntity() instanceof ServerPlayer player))
        {
            return;
        }

        // Grant any AStages stages from colonies this player manages
        for (IColony colony : IColonyManager.getInstance().getAllColonies())
        {
            if (colony.getPermissions().hasPermission(player, Action.MANAGE_HUTS))
            {
                if (colony.getResearchManager().getResearchEffects() instanceof ResearchEffectManager effectManager)
                {
                    for (String stage : effectManager.getActiveStages())
                    {
                        if (!AStagesHelper.hasStage(player, stage))
                        {
                            AStagesHelper.grantStage(player, stage);
                        }
                    }
                }
            }
        }
    }

    /**
     * Handle colony permission changes to grant/revoke stages.
     * This is called when a player's colony permissions change.
     */
    public static void onColonyPermissionChange(final IColony colony, final ServerPlayer player, final boolean isManager)
    {
        if (colony.getResearchManager().getResearchEffects() instanceof ResearchEffectManager effectManager)
        {
            for (String stage : effectManager.getActiveStages())
            {
                if (isManager)
                {
                    // Player became a manager, grant stages
                    if (!AStagesHelper.hasStage(player, stage))
                    {
                        AStagesHelper.grantStage(player, stage);
                    }
                }
                else
                {
                    // Player is no longer a manager, revoke stages
                    if (AStagesHelper.hasStage(player, stage))
                    {
                        // Only revoke if they don't manage any other colonies with this stage
                        boolean hasStageFromOtherColony = false;
                        for (IColony otherColony : IColonyManager.getInstance().getAllColonies())
                        {
                            if (otherColony.getID() != colony.getID() && 
                                otherColony.getPermissions().hasPermission(player, Action.MANAGE_HUTS))
                            {
                                if (otherColony.getResearchManager().getResearchEffects() instanceof ResearchEffectManager otherEffectManager)
                                {
                                    if (otherEffectManager.getActiveStages().contains(stage))
                                    {
                                        hasStageFromOtherColony = true;
                                        break;
                                    }
                                }
                            }
                        }
                        
                        if (!hasStageFromOtherColony)
                        {
                            AStagesHelper.revokeStage(player, stage);
                        }
                    }
                }
            }
        }
    }
}