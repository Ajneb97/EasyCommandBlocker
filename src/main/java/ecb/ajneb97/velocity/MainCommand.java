package ecb.ajneb97.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class MainCommand implements SimpleCommand {

    private EasyCommandBlocker plugin;
    public MainCommand(EasyCommandBlocker plugin){
        this.plugin = plugin;
    }

    @Override
    public void execute(final Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        if(args.length >= 1){
            if(args[0].equalsIgnoreCase("reload")){
                reload(source);
            }
        }
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("easycommandblocker.admin");
    }

    public void reload(CommandSource source){
        plugin.customReload();
        source.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(plugin.prefix+" &aConfig reloaded!"));
    }
}
