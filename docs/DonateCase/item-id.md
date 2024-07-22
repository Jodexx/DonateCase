---
id: item-id
title: Items ID
sidebar_position: 6
---

# DonateCase Itemd ID

## Minecraft items
DonateCase support several options for minecraft items
### Material names
```yml
Item:
  ID: GOLDEN_SWORD
```
### Material ids (1.12.2 and below)
```yml
Item:
  ID: WOOL:14 // (RED_WOOL in 1.13+)
```
## CustomHeads
DonateCase supports several options for custom heads
### Player head
```yml
Item:
  ID: HEAD:_Jodex__ # HEAD:<Player name>
```
### [HeadDataBase](https://www.spigotmc.org/resources/head-database.14280/)
```yml
Item:
  ID: HDB:1234 # HDB:<ID>
```
### [CustomHeads](https://www.spigotmc.org/resources/custom-heads-1-8-1-19-2.29057/)
```yml
Item:
  ID: CH:Food:131 # CH:<category>:<id>
```

### [ItemsAdder](https://www.spigotmc.org/resources/%E2%9C%A8itemsadder%E2%AD%90emotes-mobs-items-armors-hud-gui-emojis-blocks-wings-hats-liquids.73355/)
```yml
Item:
  ID: IA:realcraft:waterskin # IA:<namespace>:<id>
```
### Base64 [Minecraft-heads](https://minecraft-heads.com/)
```yml
Item:
  ID: BASE64:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDY3YzVlOGMzYTIwOGRhN2Y3ODBiMzQwY2VmMjI2NDJkNTVlMDA0NzJkMzY5M2IzNDg2ZDcxNDVkNDk5NzBiYiJ9fX0= #BASE64:<value>
```
### MCURL [Minecraft-heads](https://minecraft-heads.com/)
```yml
Item:
  ID: MCURL:3ba311761e3234810bb2b451f6bd0b506f8cb48e1195bef784eb7e2c6095d277 #MCURL:<texture-id> (Minecraft-URL)
```
![Example MCURL](../assets/base64.png)
