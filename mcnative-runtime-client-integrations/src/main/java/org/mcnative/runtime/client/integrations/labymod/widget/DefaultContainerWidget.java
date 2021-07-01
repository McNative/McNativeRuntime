package org.mcnative.runtime.client.integrations.labymod.widget;

import org.mcnative.runtime.api.player.client.labymod.widget.ContainerWidget;

public class DefaultContainerWidget<T extends ContainerWidget<T>> extends DefaultWidget<T> implements ContainerWidget<T> {

    private int width;
    private int height;

    public DefaultContainerWidget(int id, String alias) {
        super(id, alias);
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public T setWidth(int width) {
        this.width = width;
        return (T) this;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public T setHeight(int height) {
        this.height = height;
        return (T) this;
    }
}
