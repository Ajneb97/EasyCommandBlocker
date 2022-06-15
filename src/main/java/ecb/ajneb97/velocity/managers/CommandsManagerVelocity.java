package ecb.ajneb97.velocity.managers;

import ecb.ajneb97.core.managers.CommandsManager;
import ecb.ajneb97.core.model.ConfigStructure;
import ecb.ajneb97.core.model.CustomCommandGroup;
import ecb.ajneb97.core.model.TabCommandList;
import ecb.ajneb97.velocity.EasyCommandBlocker;
import org.simpleyaml.configuration.file.YamlFile;

import java.util.ArrayList;
import java.util.List;

public class CommandsManagerVelocity extends CommandsManager {
    private EasyCommandBlocker plugin;
    public CommandsManagerVelocity(EasyCommandBlocker plugin){
        this.plugin = plugin;
        load();
    }

    public void load(){
        YamlFile config = plugin.getConfigManager().getConfig();
        List<String> commands = config.getStringList("commands");
        List<String> blockedCommandDefaultActions = config.getStringList("blocked_command_default_actions");
        List<TabCommandList> tabCommands = new ArrayList<TabCommandList>();
        for(String key : config.getConfigurationSection("tab").getKeys(false)){
            List<String> tab = config.getStringList("tab."+key+".commands");
            int priority = config.getInt("tab."+key+".priority");
            TabCommandList tabCommandList = new TabCommandList(key,priority,tab);
            tabCommands.add(tabCommandList);
        }
        boolean useCommandsAsWhitelist = config.getBoolean("use_commands_as_whitelist");

        List<CustomCommandGroup> customCommandGroupList = new ArrayList<CustomCommandGroup>();

        if(config.contains("custom_commands_actions")){
            for(String key : config.getConfigurationSection("custom_commands_actions").getKeys(false)){
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
