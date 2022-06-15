package ecb.ajneb97.core.managers;

import ecb.ajneb97.core.model.ConfigStructure;
import ecb.ajneb97.core.model.CustomCommandGroup;
import ecb.ajneb97.core.model.TabCommandList;
import ecb.ajneb97.core.model.internal.UseCommandResult;
import java.util.List;

public abstract class CommandsManager {
    protected ConfigStructure configStructure;

    public abstract void load();

    public List<String> getBlockCommandDefaultActions(){
        return configStructure.getBlockedCommandActions();
    }

    public List<String> getTabCommands(List<String> permissions){
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
            if(permissions.contains(perm)){
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

    public ConfigStructure getConfigStructure() {
        return configStructure;
    }
}
