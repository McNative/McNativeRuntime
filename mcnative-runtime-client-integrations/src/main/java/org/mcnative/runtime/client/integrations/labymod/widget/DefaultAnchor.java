package org.mcnative.runtime.client.integrations.labymod.widget;

import org.mcnative.runtime.api.player.client.labymod.widget.Anchor;

public class DefaultAnchor implements Anchor {

    private final double x;
    private final double y;

    public DefaultAnchor(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public double getX() {
        return this.x;
    }

    @Override
    public double getY() {
        return this.y;
    }
}
