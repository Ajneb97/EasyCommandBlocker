package ecb.ajneb97.core.model;

import java.util.List;

public class ConfigStructure {
    private List<String> commands;
    private List<String> blockedCommandActions;
    private List<TabCommandList> tabCommandList;
    private List<CustomCommandGroup> customCommands;
    private boolean useCommandsAsWhitelist;

    public ConfigStructure(List<String> commands, List<String> blockedCommandActions, List<TabCommandList> tabCommandList
                           ,boolean useCommandsAsWhitelist,List<CustomCommandGroup> customCommands) {
        this.commands = commands;
        this.blockedCommandActions = blockedCommandActions;
        this.tabCommandList = tabCommandList;
        this.useCommandsAsWhitelist = useCommandsAsWhitelist;
        this.customCommands = customCommands;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }


    public List<TabCommandList> getTabCommandList() {
        return tabCommandList;
    }

    public void setTabCommandList(List<TabCommandList> tabCommandList) {
        this.tabCommandList = tabCommandList;
    }

    public boolean isUseCommandsAsWhitelist() {
        return useCommandsAsWhitelist;
    }

    public void setUseCommandsAsWhitelist(boolean useCommandsAsWhitelist) {
        this.useCommandsAsWhitelist = useCommandsAsWhitelist;
    }

    public List<String> getBlockedCommandActions() {
        return blockedCommandActions;
    }

    public void setBlockedCommandActions(List<String> blockedCommandActions) {
        this.blockedCommandActions = blockedCommandActions;
    }

    public List<CustomCommandGroup> getCustomCommands() {
        return customCommands;
    }

    public void setCustomCommands(List<CustomCommandGroup> customCommands) {
        this.customCommands = customCommands;
    }
}
