package com.minecolonies.core.research;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for AStagesResearchEffect
 */
public class AStagesResearchEffectTest
{
    private ResourceLocation testId;
    private String testStage;

    @BeforeEach
    void setUp()
    {
        testId = new ResourceLocation("minecolonies", "effects/add_astages_stage");
        testStage = "mystage";
    }

    @Test
    void testAStagesResearchEffectCreation()
    {
        AStagesResearchEffect effect = new AStagesResearchEffect(testId, testStage);
        
        assertEquals(testId, effect.getId());
        assertEquals(testStage, effect.getStage());
        assertEquals(1.0, effect.getEffect());
        
        // Check translation keys are generated correctly
        String expectedDescKey = "com.minecolonies.research.effects.add.astages.stage.description";
        assertEquals(expectedDescKey, effect.getName().getKey());
    }

    @Test
    void testAStagesResearchEffectNBTSerialization()
    {
        AStagesResearchEffect originalEffect = new AStagesResearchEffect(testId, testStage);
        
        // Serialize to NBT
        CompoundTag nbt = originalEffect.writeToNBT();
        
        // Deserialize from NBT
        AStagesResearchEffect deserializedEffect = new AStagesResearchEffect(nbt);
        
        // Verify the deserialized effect matches the original
        assertEquals(originalEffect.getId(), deserializedEffect.getId());
        assertEquals(originalEffect.getStage(), deserializedEffect.getStage());
        assertEquals(originalEffect.getEffect(), deserializedEffect.getEffect());
    }

    @Test
    void testAStagesResearchEffectOverrides()
    {
        AStagesResearchEffect effect1 = new AStagesResearchEffect(testId, testStage);
        AStagesResearchEffect effect2 = new AStagesResearchEffect(testId, "anotherstage");
        
        // AStages effects should not override each other
        assertFalse(effect1.overrides(effect2));
        assertFalse(effect2.overrides(effect1));
    }
}