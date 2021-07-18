package org.mcnative.runtime.client.integrations.labymod.widget;

import org.mcnative.runtime.api.player.client.labymod.widget.ValueContainerWidget;

public class DefaultValueContainerWidget<T extends ValueContainerWidget<T>> extends DefaultContainerWidget<T> implements ValueContainerWidget<T> {

    private String value;

    public DefaultValueContainerWidget(int id, String alias) {
        super(id, alias);
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public T setValue(String value) {
        this.value = value;
        return (T) this;
    }
}
