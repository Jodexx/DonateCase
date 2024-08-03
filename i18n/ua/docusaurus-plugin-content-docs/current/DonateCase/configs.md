---
id: configurations
title: Конфігурації
sidebar_position: 3
---

~~**Спробуйте наш новий генератор кейсів для DonateCase! -> [LINK](https://jodexindustries.xyz/donatecasegenerator/)**~~

```yaml
├──
├── cases
│   └── case.yml # ваша конфігурація кейсу
├── lang # мови
│   ├── ru_RU.yml 
│   ├── en_US.yml
│   └── ua_UA.yml
├── Cases.yml # сховище кейсів (створених)
├── Config.yml # головний конфігураційний
├── Data.yml # сховище історії відкриттів кейсів
├── Keys.yml # сховище ключів
├── Animations.yml # конфігурація анімацій
└──
```

## Config.yml
<details>
<summary>Config.yml</summary>

```yml
config: '2.5' #DON'T CHANGE THIS PLEASE

DonatCase:
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
  Languages: en #ru/en/ua
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
  NoKeyWarningSound: ENTITY_ENDERMAN_TELEPORT
  DateFormat: "dd.MM HH:mm:ss"
  AddonsHelp: true # Show help list for addons commands?
  UsePackets: false # 1.18+ for using packets, you need to install packetevents plugin
  # Caching for getting number of keys and case opens
  # Actually used in placeholders (GUI and PlaceholderAPI)
  Caching: 20 # in ticks
```
</details>

## case.yml
<details>
<summary>case.yml</summary>

```yml
config: 1.0
case:
  Animation: SHAPE #SHAPE, FIREWORK, RAINLY, WHEEL, FULLWHEEL see: https://wiki.jodexindustries.xyz/docs/DonateCase/animations
  AnimationSound: ENTITY_EXPERIENCE_ORB_PICKUP # remove this line if you don't want to play sound
  Sound:
    Volume: 10
    Pitch: 5
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

  LevelGroups: # delete all of this section, if you want to use default LevelGroups from Config.yml
    legend: 4
    deluxe: 3
    vipplus: 2
    vip: 1
    default: 0

  Title: "&c&lDonate-&a&lCase" # Title in GUI
  DisplayName: "&c&lDonate-&a&lCase" # Name for placeholders
  Gui: # see more about items configuring -> https://wiki.jodexindustries.xyz/docs/DonateCase/items-settings
    Size: 45
    Items:
      "1":
        DisplayName: "&cJodexIndustries.xyz"
        Enchanted: false
        Lore:
          - ""
        Slots: # or Slots: 0-10 (range)
          - 0
          - 8
        Material: WHITE_STAINED_GLASS_PANE # see: https://wiki.jodexindustries.xyz/docs/DonateCase/item-id
        Type: DEFAULT
      Open:
        DisplayName: "&bOpen Case"
        Enchanted: false
        Lore:
          - '&6The case can be bought on the website: &cJodexIndustries.xyz'
          - ''
          - '&7Keys: %keys%'
          - ''
          - '&6Drops:'
          - '&7&l[&aVIP&7&l] - &b&n50%'
          - '&7&l[&bVIP&6+&7&l] - &b&n25%'
          - '&7&l[&5Deluxe&7&l] - &b&n15%'
          - '&7&l[&dLegend&7&l] - &b&n10%'
          - ''
        Slots: # or Slots: 0-10 (range)
          - 22
        Material: TRIPWIRE_HOOK # see: https://wiki.jodexindustries.xyz/docs/DonateCase/item-id
        Type: OPEN # or you can use OPEN_<anotherCaseName> for opening another case 
  Items:
    VIP:
      Group: vip
      Chance: 50
      Item:
        ID: IRON_SWORD # see: https://wiki.jodexindustries.xyz/docs/DonateCase/item-id
        DisplayName: '&7&l[&aVIP&7&l]'
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
    VIPPLUS:
      Group: vipplus
      Chance: 25
      Item:
        ID: GOLDEN_SWORD # see: https://wiki.jodexindustries.xyz/docs/DonateCase/item-id
        DisplayName: '&7&l[&bVIP&6+&7&l]'
        Enchanted: false
      Index: 1
      GiveType: ONE # or RANDOM
      Actions: # GiveType: ONE
        - '[command] lp user %player% parent set %group%'
        - '[title] &aCongratulations!;&5you won %groupdisplayname%'
        - '[broadcast] &a>&c>&e> &c%player% &6won a donate %groupdisplayname% &6from &5Ultra-Case.'
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
        ID: DIAMOND_SWORD # see: https://wiki.jodexindustries.xyz/docs/DonateCase/item-id
        DisplayName: '&7&l[&5Deluxe&7&l]'
        Enchanted: false
      Index: 2
      GiveType: ONE # or RANDOM
      Actions: # GiveType: ONE
        - '[command] lp user %player% parent set %group%'
        - '[title] &aCongratulations!;&5you won %groupdisplayname%'
        - '[broadcast] &a>&c>&e> &c%player% &6won a donate %groupdisplayname% &6from &5Ultra-Case.'
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
        ID: NETHERITE_SWORD # see: https://wiki.jodexindustries.xyz/docs/DonateCase/item-id
        DisplayName: '&7&l[&dLegend&7&l]'
        Enchanted: false
      Index: 3
      GiveType: ONE # or RANDOM
      Actions: # GiveType: ONE
        - '[command] lp user %player% parent set %group%'
        - '[title] &aCongratulations!;&5you won %groupdisplayname%'
        - '[broadcast] &a>&c>&e> &c%player% &6won a donate %groupdisplayname% &6from &5Ultra-Case.'
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