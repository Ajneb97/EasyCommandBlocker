package ecb.ajneb97.waterfall.managers;

import ecb.ajneb97.core.managers.CommandsManager;
import ecb.ajneb97.core.model.ConfigStructure;
import ecb.ajneb97.core.model.CustomCommandGroup;
import ecb.ajneb97.core.model.TabCommandList;
import ecb.ajneb97.core.model.internal.UseCommandResult;
import ecb.ajneb97.waterfall.EasyCommandBlocker;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.util.ArrayList;
import java.util.List;

public class CommandsManagerWaterfall extends CommandsManager {
    private EasyCommandBlocker plugin;
    public CommandsManagerWaterfall(EasyCommandBlocker plugin){
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

}
