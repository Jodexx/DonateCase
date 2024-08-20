---
id: register-subcommands
title: Register Sub Commands
sidebar_position: 7
---
# Register sub commands

## New methods

### 1#
TestCommand class
```java
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandTabCompleter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TestCommand implements SubCommandExecutor, SubCommandTabCompleter {

    /**
     * Executes the given sub command
     * @param sender Source of the command
     * @param label Command label
     * @param args Passed command arguments
     */
    @Override
    public void execute(CommandSender sender, @NotNull String label, String[] args) {
        sender.sendMessage("Bukkit command");
    }

    /**
     * Get command tab completions
     * @param sender Command sender
     * @param label Command label
     * @param args Command args
     * @return tab completions
     */
    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        return new ArrayList<>();
    }

}
```

Main class
```java
    @Override
    public void onEnable() {
        // getting CaseManager
        CaseManager api = new CaseManager(this);
        // register subcommand
        SubCommandManager subCommandManager = api.getSubCommandManager();
    
        TestCommand executor = new TestCommand();
    
        SubCommand subCommand = subCommandManager.builder("test")
                .type(SubCommandType.PLAYER)
                .executor(executor)
                .tabCompleter(executor)
                .args(new String[]{"(test)", "(test2)"})
                .description("This is cool command!")
                .build();
    
        subCommandManager.registerSubCommand(subCommand);
}
```

### 2#
SecondCommand class
```java
import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.SubCommandType;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SecondCommand extends SubCommand {

    public SecondCommand(String name, Addon addon) {
        super(name, addon);

        setType(SubCommandType.PLAYER);
        setDescription("This is a second command");
        setArgs(new String[]{"(test)", "(test2)"});
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        sender.sendMessage("Second command");
    }

    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        return new ArrayList<>();
    }
}
```

Main class
```java
    @Override
    public void onEnable() {
        // getting CaseManager
        CaseManager api = new CaseManager(this);
        // register subcommand
        SubCommandManager subCommandManager = api.getSubCommandManager();
        
        SecondCommand second = new SecondCommand("test2", api.getAddon());
        subCommandManager.registerSubCommand(second);
    }
```


## ~~Old method~~
To create a subcommand class, we will use the [SubCommand](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/latest/.cache/unpack/com/jodexindustries/donatecase/api/data/SubCommand.html) interface

TestSubCommand class
```java
import com.jodexindustries.donatecase.api.SubCommand;
import com.jodexindustries.donatecase.api.SubCommandType;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class TestSubCommand implements SubCommand {
    /**
     * Executes the given sub command
     * @param sender Source of the command
     * @param args Passed command arguments
     */
    @Override
    public void execute(CommandSender sender, String[] args) {
        // code here as normal command
    }

    /**
     *  Tab completions
     * @param sender Source of the command
     * @param args Passed command arguments
     * @return Tab completions
     */
    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        //code here for tab completions
        return new ArrayList<>();
    }

    /**
     * Command type (ADMIN, MODER, PLAYER)
     * @return SubCommandType
     */

    @Override
    public SubCommandType getType() {
        return SubCommandType.PLAYER;
    }
    
    /**
     * Get command arguments     *  to appear in the Command Description in /dc help
     * @return Command arguments
     */
    @Override
    public String[] getArgs() {
        return null;
    }
    
    /**
     * Get command description
     * A description that should act as a description of the command in the /dc help
     * @return Command description
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
        // getting CaseManager
        CaseManager api = new CaseManager(this);
        // register subcommand
        SubCommandManager subCommandManager = api.getSubCommandManager();
        subCommandManager.registerSubCommand("test", new TestSubCommand());
    }
```