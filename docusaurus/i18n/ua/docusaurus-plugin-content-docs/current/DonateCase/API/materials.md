---
id: register-materials
title: Реєстрація матеріалів
sidebar_position: 8
---
# Реєстрація матеріалів

Користувацькі матеріали оброблюються за допомогою інтерфейсу [MaterialHandler](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/spigot/2.2.6.6/raw/com/jodexindustries/donatecase/api/data/material/MaterialHandler.html)

## Приклад реалізації `HEAD` матеріалу
> HEADMaterialHandlerImpl клас
```java
import com.jodexindustries.donatecase.api.data.material.MaterialHandler;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class HEADMaterialHandlerImpl implements MaterialHandler {

    @Override
    public @NotNull ItemStack handle(@NotNull String context) {

        Material type = Material.getMaterial("SKULL_ITEM");
        ItemStack item;
        if (type == null) {
            item = new ItemStack(Objects.requireNonNull(Material.getMaterial("PLAYER_HEAD")));
        } else {
            item = new ItemStack(Objects.requireNonNull(Material.getMaterial("SKULL_ITEM")), 1, (short) 3);
        }
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta != null) {
            meta.setOwner(context);
            item.setItemMeta(meta);
        }
        return item;
    }
}
```
> Main клас
```java
    @Override
    public void onEnable() {
        // отримання CaseManager
        CaseManager api = new CaseManager(this);
        
        // отримання MaterialManager
        MaterialManager manager = api.getMaterialManager();
        
        // реєстрація матеріалу
        manager.registerMaterial("HEAD", new HEADMaterialHandlerImpl(),
                "Default Minecraft heads by nickname");
    }
```


### Пояснення
Метод `handle` повинен повертати [ItemStack](https://helpch.at/docs/1.16.5/org/bukkit/inventory/ItemStack.html),
як буде використовуватися після виклику обробника `MaterialHandler`.

#### Приклад виклику:

```java
public static ItemStack loadCaseItem(String id) {
    ItemStack itemStack = null;
    String temp = id != null ? MaterialManager.getByStart(id) : null;

    if (temp != null) {
        CaseMaterial caseMaterial = MaterialManager.getRegisteredMaterial(temp);
        if (caseMaterial != null) {
            String context = id.replace(temp, "").replaceFirst(":", "").trim();
            itemStack = caseMaterial.handle(context);
        }
    }

    return itemStack;
}
```
