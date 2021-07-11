package org.mcnative.runtime.network.integrations;

import net.pretronic.libraries.document.Document;
import org.mcnative.runtime.api.network.Network;
import org.mcnative.runtime.api.network.component.server.ProxyServer;
import org.mcnative.runtime.api.network.messaging.MessageReceiver;
import org.mcnative.runtime.api.network.messaging.MessagingChannelListener;

import java.util.Collection;
import java.util.UUID;

public class SmartLeaderElector implements MessagingChannelListener {

    public static final String CHANNEL = "mcnative_leader_election";

    private final Network network;
    private UUID leader;
    private long lastNanoStamp;

    public SmartLeaderElector(Network network) {
        this.network = network;
    }

    public UUID getLeader() {
        return leader;
    }

    public void detectCurrentLeader(){
        Collection<ProxyServer> proxies = network.getProxies();
        leader = network.getLocalIdentifier().getUniqueId();
        if(proxies.size() > 1){
            Document document = Document.newDocument();
            document.set("action","pingLeader");
            network.sendQueryMessageAsync(CHANNEL,document).thenAccept(result -> {
                this.leader = result.getObject("uuid",UUID.class);
            }).exceptionally(throwable -> {
                electLeaderProxy();
                return null;
            });
        }
    }

    public void electLeaderProxy(){
        Collection<ProxyServer> proxies = network.getProxies();
        if(proxies.size() <= 1){
            leader = network.getLocalIdentifier().getUniqueId();
        }

        leader = network.getLocalIdentifier().getUniqueId();
        lastNanoStamp = System.nanoTime();
        Document document = Document.newDocument();
        document.set("action","electMe");
        document.set("nanoStamp",lastNanoStamp);
        network.sendMessage(CHANNEL,document);
    }

    @Override
    public Document onMessageReceive(MessageReceiver sender, UUID uuid, Document request) {
        String action = request.getString("action");
        if(action.equals("electMe")){
            long nanoStamp = request.getLong("nanoStamp");
            if(nanoStamp < lastNanoStamp){
                lastNanoStamp = nanoStamp;
                leader = sender.getUniqueId();
            }
        }else if(action.equals("pingLeader")){
            if(network.getLocalIdentifier().getUniqueId().equals(leader)){
                return Document.newDocument().set("uuid",uuid);
            }
        }
        return null;
    }
}
