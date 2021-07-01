package org.mcnative.runtime.client.integrations.labymod.widget;

import net.pretronic.libraries.utility.annonations.Internal;
import org.mcnative.runtime.api.player.client.labymod.widget.Anchor;
import org.mcnative.runtime.api.player.client.labymod.widget.Widget;

public class DefaultWidget<T extends Widget<T>> implements Widget<T> {

    private final int id;
    private final String alias;

    private Anchor anchor;
    private double offsetX;
    private double offsetY;

    public DefaultWidget(int id, String alias) {
        this.id = id;
        this.alias = alias;
    }

    @Override
    public String getAlias() {
        return this.alias;
    }

    @Override
    public Anchor getAnchor() {
        return this.anchor;
    }

    @Override
    public T setAnchor(Anchor anchor) {
        this.anchor = anchor;
        return (T) this;
    }

    @Override
    public double getOffsetX() {
        return this.offsetX;
    }

    @Override
    public T setOffsetX(double offsetX) {
        this.offsetX = offsetX;
        return (T) this;
    }

    @Override
    public double getOffsetY() {
        return this.offsetY;
    }

    @Override
    public T setOffsetY(double offsetY) {
        this.offsetY = offsetY;
        return (T) this;
    }

    @Internal
    public int getId() {
        return id;
    }
}
