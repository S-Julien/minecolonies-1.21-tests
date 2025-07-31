# AStages Research Effect

This feature adds support for granting AStages stages to colony managers when research is completed in Minecolonies.

## Overview

The AStages Research Effect allows modpack authors to integrate Minecolonies research with the AStages mod, providing a way to gate content based on colony research progression.

## Usage

Add the following effect to any research JSON file in the `data/minecolonies/researches/` directory:

```json
{
  "effects": [
    {
      "id": "minecolonies:effects/add_astages_stage",
      "stage": "your_stage_name_here"
    }
  ]
}
```

## Behavior

1. **Research Completion**: When a research with this effect is unlocked, all online colony managers are granted the specified AStages stage.

2. **Player Login**: If a colony manager was offline when research was completed, they will receive the stage upon their next login.

3. **Permission Changes**: When a player becomes a colony manager, they receive all active AStages stages for that colony. When they lose management permissions, the stages are revoked (unless they manage other colonies with the same stages).

4. **Colony Management**: Only players with colony management permissions (MANAGE_HUTS action) receive the stages.

## Example

Here's a complete research example that grants the "advanced_weaponry" stage:

```json
{
  "branch": "minecolonies:combat",
  "costs": [
    {
      "count": 64,
      "item": "minecraft:iron_sword"
    }
  ],
  "effects": [
    {
      "id": "minecolonies:effects/add_astages_stage",
      "stage": "advanced_weaponry"
    }
  ],
  "icon": "minecraft:iron_sword",
  "parentResearch": "minecolonies:combat/strength",
  "researchLevel": 3,
  "subtitle": "com.minecolonies.research.combat.advanced_weaponry.subtitle"
}
```

## Technical Details

- **Soft Dependency**: The implementation uses reflection to interact with AStages, so the mod is not required to be installed.
- **Event-Driven**: Uses Minecraft's event system to handle player login and colony permission changes.
- **Multi-Colony Support**: Players who manage multiple colonies will keep stages as long as at least one of their colonies has the research completed.

## Requirements

- AStages mod (optional, but required for the effect to work)
- Minecolonies with this research effect implementation

## Files Modified

- `AStagesResearchEffect.java`: Main effect implementation
- `AStagesHelper.java`: AStages integration utility
- `ResearchEffectManager.java`: Extended to handle AStages effects
- `ResearchListener.java`: JSON parsing support
- `Permissions.java`: Permission change hooks
- `AStagesResearchEventHandler.java`: Event handling for player login and permission changes