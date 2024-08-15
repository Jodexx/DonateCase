---
id: register-events
title: Регистрация ивентов
sidebar_position: 4
---

# Регистрация ивентов
Ивенты DonateCase регистрируются так же, как и обычные ивенты Bukkit
```java
// EventListener класс
public class EventListener implements Listener {
    @EventHandler
    public void onCaseInteract(CaseInteractEvent e) {
        Player p = e.getPlayer();
        p.sendMessage(e.getClickedBlock().getLocation().toString());
    }
}
```
```java
//Main класс
public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new EventListener(), this);
    }
}
```