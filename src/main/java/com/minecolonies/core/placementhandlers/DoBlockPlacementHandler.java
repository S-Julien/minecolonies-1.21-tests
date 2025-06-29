package com.minecolonies.core.placementhandlers;

import com.ldtteam.domumornamentum.block.AbstractPostBlock;
import com.ldtteam.domumornamentum.block.IMateriallyTexturedBlock;
import com.ldtteam.domumornamentum.block.decorative.*;
import com.ldtteam.domumornamentum.block.types.TimberFrameType;
import com.ldtteam.domumornamentum.block.vanilla.DoorBlock;
import com.ldtteam.domumornamentum.block.vanilla.TrapdoorBlock;
import com.ldtteam.domumornamentum.util.BlockUtils;
import com.ldtteam.structurize.api.RotationMirror;
import com.ldtteam.structurize.placement.handlers.placement.IPlacementHandler;
import com.minecolonies.api.blocks.ModBlocks;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.api.util.Log;
import com.minecolonies.api.util.WorldUtil;
import com.minecolonies.api.util.constant.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ldtteam.structurize.placement.handlers.placement.PlacementHandlers.handleTileEntityPlacement;

public class DoBlockPlacementHandler implements IPlacementHandler
{
    @Override
    public boolean canHandle(@NotNull final Level world, @NotNull final BlockPos pos, @NotNull final BlockState blockState)
    {
        return blockState.getBlock() instanceof IMateriallyTexturedBlock && blockState.getBlock() != ModBlocks.blockRack;
    }

    @Override
    public ActionProcessingResult handle(
      @NotNull final Level world,
      @NotNull final BlockPos pos,
      @NotNull final BlockState blockState,
      @Nullable final CompoundTag tileEntityData,
      final boolean complete,
      final BlockPos centerPos,
      final RotationMirror settings)
    {
        BlockState placementState = blockState;
        if (blockState.getBlock() instanceof WallBlock || blockState.getBlock() instanceof FenceBlock || blockState.getBlock() instanceof PillarBlock || blockState.getBlock() instanceof IronBarsBlock)
        {
            try
            {
                final BlockState tempState = blockState.getBlock().getStateForPlacement(
                  new BlockPlaceContext(world, null, InteractionHand.MAIN_HAND, ItemStack.EMPTY,
                    new BlockHitResult(new Vec3(0, 0, 0), Direction.DOWN, pos, true)));
                if (tempState != null)
                {
                    placementState = tempState;
                }
            }
            catch (final Exception ex)
            {
                // Noop
            }
        }

        if (world.getBlockState(pos).equals(placementState))
        {
            world.removeBlock(pos, false);
            WorldUtil.setBlockState(world, pos, placementState, Constants.UPDATE_FLAG);
            if (tileEntityData != null)
            {
                try
                {
                    handleTileEntityPlacement(tileEntityData, world, pos, settings);
                    placementState.getBlock().setPlacedBy(world, pos, placementState, null, placementState.getBlock().getCloneItemStack(placementState,
                      new BlockHitResult(new Vec3(0,0,0), Direction.NORTH, pos, false), world, pos, null));
                }
                catch (final Exception ex)
                {
                    Log.getLogger().warn("Unable to place TileEntity");
                }
            }
            return ActionProcessingResult.PASS;
        }

        if (!WorldUtil.setBlockState(world, pos, placementState, Constants.UPDATE_FLAG))
        {
                return ActionProcessingResult.PASS;
        }

        if (tileEntityData != null)
        {
            try
            {
                handleTileEntityPlacement(tileEntityData, world, pos, settings);
                blockState.getBlock().setPlacedBy(world, pos, placementState, null, placementState.getBlock().getCloneItemStack(placementState,
                  new BlockHitResult(new Vec3(0,0,0), Direction.NORTH, pos, false), world, pos, null));
            }
            catch (final Exception ex)
            {
                Log.getLogger().warn("Unable to place TileEntity");
            }
        }

        return ActionProcessingResult.SUCCESS;
    }

    @Override
    public List<ItemStack> getRequiredItems(
      @NotNull final Level world,
      @NotNull final BlockPos pos,
      @NotNull final BlockState blockState,
      @Nullable final CompoundTag tileEntityData,
      final boolean complete)
    {
        final List<ItemStack> itemList = new ArrayList<>();
        if (tileEntityData != null)
        {
            BlockPos blockpos = new BlockPos(tileEntityData.getInt("x"), tileEntityData.getInt("y"), tileEntityData.getInt("z"));
            final BlockEntity tileEntity = BlockEntity.loadStatic(blockpos, blockState, tileEntityData, world.registryAccess());
            if (tileEntity == null)
            {
                return Collections.emptyList();
            }
            itemList.add(getCorrectDOItem(tileEntity, BlockUtils.getMaterializedItemStack(null, world.registryAccess()), blockState, world));
        }
        itemList.removeIf(ItemStackUtils::isEmpty);
        return itemList;
    }

    /**
     * Calculate the correct DO item.
     * Considering type and, for the builder we do want the generic type to be used here.
     *
     * @param tileEntity
     * @param item       the item to output.
     * @param blockState the blockstate in the world.
     * @param world
     * @return the adjusted item.
     */
    public static ItemStack getCorrectDOItem(BlockEntity tileEntity, final ItemStack item, final BlockState blockState, @NotNull Level world)
    {
        final Property<?> property;
        if (blockState.getBlock() instanceof DoorBlock)
        {
            property = DoorBlock.TYPE;
        }
        else if (blockState.getBlock() instanceof FancyDoorBlock)
        {
            property = FancyDoorBlock.TYPE;
        }
        else if (blockState.getBlock() instanceof TrapdoorBlock)
        {
            property = TrapdoorBlock.TYPE;
        }
        else if (blockState.getBlock() instanceof FancyTrapdoorBlock)
        {
            property = FancyTrapdoorBlock.TYPE;
        }
        else if (blockState.getBlock() instanceof PanelBlock)
        {
            property = PanelBlock.TYPE;
        }
        else if (blockState.getBlock() instanceof AbstractPostBlock<?>)
        {
            property = AbstractPostBlock.TYPE;
        }
        else if (blockState.getBlock() instanceof TimberFrameBlock || blockState.getBlock() instanceof DynamicTimberFrameBlock)
        {
            property = TimberFrameBlock.timberFrameType;
            final ItemStack stack = BlockUtils.getMaterializedItemStack(tileEntity, world.registryAccess(), property);
            item.getOrCreateTag().putString("type", TimberFrameType.FRAMED.toString().toUpperCase());

            final ItemStack tempItem = new ItemStack(com.ldtteam.domumornamentum.block.ModBlocks.getInstance().getTimberFrames().get(2));
            tempItem.setTag(item.getTag());
            return tempItem;
        }
        else
        {
            property = null;
        }

        return property == null ? BlockUtils.getMaterializedItemStack(tileEntity, world.registryAccess()) : BlockUtils.getMaterializedItemStack(tileEntity, world.registryAccess(), property);
    }

    @Override
    public void handleRemoval(
        final IStructureHandler handler,
        final Level world,
        final BlockPos pos)
    {
        if (!handler.isCreative())
        {
            final List<ItemStack> items = com.ldtteam.structurize.util.BlockUtils.getBlockDrops(world, pos, 0, handler.getHeldItem());
            for (final ItemStack item : items)
            {

                final BlockState state = world.getBlockState(pos);
                InventoryUtils.transferIntoNextBestSlot(getCorrectDOItem(tileEntity, item, state, world), handler.getInventory());
            }

        }
        world.removeBlock(pos, false);
    }
}
