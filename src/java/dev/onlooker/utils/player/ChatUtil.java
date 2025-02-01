package dev.onlooker.utils.player;

import dev.onlooker.utils.Utils;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class ChatUtil implements Utils {

    public static void print(boolean prefix, String message) {
        if (mc.thePlayer != null) {
            if (prefix) message = "§7[§b§lO§r§7] " + message;
            mc.thePlayer.addChatMessage(new ChatComponentText(message));
        }
    }

    public static void error(String message) {
        if (mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(new ChatComponentText("§7[§c§lERROR§r§7] " + message));
        }
    }

    public static void print(String prefix, EnumChatFormatting color, String message) {
        if (mc.thePlayer != null) {
            message = "§7[§" + color.formattingCode + "§l" + prefix.toUpperCase() + "§r§7]§2 §" + color.formattingCode + message;
            mc.thePlayer.addChatMessage(new ChatComponentText(message));
        }
    }

    public static void print(Object o) {
        print(true, String.valueOf(o));
    }

    public static void send(String message) {
        if (mc.thePlayer != null) {
            mc.thePlayer.sendChatMessage(message);
        }
    }

}
