---
id: register-events
title: Register Events
sidebar_position: 4
---

# Register events
DonateCase events are registered in the same way as regular Bukkit events
> EventListener class
```java
public class EventListener implements Listener {
    @EventHandler
    public void onCaseInteract(CaseInteractEvent e) {
        Player p = e.getPlayer();
        p.sendMessage(e.getClickedBlock().getLocation().toString());
    }
}
```
> Main class
```java
public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new EventListener(), this);
    }
}
```