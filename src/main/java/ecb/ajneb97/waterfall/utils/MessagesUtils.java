package ecb.ajneb97.waterfall.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessagesUtils {

    public static BaseComponent[] getColoredMessage(String text) {
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher match = pattern.matcher(text);

        while(match.find()) {
            String color = text.substring(match.start(),match.end());
            text = text.replace(color, ChatColor.of(color)+"");

            match = pattern.matcher(text);
        }

        text = ChatColor.translateAlternateColorCodes('&', text);
        return TextComponent.fromLegacyText(text);
    }
}
