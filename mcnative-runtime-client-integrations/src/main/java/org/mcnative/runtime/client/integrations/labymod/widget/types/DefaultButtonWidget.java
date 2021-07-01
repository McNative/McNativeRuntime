package org.mcnative.runtime.client.integrations.labymod.widget.types;

import net.pretronic.libraries.document.annotations.DocumentKey;
import org.mcnative.runtime.api.player.client.labymod.widget.types.ButtonWidget;
import org.mcnative.runtime.client.integrations.labymod.widget.DefaultValueContainerWidget;

public class DefaultButtonWidget extends DefaultValueContainerWidget<ButtonWidget> implements ButtonWidget {

    @DocumentKey("close_screen_on_click")
    private boolean closeScreenOnClick;

    public DefaultButtonWidget(int id, String alias) {
        super(id, alias);
    }

    @Override
    public boolean isCloseScreenOnClick() {
        return this.closeScreenOnClick;
    }

    @Override
    public DefaultButtonWidget setCloseScreenOnClick(boolean closeScreenOnClick) {
        this.closeScreenOnClick = closeScreenOnClick;
        return this;
    }
}
