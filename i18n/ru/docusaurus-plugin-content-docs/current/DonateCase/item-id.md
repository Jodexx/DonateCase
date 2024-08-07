---
id: item-id
title: ID Предметов
sidebar_position: 6
---

# DonateCase Items ID

## Minecraft items
DonateCase поддерживает несколько ID предметов от Minecraft
### Название материалов
```yml
Item:
  ID: GOLDEN_SWORD
```
### ID материалов (1.12.2 и ниже)
```yml
Item:
  ID: WOOL:14 // (RED_WOOL в 1.13+)
```
## CustomHeads
DonateCase поддерживает несколько ID предметов для голов
### Голова игрока
```yml
Item:
  ID: HEAD:_Jodex__ # HEAD:<Имя игрока>
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
![Пример MCURL](../assets/base64.png)

### [Oraxen](https://www.spigotmc.org/resources/%E2%98%84%EF%B8%8F-oraxen-custom-items-blocks-emotes-furniture-resourcepack-and-gui-1-18-1-21.72448/)
```yml
Item:
  ID: ORAXEN:test # ORAXEN:<id>
```