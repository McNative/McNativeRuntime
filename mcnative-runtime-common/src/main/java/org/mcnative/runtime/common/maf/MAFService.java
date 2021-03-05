package org.mcnative.runtime.common.maf;

import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.actionframework.sdk.client.MAFClient;
import org.mcnative.actionframework.sdk.client.discovery.DnsServiceDiscovery;
import org.mcnative.actionframework.sdk.common.ClientType;
import org.mcnative.actionframework.sdk.common.protocol.packet.handshake.authentication.KeyAuthentication;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.McNativeConsoleCredentials;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MAFService {

    public static void start(){
        McNativeConsoleCredentials credentials = McNative.getInstance().getConsoleCredentials();
        MAFStatusListener statusListener = new MAFStatusListener();
        MAFClient client = MAFClient.build()
                .serviceDiscovery(new DnsServiceDiscovery("_maf._tcp.mcnative.org"))
                .authentication(new KeyAuthentication(UUID.fromString(credentials.getNetworkId()),credentials.getSecret()))
                .logger(McNative.getInstance().getLogger())//@Todo add and implement prefixed logger
                .autoReconnect(2500)
                .statusListener(statusListener)
                .uniqueId(McNative.getInstance().getLocal().getUniqueId())
                .name(McNative.getInstance().getLocal().getName())
                .type(ClientType.GENERIC)
                .create();

        McNative.getInstance().getLocal().getEventBus().subscribe(ObjectOwner.SYSTEM,new MAFListener(client));
        statusListener.setClient(client);
        client.connectAsync();

        McNative.getInstance().getScheduler().createTask(ObjectOwner.SYSTEM).async()
                .delay(5, TimeUnit.SECONDS).interval(30, TimeUnit.SECONDS)
                .execute(() -> MAFUtil.sendStatusAction(client));
    }

}
