---
id: items-settings
title: Items settings
toc_min_heading_level: 2
toc_max_heading_level: 5
sidebar_position: 4
---


## Setup gui items
`DisplayName` - Displaying the item name <br />
`Enchanted` - Has 2 data types: `true` or `false`, if `true` - the item will be enchanted <br />
`Lore` - Description of the item <br/>
`ModelData (optional)` - Custom model data

### Example lore
```yaml
Lore:
  - "&cVery cool lore"
  - "&dYeah, its colored"
  - "&bPlaceholders? %keys%"
```

### Placeholders
- `%keys%` - Number of player keys
- `%case%` - Name of the case

`Slots` - A list or range of slots that this item will be in

### Example list
```yaml
Slots:
  - 0
  - 8
  - 9-16 # can be like range
```
`Material` - Item material, all item types are described [here](https://wiki.jodexindustries.space/docs/DonateCase/materials) <br />
`Rgb (optional)` - Ability to change the color of leather items <br />

### Example RGB
```yaml
Rgb: 123, 50, 15
```
`Type` - Type of item, there are 3 types: 
- `DEFAULT` - a normal item for the beauty of GUI
- `OPEN` - an item that opens the case (or you can use OPEN_\<anotherCaseName\> for opening another case)
- `HISTORY` - an item that displays the history of recent case openings <br/>

### Example type for opening another case
```yml
Type: OPEN_donate
# donate - another case name
```

### Setting up an item with type HISTORY
Placeholders:
- `%player%` - Player name
- `%group%` - Group name
- `%groupdisplayname%` - Group display name
- `%time%` - Case opening time
- `%action%` - RandomAction name
- `%actiondisplayname%` - RandomAction display name
- `%casename%` - Case name
- `%casedisplayname%` - Case display name
- `%casetitle` - Case title

Type: HISTORY-[index]-[case]     (_index - index of recent case openings, range 0-9; case - case type optional_)
```yaml
History0:
  DisplayName: "&c%player%"
  Enchanted: false
  Lore:
    - '&6Group &f- &c%group%'
    - '&6Time &f- &c%time%'
    - ''
  Slots:
    - 36
  # Material: TRIPWIRE_HOOK - The material will already be like player_head, if commented, can be DEFAULT, if you want to use win item material
  Type: HISTORY-0-case # 0 is the index of recent case openings, range 0-9; case is the case type, if null, then default case type (optional)
```
You can use `HISTORY-[index]-GLOBAL` option, if you want to display sorted opens of all cases

#### History not found
Advanced customization is also available for this type of item. If the recent discovery index is not yet populated (the case has not been opened), you can set a completely different item instead of history in the ``HistoryNotFound`` section:
```yaml
History0:
  DisplayName: '&c%player%'
  Enchanted: false
  Lore:
  - '&6Group &f- &c%group%'
  - '&6Time &f- &c%time%'
  - ''
  Slots:
  - 36
  # Material: TRIPWIRE_HOOK - Material will already be player_head if commented out, can be DEFAULT if you want to use the winning item's material
  Type: HISTORY-0-case # 0 - index of recent case openings, range 0-9; case - case type, if empty, will be the default case (optional)
  HistoryNotFound: # Section for unfilled indexes
    DisplayName: "&cNot found"
    Material: BARRIER
    #Enchanted: false
    #Lore:
    # - "&cSorry..."
    #ModelData: 1234
    #Rgb: 255,255,255.
```

## Setup win items
`Group` - A group that is given to the player as a prize <br />
`Chance` - The chance at which this prize is awarded <br />
`GiveType` - Award type, if ONE, then the player is given only one prize (Actions), if RANDOM, then a prize with different chances is selected (RandomActions)
### Placeholders
- `%player%` - Player name
- `%group%` - Group name
- `%groupdisplayname%` - Group display name
- `%casename%` - Case name
- `%casedisplayname%` - Case display name
- `%casetitle` - Case title

### Actions
- `[command]` - the console command will be executed
- `[broadcast]` - the message will be sent to all players
- `[message]` - the message will be sent to player who opened the case
- `[title]` - the title will be sent to the player who opened the case (sign `;` separates the title from the subtitle)
```yml
- '[title] (title);(subtitle)'
```
- `[sound]` - the sound will be played for player who opened the case:
```yml
- '[sound] (sound) (volume) (pitch)'
```

#### GiveType: `ONE`
```yaml
      Actions: # GiveType: ONE
        - '[command] lp user %player% parent set %group%'
        - '[title] &aCongratulations!;&5you won %groupdisplayname%'
        - '[broadcast] &a>&c>&e> &c%player% &6won a donate %groupdisplayname% &6from &5Ultra-Case.'
        - '[sound] ENTITY_ENDERMAN_DEATH 2 1'
```

#### GiveType: `RANDOM`
```yaml
      RandomActions: # GiveType: RANDOM
        first:
          Chance: 50 # Chance between random actions
          DisplayName: "something" # displayname for historydata displaying
          Actions:
            - '[command] say something'
            - '[broadcast] &a>&c>&e> &c%player% &6won a donate %groupdisplayname% &6from &5Ultra-Case.'
        second:
          Chance: 50 # Chance between random actions
          Actions:
            - '[command] say something'
            - '[broadcast] &a>&c>&e> &c%player% &6won a donate %groupdisplayname% &6from &5Ultra-Case.'
```

#### Alternative actions
It is performed when the player's group is higher than the one he won (available for both types of GiveType)
```yaml
      AlternativeActions: # GiveType: any, it doesn't matter; is performed if the group is lower in rank than the player's group in LevelGroups
        - "[message] &cI'm sorry %player%, but you have group a stronger group than you won:("
```
#### Actions cooldown
`[cooldown:<delay in seconds)]`
With this option, you will be able to perform actions with a certain delay.
```yaml
      Actions: # This command will be executed 1 second after the case is opened
        - '[cooldown:1][command] lp user %player% parent set %group%'
```