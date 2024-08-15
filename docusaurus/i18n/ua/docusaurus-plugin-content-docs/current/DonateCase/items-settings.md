---
id: items-settings
title: Налаштування предметів
toc_min_heading_level: 2
toc_max_heading_level: 5
sidebar_position: 4
---


## Налаштування предметів в гюї
`DisplayName` - Відображення назви предмета <br />
`Enchanted` - Є 2 типа данних: `true` або  `false`, якщо `true` - предмет буде зачаровано <br />
`Lore` - Опис предмета <br/>
`ModelData (необов'язково)` - Користувацькі данні моделі

### Приклад опису
```yaml
Lore:
  - "&cДуже крутий опис"
  - "&dТак, це різнокольорово"
  - "&bПлейсхолдери? %keys%"
```

### Плейсхолдери
- `%keys%` - Кількість ключів гравця
- `%case%` - Назва кейса

`Slots` - Список чи діапазон слотів, в яких буде знаходитись даний предмет

### Приклад списку
```yaml
Slots:
  - 0
  - 8
  - 9-16 # може бути діапазоном
```
`Material` - Матеріал предмета, всі типи предметів описані [тут](https://wiki.jodexindustries.xyz/docs/DonateCase/item-id) <br />
`Rgb (необов'язково)` - Можливість міняти колір шкіряних предметів <br />

### Приклад RGB
```yaml
Rgb: 123, 50, 15
```
`Type` - Тип предмета, існує 3 типа: 
- `DEFAULT` - звичайний предмет для краси графічного інтерфейсу
- `OPEN` - предмет,який відкриває кейс (або можна використовувати OPEN_\<інша_назва_кейсу\> для відкриття іншого кейсу)
- `HISTORY` - предмет, відображаючий історію недавніх відкриттів кейсів<br/>

### Приклад типу для відкриття іншого кейсу
```yml
Type: OPEN_donate
# donate - інша назва кейсу
```

### Налаштування предметів з типом HISTORY
Плейсхолдери:
- `%player%` - Ім'я гравця
- `%group%` - Ім'я групи
- `%groupdisplayname%` - Відображаєме ім'я групи
- `%time%` - Час відкриття кейсу
- `%action%` - Назва RandomAction
- `%actiondisplayname%` - Відображаєме ім'я RandomAction
- `%casename%` - Назва кейса
- `%casedisplayname%` - Відображаєме ім'я кейсу
- `%casetitle` - Тайтл кейсу (заголовок)

Type: HISTORY-[index]-[case]     (_index - індекс недавніх відкриттів кейсу, діапазон 0-9; case - тип кейсу, необов'язковий_)
```yaml
History0:
  DisplayName: "&c%player%"
  Enchanted: false
  Lore:
    - '&6Група &f- &c%group%'
    - '&6Час &f- &c%time%'
    - ''
  Slots:
    - 36
  # Material: TRIPWIRE_HOOK - Матеріал вже буде player_head, може бути DEFAULT, якщо ви хочете використовувати матеріал виграшного предмету
  Type: HISTORY-0-case # 0 – індекс недавніх відкриттів кейсів, діапазон 0-9; case - тип кейсу, якщо пусто, то буде кейс за замовчанням (необов'язково)
```
Ви можете використовувати `HISTORY-[index]-GLOBAL` опцію, якщо ви хочете відобразити відсортовані відкриття всіх кейсів

#### Історію не знайдено
Також для цього типу предмета доступне розширене налаштування. Якщо індекс недавніх відкриттів ще не заповнений (кейс не було відкрито), то можна встановити зовсім інший предмет замість історії в секції `HistoryNotFound`:
```yaml
History0:
  DisplayName: "&c%player%"
  Enchanted: false
  Lore:
    - '&6Группа &f- &c%group%'
    - '&6Час &f- &c%time%'
    - ''
  Slots:
    - 36
  # Material: TRIPWIRE_HOOK - Матеріал уже буде player_head, якщо він закоментований, може бути DEFAULT, якщо ви хочете використовувати матеріал виграшного предмета
  Type: HISTORY-0-case # 0 - індекс недавніх відкриттів кейсів, діапазон 0-9; case - тип кейса, якщо порожньо, то буде кейс за замовчуванням (опціонально)
  HistoryNotFound: # Секція для незаповнених індексів
    DisplayName: "&cNot found"
    Матеріал: BARRIER
    #Enchanted: false
    #Lore:
    # - "&cSorry..."
    #ModelData: 1234
    #Rgb: 255,255,255
```

## Налаштування призових предметів
`Group` - Група, яка дається гравцю в якості призу<br />
`Chance` - Шанс, при якому видається цей приз<br />
`GiveType` - Тип нагороди, якщо ONE, то гравцю дається лише один приз (Actions), якщо RANDOM, то вибирається приз з різними шансами(RandomActions)
### Плейсхолдери
- `%player%` - Ім'я гравця
- `%group%` - Назва групи
- `%groupdisplayname%` - Відображаєме ім'я групи
- `%casename%` - Назва кейсу
- `%casedisplayname%` - Відображаєме ім'я кейсу
- `%casetitle` - Тайтл кейсу (заголовок)

### Дії
- `[command]` - консольна команда
- `[broadcast]` - повідомлення для всіх гравців
- `[message]` - повідомлення гравця, відкрившого кейс
- `[title]` - тайтл для гравця, відкрившого кейс (знак `;` розділяє тайтл і субтайтл)
```yml
- '[title] (title);(subtitle)'
```
- `[sound]` - звук буде програно для гравця, відкрившого кейс
```yml
- '[sound] (sound) (volume) (pitch)'
```

#### GiveType: `ONE`
```yaml
      Actions: # GiveType: ONE
        - '[command] lp user %player% parent set %group%'
        - '[title] &aВітаємо!;&5ви виграли %groupdisplayname%'
        - '[broadcast] &a>&c>&e> &c%player% &6виграв групу %groupdisplayname% &6з &5Ultra-Case.'
        - '[sound] ENTITY_ENDERMAN_DEATH 2 1'
```

#### GiveType: `RANDOM`
```yaml
      RandomActions: # GiveType: RANDOM
        first:
          Chance: 50 # Шанс між випадковими діями
          DisplayName: "something" # відображаєме ім'я для історії відкриття
          Actions:
            - '[command] say something'
            - '[broadcast] &a>&c>&e> &c%player% &6виграв групу %groupdisplayname% &6з &5Ultra-Case.'
        second:
          Chance: 50 # Шанс між випадковими діями
          Actions:
            - '[command] say something'
            - '[broadcast] &a>&c>&e> &c%player% &6виграв групу %groupdisplayname% &6з &5Ultra-Case.'
```

#### Альтернативні дії
Виконується, коли група гравця вища тієї, яку він виграв (доступно для обох типів GiveType)
```yaml
      AlternativeActions: # GiveType: будь-який, це не має значення; виконується, якщо група нижче за рангом, ніж група гравця в LevelGroups
        - "[message] &cВибачте %player%, але у вас є група вище, ніж ви виграли:("
```
#### Затримка дій
`[cooldown:<затримка в секундах>]`
За допомогою цієї опції, ви зможете виконувати дії з певною затримкою
```yaml
      Actions: # Ця команда буде виконана за 1 секунду після відкриття кейсу
        - '[cooldown:1][command] lp user %player% parent set %group%'
```