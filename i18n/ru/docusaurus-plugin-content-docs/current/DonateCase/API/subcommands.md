---
id: register-subcommands
title: Регистрация субкоманд
sidebar_position: 6
---
# Регистрация субкоманд

Чтобы создать класс подкоманды, мы будем использовать [SubCommand](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/latest/.cache/unpack/com/jodexindustries/donatecase/api/data/SubCommand.html) интерфейс

TestSubCommand класс
```java
import com.jodexindustries.donatecase.api.SubCommand;
import com.jodexindustries.donatecase.api.SubCommandType;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class TestSubCommand implements SubCommand {
    /**
     * Выполняет заданную подкоманду
     * @param sender Источник команды
     * @param args Передаваемые аргументы команды
     */
    @Override
    public void execute(CommandSender sender, String[] args) {
        // здесь код как обычной Bukkit команды
    }

    /**
     *  Заполнение табуляции
     * @param sender Источник команды
     * @param args Передаваемые аргументы команды
     * @return Заполнение табуляции
     */
    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        // код для табуляции
        return new ArrayList<>();
    }

    /**
     * Тип команды (ADMIN, MODER, PLAYER)
     * @return SubCommandType
     */

    @Override
    public SubCommandType getType() {
        return SubCommandType.PLAYER;
    }
    
    /**
     * Получение аргументов команды * для отображения в описании команды в справке /dc
     * @return Аргументы команды
     */
    @Override
    public String[] getArgs() {
        return null;
    }
    
    /**
     * Получить описание команды
     * Описание, которое должно выступать в качестве описания команды в справке /dc
     * @return Аргументы команды
     */
    @Override
    public String getDescription() {
        return null;
    }
}

```

Main class
```java
    @Override
    public void onEnable() {
        // получение CaseManager
        CaseManager api = new CaseManager(this);
        // регистрация суб команды
        SubCommandManager subCommandManager = api.getSubCommandManager();
        subCommandManager.registerSubCommand("test", new TestCommand());
    }
```