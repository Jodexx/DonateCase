---
id: casemanager
title: CaseManager
sidebar_position: 5
---

# CaseManager
[CaseManager](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/2.2.5.9/raw/com/jodexindustries/donatecase/api/CaseManager.html) -
класс, который содержит все менеджеры для взаимодействия с динамичными объектами в DonateCase.

## Доступные менеджеры
- [ActionManager](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/2.2.5.9/raw/com/jodexindustries/donatecase/api/ActionManager.html)
используется для управления исполняемыми действиями
- [AddonManager](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/2.2.5.9/raw/com/jodexindustries/donatecase/api/AddonManager.html)
используется для управления аддонами
- [AnimationManager](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/2.2.5.9/raw/com/jodexindustries/donatecase/api/AnimationManager.html)
используется для управления анимациями
- [GUITypedItemManager](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/2.2.5.9/raw/com/jodexindustries/donatecase/api/GUITypedItemManager.html)
используется для управления предметов, заданными в гюи
- [MaterialManager](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/2.2.5.9/raw/com/jodexindustries/donatecase/api/MaterialManager.html)
используется для управления материалов
- [SubCommandManager](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/2.2.5.9/raw/com/jodexindustries/donatecase/api/SubCommandManager.html)
используется для управления субкомандами

## Получение CaseManager

CaseManager имеет 2 конструктора для инициализации: [CaseManager#\<init>(Addon)](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/2.2.5.9/raw/com/jodexindustries/donatecase/api/CaseManager.html#%3Cinit%3E(com.jodexindustries.donatecase.api.addon.Addon)) и [CaseManager#\<init>(Plugin)](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/2.2.5.9/raw/com/jodexindustries/donatecase/api/CaseManager.html#%3Cinit%3E(org.bukkit.plugin.Plugin))

### Внутренний аддон
CaseManager автоматически инициализируется при загрузке аддона, поэтому получение доступно
с помощью метода [InternalJavaAddon#getCaseAPI()](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/2.2.5.9/raw/com/jodexindustries/donatecase/api/addon/internal/InternalJavaAddon.html#getCaseAPI())

Пример:
```java
public class TestAddon extends InternalJavaAddon {
    @Override
    public void onEnable() {
        CaseManager caseManager = getCaseAPI();
        
        // или можно использовать конструктор CaseManager#<init>(addon)
        CaseManager caseManager = new CaseManager(this);
    }
}
```
### Внешний аддон

Пример:
```java
public final class TestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // в данном случае используется CaseManager#<init>(Plugin)
        CaseManager caseManager = new CaseManager(this);
    }
}

```