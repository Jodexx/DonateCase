---
id: holograms
title: Holograms
sidebar_position: 7
---

# DonateCase Holograms

## ArmorStand
### [HolographicDisplays](https://dev.bukkit.org/projects/holographic-displays) (1.16-1.20.4)
### [DecentHolograms](https://www.spigotmc.org/resources/decentholograms-1-8-1-21-1-papi-support-no-dependencies.96927/) (1.16+)
### [CMI Holograms](https://www.zrips.net/cmi/holograms/) (1.16+)
#### Configuration
```yaml
  Hologram:
    # Toggle on and off the holograms for the crates.
    Toggle: true
    # The height of the hologram above the crate.
    Height: 1.5
    # The distance the hologram can be seen. Only works with CMI and DecentHolograms
    Range: 8
    # The message that will be displayed.
    Message:
      - '&6DonateCase'
```
## Display entity (1.19.4+)
### [FancyHolograms](https://hangar.papermc.io/Oliver/FancyHolograms)
#### Configuration
```yaml
  Hologram:
    # Toggle on and off the holograms for the crates.
    Toggle: true
    FancyHolograms: # Here section from FancyHolograms configuration
      type: TEXT
      location:
        world: world
        x: 262.5
        y: 72
        z: -163.5
        yaw: -180
        pitch: 0
      visibility_distance: -1
      visibility: ALL
      persistent: true
      scale_x: 1.5
      scale_y: 1.5
      scale_z: 1.5
      shadow_radius: 1.0
      shadow_strength: 1.0
      text:
        - "Test"
      text_shadow: false
      see_through: false
      text_alignment: center
      update_text_interval: -1
```