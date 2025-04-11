package com.minecolonies.api.configuration;

import com.minecolonies.api.colony.permissions.Explosions;
import com.minecolonies.api.configuration.builders.builder.ConfigBuilder;
import com.minecolonies.api.configuration.builders.builder.IConfigBuilder;
import com.minecolonies.api.configuration.builders.values.IConfigValue;
import com.minecolonies.api.util.constant.CitizenConstants;

import java.util.List;

import static com.minecolonies.api.util.constant.Constants.*;

/**
 * Mod server configuration. Loaded serverside, synced on connection.
 */
public class ServerConfiguration
{
    /*  --------------------------------------------------------------------------- *
     *  ------------------- ######## Gameplay settings ######## ------------------- *
     *  --------------------------------------------------------------------------- */

    public final IConfigValue<Integer> initialCitizenAmount;
    public final IConfigValue<Boolean> allowInfiniteSupplyChests;
    public final IConfigValue<Boolean> allowInfiniteColonies;
    public final IConfigValue<Boolean> allowOtherDimColonies;
    public final IConfigValue<Integer> maxCitizenPerColony;
    public final IConfigValue<Boolean> enableInDevelopmentFeatures;
    public final IConfigValue<Boolean> alwaysRenderNameTag;
    public final IConfigValue<Boolean> workersAlwaysWorkInRain;
    public final IConfigValue<Integer> luckyBlockChance;
    public final IConfigValue<Integer> minThLevelToTeleport;
    public final IConfigValue<Double>  foodModifier;
    public final IConfigValue<Integer> diseaseModifier;
    public final IConfigValue<Boolean> forceLoadColony;
    public final IConfigValue<Integer> loadtime;
    public final IConfigValue<Integer> colonyLoadStrictness;
    public final IConfigValue<Integer> maxTreeSize;
    public final IConfigValue<Boolean> noSupplyPlacementRestrictions;
    public final IConfigValue<Boolean> skyRaiders;

    /*  --------------------------------------------------------------------------- *
     *  ------------------- ######## Research settings ######## ------------------- *
     *  --------------------------------------------------------------------------- */
    public final IConfigValue<Boolean>                researchCreativeCompletion;
    public final IConfigValue<Boolean>                researchDebugLog;
    public final IConfigValue<List<? extends String>> researchResetCost;

    /*  --------------------------------------------------------------------------- *
     *  ------------------- ######## Command settings ######## ------------------- *
     *  --------------------------------------------------------------------------- */

    public final IConfigValue<Boolean> canPlayerUseRTPCommand;
    public final IConfigValue<Boolean> canPlayerUseColonyTPCommand;
    public final IConfigValue<Boolean> canPlayerUseAllyTHTeleport;
    public final IConfigValue<Boolean> canPlayerUseHomeTPCommand;
    public final IConfigValue<Boolean> canPlayerUseShowColonyInfoCommand;
    public final IConfigValue<Boolean> canPlayerUseKillCitizensCommand;
    public final IConfigValue<Boolean> canPlayerUseAddOfficerCommand;
    public final IConfigValue<Boolean> canPlayerUseDeleteColonyCommand;
    public final IConfigValue<Boolean> canPlayerUseResetCommand;

    /*  --------------------------------------------------------------------------- *
     *  ------------------- ######## Claim settings ######## ------------------- *
     *  --------------------------------------------------------------------------- */

    public final IConfigValue<Integer> maxColonySize;
    public final IConfigValue<Integer> minColonyDistance;
    public final IConfigValue<Integer> initialColonySize;
    public final IConfigValue<Integer> maxDistanceFromWorldSpawn;
    public final IConfigValue<Integer> minDistanceFromWorldSpawn;

    /*  ------------------------------------------------------------------------- *
     *  ------------------- ######## Combat Settings ######## ------------------- *
     *  ------------------------------------------------------------------------- */

    public final IConfigValue<Boolean> enableColonyRaids;
    public final IConfigValue<Integer> raidDifficulty;
    public final IConfigValue<Integer> maxRaiders;
    public final IConfigValue<Boolean> raidersbreakblocks;
    public final IConfigValue<Integer> averageNumberOfNightsBetweenRaids;
    public final IConfigValue<Integer> minimumNumberOfNightsBetweenRaids;
    public final IConfigValue<Boolean> raidersbreakdoors;
    public final IConfigValue<Boolean> mobAttackCitizens;
    public final IConfigValue<Double>  guardDamageMultiplier;
    public final IConfigValue<Double>  guardHealthMult;
    public final IConfigValue<Boolean> pvp_mode;

