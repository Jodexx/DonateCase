---
id: casemanager
title: CaseManager
sidebar_position: 5
---

# CaseManager
[CaseManager](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/spigot/2.2.6.7/raw/com/jodexindustries/donatecase/api/CaseManager.html) -
клас, який містить усі менеджери для взаємодії з динамічними об'єктами в DonateCase.

## Доступні менеджери
- [ActionManager](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/spigot/2.2.6.7/raw/com/jodexindustries/donatecase/api/ActionManager.html)
використовується для керування виконуваними діями
- [AddonManager](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/spigot/2.2.6.7/raw/com/jodexindustries/donatecase/api/AddonManager.html)
використовується для керування аддонами
- [AnimationManager](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/spigot/2.2.6.7/raw/com/jodexindustries/donatecase/api/AnimationManager.html)
використовується для керування анімаціями
- [GUITypedItemManager](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/spigot/2.2.6.7/raw/com/jodexindustries/donatecase/api/GUITypedItemManager.html)
використовується для керування предметами, заданими в гюї
- [MaterialManager](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/spigot/2.2.6.7/raw/com/jodexindustries/donatecase/api/MaterialManager.html)
використовується для керування матеріалами
- [SubCommandManager](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/spigot/2.2.6.7/raw/com/jodexindustries/donatecase/api/SubCommandManager.html)
використовується для керування субкомандами

## Отримання CaseManager

CaseManager має 2 конструктори для ініціалізації:
[CaseManager#\<init>(Addon)](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/spigot/2.2.6.7/raw/com/jodexindustries/donatecase/api/CaseManager.html#%3Cinit%3E(com.jodexindustries.donatecase.api.addon.Addon)) та [CaseManager#\<init>(Plugin)](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/spigot/2.2.6.7/raw/com/jodexindustries/donatecase/api/CaseManager.html#%3Cinit%3E(org.bukkit.plugin.Plugin))

### Внутрішній аддон
CaseManager автоматично ініціалізується під час завантаження аддона, тому отримання доступне
за допомогою методу [InternalJavaAddon#getCaseAPI()](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/spigot/2.2.6.7/raw/com/jodexindustries/donatecase/api/addon/internal/InternalJavaAddon.html#getCaseAPI())

Приклад:
```java
public class TestAddon extends InternalJavaAddon {
    @Override
    public void onEnable() {
        CaseManager caseManager = getCaseAPI();
        // або можна використати конструктор CaseManager#<init>(Addon)
        CaseManager caseManager = new CaseManager(this);
    }
}
```
### Зовнішній аддон

Приклад:
```java
public final class TestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // у цьому випадку використовується CaseManager#<init>(Plugin)
        CaseManager caseManager = new CaseManager(this);
    }
}

```