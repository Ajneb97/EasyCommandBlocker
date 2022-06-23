package ecb.ajneb97.core.managers;

import org.simpleyaml.configuration.file.YamlFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    private YamlFile yamlFile;

    private Path dataDirectoryPath;
    private String originalFileName;
    private String newFileName;

    public ConfigManager(Path dataDirectoryPath,String originalFileName,String newFileName){
        this.dataDirectoryPath = dataDirectoryPath;
        this.originalFileName = originalFileName;
        this.newFileName = newFileName;
    }

    public void registerConfig() {
        String path = dataDirectoryPath+File.separator+newFileName;
        createDefaultConfigFile();

        yamlFile = new YamlFile(path);
        try {
            if (!yamlFile.exists()) {
                yamlFile.createOrLoad();
            }
            yamlFile.load();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createDefaultConfigFile(){
        File dataDirectoryFile = new File(dataDirectoryPath.toString());
        if (!dataDirectoryFile.exists()){
            dataDirectoryFile.mkdir();
        }
        File configFile = new File(dataDirectoryFile, newFileName);
        try {
            if (!configFile.exists()){
                Files.copy(this.getClass().getClassLoader().getResourceAsStream(originalFileName), configFile.toPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public YamlFile getConfig() {
        return yamlFile;
    }

    public void checkMessagesUpdate() {
        Path configFile = Paths.get(dataDirectoryPath+File.separator+newFileName);
        try {
            String configText = new String(Files.readAllBytes(configFile));
            if(!configText.contains("custom_commands_actions:")){
                List<String> list = new ArrayList<String>();
                list.add("/bungee");
                yamlFile.set("custom_commands_actions.example1.commands", list);
                list = new ArrayList<String>();
                list.add("message: &8[&b&lECB&8] &cYou can't see the BungeeCord version!");
                yamlFile.set("custom_commands_actions.example1.actions", list);
                yamlFile.save();
                yamlFile.load();
            }

            if(!configText.contains("blocked_command_default_actions:")){
                List<String> list = new ArrayList<String>();
                list.add("message: &8[&b&lECB&8] &cYou don't have permissions to use that command.");
                list.add("playsound: BLOCK_NOTE_BLOCK_PLING;10;0.1");
                list.add("title: 20;40;20;&cWhat are you doing?;&7Don't use that command!");
                yamlFile.set("blocked_command_default_actions", list);
                yamlFile.save();
                yamlFile.load();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