    /*  ----------------------------------------------------------------------------- *
     *  ------------------- ######## Permission Settings ######## ------------------- *
     *  ----------------------------------------------------------------------------- */

    public final IConfigValue<Boolean>    enableColonyProtection;
    public final IConfigValue<Explosions> turnOffExplosionsInColonies;

    /*  -------------------------------------------------------------------------------- *
     *  ------------------- ######## Compatibility Settings ######## ------------------- *
     *  -------------------------------------------------------------------------------- */

    public final IConfigValue<Boolean> auditCraftingTags;
    public final IConfigValue<Boolean> debugInventories;
    public final IConfigValue<Boolean> blueprintBuildMode;

    /*  ------------------------------------------------------------------------------ *
     *  ------------------- ######## Pathfinding Settings ######## ------------------- *
     *  ------------------------------------------------------------------------------ */

    public final IConfigValue<Integer> pathfindingDebugVerbosity;
    public final IConfigValue<Integer> pathfindingMaxThreadCount;
    public final IConfigValue<Integer> minimumRailsToPath;

    /*  --------------------------------------------------------------------------------- *
     *  ------------------- ######## Request System Settings ######## ------------------- *
     *  --------------------------------------------------------------------------------- */

    public final IConfigValue<Boolean> creativeResolve;

