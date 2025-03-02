package com.jodexindustries.donatecase.common.hook;

import lombok.Getter;
import lombok.Setter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Setter
@Getter
public class LuckPermsSupport {

    private LuckPerms provider;

    @Nullable
    public String getPrimaryGroup(UUID uuid) {
        User user = provider.getUserManager().getUser(uuid);
        if(user != null) return user.getPrimaryGroup();
        return null;
    }

}
