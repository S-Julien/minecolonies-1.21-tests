package com.minecolonies.core.research;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.util.Log;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.ModList;

import java.lang.reflect.Method;

/**
 * Utility class for interacting with AStages mod.
 */
public class AStagesHelper
{
    private static boolean astagesLoaded = false;
    private static Class<?> stageUtilClass;
    private static Method addStageMethod;
    private static Method removeStageMethod;
    private static Method hasStageMethod;

    static
    {
        try
        {
            if (ModList.get().isLoaded("astages"))
            {
                astagesLoaded = true;
                // Reflect to AStages classes to avoid hard dependency
                stageUtilClass = Class.forName("net.darkhax.astages.util.StageUtil");
                addStageMethod = stageUtilClass.getMethod("addStage", ServerPlayer.class, String.class);
                removeStageMethod = stageUtilClass.getMethod("removeStage", ServerPlayer.class, String.class);
                hasStageMethod = stageUtilClass.getMethod("hasStage", ServerPlayer.class, String.class);
                
                Log.getLogger().info("AStages integration enabled for research effects.");
            }
            else
            {
                Log.getLogger().debug("AStages not found, stage research effects will be ignored.");
            }
        }
        catch (Exception e)
        {
            Log.getLogger().error("Failed to initialize AStages integration", e);
            astagesLoaded = false;
        }
    }

    /**
     * Check if AStages is loaded and available.
     *
     * @return true if AStages is available.
     */
    public static boolean isAStagesLoaded()
    {
        return astagesLoaded;
    }

    /**
     * Grant a stage to a player.
     *
     * @param player the player to grant the stage to.
     * @param stage  the stage name.
     * @return true if the stage was granted successfully.
     */
    public static boolean grantStage(final ServerPlayer player, final String stage)
    {
        if (!astagesLoaded)
        {
            return false;
        }

        try
        {
            addStageMethod.invoke(null, player, stage);
            return true;
        }
        catch (Exception e)
        {
            Log.getLogger().error("Failed to grant AStages stage '{}' to player '{}'", stage, player.getName().getString(), e);
            return false;
        }
    }

    /**
     * Revoke a stage from a player.
     *
     * @param player the player to revoke the stage from.
     * @param stage  the stage name.
     * @return true if the stage was revoked successfully.
     */
    public static boolean revokeStage(final ServerPlayer player, final String stage)
    {
        if (!astagesLoaded)
        {
            return false;
        }

        try
        {
            removeStageMethod.invoke(null, player, stage);
            return true;
        }
        catch (Exception e)
        {
            Log.getLogger().error("Failed to revoke AStages stage '{}' from player '{}'", stage, player.getName().getString(), e);
            return false;
        }
    }

    /**
     * Check if a player has a specific stage.
     *
     * @param player the player to check.
     * @param stage  the stage name.
     * @return true if the player has the stage.
     */
    public static boolean hasStage(final ServerPlayer player, final String stage)
    {
        if (!astagesLoaded)
        {
            return false;
        }

        try
        {
            return (Boolean) hasStageMethod.invoke(null, player, stage);
        }
        catch (Exception e)
        {
            Log.getLogger().error("Failed to check AStages stage '{}' for player '{}'", stage, player.getName().getString(), e);
            return false;
        }
    }

    /**
     * Grant a stage to all online colony managers.
     *
     * @param colony the colony.
     * @param stage  the stage name.
     */
    public static void grantStageToColonyManagers(final IColony colony, final String stage)
    {
        if (!astagesLoaded)
        {
            return;
        }

        for (ServerPlayer player : colony.getMessagePlayerEntities())
        {
            if (colony.getPermissions().hasPermission(player, com.minecolonies.api.colony.permissions.Action.MANAGE_HUTS))
            {
                grantStage(player, stage);
            }
        }
    }

    /**
     * Revoke a stage from all online colony managers.
     *
     * @param colony the colony.
     * @param stage  the stage name.
     */
    public static void revokeStageFromColonyManagers(final IColony colony, final String stage)
    {
        if (!astagesLoaded)
        {
            return;
        }

        for (ServerPlayer player : colony.getMessagePlayerEntities())
        {
            if (colony.getPermissions().hasPermission(player, com.minecolonies.api.colony.permissions.Action.MANAGE_HUTS))
            {
                revokeStage(player, stage);
            }
        }
    }
}