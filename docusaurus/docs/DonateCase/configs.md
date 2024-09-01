---
id: configurations
title: Configurations
sidebar_position: 3
---

~~**Try our new DonateCase case generator! -> [LINK](https://jodexindustries.xyz/donatecasegenerator/)**~~

```yaml
├──
├── cases
│   └── case.yml # your case configuration
├── lang # languages
│   ├── ru_RU.yml 
│   ├── en_US.yml
│   └── ua_UA.yml
├── Cases.yml # cases data
├── Config.yml # configuration file
├── Data.yml # data file
├── Keys.yml # keys data
├── Animations.yml # animations configuration
└──
```

## Config.yml
<details>
<summary>Config.yml</summary>

```yml
config: '2.5' #DON'T CHANGE THIS PLEASE

DonateCase:
  #Do I need to check for plugin updates?
  UpdateChecker: true
  #MySQL Settings
  MySql:
    Enabled: false
    Host: 'localhost'
    Port: 3306
    DataBase: 'nameDataBase'
    User: 'root'
    Password: 'password'
  # en_US, es_ES, ru_RU, ua_UA
  Languages: en_US
  HologramDriver: DecentHolograms # CMI, DecentHolograms or HolographicDisplays
  PermissionDriver: luckperms # luckperms or vault
  # Only if Vault enabled
  LevelGroup: false #Enable if you use PermissionEX, if you use LuckPerms, don't enable if you use "parent add"
  LevelGroups:
    legend: 4
    deluxe: 3
    vipplus: 2
    vip: 1
    default: 0
  DateFormat: "dd.MM HH:mm:ss"
  AddonsHelp: true # Show help list for addons commands?
  UsePackets: false # 1.18+ for using packets, you need to install packetevents plugin
  # Caching for getting number of keys, case opens and history data
  # Used only if MySQL enabled
  # Actually used in placeholders (GUI and PlaceholderAPI)
  Caching: 20 # in ticks
  # Set spawn-protection to 0 in server.properties
  DisableSpawnProtection: true
```
</details>

## case.yml
<details>
<summary>case.yml</summary>

```yml
config: 1.2
case:
  Animation: SHAPE #SHAPE, FIREWORK, RAINLY, WHEEL, FULLWHEEL see: https://wiki.jodexindustries.xyz/docs/DonateCase/animations
  Hologram: # Thanks CrazyCrates for realisation
    # Toggle on and off the holograms for the crates.
    Toggle: true
    # The height of the hologram above the crate.
    Height: 1.5
    # The distance the hologram can be seen. Only works with CMI and DecentHolograms
    Range: 8
    # The message that will be displayed.
    Message:
      - '&6DonateCase'
      -

  OpenType: GUI # or BLOCK

  NoKeyActions:
    - "[message] &cYou don't have keys for this case. You can buy them here >>> &6JodexIndustries.com"
    - "[sound] ENTITY_ENDERMAN_TELEPORT"

  LevelGroups: # delete all of this section, if you want to use default LevelGroups from Config.yml
    ultra: 6
    legend: 5
    deluxe: 4
    premium: 3
    vipplus: 2
    vip: 1
    default: 0

  DisplayName: "&c&lDonate-&a&lCase" # Name for placeholders
  Gui: # see more about items configuring -> https://wiki.jodexindustries.xyz/docs/DonateCase/items-settings
    Title: "&c&lDonate-&a&lCase"
    Size: 45
    UpdateRate: 20 # in ticks, set -1 to disable updating
    Items:
      "1":
        DisplayName: "&cJodexIndustries.xyz"
        Enchanted: false
        Lore:
          - ""
        Slots: # or Slots: 0-10 (range)
          - 0
          - 8
        Material: WHITE_STAINED_GLASS_PANE # see: https://wiki.jodexindustries.xyz/docs/DonateCase/materials
        Type: DEFAULT
      Open:
        DisplayName: "&bOpen Case"
        Enchanted: false
        Lore:
          - '&6The case can be bought on the website: &cJodexIndustries.xyz'
          - ''
          - '&7Keys: &e%keys%'
          - ''
          - '&6Drops:'
          - '&7[&eVIP&7] - &b&n30%'
          - '&7[&bVIP&6+&7] - &b&n25%'
          - '&7[&3Premium&7] - &b&n20%'
          - '&7[&5Deluxe&7] - &b&n15%'
          - '&7[&dLegend&7] - &b&n10%'
          - '&7[&cUltra&7] - &b&n5%'
          - ''
        Slots: # or Slots: 0-10 (range)
          - 22
        Material: TRIPWIRE_HOOK # see: https://wiki.jodexindustries.xyz/docs/DonateCase/materials
        Type: OPEN # or you can use OPEN_<anotherCaseName> for opening another case 
  Items:
    Vip:
      Group: vip
      Chance: 30
      Item:
        ID: YELLOW_WOOL # see: https://wiki.jodexindustries.xyz/docs/DonateCase/materials
        DisplayName: '&7[&eVIP&7]'
        Enchanted: false
      Index: 0
      GiveType: ONE # or RANDOM
      Actions: # GiveType: ONE
        - '[command] lp user %player% parent set %group%'
        - '[title] &aCongratulations!;&5you won %groupdisplayname%'
        - '[broadcast] &a>&c>&e> &c%player% &6won a donate %groupdisplayname% &6from &5Ultra-Case.'
      AlternativeActions: # GiveType: any, it doesn't matter; is performed if the group is lower in rank than the player's group in LevelGroups
        - "[message] &cI'm sorry %player%, but you have group a stronger group than you won:("
      RandomActions: # GiveType: RANDOM
        first:
          Chance: 50
          DisplayName: "something" # displayname for historydata displaying
          Actions:
            - '[command] say something'
            - '[broadcast] &a>&c>&e> &c%player% &6won a donate %groupdisplayname% &6from &5Ultra-Case.'
        second:
          Chance: 50
          Actions:
            - '[command] say something'
            - '[broadcast] &a>&c>&e> &c%player% &6won a donate %groupdisplayname% &6from &5Ultra-Case.'
    VipPlus:
      Group: vipplus
      Chance: 25
      Item:
        ID: LIGHT_BLUE_WOOL # see: https://wiki.jodexindustries.xyz/docs/DonateCase/materials
        DisplayName: '&7[&bVIP&6+&7]'
        Enchanted: false
      Index: 1
      GiveType: ONE # or RANDOM
      Actions: # GiveType: ONE
        - '[command] lp user %player% parent set %group%'
        - '[title] &aCongratulations!;&5you won %groupdisplayname%'
        - '[broadcast] &a>&c>&e> &c%player% &6won a donate %groupdisplayname% &6from &5Ultra-Case.'
      AlternativeActions: # GiveType: any, it doesn't matter; is performed if the group is lower in rank than the player's group in LevelGroups
        - "[message] &cI'm sorry %player%, but you have group a stronger group than you won:("
      RandomActions: # GiveType: RANDOM
        first:
          Chance: 50
          DisplayName: "something" # displayname for historydata displaying
          Actions:
            - '[command] say something'
            - '[broadcast] &a>&c>&e> &c%player% &6won a donate %groupdisplayname% &6from &5Ultra-Case.'
        second:
          Chance: 50
          Actions:
            - '[command] say something'
            - '[broadcast] &a>&c>&e> &c%player% &6won a donate %groupdisplayname% &6from &5Ultra-Case.'
    Premium:
      Group: premium
      Chance: 20
      Item:
        ID: CYAN_WOOL
        DisplayName: '&7[&3Premium&7]'
      Index: 2
      GiveType: ONE
      Actions:
        - '[command] lp user %player% parent set %group%'
        - '[title] &aCongratulations!;&5you won %groupdisplayname%'
        - '[broadcast] &a>&c>&e> &c%player% &6won a donate %groupdisplayname% &6from
        &5Ultra-Case.'
      AlternativeActions: # GiveType: any, it doesn't matter; is performed if the group is lower in rank than the player's group in LevelGroups
        - "[message] &cI'm sorry %player%, but you have group a stronger group than you won:("
      RandomActions: # GiveType: RANDOM
        first:
          Chance: 50
          DisplayName: "something" # displayname for historydata displaying
          Actions:
            - '[command] say something'
            - '[broadcast] &a>&c>&e> &c%player% &6won a donate %groupdisplayname% &6from &5Ultra-Case.'
        second:
          Chance: 50
          Actions:
            - '[command] say something'
            - '[broadcast] &a>&c>&e> &c%player% &6won a donate %groupdisplayname% &6from &5Ultra-Case.'
    Deluxe:
      Group: deluxe
      Chance: 15
      Item:
        ID: PURPLE_WOOL # see: https://wiki.jodexindustries.xyz/docs/DonateCase/materials
        DisplayName: '&7[&5Deluxe&7]'
        Enchanted: false
      Index: 3
      GiveType: ONE # or RANDOM
      Actions: # GiveType: ONE
        - '[command] lp user %player% parent set %group%'
        - '[title] &aCongratulations!;&5you won %groupdisplayname%'
        - '[broadcast] &a>&c>&e> &c%player% &6won a donate %groupdisplayname% &6from &5Ultra-Case.'
      AlternativeActions: # GiveType: any, it doesn't matter; is performed if the group is lower in rank than the player's group in LevelGroups
        - "[message] &cI'm sorry %player%, but you have group a stronger group than you won:("
      RandomActions: # GiveType: RANDOM
        first:
          Chance: 50
          Actions:
            - '[command] say something'
            - '[broadcast] &a>&c>&e> &c%player% &6won a donate %groupdisplayname% &6from &5Ultra-Case.'
        second:
          Chance: 50
          Actions:
            - '[command] say something'
            - '[broadcast] &a>&c>&e> &c%player% &6won a donate %groupdisplayname% &6from &5Ultra-Case.'
    Legend:
      Group: legend
      Chance: 10
      Item:
        ID: PINK_WOOL # see: https://wiki.jodexindustries.xyz/docs/DonateCase/materials
        DisplayName: '&7[&dLegend&7]'
        Enchanted: false
      Index: 4
      GiveType: ONE # or RANDOM
      Actions: # GiveType: ONE
        - '[command] lp user %player% parent set %group%'
        - '[title] &aCongratulations!;&5you won %groupdisplayname%'
        - '[broadcast] &a>&c>&e> &c%player% &6won a donate %groupdisplayname% &6from &5Ultra-Case.'
      AlternativeActions: # GiveType: any, it doesn't matter; is performed if the group is lower in rank than the player's group in LevelGroups
        - "[message] &cI'm sorry %player%, but you have group a stronger group than you won:("
      RandomActions: # GiveType: RANDOM
        first:
          Chance: 50
          Actions:
            - '[command] say something'
            - '[broadcast] &a>&c>&e> &c%player% &6won a donate %groupdisplayname% &6from &5Ultra-Case.'
        second:
          Chance: 50
          Actions:
            - '[command] say something'
            - '[broadcast] &a>&c>&e> &c%player% &6won a donate %groupdisplayname% &6from &5Ultra-Case.'
    Ultra:
      Group: ultra
      Chance: 5
      Item:
        ID: RED_WOOL
        DisplayName: '&7[&cUltra&7]'
        Enchanted: true
      Index: 5
      GiveType: ONE
      Actions:
        - '[command] lp user %player% parent set %group%'
        - '[command] say lp user %player% parent set %group%'
        - '[broadcast] &a>&c>&e> &c%player% &6won a donate %groupdisplayname% &6from
        &5Ultra-Case.'
      AlternativeActions: # GiveType: any, it doesn't matter; is performed if the group is lower in rank than the player's group in LevelGroups
        - "[message] &cI'm sorry %player%, but you have group a stronger group than you won:("
      RandomActions: # GiveType: RANDOM
        first:
          Chance: 50
          DisplayName: "something" # displayname for historydata displaying
          Actions:
            - '[command] say something'
            - '[broadcast] &a>&c>&e> &c%player% &6won a donate %groupdisplayname% &6from &5Ultra-Case.'
        second:
          Chance: 50
          Actions:
            - '[command] say something'
            - '[broadcast] &a>&c>&e> &c%player% &6won a donate %groupdisplayname% &6from &5Ultra-Case.'
```
</details>

## Animations.yml
<details>
<summary>Animations.yml</summary>

```yml
config: "1.3"
Shape:
  ItemSlot: HEAD # HEAD, HAND, OFF_HAND, FEET, LEGS, CHEST
  SmallArmorStand: true
  Pose:
    Head: 0, 0, 0
    Body: 0, 0, 0
    RightArm: 0, 0, 0
    LeftArm: 0, 0, 0
    RightLeg: 0, 0, 0
    LeftLeg: 0, 0, 0
  Particle: # 1.13+
    Orange:
      Size: 1.0
      Rgb: 255, 165, 0
    White:
      Size: 1.0
      Rgb: 255, 255, 255

Rainly:
  ItemSlot: HEAD # HEAD, HAND, OFF_HAND, FEET, LEGS, CHEST
  SmallArmorStand: true
  Pose:
    Head: 0, 0, 0
    Body: 0, 0, 0
    RightArm: 0, 0, 0
    LeftArm: 0, 0, 0
    RightLeg: 0, 0, 0
    LeftLeg: 0, 0, 0
  FallingParticle: FALLING_WATER # you can use all particles, full list: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html
  # FALLING_WATER
  # FALLING_LAVA
  # FALLING_DUST
  # FALLING_HONEY   1.16+
  # FALLING_NECTAR  1.16+
  # FALLING_OBSIDIAN_TEAR

Firework:
  ItemSlot: HEAD # HEAD, HAND, OFF_HAND, FEET, LEGS, CHEST
  SmallArmorStand: true
  Pose:
    Head: 0, 0, 0
    Body: 0, 0, 0
    RightArm: 0, 0, 0
    LeftArm: 0, 0, 0
    RightLeg: 0, 0, 0
    LeftLeg: 0, 0, 0
  Power: 0 # Firework power
  FireworkColors:
    - RED
    - BLUE
    - GREEN
    - YELLOW

Wheel:
  ItemSlot: HEAD # HEAD, HAND, OFF_HAND, FEET, LEGS, CHEST
  SmallArmorStand: true
  Pose:
    Head: 0, 0, 0
    Body: 0, 0, 0
    RightArm: 0, 0, 0
    LeftArm: 0, 0, 0
    RightLeg: 0, 0, 0
    LeftLeg: 0, 0, 0
  ItemsCount: 6
  CircleSpeed: 0.5
  CircleRadius: 1.5
  LiftingAlongX: 0
  LiftingAlongY: 0
  LiftingAlongZ: 0
  Flame:
    Enabled: true
    Particle: FLAME
  Scroll:
    Time: 100
    Sound: UI_BUTTON_CLICK
    Volume: 10
    Pitch: 1

FullWheel:
  ItemSlot: HEAD # HEAD, HAND, OFF_HAND, FEET, LEGS, CHEST
  SmallArmorStand: true
  Pose:
    Head: 0, 0, 0
    Body: 0, 0, 0
    RightArm: 0, 0, 0
    LeftArm: 0, 0, 0
    RightLeg: 0, 0, 0
    LeftLeg: 0, 0, 0
  CircleSpeed: 0.5
  CircleRadius: 1.5
  LiftingAlongX: 0
  LiftingAlongY: 0
  LiftingAlongZ: 0
  Flame:
    Enabled: true
    Particle: FLAME
  Scroll:
    Time: 100
    Sound: UI_BUTTON_CLICK
    Volume: 10
    Pitch: 1
```
</details>