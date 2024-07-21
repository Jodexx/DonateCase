---
id: register-animations
title: Регистрация анимаций
sidebar_position: 5
---
# Регистрация анимаций


Для создания класса анимации мы будем использовать [Animation](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/latest/.cache/unpack/com/jodexindustries/donatecase/api/data/Animation.html) интерфейс

TestAnimation класс

##### Обратите внимание на методы: [Case#animationPreEnd](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/2.2.2.4/raw/com/jodexindustries/donatecase/api/Case.html#animationPreEnd(com.jodexindustries.donatecase.api.data.CaseData,org.bukkit.entity.Player,boolean,com.jodexindustries.donatecase.api.data.CaseData.Item)) и [Case#animationEnd](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/2.2.2.4/raw/com/jodexindustries/donatecase/api/Case.html#animationEnd(com.jodexindustries.donatecase.api.data.CaseData,org.bukkit.entity.Player,java.util.UUID,com.jodexindustries.donatecase.api.data.CaseData.Item))
- [Case#animationPreEnd](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/2.2.2.4/raw/com/jodexindustries/donatecase/api/Case.html#animationPreEnd(com.jodexindustries.donatecase.api.data.CaseData,org.bukkit.entity.Player,boolean,com.jodexindustries.donatecase.api.data.CaseData.Item)) вызывается для предоставления группы, отправки сообщения и т.д.
- [Case#animationEnd](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/2.2.2.4/raw/com/jodexindustries/donatecase/api/Case.html#animationEnd(com.jodexindustries.donatecase.api.data.CaseData,org.bukkit.entity.Player,java.util.UUID,com.jodexindustries.donatecase.api.data.CaseData.Item)) вызывается для полного завершения анимации.
```java
import com.jodexindustries.donatecase.api.Animation;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.tools.Tools;
import com.jodexindustries.donatecase.tools.support.PAPISupport;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TestAnimation implements Animation {

    @Override
    public void start(Player player, Location location, UUID uuid, CaseData caseData, CaseData.Item item) {
        Case.animationPreEnd(caseData, player, true, item);
        Bukkit.getScheduler().runTaskLater(Case.getInstance(), () -> Case.animationEnd(caseData, player, uuid, item),20L);
    }
}

```

Main class
```java
    @Override
    public void onEnable() {
        // получение CaseManager
        CaseManager api = new CaseManager(this);
        // регистрация анимации
        AnimationManager animationManager = api.getAnimationManager();
        animationManager.registerAnimation("test", new TestAnimation());
    }
```