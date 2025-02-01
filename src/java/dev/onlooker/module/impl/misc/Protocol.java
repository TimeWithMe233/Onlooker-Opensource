package dev.onlooker.module.impl.misc;

import dev.onlooker.event.impl.network.PacketReceiveEvent;
import dev.onlooker.event.impl.network.PacketSendEvent;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.module.settings.impl.ModeSetting;
import io.netty.buffer.Unpooled;
import net.minecraft.event.ClickEvent;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Protocol extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Quick Macro", "Quick Macro");
    public Protocol() {
        super("Protocol", Category.MISC, "Protocol HYT Server");
    }


    private static HashMap<String,byte[]> data = new HashMap<>();

    static{
        data.put("BW-Practice",new byte[]{0, 0, 0, 26, 20, 71, 85, 73, 36, 109, 97, 105, 110, 109, 101, 110, 117, 64, 101, 110, 116, 114, 121, 47, 48, 34, 123, 34, 101, 110, 116, 114, 121, 34, 58, 48, 44, 34, 115, 105, 100, 34, 58, 34, 66, 69, 68, 87, 65, 82, 47, 98, 119, 45, 100, 97, 108, 117, 34, 125});
        data.put("BW-Solo",new byte[]{0, 0, 0, 26, 20, 71, 85, 73, 36, 109, 97, 105, 110, 109, 101, 110, 117, 64, 101, 110, 116, 114, 121, 47, 49, 34, 123, 34, 101, 110, 116, 114, 121, 34, 58, 49, 44, 34, 115, 105, 100, 34, 58, 34, 66, 69, 68, 87, 65, 82, 47, 98, 119, 45, 115, 111, 108, 111, 34, 125});
        data.put("BW-Double",new byte[]{0, 0, 0, 26, 20, 71, 85, 73, 36, 109, 97, 105, 110, 109, 101, 110, 117, 64, 101, 110, 116, 114, 121, 47, 50, 36, 123, 34, 101, 110, 116, 114, 121, 34, 58, 50, 44, 34, 115, 105, 100, 34, 58, 34, 66, 69, 68, 87, 65, 82, 47, 98, 119, 45, 100, 111, 117, 98, 108, 101, 34, 125});
        data.put("BW-Teams",new byte[]{0, 0, 0, 26, 20, 71, 85, 73, 36, 109, 97, 105, 110, 109, 101, 110, 117, 64, 101, 110, 116, 114, 121, 47, 51, 34, 123, 34, 101, 110, 116, 114, 121, 34, 58, 51, 44, 34, 115, 105, 100, 34, 58, 34, 66, 69, 68, 87, 65, 82, 47, 98, 119, 45, 116, 101, 97, 109, 34, 125});
        data.put("SW-Solo",new byte[]{0, 0, 0, 26, 20, 71, 85, 73, 36, 109, 97, 105, 110, 109, 101, 110, 117, 64, 101, 110, 116, 114, 121, 47, 48, 34, 123, 34, 101, 110, 116, 114, 121, 34, 58, 48, 44, 34, 115, 105, 100, 34, 58, 34, 83, 75, 89, 87, 65, 82, 47, 110, 115, 107, 121, 119, 97, 114, 34, 125});
        data.put("SW-Double",new byte[]{0, 0, 0, 26, 20, 71, 85, 73, 36, 109, 97, 105, 110, 109, 101, 110, 117, 64, 101, 110, 116, 114, 121, 47, 49, 41, 123, 34, 101, 110, 116, 114, 121, 34, 58, 49, 44, 34, 115, 105, 100, 34, 58, 34, 83, 75, 89, 87, 65, 82, 47, 110, 115, 107, 121, 119, 97, 114, 45, 100, 111, 117, 98, 108, 101, 34, 125});
        data.put("RoleWar",new byte[]{0, 0, 0, 26, 20, 71, 85, 73, 36, 109, 97, 105, 110, 109, 101, 110, 117, 64, 101, 110, 116, 114, 121, 47, 50, 33, 123, 34, 101, 110, 116, 114, 121, 34, 58, 50, 44, 34, 115, 105, 100, 34, 58, 34, 70, 73, 71, 72, 84, 47, 107, 98, 45, 103, 97, 109, 101, 34, 125});
    }

    // Network
    public void onPacketReceiveEvent(PacketReceiveEvent event) {
        setSuffix(mode.getMode());
        if(event.getPacket() instanceof S3FPacketCustomPayload) {
            S3FPacketCustomPayload packet = (S3FPacketCustomPayload) event.getPacket();


            if (packet.getChannelName().equalsIgnoreCase("germplugin-netease")) {

                PacketBuffer buffer = packet.getBufferData();
                byte[] data = new byte[buffer.readableBytes()];
                buffer.readBytes(data);
                if (new String(data, StandardCharsets.UTF_8).contains("h*0.5-min(h,w*9/16)*0.5")) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.wrappedBuffer(new byte[]{0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 8, 109, 97, 105, 110, 109, 101, 110, 117, 8, 109, 97, 105, 110, 109, 101, 110, 117, 8, 109, 97, 105, 110, 109, 101, 110, 117}))));
                    for (String name : Protocol.data.keySet()) {
                        ChatComponentText textComponents = new ChatComponentText("");
                        textComponents.appendSibling(createClickableText("§8[§f" + name + "§8] ", "/germ " + name));
                        mc.thePlayer.addChatComponentMessage(textComponents);

                    }
                }
            }
        }

    }

    public void onPacketSendEvent(PacketSendEvent event) {
        if(event.getPacket() instanceof C01PacketChatMessage) {
            String message = ((C01PacketChatMessage) event.getPacket()).getMessage();
            if (message.startsWith("/germ ")) {
                String name = message.replace("/germ ", "");
                if (data.containsKey(name)) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.wrappedBuffer(data.get(name)))));
                }
            }
        }

    }
    private IChatComponent createClickableText(String text, String command) {
        ChatComponentText clickableText = new ChatComponentText(text);
        clickableText.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        return clickableText;
    }

}