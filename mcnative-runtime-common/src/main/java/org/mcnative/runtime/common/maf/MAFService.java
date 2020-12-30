package org.mcnative.runtime.common.maf;

import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.actionframework.sdk.client.MAFClient;
import org.mcnative.actionframework.sdk.client.discovery.DnsServiceDiscovery;
import org.mcnative.actionframework.sdk.common.ClientType;
import org.mcnative.actionframework.sdk.common.protocol.packet.handshake.authentication.KeyAuthentication;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.McNativeConsoleCredentials;

import java.util.UUID;

public class MAFService {

    public static void start(){
        McNativeConsoleCredentials credentials = McNative.getInstance().getConsoleCredentials();
        MAFClient client = MAFClient.build()
                .serviceDiscovery(new DnsServiceDiscovery("_maf._tcp.mcnative.org"))
                .authentication(new KeyAuthentication(UUID.fromString(credentials.getNetworkId()),credentials.getSecret()))
                .logger(McNative.getInstance().getLogger())
                .autoReconnect(1000)
                .statusListener(new MAFStatusListener())
                .uniqueId(McNative.getInstance().getLocal().getUniqueId())
                .name(McNative.getInstance().getLocal().getName())
                .type(ClientType.GENERIC)
                .create();

        try{
            client.connect();
            McNative.getInstance().getLocal().getEventBus().subscribe(ObjectOwner.SYSTEM,new MAFListener(client));
        }catch (Exception exception){
            McNative.getInstance().getLogger().info("[MAF] Could not connect to McNative action framework");
            exception.printStackTrace();
        }
    }

}
