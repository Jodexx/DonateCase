---
id: items-settings
title: Настройка предметов
toc_min_heading_level: 2
toc_max_heading_level: 5
sidebar_position: 4
---


## Настройка предметов в гюи
`DisplayName` - Отображение названия предмета <br />
`Enchanted` - Есть 2 типа данных: `true` или  `false`, если `true` - предмет будет зачарован <br />
`Lore` - Описание предмета <br/>
`ModelData (необязательно)` - Пользовательские данные модели

### Пример описания
```yaml
Lore:
  - "&cОчень крутое описание"
  - "&dДа, это разноцветно"
  - "&bПлейсхолдеры? %keys%"
```

### Плейсхолдеры
- `%keys%` - Количество ключей игрока
- `%case%` - Название кейса

`Slots` - Список или диапазон слотов, в которых будет находиться данный предмет

### Пример списка
```yaml
Slots:
  - 0
  - 8
  - 9-16 # может быть диапазоном
```
`Material` - Материал предмета, все типы предметов описаны [здесь](https://wiki.jodexindustries.space/docs/DonateCase/materials) <br />
`Rgb (необязательно)` - Возможность менять цвет кожаных предметов <br />

### Пример RGB
```yaml
Rgb: 123, 50, 15
```
`Type` - Тип предмета, существует 3 типа: 
- `DEFAULT` - обычный предмет для красоты графического интерфейса
- `OPEN` - предмет, открывающий кейс (или можно использовать OPEN_\<другое_название_кейса\> для открытия другого кейса)
- `HISTORY` - предмет, отображающий историю недавних открытий кейсов <br/>

### Пример типа для открытия другого кейса
```yml
Type: OPEN_donate
# donate - другое название кейса
```

### Настройка предметов с типом HISTORY
Плейсхолдеры:
- `%player%` - Имя игрока
- `%group%` - Имя групы
- `%groupdisplayname%` - Отображаемое имя групы
- `%time%` - Время отрытия кейса
- `%action%` - Название RandomAction
- `%actiondisplayname%` - Отображаемое имя RandomAction
- `%casename%` - Название кейса
- `%casedisplayname%` - Отображаемое имя кейса
- `%casetitle` - Тайтл кейса (заголовок)

Type: HISTORY-[index]-[case]     (_index - индекс недавних открытий кейсов, диапазон 0-9; case - тип кейса, необязательный_)
```yaml
History0:
  DisplayName: "&c%player%"
  Enchanted: false
  Lore:
    - '&6Группа &f- &c%group%'
    - '&6Время &f- &c%time%'
    - ''
  Slots:
    - 36
  # Material: TRIPWIRE_HOOK - Материал уже будет player_head, если он закомментирован, может быть DEFAULT, если вы хотите использовать материал выиграшного предмета
  Type: HISTORY-0-case # 0 – индекс недавних открытий кейсов, диапазон 0-9; case - тип кейса, если пусто, то будет кейс по умолчанию (опционально)
```
Вы можете использовать `HISTORY-[index]-GLOBAL` опцию, если вы хотите отобразить отсортированные открытия всех кейсов

#### Историю не найдено
Также для данного типа предмета доступна расширенная настройка. Если индекс недавних открытий ещё не заполнен (кейс не был открыт), то можно установить совершенно другой предмет вместо истории в секции `HistoryNotFound`:
```yaml
History0:
  DisplayName: "&c%player%"
  Enchanted: false
  Lore:
    - '&6Группа &f- &c%group%'
    - '&6Время &f- &c%time%'
    - ''
  Slots:
    - 36
  # Material: TRIPWIRE_HOOK - Материал уже будет player_head, если он закомментирован, может быть DEFAULT, если вы хотите использовать материал выиграшного предмета
  Type: HISTORY-0-case # 0 – индекс недавних открытий кейсов, диапазон 0-9; case - тип кейса, если пусто, то будет кейс по умолчанию (опционально)
  HistoryNotFound: # Секция для незаполненных индексов
    DisplayName: "&cNot found"
    Material: BARRIER
    #Enchanted: false
    #Lore:
    #  - "&cSorry..."
    #ModelData: 1234
    #Rgb: 255,255,255
```

## Настройка призовых предметов
`Group` - Группа, которая дается игроку в качестве приза <br />
`Chance` - Шанс, при котором присуждается этот приз <br />
`GiveType` - Тип награды, если ONE, то игроку дается только один приз (Actions), если RANDOM, то выбирается приз с разными шансами (RandomActions)
### Плейсхолдеры
- `%player%` - Имя игрока
- `%group%` - Название групы
- `%groupdisplayname%` - Отображаемое имя групы
- `%casename%` - Название кейса
- `%casedisplayname%` - Отображаемое имя кейса
- `%casetitle` - Тайтл кейса (заголовок)

### Действия
- `[command]` - консольная команда
- `[broadcast]` - сообщение для всех игроков
- `[message]` - сообщение для игрока, открывшего кейс
- `[title]` - тайтл для игрока, открывшего кейс (знак `;` разделяет тайтл и субтайтл)
```yml
- '[title] (title);(subtitle)'
```
- `[sound]` - звук будет воспроизведён для игрока, открывшего кейс
```yml
- '[sound] (sound) (volume) (pitch)'
```

#### GiveType: `ONE`
```yaml
      Actions: # GiveType: ONE
        - '[command] lp user %player% parent set %group%'
        - '[title] &aПоздравляем!;&5вы выиграли %groupdisplayname%'
        - '[broadcast] &a>&c>&e> &c%player% &6выиграл группу %groupdisplayname% &6из &5Ultra-Case.'
        - '[sound] ENTITY_ENDERMAN_DEATH 2 1'
```

#### GiveType: `RANDOM`
```yaml
      RandomActions: # GiveType: RANDOM
        first:
          Chance: 50 # Шанс между случайными действиями
          DisplayName: "something" # отображаемое имя для истории открытий кейсов
          Actions:
            - '[command] say something'
            - '[broadcast] &a>&c>&e> &c%player% &выиграл группу %groupdisplayname% &6из &5Ultra-Case.'
        second:
          Chance: 50 # Шанс между случайными действиями
          Actions:
            - '[command] say something'
            - '[broadcast] &a>&c>&e> &c%player% &6выиграл группу %groupdisplayname% &6из &5Ultra-Case.'
```

#### Альтернативные действия
Выполняется, когда группа игрока выше той, которую он выиграл (доступно для обоих типов GiveType)
```yaml
      AlternativeActions: # GiveType: любой, это не имеет значения; выполняется, если группа ниже по рангу, чем группа игрока в LevelGroups
        - "[message] &cИзвините %player%, но у вас есть группа выше, чем вы выиграли:("
```
#### Задержка действий
`[cooldown:<задержка в секундах>]`
С помощью этой опции вы сможете выполнять действия с определенной задержкой.
```yaml
      Actions: # Эта команда будет выполнена через 1 секунду после открытия кейса
        - '[cooldown:1][command] lp user %player% parent set %group%'
```