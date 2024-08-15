---
id: implementing
title: Впровадження
sidebar_position: 3
---
# Впровадження
## Приклад із зовнішнім плагіном
TestDonateCaseAPI.java
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

## Приклад без зовнішнього плагіну
TestAddon.java
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