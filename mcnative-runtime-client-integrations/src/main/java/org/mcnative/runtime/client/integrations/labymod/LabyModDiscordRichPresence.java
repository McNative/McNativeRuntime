package org.mcnative.runtime.client.integrations.labymod;

import net.pretronic.libraries.document.Document;
import org.mcnative.runtime.api.player.client.DiscordRichPresence;
import org.mcnative.runtime.api.player.client.LabyModClient;

import java.util.UUID;

public class LabyModDiscordRichPresence implements DiscordRichPresence {

    private final LabyModClient client;

    public LabyModDiscordRichPresence(LabyModClient client) {
        this.client = client;
    }

    @Override
    public void sendMatchSecrets(String secret, String domain) {
        Document data = Document.newDocument();
        data.set("hasMatchSecret",true);
        data.set("matchSecret",secret+":"+domain);
        client.sendLabyModData("discord_rpc",data);
    }

    @Override
    public void sendSpectateSecret(String secret, String domain) {
        Document data = Document.newDocument();
        data.set("hasSpectateSecret",true);
        data.set("spectateSecret",secret+":"+domain);
        client.sendLabyModData("discord_rpc",data);
    }

    @Override
    public void sendJoinSecret(String secret, String domain) {
        Document data = Document.newDocument();
        data.set("hasJoinSecret",true);
        data.set("joinSecret",secret+":"+domain);
        client.sendLabyModData("discord_rpc",data);
    }

    @Override
    public void sendGameInfo(String gamemode, long startTime , long endTime) {
        Document data = Document.newDocument();
        data.set("hasGame",true);
        data.set("game_mode",gamemode);
        data.set("game_startTime",startTime);
        data.set("game_endTime",endTime);
        client.sendLabyModData("discord_rpc",data);
    }

    @Override
    public void sendRemoveGameInfo() {
        Document data = Document.newDocument();
        data.set("hasGame",false);
        client.sendLabyModData("discord_rpc",data);
    }

    @Override
    public void sendPartyInfo(UUID id, int partySize, int maxPartyMembers ) {
        Document data = Document.newDocument();
        data.set("hasParty",true);
        data.set("partyId",id);
        data.set("party_size",partySize);
        data.set("party_max",maxPartyMembers);
        client.sendLabyModData("discord_rpc",data);
    }

    @Override
    public void sendRemovePartyInfo() {
        Document data = Document.newDocument();
        data.set("hasParty",false);
        client.sendLabyModData("discord_rpc",data);
    }
}
