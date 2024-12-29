package com.jodexindustries.donatecase.impl;

import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.api.DCAPIBukkit;
import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.config.ConfigBukkit;
import com.jodexindustries.donatecase.api.data.animation.JavaAnimationBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterialBukkit;
import com.jodexindustries.donatecase.api.database.CaseDatabase;
import com.jodexindustries.donatecase.api.events.CaseGuiClickEvent;
import com.jodexindustries.donatecase.api.gui.CaseGui;
import com.jodexindustries.donatecase.api.manager.*;
import com.jodexindustries.donatecase.impl.managers.*;
import com.jodexindustries.donatecase.tools.DCToolsBukkit;
import com.jodexindustries.donatecase.tools.ToolsImpl;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class DCAPIBukkitImpl extends DCAPIBukkit {

    public DCAPIBukkitImpl(Addon addon) {
        super(addon);
    }

    public DCAPIBukkitImpl(Plugin plugin) {
        super(plugin);
    }

    @Override
    public ActionManager<Player> getActionManager() {
        return new ActionManagerImpl(addon);
    }

    @Override
    public AddonManager getAddonManager() {
        return new AddonManagerImpl(this);
    }

    @Override
    public AnimationManager<JavaAnimationBukkit, CaseDataMaterialBukkit, Player, Location, Block, CaseDataBukkit> getAnimationManager() {
        return new AnimationManagerImpl(this);
    }

    @Override
    public CaseKeyManager getCaseKeyManager() {
        return new CaseKeyManagerImpl();
    }

    @Override
    public CaseManager<CaseDataBukkit> getCaseManager() {
        return new CaseManagerImpl();
    }

    @Override
    public CaseOpenManager getCaseOpenManager() {
        return new CaseOpenManagerImpl();
    }

    @Override
    public GUIManager<Inventory, Location, Player, CaseDataBukkit, CaseDataMaterialBukkit> getGUIManager() {
        return new GUIManagerImpl(addon);
    }

    @Override
    public GUITypedItemManager<CaseDataMaterialBukkit, CaseGui<Inventory, Location, Player, CaseDataBukkit, CaseDataMaterialBukkit>, CaseGuiClickEvent> getGuiTypedItemManager() {
        return new GUITypedItemManagerImpl(addon);
    }

    @Override
    public MaterialManager<ItemStack> getMaterialManager() {
        return new MaterialManagerImpl(addon);
    }

    @Override
    public SubCommandManager<CommandSender> getSubCommandManager() {
        return new SubCommandManagerImpl(addon);
    }

    @Override
    public CaseDatabase getDatabase() {
        return DonateCase.instance.database;
    }

    @Override
    public ConfigBukkit getConfig() {
        return DonateCase.instance.config;
    }

    @Override
    public DCToolsBukkit getTools() {
        return new ToolsImpl(DonateCase.instance);
    }

    @Override
    public @NotNull Plugin getDonateCase() {
        return DonateCase.instance;
    }

    @Override
    public @NotNull Addon getAddon() {
        return addon;
    }
}
