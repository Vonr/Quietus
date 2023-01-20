# Quietus

Minecraft Fabric mod that allows customization of player XP drops, made for a private SMP I am part of.

## Configuration

The configuration file can be found in `server_folder/config/quietus.json` with the default values of
```json
{
  "enabled": true,
  "percentage": 0.8
}
```

## Commands

The `/quietus` command allows the user to change settings in-game as well as reload the configuration files to reload external changes.

```
/quietus: Shows help
/quietus
    toggle: Toggles the mod on or off
    percent [0.0..100.0]: Sets the percentage of XP to be dropped by players
    settings: Shows the current settings of the mod.
    reload: Reloads the configuration file.
```
