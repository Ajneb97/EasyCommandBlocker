package ecb.ajneb97.spigot.titleapi;

import ecb.ajneb97.spigot.utils.MessagesUtils;
import ecb.ajneb97.spigot.utils.OtherUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

// PacketEvents imports (only used when PacketEvents is available)
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTitle;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTitle.TitleAction;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerClearTitles;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerListHeaderAndFooter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class TitleAPI implements Listener {

    /**
     * Check if PacketEvents plugin is active and enabled
     */
    private static boolean isPacketEventsActive() {
        return Bukkit.getPluginManager().getPlugin("packetevents") != null
            && Bukkit.getPluginManager().getPlugin("packetevents").isEnabled();
    }

    @Deprecated
    public static void sendSubtitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String message) {
        sendTitle(player, fadeIn, stay, fadeOut, null, message);
    }

    @Deprecated
    public static void sendFullTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
        sendTitle(player, fadeIn, stay, fadeOut, title, subtitle);
    }

    public static void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
        // Use native API for 1.11+
        if (OtherUtils.serverIsNew()) {
            if (title == null || title.isEmpty()) {
                title = " ";
            }
            if (subtitle == null || subtitle.isEmpty()) {
                subtitle = " ";
            }
            player.sendTitle(MessagesUtils.getColoredMessage(title), MessagesUtils.getColoredMessage(subtitle), fadeIn, stay, fadeOut);
            return;
        }

        // Use PacketEvents direct API if available, otherwise use NMS reflection
        if (isPacketEventsActive()) {
            sendTitleWithPacketEvents(player, fadeIn, stay, fadeOut, title, subtitle);
        } else {
            sendTitleWithNMS(player, fadeIn, stay, fadeOut, title, subtitle);
        }
    }

    /**
     * Send title using PacketEvents direct API
     */
    private static void sendTitleWithPacketEvents(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
        try {
            // Use PacketEvents with imported classes
            User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
            if (user == null) return;

            // Colorize messages
            if (title != null) {
                title = MessagesUtils.getColoredMessage(title);
                title = title.replaceAll("%player%", player.getDisplayName());
            }
            if (subtitle != null) {
                subtitle = MessagesUtils.getColoredMessage(subtitle);
                subtitle = subtitle.replaceAll("%player%", player.getDisplayName());
            }

            // Convert to components
            Component titleComponent = title != null ?
                    LegacyComponentSerializer.legacyAmpersand().deserialize(title) : null;
            Component subtitleComponent = subtitle != null ?
                    LegacyComponentSerializer.legacyAmpersand().deserialize(subtitle) : null;

            // Send TIMES packet
            WrapperPlayServerTitle timesPacket = new WrapperPlayServerTitle(
                    TitleAction.SET_TIMES_AND_DISPLAY,
                    (Component) null,
                    (Component) null,
                    (Component) null,
                    fadeIn,
                    stay,
                    fadeOut
            );
            user.sendPacket(timesPacket);

            // Send TITLE packet
            if (titleComponent != null) {
                WrapperPlayServerTitle titlePacket = new WrapperPlayServerTitle(
                        TitleAction.SET_TITLE,
                        titleComponent,
                        (Component) null,
                        (Component) null,
                        fadeIn,
                        stay,
                        fadeOut
                );
                user.sendPacket(titlePacket);
            }

            // Send SUBTITLE packet
            if (subtitleComponent != null) {
                WrapperPlayServerTitle subtitlePacket = new WrapperPlayServerTitle(
                        TitleAction.SET_SUBTITLE,
                        (Component) null,
                        subtitleComponent,
                        (Component) null,
                        fadeIn,
                        stay,
                        fadeOut
                );
                user.sendPacket(subtitlePacket);
            }

        } catch (Exception e) {
            // Fall back to NMS on PacketEvents error
            sendTitleWithNMS(player, fadeIn, stay, fadeOut, title, subtitle);
        }
    }

    /**
     * Send title using NMS reflection (legacy method)
     */
    private static void sendTitleWithNMS(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
        try {
            Object e;
            Object chatTitle;
            Object chatSubtitle;
            Constructor subtitleConstructor;
            Object titlePacket;
            Object subtitlePacket;

            if (title != null) {
                title = ChatColor.translateAlternateColorCodes('&', title);
                title = title.replaceAll("%player%", player.getDisplayName());
                // Times packets
                e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get((Object) null);
                chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke((Object) null, new Object[]{"{\"text\":\"" + title + "\"}"});
                subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[]{getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE});
                titlePacket = subtitleConstructor.newInstance(new Object[]{e, chatTitle, fadeIn, stay, fadeOut});
                sendPacket(player, titlePacket);

                e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get((Object) null);
                chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke((Object) null, new Object[]{"{\"text\":\"" + title + "\"}"});
                subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[]{getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent")});
                titlePacket = subtitleConstructor.newInstance(new Object[]{e, chatTitle});
                sendPacket(player, titlePacket);
            }

            if (subtitle != null) {
                subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
                subtitle = subtitle.replaceAll("%player%", player.getDisplayName());
                // Times packets
                e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get((Object) null);
                chatSubtitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke((Object) null, new Object[]{"{\"text\":\"" + title + "\"}"});
                subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[]{getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE});
                subtitlePacket = subtitleConstructor.newInstance(new Object[]{e, chatSubtitle, fadeIn, stay, fadeOut});
                sendPacket(player, subtitlePacket);

                e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get((Object) null);
                chatSubtitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke((Object) null, new Object[]{"{\"text\":\"" + subtitle + "\"}"});
                subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[]{getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE});
                subtitlePacket = subtitleConstructor.newInstance(new Object[]{e, chatSubtitle, fadeIn, stay, fadeOut});
                sendPacket(player, subtitlePacket);
            }
        } catch (Exception var11) {
            var11.printStackTrace();
        }
    }

    public static void clearTitle(Player player) {
        // Use native API for modern versions
        if (OtherUtils.serverIsNew()) {
            player.resetTitle();
            return;
        }

        // Use PacketEvents direct API if available
        if (isPacketEventsActive()) {
            try {
                User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
                if (user == null) return;

                // Clear titles packet
                WrapperPlayServerClearTitles clearPacket = new WrapperPlayServerClearTitles(false);
                user.sendPacket(clearPacket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // NMS fallback
            sendTitle(player, 0, 0, 0, "", "");
        }
    }

    public static void sendTabTitle(Player player, String header, String footer) {
        if (header == null) header = "";
        if (footer == null) footer = "";

        header = header.replaceAll("%player%", player.getDisplayName());
        footer = footer.replaceAll("%player%", player.getDisplayName());

        // Try native API for modern versions
        try {
            if (player.getClass().getMethod("setPlayerListHeaderFooter", String.class, String.class) != null) {
                player.setPlayerListHeaderFooter(
                    MessagesUtils.getColoredMessage(header), 
                    MessagesUtils.getColoredMessage(footer)
                );
                return;
            }
        } catch (NoSuchMethodException e) {
            // Eski versiyonlar için packet gönder
        }

        // Use PacketEvents direct API if available, yoksa NMS kullan
        if (isPacketEventsActive()) {
            sendTabTitleWithPacketEvents(player, header, footer);
        } else {
            sendTabTitleWithNMS(player, header, footer);
        }
    }

    /**
     * PacketEvents direkt API ile tab title gönder
     */
    private static void sendTabTitleWithPacketEvents(Player player, String header, String footer) {
        try {
            User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
            if (user == null) return;

            // Convert to components
            Component headerComponent = LegacyComponentSerializer.legacyAmpersand()
                    .deserialize(MessagesUtils.getColoredMessage(header));
            Component footerComponent = LegacyComponentSerializer.legacyAmpersand()
                    .deserialize(MessagesUtils.getColoredMessage(footer));

            // Send tab list header/footer packet
            WrapperPlayServerPlayerListHeaderAndFooter packet =
                    new WrapperPlayServerPlayerListHeaderAndFooter(headerComponent, footerComponent);

            user.sendPacket(packet);
        } catch (Exception e) {
            // Fall back to NMS on PacketEvents error
            sendTabTitleWithNMS(player, header, footer);
        }
    }

    /**
     * NMS reflection ile tab title gönder (eski yöntem)
     */
    private static void sendTabTitleWithNMS(Player player, String header, String footer) {
        header = ChatColor.translateAlternateColorCodes('&', header);
        footer = ChatColor.translateAlternateColorCodes('&', footer);

        try {
            Object tabHeader = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + header + "\"}");
            Object tabFooter = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + footer + "\"}");
            Constructor<?> titleConstructor = getNMSClass("PacketPlayOutPlayerListHeaderFooter").getConstructor();
            Object packet = titleConstructor.newInstance();
            try {
                Field aField = packet.getClass().getDeclaredField("a");
                aField.setAccessible(true);
                aField.set(packet, tabHeader);
                Field bField = packet.getClass().getDeclaredField("b");
                bField.setAccessible(true);
                bField.set(packet, tabFooter);
            } catch (Exception e) {
                Field aField = packet.getClass().getDeclaredField("header");
                aField.setAccessible(true);
                aField.set(packet, tabHeader);
                Field bField = packet.getClass().getDeclaredField("footer");
                bField.setAccessible(true);
                bField.set(packet, tabFooter);
            }
            sendPacket(player, packet);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // NMS yardımcı metodları
    public static void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Class<?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}