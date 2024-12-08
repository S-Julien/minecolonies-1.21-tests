package com.minecolonies.core.entity.ai.minimal;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.workerbuildings.hospital.modules.IPatientModule;
import com.minecolonies.api.colony.interactionhandling.ChatPriority;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.entity.ai.IStateAI;
import com.minecolonies.api.entity.ai.statemachine.states.CitizenAIState;
import com.minecolonies.api.entity.ai.statemachine.states.IState;
import com.minecolonies.api.entity.ai.statemachine.tickratestatemachine.TickingTransition;
import com.minecolonies.api.util.InventoryUtils;
import com.minecolonies.api.util.SoundUtils;
import com.minecolonies.core.Network;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingHospital;
import com.minecolonies.core.colony.buildings.workerbuildings.hospital.modules.SickPatientModule;
import com.minecolonies.core.colony.interactionhandling.StandardInteraction;
import com.minecolonies.core.datalistener.model.Disease;
import com.minecolonies.core.entity.citizen.EntityCitizen;
import com.minecolonies.core.network.messages.client.CircleParticleEffectMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import static com.minecolonies.api.util.constant.GuardConstants.BASIC_VOLUME;
import static com.minecolonies.api.util.constant.TranslationConstants.NO_HOSPITAL;
import static com.minecolonies.core.entity.ai.minimal.EntityAISickTask.DiseaseState.*;

/**
 * The AI task for citizens to execute when they are sick.
 */
public class EntityAISickTask extends EntityAIBeAtHospitalTask implements IStateAI
{
    /**
     * Min distance to hut before pathing to hospital.
     */
    private static final int MIN_DIST_TO_HUT = 5;

    /**
     * Required time to cure.
     */
    private static final int REQUIRED_TIME_TO_CURE = 60;

    /**
     * Chance for a random cure to happen.
     */
    private static final int CHANCE_FOR_RANDOM_CURE = 10;

    /**
     * Instantiates this task.
     *
     * @param citizen the citizen.
     */
    public EntityAISickTask(final EntityCitizen citizen)
    {
        super(citizen);

        citizen.getCitizenAI().addTransition(new TickingTransition<>(CitizenAIState.SICK, this::isSick, () -> GO_TO_HUT, 20));
        citizen.getCitizenAI().addTransition(new TickingTransition<>(GO_TO_HUT, () -> true, this::goToHut, 20));
        citizen.getCitizenAI().addTransition(new TickingTransition<>(CHECK_FOR_CURE, () -> true, this::checkForCure, 20));
        citizen.getCitizenAI().addTransition(new TickingTransition<>(WANDER, () -> true, this::wander, 200));

        citizen.getCitizenAI().addTransition(new TickingTransition<>(WAIT_FOR_CURE, () -> true, this::waitForCure, 20));
        citizen.getCitizenAI().addTransition(new TickingTransition<>(APPLY_CURE, () -> true, this::applyCure, 20));
    }

    private boolean isSick()
    {
        if (citizen.getCitizenData().getCitizenDiseaseHandler().isSick())
        {
            reset();
            return true;
        }

        return false;
    }

    /**
     * Do a bit of wandering.
     *
     * @return start over.
     */
    public IState wander()
    {
        citizen.getNavigation().moveToRandomPos(10, 0.6D);
        return CHECK_FOR_CURE;
    }

    /**
     * Checks if the citizen has the cure in the inventory and makes a decision based on that.
     *
     * @return the next state to go to.
     */
    private IState checkForCure()
    {
        final Disease disease = citizen.getCitizenData().getCitizenDiseaseHandler().getDisease();
        if (disease == null)
        {
            return CitizenAIState.IDLE;
        }
        for (final ItemStorage cure : disease.cureItems())
        {
            final int slot = InventoryUtils.findFirstSlotInProviderNotEmptyWith(citizen, Disease.hasCureItem(cure));
            if (slot == -1)
            {
                reset();
                return requiresHospital();
            }
        }
        return APPLY_CURE;
    }

