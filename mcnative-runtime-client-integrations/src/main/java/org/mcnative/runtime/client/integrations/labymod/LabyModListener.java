package org.mcnative.runtime.client.integrations.labymod;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.type.DocumentFileType;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.event.player.MinecraftPlayerDiscordRichPresenceReceiveEvent;
import org.mcnative.runtime.api.player.ConnectedMinecraftPlayer;
import org.mcnative.runtime.api.player.client.CustomClient;
import org.mcnative.runtime.api.player.client.CustomPluginMessageListener;
import org.mcnative.runtime.api.player.client.LabyModClient;

public class LabyModListener implements CustomPluginMessageListener {

    @Override
    public void onReceive(ConnectedMinecraftPlayer player, String channel, byte[] data) {
        ByteBuf buf = Unpooled.wrappedBuffer(data);
        String key = LabyModIntegration.readString(buf, Short.MAX_VALUE);
        String json = LabyModIntegration.readString(buf, Short.MAX_VALUE);
        Document document = DocumentFileType.JSON.getReader().read(json);
        System.out.println(DocumentFileType.JSON.getWriter().write(document, true));
        if(key.equalsIgnoreCase("info")) {
            if(player.getCustomClient() != null) return;
            String version = document.getString("version");
            player.setCustomClient(new DefaultLabyModClient(player,version));
        }else if(key.equalsIgnoreCase("discord_rpc")) {
            String spectateSecret = document.getString("spectateSecret");
            String joinSecret = document.getString("joinSecret");
            McNative.getInstance().getLocal().getEventBus().callEvent(MinecraftPlayerDiscordRichPresenceReceiveEvent.class
                    ,new LabyModMinecraftPlayerDiscordRichPresenceReceiveEvent(player,spectateSecret,joinSecret));
        } else if(key.equalsIgnoreCase("input_prompt")) {
            int id = document.getInt("id");
            String value = document.getString("value");
            if(player.isCustomClient(CustomClient.LABYMOD)) {
                LabyModClient labyModClient = player.getCustomClient(CustomClient.LABYMOD);
                if(labyModClient instanceof DefaultLabyModClient) {
                    ((DefaultLabyModClient)labyModClient).completeInput(id, value);
                }
            }
        } else if(key.equalsIgnoreCase("screen")) {

        }
    }
}
