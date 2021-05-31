package org.mcnative.runtime.network.integrations.cloudnet.v2;

import net.pretronic.libraries.document.Document;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.network.NetworkIdentifier;
import org.mcnative.runtime.api.network.messaging.MessageReceiver;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RemoteCloudService implements MessageReceiver {

    private final String name;

    public RemoteCloudService(String name) {
        this.name = name;
    }

    @Override
    public NetworkIdentifier getIdentifier() {
        return McNative.getInstance().getNetwork().getIdentifier(name);
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
        return getIdentifier().getUniqueId();
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