    /**
     * Actual action for applying the cure.
     *
     * @return the next state to go to, if successful CitizenAIState.IDLE.
     */
    private IState applyCure()
    {
        if (checkForCure() != APPLY_CURE)
        {
            return CHECK_FOR_CURE;
        }

        final Disease disease = citizen.getCitizenData().getCitizenDiseaseHandler().getDisease();
        if (disease == null)
        {
            return CitizenAIState.IDLE;
        }

        final List<ItemStorage> list = disease.cureItems();
        if (!list.isEmpty())
        {
            citizen.setItemInHand(InteractionHand.MAIN_HAND, list.get(citizen.getRandom().nextInt(list.size())).getItemStack());
        }

        citizen.swing(InteractionHand.MAIN_HAND);
        citizen.playSound(SoundEvents.NOTE_BLOCK_HARP.get(), (float) BASIC_VOLUME, (float) SoundUtils.getRandomPentatonic(citizen.getRandom()));
        Network.getNetwork().sendToTrackingEntity(
          new CircleParticleEffectMessage(
            citizen.position().add(0, 2, 0),
            ParticleTypes.HAPPY_VILLAGER, 1), citizen);

        waitingTicks++;
        if (waitingTicks < REQUIRED_TIME_TO_CURE)
        {
            return APPLY_CURE;
        }

        cure();
        return CitizenAIState.IDLE;
    }

    /**
     * Cure the citizen.
     */
    private void cure()
    {
        final Disease disease = citizen.getCitizenData().getCitizenDiseaseHandler().getDisease();
        if (disease != null)
        {
            for (final ItemStorage cure : disease.cureItems())
            {
                final int slot = InventoryUtils.findFirstSlotInProviderNotEmptyWith(citizen, Disease.hasCureItem(cure));
                if (slot != -1)
                {
                    citizenData.getInventory().extractItem(slot, 1, false);
                }
            }
        }

        if (usedBed != null)
        {
            final BlockPos hospitalPos = citizen.getCitizenColonyHandler().getColonyOrRegister().getBuildingManager().getBestBuilding(citizen, BuildingHospital.class);
            final IColony colony = citizen.getCitizenColonyHandler().getColonyOrRegister();
            final IBuilding hospital = colony.getBuildingManager().getBuilding(hospitalPos);
            ((BuildingHospital) hospital).registerPatient(usedBed, 0);
            usedBed = null;
            citizen.getCitizenData().setBedPos(BlockPos.ZERO);
        }
        citizen.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        citizen.getCitizenData().getCitizenDiseaseHandler().cure();
        reset();
    }

    /**
     * Stay in bed while waiting to be cured.
     *
     * @return the next state to go to.
     */
    private IState waitForCure()
    {
        final IColony colony = citizenData.getColony();
        final IState state = checkForCure();
        if (state == APPLY_CURE)
        {
            return APPLY_CURE;
        }
        else if (state == CitizenAIState.IDLE)
        {
            reset();
            return CitizenAIState.IDLE;
        }

        if (citizen.getRandom().nextInt(10000) < CHANCE_FOR_RANDOM_CURE)
        {
            cure();
            return CitizenAIState.IDLE;
        }

        if (citizen.getCitizenSleepHandler().isAsleep())
        {
            final BlockPos hospital = colony.getBuildingManager().getBestBuilding(citizen, BuildingHospital.class);
            if (hospital != null)
            {
                final IBuilding building = colony.getBuildingManager().getBuilding(hospital);
                if (building instanceof BuildingHospital && !((BuildingHospital) building).getBedList().contains(citizen.getCitizenSleepHandler().getBedLocation()))
                {
                    citizen.getCitizenSleepHandler().onWakeUp();
                }
            }
        }

        return WAIT_FOR_CURE;
    }

    /**
     * Go to the hut to move to the hospital from there.
     *
     * @return the next state to go to.
     */
    private IState goToHut()
    {
        final IBuilding buildingWorker = citizenData.getWorkBuilding();
        if (buildingWorker == null)
        {
            return WANDER;
        }

        if (citizen.isWorkerAtSiteWithMove(buildingWorker.getPosition(), MIN_DIST_TO_HUT))
        {
            return CHECK_FOR_CURE;
        }
        return GO_TO_HUT;
    }

    @Override
    protected void performActionsInHospital(final BuildingHospital hospital)
    {
        applyCure();
    }

    @Override
    protected IState nextAIStateWhenNoHospital()
    {
        final Disease disease = citizen.getCitizenData().getCitizenDiseaseHandler().getDisease();
        if (disease != null)
        {
            citizenData.triggerInteraction(new StandardInteraction(Component.translatable(NO_HOSPITAL, disease.name(), disease.getCureString()),
              Component.translatable(NO_HOSPITAL),
              ChatPriority.BLOCKING));
        }
        return WANDER;
    }

    @Override
    protected IPatientModule createPatientModule()
    {
        return new SickPatientModule(citizenData.getId());
    }

    /**
     * The different types of AIStates related to being sick.
     */
    public enum DiseaseState implements IState
    {
        CHECK_FOR_CURE,
        GO_TO_HUT,
        WAIT_FOR_CURE,
        APPLY_CURE,
        WANDER
    }
}
