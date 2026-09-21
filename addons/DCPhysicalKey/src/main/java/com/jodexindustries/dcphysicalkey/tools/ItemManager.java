package com.jodexindustries.dcphysicalkey.tools;

import com.jodexindustries.dcphysicalkey.bootstrap.MainAddon;
import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseMaterial;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import static com.jodexindustries.dcphysicalkey.bootstrap.MainAddon.NAMESPACED_KEY;

public class ItemManager {

    public static final Map<String, ItemStack> items = new HashMap<>();
    private final MainAddon addon;

    public ItemManager(MainAddon addon) {
        this.addon = addon;
    }

    public void load() {
        items.clear();
        ConfigurationNode node = addon.getConfig().node("keys");
        if (node.virtual()) return;

        Map<Object, ? extends ConfigurationNode> map = node.childrenMap();

        for (Map.Entry<Object, ? extends ConfigurationNode> entry : map.entrySet()) {
            String key = String.valueOf(entry.getKey());

            ConfigurationNode keyNode = entry.getValue();
            if (keyNode.virtual()) return;

            String caseType = keyNode.node("case-type").getString();
            if (caseType == null || caseType.isEmpty()) {
                addon.getLogger().warning("Key " + key + ": Case type is empty. Skipping this key.");
                continue;
            }

            CaseMaterial material;

            try {
                ConfigurationNode idNode = keyNode.node("id");

                // if "id" node is virtual, check "material" node
                if (idNode.virtual()) {
                    idNode.set(keyNode.node("material").raw());
                }

                material = keyNode.get(CaseMaterial.class);
                if (material == null)
                    throw new SerializationException("Item configuration is null");

                if (material.id() == null) {
                    throw new SerializationException("Item id is null");
                }
            } catch (SerializationException e) {
                addon.getLogger().log(Level.WARNING, "Error with deserialization for key: " + key, e);
                continue;
            }

            if (!DCAPI.getInstance().getCaseManager().hasByType(caseType)) {
                addon.getLogger().warning("Key " + key + ": Case type \"" + caseType + "\" not found. Skipping this key.");
                continue;
            }

            ItemStack itemStack = (ItemStack) material.itemStack();
            if (itemStack == null) {
                addon.getLogger().warning("Key " + key + ": Material id \"" + material.id() + "\" not found. Skipping this key.");
                continue;
            }

            ItemMeta meta = itemStack.getItemMeta();
            if (meta == null) {
                continue;
            }

            // very important
            material.updateMeta();

            meta.getPersistentDataContainer().set(NAMESPACED_KEY, PersistentDataType.STRING, caseType);
            itemStack.setItemMeta(meta);

            items.put(key, itemStack);
        }
    }

}
