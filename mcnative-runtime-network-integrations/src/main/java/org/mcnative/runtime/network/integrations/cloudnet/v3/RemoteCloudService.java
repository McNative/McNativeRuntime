package org.mcnative.runtime.network.integrations.cloudnet.v3;

import net.pretronic.libraries.document.Document;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.network.NetworkIdentifier;
import org.mcnative.runtime.api.network.messaging.MessageReceiver;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RemoteCloudService implements MessageReceiver {

    private final UUID id;

    public RemoteCloudService(UUID id) {
        this.id = id;
    }

    @Override
    public NetworkIdentifier getIdentifier() {
        return McNative.getInstance().getNetwork().getIdentifier(id);
    }

    @Override
    public void sendMessage(String s, Document document) {
        McNative.getInstance().getNetwork().getMessenger().sendMessage(this,s,document);
    }

    @Override
    public String getName() {
        return getIdentifier().getName();
    }

    @Override
    public UUID getUniqueId() {
        return id;
    }

    @Override
    public boolean isLocal() {
        return false;
    }

    @Override
    public CompletableFuture<Document> sendQueryMessageAsync(String s, Document document) {
        return McNative.getInstance().getNetwork().getMessenger().sendQueryMessageAsync(this,s,document);
    }
}
