---
id: register-materials
title: Register materials
sidebar_position: 8
---
# Register materials

Custom materials are processed through the interface [MaterialHandler](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/2.2.5.6/raw/com/jodexindustries/donatecase/api/data/material/MaterialHandler.html)

## Example implementation of `HEAD` material
> HEADMaterialHandlerImpl.java
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
> Main.java
```java
    @Override
    public void onEnable() {
        // getting a CaseManager
        CaseManager api = new CaseManager(this);
        
        // getting a MaterialManager
        MaterialManager manager = api.getMaterialManager();
        
        // material registration
        manager.registerMaterial("HEAD", new HEADMaterialHandlerImpl(),
                "Default Minecraft heads by nickname");
    }
```


### Explanation
The `handle` method should return [ItemStack](https://helpch.at/docs/1.16.5/org/bukkit/inventory/ItemStack.html),
which will be used after calling the `MaterialHandler` handler.

#### Example of a call:

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
