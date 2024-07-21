---
id: register-subcommands
title: Register Sub Commands
sidebar_position: 6
---
# Register sub commands

To create an subcommand class, we will use the [SubCommand](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/latest/.cache/unpack/com/jodexindustries/donatecase/api/data/SubCommand.html) interface

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