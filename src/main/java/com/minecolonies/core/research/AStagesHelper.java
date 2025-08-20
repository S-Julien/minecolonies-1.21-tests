package com.minecolonies.core.research;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.util.Log;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;

import java.lang.reflect.Method;

/**
 * Utility class for interacting with AStages mod.
 */
public class AStagesHelper
{
    private static boolean astagesLoaded = false;
    private static Class<?> astagesUtilClass;
    private static Class<?> playerStageClass;
    private static Class<?> playerStageProviderClass;
    private static Object playerStageCapability;
    private static Method hasStageMethod;
    private static Method addStageMethod;
    private static Method removeStageMethod;

    static
    {
        try
        {
            if (ModList.get().isLoaded("astages"))
            {
                astagesLoaded = true;
                // Reflect to AStages classes to avoid hard dependency
                astagesUtilClass = Class.forName("com.alessandro.astages.util.AStagesUtil");
                playerStageClass = Class.forName("com.alessandro.astages.capability.PlayerStage");
                playerStageProviderClass = Class.forName("com.alessandro.astages.capability.PlayerStageProvider");
                
                // Get the hasStage method from AStagesUtil
                hasStageMethod = astagesUtilClass.getMethod("hasStage", Player.class, String.class);
                
                // Get methods from PlayerStage class for add/remove operations
                addStageMethod = playerStageClass.getMethod("addStage", String.class);
                removeStageMethod = playerStageClass.getMethod("removeStage", String.class);
                
                // Get the PLAYER_STAGE capability field
                playerStageCapability = playerStageProviderClass.getField("PLAYER_STAGE").get(null);
                
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
            // Get the PlayerStage capability from the player
            Object capability = player.getClass().getMethod("getCapability", Class.forName("net.minecraftforge.common.capabilities.Capability"))
                    .invoke(player, playerStageCapability);
            
            // Get the PlayerStage instance from the LazyOptional
            Object lazyOptional = capability;
            Object playerStageInstance = lazyOptional.getClass().getMethod("orElse", Object.class).invoke(lazyOptional, (Object) null);
            
            if (playerStageInstance != null)
            {
                addStageMethod.invoke(playerStageInstance, stage);
                return true;
            }
            return false;
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
            // Get the PlayerStage capability from the player
            Object capability = player.getClass().getMethod("getCapability", Class.forName("net.minecraftforge.common.capabilities.Capability"))
                    .invoke(player, playerStageCapability);
            
            // Get the PlayerStage instance from the LazyOptional
            Object lazyOptional = capability;
            Object playerStageInstance = lazyOptional.getClass().getMethod("orElse", Object.class).invoke(lazyOptional, (Object) null);
            
            if (playerStageInstance != null)
            {
                removeStageMethod.invoke(playerStageInstance, stage);
                return true;
            }
            return false;
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

        for (net.minecraft.world.entity.player.Player player : colony.getMessagePlayerEntities())
        {
            if (player instanceof ServerPlayer serverPlayer && 
                colony.getPermissions().hasPermission(player, com.minecolonies.api.colony.permissions.Action.MANAGE_HUTS))
            {
                grantStage(serverPlayer, stage);
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

        for (net.minecraft.world.entity.player.Player player : colony.getMessagePlayerEntities())
        {
            if (player instanceof ServerPlayer serverPlayer && 
                colony.getPermissions().hasPermission(player, com.minecolonies.api.colony.permissions.Action.MANAGE_HUTS))
            {
                revokeStage(serverPlayer, stage);
            }
        }
    }
}