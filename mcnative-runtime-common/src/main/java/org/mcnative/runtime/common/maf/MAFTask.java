package org.mcnative.runtime.common.maf;

import org.mcnative.actionframework.sdk.actions.server.ServerStatusAction;
import org.mcnative.actionframework.sdk.client.MAFClient;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.ServerPerformance;

import java.util.concurrent.TimeUnit;

public class MAFTask extends Thread {

    private final MAFClient client;

    public MAFTask(MAFClient client) {
        this.client = client;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            ServerPerformance performance = McNative.getInstance().getLocal().getServerPerformance();

            ServerStatusAction action = new ServerStatusAction(McNative.getInstance().getLocal().getMaxPlayerCount(),
                    performance.getRecentTps(),
                    performance.getUsedMemory(),
                    performance.getCpuUsage());
            this.client.sendAction(action);

            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(30));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
