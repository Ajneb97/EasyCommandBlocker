package ecb.ajneb97.spigot.titleapi;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTitle;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTitle.TitleAction;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerClearTitles;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerListHeaderAndFooter;
import com.github.retrooper.packetevents.protocol.player.User;
import ecb.ajneb97.spigot.utils.MessagesUtils;
import ecb.ajneb97.spigot.utils.OtherUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class TitleAPI implements Listener {

    @Deprecated
    public static void sendSubtitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String message) {
        sendTitle(player, fadeIn, stay, fadeOut, null, message);
    }

    @Deprecated
    public static void sendFullTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
        sendTitle(player, fadeIn, stay, fadeOut, title, subtitle);
    }

    public static void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
        // 1.11+ için native API kullan
        if(OtherUtils.serverIsNew()) {
            if(title == null || title.isEmpty()) {
                title = " ";
            }
            if(subtitle == null || subtitle.isEmpty()) {
                subtitle = " ";
            }
            player.sendTitle(MessagesUtils.getColoredMessage(title), MessagesUtils.getColoredMessage(subtitle), fadeIn, stay, fadeOut);
            return;
        }

        // Eski versiyonlar için PacketEvents kullan
        try {
            User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
            if(user == null) return;

            // Mesajları renklendir
            if(title != null) {
                title = MessagesUtils.getColoredMessage(title);
                title = title.replaceAll("%player%", player.getDisplayName());
            }
            if(subtitle != null) {
                subtitle = MessagesUtils.getColoredMessage(subtitle);
                subtitle = subtitle.replaceAll("%player%", player.getDisplayName());
            }

            // Component'lere çevir
            Component titleComponent = title != null ?
                    LegacyComponentSerializer.legacyAmpersand().deserialize(title) : null;
            Component subtitleComponent = subtitle != null ?
                    LegacyComponentSerializer.legacyAmpersand().deserialize(subtitle) : null;

            // TIMES paketi gönder
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

            // TITLE paketi gönder
            if(titleComponent != null) {
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

            // SUBTITLE paketi gönder
            if(subtitleComponent != null) {
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
            e.printStackTrace();
        }
    }

    public static void clearTitle(Player player) {
        if(OtherUtils.serverIsNew()) {
            player.resetTitle();
            return;
        }

        try {
            User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
            if(user == null) return;

            // Clear titles packet
            WrapperPlayServerClearTitles clearPacket = new WrapperPlayServerClearTitles(false);
            user.sendPacket(clearPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void resetTitle(Player player) {
        if(OtherUtils.serverIsNew()) {
            player.resetTitle();
            return;
        }

        try {
            User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
            if(user == null) return;

            // Reset titles packet
            WrapperPlayServerClearTitles resetPacket = new WrapperPlayServerClearTitles(true);
            user.sendPacket(resetPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendTabTitle(Player player, String header, String footer) {
        // Tab title için hem yeni hem eski versiyonlarda çalışır
        try {
            // Yeni versiyonlar için native API
            if(player.getClass().getMethod("setPlayerListHeaderFooter", String.class, String.class) != null) {
                if (header == null) header = "";
                if (footer == null) footer = "";

                header = MessagesUtils.getColoredMessage(header.replaceAll("%player%", player.getDisplayName()));
                footer = MessagesUtils.getColoredMessage(footer.replaceAll("%player%", player.getDisplayName()));

                player.setPlayerListHeaderFooter(header, footer);
                return;
            }
        } catch (NoSuchMethodException e) {
            // Eski versiyonlar için PacketEvents kullan
        }

        try {
            User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
            if(user == null) return;

            // Mesajları hazırla
            if (header == null) header = "";
            if (footer == null) footer = "";

            header = MessagesUtils.getColoredMessage(header.replaceAll("%player%", player.getDisplayName()));
            footer = MessagesUtils.getColoredMessage(footer.replaceAll("%player%", player.getDisplayName()));

            // Component'lere çevir
            Component headerComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(header);
            Component footerComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(footer);

            // Tab list header/footer paketi gönder
            WrapperPlayServerPlayerListHeaderAndFooter packet =
                    new WrapperPlayServerPlayerListHeaderAndFooter(headerComponent, footerComponent);

            user.sendPacket(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendActionBar(Player player, String message) {
        if(message == null || message.isEmpty()) return;

        message = MessagesUtils.getColoredMessage(message.replaceAll("%player%", player.getDisplayName()));

        // Tüm versiyonlar için PacketEvents kullan
        try {
            User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
            if(user == null) return;

            Component actionBarComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(message);

            // TITLE paketini ACTION_BAR tipiyle kullan
            WrapperPlayServerTitle actionBarPacket = new WrapperPlayServerTitle(
                    TitleAction.SET_ACTION_BAR,
                    (Component) null,
                    (Component) null,
                    actionBarComponent,
                    0,
                    0,
                    0
            );

            user.sendPacket(actionBarPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}