package ecb.ajneb97.waterfall.managers;

import ecb.ajneb97.core.model.ConfigStructure;
import ecb.ajneb97.core.model.CustomCommandGroup;
import ecb.ajneb97.core.model.TabCommandList;
import ecb.ajneb97.core.model.internal.UseCommandResult;
import ecb.ajneb97.waterfall.EasyCommandBlocker;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.util.ArrayList;
import java.util.List;

public class CommandsManager {
    private EasyCommandBlocker plugin;
    private ConfigStructure configStructure;
    public CommandsManager(EasyCommandBlocker plugin){
        this.plugin = plugin;
        load();
    }

    public void load(){
        Configuration config = plugin.getConfig();
        List<String> commands = config.getStringList("commands");
        List<String> blockedCommandDefaultActions = config.getStringList("blocked_command_default_actions");
        List<TabCommandList> tabCommands = new ArrayList<TabCommandList>();
        for(String key : config.getSection("tab").getKeys()){
            List<String> tab = config.getStringList("tab."+key+".commands");
            int priority = config.getInt("tab."+key+".priority");
            TabCommandList tabCommandList = new TabCommandList(key,priority,tab);
            tabCommands.add(tabCommandList);
        }
        boolean useCommandsAsWhitelist = config.getBoolean("use_commands_as_whitelist");

        List<CustomCommandGroup> customCommandGroupList = new ArrayList<CustomCommandGroup>();

        if(config.contains("custom_commands_actions")){
            for(String key : config.getSection("custom_commands_actions").getKeys()){
                String path = "custom_commands_actions."+key;
                List<String> commandsList = config.getStringList(path+".commands");
                List<String> actionsList = config.getStringList(path+".actions");
                CustomCommandGroup customCommandGroup = new CustomCommandGroup(commandsList,actionsList);
                customCommandGroupList.add(customCommandGroup);
            }
        }
        configStructure = new ConfigStructure(commands,blockedCommandDefaultActions,tabCommands,useCommandsAsWhitelist
                ,customCommandGroupList);
    }

    public List<String> getBlockCommandDefaultActions(){
        return configStructure.getBlockedCommandActions();
    }

    public List<String> getTabCommands(ProxiedPlayer player){
        List<TabCommandList> tabCommandLists = configStructure.getTabCommandList();
        List<String> currentTabCommands = null;
        List<String> defaultTabCommands = null;
        int currentPriority = -1;
        for(TabCommandList t : tabCommandLists){
            if(t.getName().equals("default")){
                defaultTabCommands = t.getCommands();
                continue;
            }

            String perm = t.getPermission();
            if(player.hasPermission(perm)){
                if(t.getPriority() > currentPriority){
                    currentTabCommands = t.getCommands();
                    currentPriority = t.getPriority();
                }
            }
        }

        if(currentTabCommands != null){
            return currentTabCommands;
        }else{
            return defaultTabCommands;
        }
    }

    public UseCommandResult useCommand(String command){

        String[] commandWithArgs = command.toLowerCase().split(" ");
        for(String blockedCommand : configStructure.getCommands()){
            String[] blockedCommandWithArgs = blockedCommand.toLowerCase().split(" ");
            int equalArguments = 0;
            for(int i=0;i<blockedCommandWithArgs.length;i++){
                if(i > commandWithArgs.length-1){
                    break;
                }
                String currentArg = commandWithArgs[i];
                if(currentArg.equals(blockedCommandWithArgs[i])){
                    equalArguments++;
                }
            }
            if(equalArguments < blockedCommandWithArgs.length){
                continue;
            }else{
                if(configStructure.isUseCommandsAsWhitelist()){
                    return new UseCommandResult(true,blockedCommand);
                }
                return new UseCommandResult(false,blockedCommand);
            }
        }
        if(configStructure.isUseCommandsAsWhitelist()){
            return new UseCommandResult(false,null);
        }
        return new UseCommandResult(true,null);
    }

    public List<String> getActionsForCustomCommand(String command){
        List<CustomCommandGroup> customCommandGroupList = configStructure.getCustomCommands();
        for(CustomCommandGroup customCommandGroup : customCommandGroupList){
            List<String> commands = customCommandGroup.getCommands();
            if(commands.contains(command)){
                return customCommandGroup.getActions();
            }
        }
        return null;
    }
}
