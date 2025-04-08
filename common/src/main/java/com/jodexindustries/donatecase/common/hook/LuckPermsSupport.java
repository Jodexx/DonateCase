package com.jodexindustries.donatecase.common.hook;

import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Getter
public class LuckPermsSupport {

    private LuckPerms provider;

    public void load() {
        this.provider = LuckPermsProvider.get();
    }

    @Nullable
    public String getPrimaryGroup(UUID uuid) {
        if (provider != null) {
            User user = provider.getUserManager().getUser(uuid);
            if (user != null) return user.getPrimaryGroup();
        }
        return null;
    }
}
