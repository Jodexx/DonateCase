package com.jodexindustries.donatecase.impl;

import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.api.DCAPIBukkit;
import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.addon.external.ExternalJavaAddon;
import com.jodexindustries.donatecase.api.data.animation.JavaAnimationBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterialBukkit;
import com.jodexindustries.donatecase.api.database.CaseDatabase;
import com.jodexindustries.donatecase.api.events.CaseGuiClickEvent;
import com.jodexindustries.donatecase.api.gui.CaseGui;
import com.jodexindustries.donatecase.api.manager.*;
import com.jodexindustries.donatecase.impl.managers.*;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class DCAPIBukkitImpl extends DCAPIBukkit {

    private final Addon addon;

    public DCAPIBukkitImpl(Addon addon) {
        this.addon = addon;
    }

    public DCAPIBukkitImpl(Plugin plugin) {
        this.addon = new ExternalJavaAddon(plugin);
    }

    @Override
    public ActionManager<Player> getActionManager() {
        return new ActionManagerImpl(addon);
    }

    @Override
    public AddonManager getAddonManager() {
        return new AddonManagerImpl(addon);
    }

    @Override
    public AnimationManager<JavaAnimationBukkit, CaseDataMaterialBukkit, ItemStack, Player, Location, CaseDataBukkit> getAnimationManager() {
        return new AnimationManagerImpl(addon);
    }

    @Override
    public CaseKeyManager getCaseKeyManager() {
        return new CaseKeyManagerImpl();
    }

    @Override
    public CaseOpenManager getCaseOpenManager() {
        return new CaseOpenManagerImpl();
    }

    @Override
    public GUITypedItemManager<CaseDataMaterialBukkit, CaseGui, CaseGuiClickEvent> getGuiTypedItemManager() {
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
}
