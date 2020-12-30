package org.mcnative.runtime.common.maf;

import org.mcnative.actionframework.sdk.client.StatusListener;
import org.mcnative.runtime.api.McNative;

public class MAFStatusListener implements StatusListener {

    @Override
    public void onConnect() {
        McNative.getInstance().getLogger().info("[MAF] Connected to McNative Action Framework");
    }

    @Override
    public void onDisconnect() {
        McNative.getInstance().getLogger().info("[MAF] Disconnected from McNative Action Framework");
    }
}
