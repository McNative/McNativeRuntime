package org.mcnative.runtime.client.integrations.labymod.widget.types;

import net.pretronic.libraries.document.annotations.DocumentKey;
import org.mcnative.runtime.api.player.client.labymod.widget.types.TextFieldWidget;
import org.mcnative.runtime.client.integrations.labymod.widget.DefaultValueContainerWidget;

public class DefaultTextFieldWidget extends DefaultValueContainerWidget<TextFieldWidget> implements TextFieldWidget {

    private String placeholder;

    @DocumentKey("max_length")
    private int maxLength;
    private boolean focused;

    public DefaultTextFieldWidget(int id, String alias) {
        super(id, alias);
    }

    @Override
    public String getPlaceholder() {
        return this.placeholder;
    }

    @Override
    public TextFieldWidget setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    @Override
    public int getMaxLength() {
        return this.maxLength;
    }

    @Override
    public TextFieldWidget setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    @Override
    public boolean isFocused() {
        return this.focused;
    }

    @Override
    public TextFieldWidget setFocused(boolean focused) {
        this.focused = focused;
        return this;
    }
}
