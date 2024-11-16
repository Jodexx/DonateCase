package com.jodexindustries.dcphysicalkey.tools;

import com.jodexindustries.dcphysicalkey.bootstrap.Bootstrap;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jodexindustries.donatecase.tools.Tools.rc;

public class ItemManager {

    private final Bootstrap bootstrap;

    public static final Map<String, ItemStack> items = new HashMap<>();

    public ItemManager(Bootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    public void load() {
        items.clear();
        ConfigurationSection section = bootstrap.getConfig().get().getConfigurationSection("keys");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            ConfigurationSection keySection = section.getConfigurationSection(key);
            if (keySection == null) return;

            Material material = Material.getMaterial(keySection.getString("material", "STONE"));
            if (material == null) {
                bootstrap.getPlugin().getLogger().warning("Key " + key + ": Material not found. Skipping this key.");
                continue;
            }

            String caseType = keySection.getString("case-type", "");
            if (caseType.isEmpty()) {
                bootstrap.getPlugin().getLogger().warning("Key " + key + ": Case type is empty. Skipping this key.");
                continue;
            }

            if (!bootstrap.getPlugin().getDCAPI().getCaseManager().hasCaseByType(caseType)) {
                bootstrap.getPlugin().getLogger().warning("Key " + key + ": Case type \"" + caseType + "\" not found. Skipping this key.");
                continue;
            }

            String displayName = keySection.getString("display-name", "");
            List<String> lore = keySection.getStringList("lore");

            ItemStack itemStack = new ItemStack(material);
            ItemMeta meta = itemStack.getItemMeta();
            if (meta == null) {
                bootstrap.getPlugin().getLogger().warning("Key " + key + ": Item meta is null. Skipping this key.");
                continue;
            }

            meta.setDisplayName(rc(displayName));
            meta.setLore(rc(lore));
            meta.getPersistentDataContainer().set(Bootstrap.NAMESPACED_KEY, PersistentDataType.STRING, caseType);
            itemStack.setItemMeta(meta);

            items.put(key, itemStack);
        }
    }

}
