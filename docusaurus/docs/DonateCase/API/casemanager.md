---
id: casemanager
title: CaseManager
sidebar_position: 5
---

# CaseManager
[CaseManager](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/2.2.5.9/raw/com/jodexindustries/donatecase/api/CaseManager.html) -
is a class that contains all the managers for interacting with dynamic objects in DonateCase.

## Available managers
- [ActionManager](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/2.2.5.9/raw/com/jodexindustries/donatecase/api/ActionManager.html)
is used to manage executable actions
- [AddonManager](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/2.2.5.9/raw/com/jodexindustries/donatecase/api/AddonManager.html)
is used to manage addons
- [AnimationManager](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/2.2.5.9/raw/com/jodexindustries/donatecase/api/AnimationManager.html)
is used to manage animations
- [GUITypedItemManager](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/2.2.5.9/raw/com/jodexindustries/donatecase/api/GUITypedItemManager.html)
is used to manage GUITyped items
- [MaterialManager](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/2.2.5.9/raw/com/jodexindustries/donatecase/api/MaterialManager.html)
is used to manage materials
- [SubCommandManager](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/2.2.5.9/raw/com/jodexindustries/donatecase/api/SubCommandManager.html)
is used to manage subcommands

## Getting CaseManager

CaseManager has 2 constructors for initialization: [CaseManager#\<init>(Addon)](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/2.2.5.9/raw/com/jodexindustries/donatecase/api/CaseManager.html#%3Cinit%3E(com.jodexindustries.donatecase.api.addon.Addon)) and [CaseManager#\<init>(Plugin)](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/2.2.5.9/raw/com/jodexindustries/donatecase/api/CaseManager.html#%3Cinit%3E(org.bukkit.plugin.Plugin))

### Internal addon
CaseManager is automatically initialized when the addon is loaded, so retrieval is available
by using the method [InternalJavaAddon#getCaseAPI()](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/2.2.5.9/raw/com/jodexindustries/donatecase/api/addon/internal/InternalJavaAddon.html#getCaseAPI())

Example:
```java
public class TestAddon extends InternalJavaAddon {
    @Override
    public void onEnable() {
        CaseManager caseManager = getCaseAPI();

        // or you can use the CaseManager#<init>(addon) constructor.
        CaseManager caseManager = new CaseManager(this);
    }
}
```
### External addon

Example:
```java
public final class TestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // CaseManager#<init>(Plugin) is used in this case
        CaseManager caseManager = new CaseManager(this);
    }
}

```