package org.mcnative.runtime.common.player;

import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.annonations.Internal;
import org.mcnative.runtime.api.player.ConnectedMinecraftPlayer;
import org.mcnative.runtime.api.player.bossbar.BarColor;
import org.mcnative.runtime.api.player.bossbar.BarDivider;
import org.mcnative.runtime.api.player.bossbar.BarFlag;
import org.mcnative.runtime.api.player.bossbar.BossBar;
import org.mcnative.runtime.api.player.receiver.LocalReceiverChannel;
import org.mcnative.runtime.api.protocol.packet.type.MinecraftBossBarPacket;
import org.mcnative.runtime.api.text.components.MessageComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class DefaultBossBar implements BossBar {

    public final UUID id;
    private final Collection<ConnectedMinecraftPlayer> receivers;

    private MessageComponent<?> title;
    private VariableSet variables;

    private BarColor color;
    private BarDivider divider;
    private BarFlag flag;

    private int maximum;
    private int progress;

    public DefaultBossBar() {
        this.id = UUID.randomUUID();
        receivers = new ArrayList<>();
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public Collection<ConnectedMinecraftPlayer> getReceivers() {
        return receivers;
    }

    @Override
    public MessageComponent<?> getTitle() {
        return title;
    }

    @Override
    public BossBar setTitle(MessageComponent<?> title) {
        this.title = title;
        return this;
    }

    @Override
    public VariableSet getVariables() {
        return variables;
    }

    @Override
    public BossBar setVariables(VariableSet variables) {
        this.variables = variables;
        return this;
    }

    @Override
    public BarColor getColor() {
        return color;
    }

    @Override
    public BossBar setColor(BarColor color) {
        this.color = color;
        return this;
    }

    @Override
    public BarDivider getDivider() {
        return divider;
    }

    @Override
    public BossBar setDivider(BarDivider divider) {
        this.divider = divider;
        return this;
    }

    @Override
    public BarFlag getFlag() {
        return flag;
    }

    @Override
    public BossBar setFlag(BarFlag flag) {
        this.flag = flag;
        return this;
    }

    @Override
    public int getMaximum() {
        return maximum;
    }

    @Override
    public BossBar setMaximum(int maximum) {
        this.maximum = maximum;
        return this;
    }

    @Override
    public int getProgress() {
        return progress;
    }

    @Override
    public BossBar setProgress(int progress) {
        this.progress = progress;
        return this;
    }

    @Override
    public void update() {
        for (ConnectedMinecraftPlayer receiver : receivers) update(receiver);
    }

    @Override
    public void update(ConnectedMinecraftPlayer player) {
        MinecraftBossBarPacket packet = new MinecraftBossBarPacket();
        packet.setBarId(id);
        packet.setAction(MinecraftBossBarPacket.Action.ADD);
        packet.setTitle(title);
        packet.setTitleVariables(variables);
        packet.setHealth((float) progress / (float)maximum);
        packet.setColor(color);
        packet.setDivider(divider);
        packet.setFlag(flag);
        player.sendPacket(packet);
    }

    @Override
    public void execute(LocalReceiverChannel channel) {
        for (ConnectedMinecraftPlayer player : channel) player.addBossBar(this);
    }

    @Internal
    public void attachReceiver(ConnectedMinecraftPlayer player){
        this.receivers.add(player);
        update(player);
    }

    @Internal
    public void detachReceiver(ConnectedMinecraftPlayer player){
        this.receivers.remove(player);
        MinecraftBossBarPacket packet = new MinecraftBossBarPacket();
        packet.setAction(MinecraftBossBarPacket.Action.REMOVE);
        packet.setBarId(id);
        player.sendPacket(packet);
    }
}
