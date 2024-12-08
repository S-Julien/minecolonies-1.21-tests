package com.minecolonies.core.entity.citizen.citizenhandlers;

import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.api.entity.citizen.citizenhandlers.ICitizenHealthHandler;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.core.colony.jobs.AbstractJobGuard;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import static com.minecolonies.api.research.util.ResearchConstants.REGENERATION;
import static com.minecolonies.api.research.util.ResearchConstants.SATLIMIT;
import static com.minecolonies.api.util.constant.CitizenConstants.*;
import static com.minecolonies.api.util.constant.StatisticsConstants.CITIZENS_HEALED;

/**
 * Handler taking care of citizens their health.
 */
public class CitizenHealthHandler implements ICitizenHealthHandler
{
    /**
     * NBT tags.
     */
    private static final String TAG_MARKED_HURT = "markedHurt";

    /**
     * Health percentage at which citizens seek a doctor.
     */
    private static final float SEEK_DOCTOR_HEALTH_HIGH = 0.5f;
    private static final float SEEK_DOCTOR_HEALTH_LOW  = 0.2f;

    /**
     * The citizen assigned to this manager.
     */
    private final AbstractEntityCitizen citizen;

    /**
     * Position of the hospital the citizen might be sleeping at (or null if not sleeping at a hospital).
     */
    private BlockPos hospitalPos = null;

    /**
     * Whether a citizen was randomly selected to be marked hurt, forcing them to go to the hospital on being hurt.
     */
    private boolean isMarkedHurt = false;

    /**
     * Constructor for the health handler.
     *
     * @param citizen the citizen owning the handler.
     */
    public CitizenHealthHandler(final AbstractEntityCitizen citizen)
    {
        this.citizen = citizen;
    }

    @Override
    public boolean isHurt()
    {
        if (citizen.getCitizenJobHandler() instanceof AbstractJobGuard)
        {
            return false;
        }

        if (canContinueToWork())
        {
            return true;
        }

        if (isMarkedHurt)
        {
            return true;
        }

        float chance = citizen.getHealth() / citizen.getMaxHealth();
        float randomPercent = 1 - (chance - SEEK_DOCTOR_HEALTH_LOW) / (SEEK_DOCTOR_HEALTH_HIGH - SEEK_DOCTOR_HEALTH_LOW);
        if (chance <= SEEK_DOCTOR_HEALTH_HIGH && citizen.getRandom().nextFloat() > randomPercent)
        {
            isMarkedHurt = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinueToWork()
    {
        return citizen.getHealth() / citizen.getMaxHealth() > SEEK_DOCTOR_HEALTH_LOW;
    }

    @Override
    public BlockPos getActiveHospital()
    {
        return hospitalPos;
    }

    @Override
    public void setActiveHospital(final BlockPos hospitalPos)
    {
        this.hospitalPos = hospitalPos;
    }

    @Override
    public void autoHeal()
    {
        final ICitizenData citizenData = citizen.getCitizenData();
        if (citizenData == null)
        {
            return;
        }

        final float potentialMaxHealth = citizenData.getCitizenDiseaseHandler().isSick() ? citizen.getMaxHealth() / 3 : citizen.getMaxHealth();
        if (citizen.getHealth() < potentialMaxHealth && citizen.getLastHurtByMob() == null)
        {
            final double limitDecrease = citizenData.getColony().getResearchManager().getResearchEffects().getEffectStrength(SATLIMIT);
            final double citizenSaturation = citizenData.getSaturation();
            final double healAmount;
            if (hospitalPos != null)
            {
                healAmount = 4 * (1.0 + citizenData.getColony().getResearchManager().getResearchEffects().getEffectStrength(REGENERATION));
            }
            else if (citizenSaturation >= FULL_SATURATION + limitDecrease)
            {
                healAmount = 2 * (1.0 + citizenData.getColony().getResearchManager().getResearchEffects().getEffectStrength(REGENERATION));
            }
            else if (citizenSaturation < LOW_SATURATION)
            {
                healAmount = 1 * (citizenSaturation / FULL_SATURATION) / 2.0;
            }
            else
            {
                healAmount = 1 * (1.0 + citizenData.getColony().getResearchManager().getResearchEffects().getEffectStrength(REGENERATION));
            }

            heal((float) healAmount);
        }
    }

    @Override
    public void heal(float amount)
    {
        citizen.setHealth(citizen.getMaxHealth());
        citizen.getCitizenData().getColony().getStatisticsManager().increment(CITIZENS_HEALED, citizen.getCitizenData().getColony().getDay());
    }

    @Override
    public void healFully()
    {
        heal(citizen.getMaxHealth());
    }

    @Override
    public void write(final CompoundTag compound)
    {
        if (hospitalPos != null)
        {
            BlockPosUtil.write(compound, TAG_HOSPITAL, hospitalPos);
        }
        compound.putBoolean(TAG_MARKED_HURT, isMarkedHurt);
    }

    @Override
    public void read(final CompoundTag compound)
    {
        if (compound.contains(TAG_HOSPITAL))
        {
            hospitalPos = BlockPosUtil.read(compound, TAG_HOSPITAL);
        }
        isMarkedHurt = compound.getBoolean(TAG_MARKED_HURT);
    }

    @Override
    public void reset()
    {
        isMarkedHurt = false;
        hospitalPos = null;
    }
}
