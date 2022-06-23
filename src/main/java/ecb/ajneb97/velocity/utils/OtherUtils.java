package ecb.ajneb97.velocity.utils;

import com.velocitypowered.api.proxy.Player;
import ecb.ajneb97.core.managers.CommandsManager;
import ecb.ajneb97.core.model.ConfigStructure;
import ecb.ajneb97.core.model.TabCommandList;

import java.util.ArrayList;
import java.util.List;

public class OtherUtils {

    public static List<String> getPermissions(Player player, CommandsManager commandsManager){
        List<String> permissions = new ArrayList<String>();
        ConfigStructure configStructure = commandsManager.getConfigStructure();
        for(TabCommandList t : configStructure.getTabCommandList()){
            String perm = t.getPermission();
            if(player.hasPermission(perm)){
                permissions.add(perm);
            }
        }
        return permissions;
    }
}
