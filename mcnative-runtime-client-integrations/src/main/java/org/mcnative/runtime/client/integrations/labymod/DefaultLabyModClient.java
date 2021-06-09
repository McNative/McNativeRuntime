package org.mcnative.runtime.client.integrations.labymod;

import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.libraries.utility.annonations.Internal;
import org.mcnative.runtime.api.player.ConnectedMinecraftPlayer;
import org.mcnative.runtime.api.player.client.DiscordRichPresence;
import org.mcnative.runtime.api.player.client.LabyModClient;
import org.mcnative.runtime.api.protocol.MinecraftProtocolVersion;
import org.mcnative.runtime.api.text.components.MessageComponent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class DefaultLabyModClient implements LabyModClient {

    private final AtomicInteger inputId = new AtomicInteger();
    private final Map<Integer, Consumer<String>> inputCallbacks = new ConcurrentHashMap<>();

    private final ConnectedMinecraftPlayer player;
    private final String version;
    private final DiscordRichPresence discordRichPresence;

    public DefaultLabyModClient(ConnectedMinecraftPlayer player,String version) {
        this.player = player;
        this.version = version;
        this.discordRichPresence = new LabyModDiscordRichPresence(this);
    }

    @Override
    public String getName() {
        return "LabyMod";
    }

    public String getVersion(){
        return version;
    }

    public int getVersionNumber(){
        return Integer.parseInt(version.replace(".",""));
    }

    @Override
    public DiscordRichPresence getDiscordRichPresence() {
        return discordRichPresence;
    }

    @Override
    public void sendServerBanner(String url) {
        Document data = Document.newDocument();
        data.set("url",url);
        sendLabyModData("server_banner",data);
    }

    @Override
    public void sendLanguageFlag(UUID uuid, String countryCode) {
        Document data = Document.newDocument();

        Document array = Document.factory().newArrayEntry("users");
        data.addEntry(array);

        Document user = Document.newDocument();
        user.set("uuid",uuid);
        user.set("code",countryCode);
        array.addEntry(user);

        sendLabyModData("language_flag",data);
    }

    @Override
    public void sendWatermark(boolean visible) {
        Document data = Document.newDocument();
        data.set("visible",visible);
        sendLabyModData("watermark",data);
    }

    @Override
    public void updateBalanceDisplay(EnumBalanceType type, int balance) {
        Document data = Document.newDocument();

        Document coins = Document.newDocument();
        coins.set("visible",true);
        coins.set("balance",balance);

        data.set(type.getKey(),coins);
        sendLabyModData("economy",data);
    }

    @Override
    public void sendSubtitle(UUID uuid, double size, String text) {
        Document data = Document.factory().newArrayEntry("root");

        Document subtitle = Document.newDocument();
        subtitle.set("uuid",uuid.toString());
        subtitle.set("size",size);
        subtitle.set("value",text);

        data.addEntry(subtitle);
        sendLabyModData("account_subtitle",data);
    }

    @Override
    public void sendSubtitle(ConnectedMinecraftPlayer target, double size, String text) {
        Document data = Document.factory().newArrayEntry("root");

        Document subtitle = Document.newDocument();
        subtitle.set("uuid",target.getUniqueId().toString());
        subtitle.set("size",size);
        subtitle.set("value",text);

        data.addEntry(subtitle);
        sendLabyModData("account_subtitle",data);
    }

    @Override
    public void sendSubtitle(ConnectedMinecraftPlayer target, double size, MessageComponent<?> component, VariableSet variables) {
        Document data = Document.factory().newArrayEntry("root");

        Document subtitle = Document.newDocument();
        subtitle.set("uuid",target.getUniqueId().toString());
        subtitle.set("size",size);

        if(player.getConnection().getProtocolVersion().isNewerOrSame(MinecraftProtocolVersion.JE_1_16)){
            subtitle.set("raw_json_text",component.compileToString(target, variables, player.getLanguage()));
        }else{
            subtitle.set("value",component.compileToString(target, MinecraftProtocolVersion.JE_1_7, variables, player.getLanguage()));
        }

        data.addEntry(subtitle);
        sendLabyModData("account_subtitle",data);
    }

    @Override
    public void sendToServer(String title, String address, boolean preview ) {
        sendToServer(title, address, preview,null);
    }

    @Override
    public void sendToServer(String title, String address, boolean preview , Consumer<Boolean> consumer) {
        Document data = Document.newDocument();
        data.set("title",title);
        data.set("address",address);
        data.set("preview",preview);
        sendLabyModData("server_switch",data);
    }

    @Override
    public void enableVoiceChat() {
        Document data = Document.newDocument();
        data.set("allowed",true);
        sendLabyModData("voicechat",data);
    }

    @Override
    public void disableVoiceChat() {
        Document data = Document.newDocument();
        data.set("allowed",false);
        sendLabyModData("voicechat",data);
    }

    @Override
    public void sendVoiceChatMuteInfo(UUID uuid, boolean muted) {
        Document data = Document.newDocument();

        Document player = Document.newDocument();
        player.set("mute",muted);
        player.set("target",uuid);

        data.set("mute_player",player);
        sendLabyModData("voicechat",data);
    }

    @Override
    public void sendCurrentGameModeInfo(String gamemode) {
        Document data = Document.newDocument();
        data.set("show_gamemode",true);
        data.set("gamemode_name",gamemode);
        sendLabyModData("server_gamemode",data);
    }

    @Override
    public void sendLabyModData(String message, Document document) {
        String channel = "labymod3:main";
        if(getVersionNumber() < 342) channel = "LMC";
        LabyModIntegration.sendLabyModMessage(player,channel,message,document);
    }

    @Override
    public void sendInput(String label, String placeholder, String defaultValue, int maxLength, Consumer<String> callback) {
        Validate.notNull(label);
        Validate.notNull(placeholder);
        Validate.notNull(defaultValue);
        Validate.notNull(callback);

        int id = inputId.incrementAndGet();
        Document data = Document.newDocument().add("id", id)
                .add("message", label)
                .add("value", defaultValue)
                .add("placeholder", placeholder);
        if(maxLength > 0) data.add("max_length", maxLength);
        sendLabyModData("input_prompt", data);
        inputCallbacks.put(id, callback);
    }

    @Internal
    public boolean completeInput(int id, String value) {
        Consumer<String> callback = inputCallbacks.remove(id);
        if(callback == null) return false;
        callback.accept(value);
        return true;
    }
}
