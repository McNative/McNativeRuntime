package org.mcnative.runtime.client.integrations;

import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.client.integrations.labymod.LabyModListener;

public class ClientIntegration {

    public static void register(){
        LabyModListener listener = new LabyModListener();
        McNative.getInstance().getLocal().registerCustomPluginMessageListener(McNative.getInstance(),"labymod3:main",listener);
        try {
            McNative.getInstance().getLocal().registerCustomPluginMessageListener(McNative.getInstance(),"LMC",listener);
        }catch (IllegalArgumentException ignored){}
    }
}
