---
id: register-events
title: Реєстрація івентів
sidebar_position: 4
---

# Реєстрація івентів
Івенти DonateCase реєструються так само, як і звичайні івенти Bukkit
```java
// EventListener клас
public class EventListener implements Listener {
    @EventHandler
    public void onCaseInteract(CaseInteractEvent e) {
        Player p = e.getPlayer();
        p.sendMessage(e.getClickedBlock().getLocation().toString());
    }
}
```
```java
//Main клас
public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new EventListener(), this);
    }
}
```