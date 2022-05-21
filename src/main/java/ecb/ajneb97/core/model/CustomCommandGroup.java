package ecb.ajneb97.core.model;

import java.util.List;

public class CustomCommandGroup {
    private List<String> commands;
    private List<String> actions;

    public CustomCommandGroup(List<String> commands, List<String> actions) {
        this.commands = commands;
        this.actions = actions;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }
}
