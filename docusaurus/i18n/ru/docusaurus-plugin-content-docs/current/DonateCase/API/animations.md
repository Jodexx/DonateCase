---
id: register-animations
title: Регистрация анимаций
sidebar_position: 6
---
# Регистрация анимаций


Для создания класса анимации мы будем использовать [JavaAnimation](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/latest/.cache/unpack/com/jodexindustries/donatecase/api/data/JavaAnimation.html) абстрактный класс

TestAnimation класс

##### Обратите внимание на методы: [Case#animationPreEnd](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/2.2.4.2/raw/com/jodexindustries/donatecase/api/Case.html#animationPreEnd(com.jodexindustries.donatecase.api.data.CaseData,org.bukkit.entity.Player,boolean,com.jodexindustries.donatecase.api.data.CaseData.Item)) и [Case#animationEnd](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/2.2.4.2/raw/com/jodexindustries/donatecase/api/Case.html#animationEnd(com.jodexindustries.donatecase.api.data.CaseData,org.bukkit.entity.Player,java.util.UUID,com.jodexindustries.donatecase.api.data.CaseData.Item))
- [Case#animationPreEnd](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/2.2.4.2/raw/com/jodexindustries/donatecase/api/Case.html#animationPreEnd(com.jodexindustries.donatecase.api.data.CaseData,org.bukkit.entity.Player,boolean,com.jodexindustries.donatecase.api.data.CaseData.Item)) вызывается для предоставления группы, отправки сообщения и т.д.
- [Case#animationEnd](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/2.2.4.2/raw/com/jodexindustries/donatecase/api/Case.html#animationEnd(com.jodexindustries.donatecase.api.data.CaseData,org.bukkit.entity.Player,java.util.UUID,com.jodexindustries.donatecase.api.data.CaseData.Item)) вызывается для полного завершения анимации.
```java
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.JavaAnimation;
import org.bukkit.Bukkit;

public class TestAnimation extends JavaAnimation {
    @Override
    public void start() {
        Case.animationPreEnd(getCaseData(), getPlayer(), getUuid(), getWinItem());
        Bukkit.getScheduler().runTaskLater(Case.getInstance(), () -> Case.animationEnd(getCaseData(), getPlayer(), getUuid(), getWinItem()),20L);
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
        animationManager.registerAnimation("test", TestAnimation.class);
    }
```