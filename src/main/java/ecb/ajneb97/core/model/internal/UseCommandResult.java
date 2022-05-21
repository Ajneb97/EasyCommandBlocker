package ecb.ajneb97.core.model.internal;

public class UseCommandResult {
    private boolean canUseCommand;
    private String foundCommand;

    public UseCommandResult(boolean canUseCommand, String foundCommand) {
        this.canUseCommand = canUseCommand;
        this.foundCommand = foundCommand;
    }

    public boolean isCanUseCommand() {
        return canUseCommand;
    }

    public String getFoundCommand() {
        return foundCommand;
    }
}
