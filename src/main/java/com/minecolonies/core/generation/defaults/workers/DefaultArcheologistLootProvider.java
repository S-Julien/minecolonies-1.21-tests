package com.minecolonies.core.generation.defaults.workers;

import com.minecolonies.api.colony.jobs.ModJobs;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.items.ModItems;
import com.minecolonies.core.colony.crafting.LootTableAnalyzer;
import com.minecolonies.core.generation.CustomRecipeAndLootTableProvider;
import com.minecolonies.core.generation.CustomRecipeProvider;
import com.minecolonies.core.generation.DatagenLootTableManager;
import com.minecolonies.core.generation.SimpleLootTableProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.entries.TagEntry;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.minecolonies.api.util.constant.BuildingConstants.MODULE_CUSTOM;
import static com.minecolonies.api.util.constant.Constants.MOD_ID;
import static com.minecolonies.api.util.constant.NbtTagConstants.*;

/**
 * Datagen for Archeologist Worker
 */
public class DefaultArcheologistLootProvider extends CustomRecipeAndLootTableProvider
{
    public static final  String ARCHEOLOGIST       = ModJobs.ARCHEOLOGIST_ID.getPath();
    private static final int    MAX_BUILDING_LEVEL = 5;

    private final DatagenLootTableManager lootTableManager;
    private final List<LootTable.Builder> levels;

    public DefaultArcheologistLootProvider(
        @NotNull final PackOutput packOutput,
        @NotNull final DatagenLootTableManager lootTableManager)
    {
        super(packOutput);
        this.lootTableManager = lootTableManager;

        levels = new ArrayList<>();

        for (int buildingLevel = 1; buildingLevel <= MAX_BUILDING_LEVEL; ++buildingLevel)
        {
            levels.add(createTripLoot(buildingLevel));
        }
    }

    private LootTable.Builder createTripLoot(final int buildingLevel)
    {
        return new LootTable.Builder()
            .withPool(createBlocksPool(buildingLevel))
            .withPool(createMobsPool(buildingLevel));
    }

    @NotNull
    private LootPool.Builder createBlocksPool(final int buildingLevel)
    {
        final LootPool.Builder blocks = new LootPool.Builder()
            .setRolls(UniformGenerator.between(3, 10))
            .setBonusRolls(UniformGenerator.between(0.3F, 0.3F));

        blocks.add(LootItem.lootTableItem(Items.BONE)
            .setWeight(30)
            .apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 10))));

        blocks.add(LootItem.lootTableItem(Items.SUSPICIOUS_SAND)
            .setWeight(20)
            .apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 25))));
        blocks.add(LootItem.lootTableItem(Items.SUSPICIOUS_GRAVEL)
            .setWeight(20)
            .apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 25))));

        blocks.add(LootItem.lootTableItem(Items.SUSPICIOUS_STEW)
            .setWeight(1)
            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1))));

        if (buildingLevel >= 2)
        {
            blocks.add(TagEntry.expandTag(ItemTags.DECORATED_POT_SHERDS)
                .setWeight(1)
                .setQuality(2)
                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))));
        }

        return blocks;
    }

    @NotNull
    private LootPool.Builder createMobsPool(final int buildingLevel)
    {
        final LootPool.Builder mobs = new LootPool.Builder()
            .setRolls(UniformGenerator.between(2, 6))
            .setBonusRolls(UniformGenerator.between(0.1F, 0.1F));

        mobs.add(createAdventureToken(EntityType.CREEPER, 5, 5)
            .setWeight(5500).setQuality(-10));

        mobs.add(createAdventureToken(EntityType.SKELETON, 3, 4)
            .setWeight(300).setQuality(10));

        mobs.add(createAdventureToken(EntityType.EVOKER, 3, 5)
            .setWeight(500).setQuality(-1));

        mobs.add(createAdventureToken(EntityType.VINDICATOR, 12, 5)
            .setWeight(300).setQuality(-3));

        mobs.add(createAdventureToken(EntityType.ENDERMAN, 7, 5)
            .setWeight(300).setQuality(-3));

        return mobs;
    }

    private LootPoolSingletonContainer.Builder<?> createAdventureToken(@NotNull final EntityType<?> mob, final int damage_done, final int xp_gained)
    {
        final CompoundTag nbt = new CompoundTag();
        nbt.putString(TAG_ENTITY_TYPE, ForgeRegistries.ENTITY_TYPES.getKey(mob).toString());
        nbt.putInt(TAG_DAMAGE, damage_done);
        nbt.putInt(TAG_XP_DROPPED, xp_gained);

        final ItemStack stack = new ItemStack(ModItems.adventureToken);
        stack.setTag(nbt);

        return SimpleLootTableProvider.itemStack(stack);
    }

    @NotNull
    @Override
    public String getName()
    {
        return "ArcheologistWorkerLootProvider";
    }

    @Override
    protected void registerRecipes(@NotNull final Consumer<FinishedRecipe> consumer)
    {
        final List<ItemStorage> inputs = Arrays.asList(
            new ItemStorage(new ItemStack(Items.COBBLESTONE, 64)),
            new ItemStorage(new ItemStack(Items.TORCH, 32)),
            new ItemStorage(new ItemStack(Items.LADDER, 16))
        );

        for (int i = 0; i < levels.size(); ++i)
        {
            final int buildingLevel = i + 1;

            final List<LootTableAnalyzer.LootDrop> drops = LootTableAnalyzer.toDrops(lootTableManager, levels.get(i).build());
            final Stream<Item> loot = drops.stream().flatMap(drop -> drop.getItemStacks().stream()
                .sorted(Comparator.comparing(ItemStack::getCount).reversed().thenComparing(ItemStack::getDescriptionId))
                .map(ItemStack::getItem));

            CustomRecipeProvider.CustomRecipeBuilder.create(ARCHEOLOGIST, MODULE_CUSTOM, "trip" + buildingLevel)
                .minBuildingLevel(buildingLevel)
                .maxBuildingLevel(buildingLevel)
                .inputs(inputs)
                .secondaryOutputs(loot.map(ItemStack::new).collect(Collectors.toList()))
                .lootTable(new ResourceLocation(MOD_ID, "recipes/" + ARCHEOLOGIST + "/trip" + buildingLevel))
                .build(consumer);
        }

        // and also a lava bucket recipe for good measure
        CustomRecipeProvider.CustomRecipeBuilder.create(ARCHEOLOGIST, MODULE_CUSTOM, "lava")
            .inputs(Collections.singletonList(new ItemStorage(new ItemStack(Items.BUCKET))))
            .result(new ItemStack(Items.LAVA_BUCKET))
            .build(consumer);
    }

    @Override
    protected void registerTables(@NotNull final SimpleLootTableProvider.LootTableRegistrar registrar)
    {
        for (int i = 0; i < levels.size(); ++i)
        {
            final int buildingLevel = i + 1;
            final LootTable.Builder lootTable = levels.get(i);

            registrar.register(new ResourceLocation(MOD_ID, "recipes/" + ARCHEOLOGIST + "/trip" + buildingLevel),
                LootContextParamSets.ALL_PARAMS, lootTable);
        }
    }
}
