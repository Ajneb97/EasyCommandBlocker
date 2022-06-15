package ecb.ajneb97.velocity.managers;

import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

public class ConfigManager {

    private YamlFile yamlFile;

    public void registerConfig(Path dataDirectory) {
        yamlFile = new YamlFile(dataDirectory+ File.separator+"config.yml");
        try {
            if (!yamlFile.exists()) {
                yamlFile.createNewFile();
                addDefaults();
                yamlFile.save();
            }
            yamlFile.load();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addDefaults(){
        yamlFile.set("tab.default.priority",0);
        yamlFile.set("tab.default.commands",new ArrayList<String>());

        ArrayList<String> list = new ArrayList<String>();
        list.add("message: &8[&b&lECB&8] &cYou don't have permissions to use that command.");
        yamlFile.set("blocked_command_default_actions",list);

        yamlFile.set("use_commands_as_whitelist",false);

        list = new ArrayList<String>();list.add("/velocity");list.add("/server");
        yamlFile.set("commands",list);

        list = new ArrayList<String>();list.add("/velocity");
        yamlFile.set("custom_commands_actions.example1.commands",list);
        list = new ArrayList<String>();list.add("message: &8[&b&lECB&8] &cYou can't see the Velocity version!");
        yamlFile.set("custom_commands_actions.example1.actions",list);
    }

    public YamlFile getConfig() {
        return yamlFile;
    }
}
