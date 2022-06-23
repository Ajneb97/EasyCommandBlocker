package ecb.ajneb97.core.model;

import java.util.List;

public class TabCommandList {
    private String name;
    private int priority;
    private List<String> commands;
    private String extendTabName;

    public TabCommandList(String name, int priority, List<String> commands, String extendTabName) {
        this.name = name;
        this.commands = commands;
        this.priority = priority;
        this.extendTabName = extendTabName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getExtendTabName() {
        return extendTabName;
    }

    public void setExtendTabName(String extendTabName) {
        this.extendTabName = extendTabName;
    }

    public String getPermission(){
        return "easycommandblocker.tab."+name;
    }
}
