---
id: implementing
title: Внедрение
sidebar_position: 3
---
# Внедрение
## Пример с внешним плагином
> TestDonateCaseAPI класс
```java
import com.jodexindustries.donatecase.api.Case;
import org.bukkit.plugin.java.JavaPlugin;

public final class TestDonateCaseAPI extends JavaPlugin {

    @Override
    public void onEnable() {
      int playerKeys = Case.getKeys("case", "_Jodex__");
      getLogger().info(String.valueOf(playerKeys));
    
    }
}

```

plugin.yml
```yaml
depend:
  - DonateCase
```

## Пример без внешнего плагина
> TestAddon класс
```java
import com.jodexindustries.donatecase.api.addon.internal.InternalJavaAddon;

public class TestAddon extends InternalJavaAddon {
    @Override
    public void onEnable() {
        getLogger().info("TestAddon enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("TestAddon disabled!");
    }
}
```

addon.yml
```yaml
name: TestAddon
main: com.jodexindustries.testaddon.Main
version: 1.0
```