    /**
     * Builds server configuration.
     *
     * @param builder config builder
     */
    public ServerConfiguration(final IConfigBuilder builder)
    {
        final ConfigBuilder.ConfigCategoryBuilder gameplay = builder.createCategory("gameplay");
        initialCitizenAmount = gameplay.defineInteger("initialcitizenamount", 4, 1, 10);
        allowInfiniteSupplyChests = gameplay.defineBoolean("allowinfinitesupplychests", false);
        allowInfiniteColonies = gameplay.defineBoolean("allowinfinitecolonies", false);
        allowOtherDimColonies = gameplay.defineBoolean("allowotherdimcolonies", true);
        maxCitizenPerColony = gameplay.defineInteger("maxcitizenpercolony", 250, 30, CitizenConstants.CITIZEN_LIMIT_MAX);
        enableInDevelopmentFeatures = gameplay.defineBoolean("enableindevelopmentfeatures", false);
        alwaysRenderNameTag = gameplay.defineBoolean("alwaysrendernametag", true);
        workersAlwaysWorkInRain = gameplay.defineBoolean("workersalwaysworkinrain", false);
        luckyBlockChance = gameplay.defineInteger("luckyblockchance", 1, 0, 100);
        minThLevelToTeleport = gameplay.defineInteger("minthleveltoteleport", 3, 0, 5);
        foodModifier = gameplay.defineDouble("foodmodifier", 1.0, 0.1, 100);
        diseaseModifier = gameplay.defineInteger("diseasemodifier", 5, 1, 100);
        forceLoadColony = gameplay.defineBoolean("forceloadcolony", true);
        loadtime = gameplay.defineInteger("loadtime", 10, 1, 1440);
        colonyLoadStrictness = gameplay.defineInteger("colonyloadstrictness", 3, 1, 15);
        maxTreeSize = gameplay.defineInteger("maxtreesize", 400, 1, 1000);
        noSupplyPlacementRestrictions = gameplay.defineBoolean("nosupplyplacementrestrictions", false);
        skyRaiders = gameplay.defineBoolean("skyraiders", false);

        final ConfigBuilder.ConfigCategoryBuilder research = builder.createCategory("research");
        researchCreativeCompletion = research.defineBoolean("researchcreativecompletion", true);
        researchDebugLog = research.defineBoolean("researchdebuglog", false);
        researchResetCost = research.defineList("researchresetcost", List.of("minecolonies:ancienttome:1"), s -> s instanceof String);

        final ConfigBuilder.ConfigCategoryBuilder commands = builder.createCategory("commands");
        canPlayerUseRTPCommand = commands.defineBoolean("canplayerusertpcommand", false);
        canPlayerUseColonyTPCommand = commands.defineBoolean("canplayerusecolonytpcommand", false);
        canPlayerUseAllyTHTeleport = commands.defineBoolean("canplayeruseallytownhallteleport", true);
        canPlayerUseHomeTPCommand = commands.defineBoolean("canplayerusehometpcommand", false);
        canPlayerUseShowColonyInfoCommand = commands.defineBoolean("canplayeruseshowcolonyinfocommand", true);
        canPlayerUseKillCitizensCommand = commands.defineBoolean("canplayerusekillcitizenscommand", false);
        canPlayerUseAddOfficerCommand = commands.defineBoolean("canplayeruseaddofficercommand", true);
        canPlayerUseDeleteColonyCommand = commands.defineBoolean("canplayerusedeletecolonycommand", false);
        canPlayerUseResetCommand = commands.defineBoolean("canplayeruseresetcommand", false);

        final ConfigBuilder.ConfigCategoryBuilder claims = builder.createCategory("claims");
        maxColonySize = claims.defineInteger("maxColonySize", 20, 1, 250);
        minColonyDistance = claims.defineInteger("minColonyDistance", 8, 1, 200);
        initialColonySize = claims.defineInteger("initialColonySize", 4, 1, 15);
        maxDistanceFromWorldSpawn = claims.defineInteger("maxdistancefromworldspawn", 30000, 1000, Integer.MAX_VALUE);
        minDistanceFromWorldSpawn = claims.defineInteger("mindistancefromworldspawn", 0, 0, 1000);

        final ConfigBuilder.ConfigCategoryBuilder combat = builder.createCategory("combat");
        enableColonyRaids = combat.defineBoolean("dobarbariansspawn", true);
        raidDifficulty = combat.defineInteger("barbarianhordedifficulty", DEFAULT_BARBARIAN_DIFFICULTY, MIN_BARBARIAN_DIFFICULTY, MAX_BARBARIAN_DIFFICULTY);
        maxRaiders = combat.defineInteger("maxBarbarianSize", 80, MIN_BARBARIAN_HORDE_SIZE, MAX_BARBARIAN_HORDE_SIZE);
        raidersbreakblocks = combat.defineBoolean("dobarbariansbreakthroughwalls", true);
        averageNumberOfNightsBetweenRaids = combat.defineInteger("averagenumberofnightsbetweenraids", 14, 1, 50);
        minimumNumberOfNightsBetweenRaids = combat.defineInteger("minimumnumberofnightsbetweenraids", 10, 1, 30);
        mobAttackCitizens = combat.defineBoolean("mobattackcitizens", true);
        raidersbreakdoors = combat.defineBoolean("shouldraiderbreakdoors", true);
        guardDamageMultiplier = combat.defineDouble("guardDamageMultiplier", 1.0, 0.1, 15.0);
        guardHealthMult = combat.defineDouble("guardhealthmult", 1.0, 0.1, 5.0);
        pvp_mode = combat.defineBoolean("pvp_mode", false);

        final ConfigBuilder.ConfigCategoryBuilder permissions = builder.createCategory("permissions");
        enableColonyProtection = permissions.defineBoolean("enablecolonyprotection", true);
        turnOffExplosionsInColonies = permissions.defineEnum("turnoffexplosionsincolonies", Explosions.DAMAGE_ENTITIES);

        final ConfigBuilder.ConfigCategoryBuilder compatibility = builder.createCategory("compatibility");
        auditCraftingTags = compatibility.defineBoolean("auditcraftingtags", false);
        debugInventories = compatibility.defineBoolean("debuginventories", false);
        blueprintBuildMode = compatibility.defineBoolean("blueprintbuildmode", false);

        final ConfigBuilder.ConfigCategoryBuilder pathfinding = builder.createCategory("pathfinding");
        pathfindingDebugVerbosity = pathfinding.defineInteger("pathfindingdebugverbosity", 0, 0, 10);
        minimumRailsToPath = pathfinding.defineInteger("minimumrailstopath", 8, 5, 100);
        pathfindingMaxThreadCount = pathfinding.defineInteger("pathfindingmaxthreadcount", 1, 1, 10);

        final ConfigBuilder.ConfigCategoryBuilder requestSystem = builder.createCategory("requestSystem");
        creativeResolve = requestSystem.defineBoolean("creativeresolve", false);
    }
}
