package org.mcnative.runtime.client.integrations.labymod.widget.types;

import net.pretronic.libraries.document.annotations.DocumentKey;
import org.mcnative.runtime.api.player.client.labymod.widget.types.ColorPickerWidget;
import org.mcnative.runtime.api.text.format.TextColor;
import org.mcnative.runtime.client.integrations.labymod.widget.DefaultContainerWidget;

public class DefaultColorPickerWidget extends DefaultContainerWidget<ColorPickerWidget> implements ColorPickerWidget {

    private String title;

    @DocumentKey("selected_color")
    private TextColor selectedColor;

    private boolean rgb;

    public DefaultColorPickerWidget(int id, String alias) {
        super(id, alias);
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public ColorPickerWidget setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public TextColor getSelectedColor() {
        return this.selectedColor;
    }

    @Override
    public ColorPickerWidget setSelectedColor(TextColor selectedColor) {
        this.selectedColor = selectedColor;
        return this;
    }

    @Override
    public boolean isRgb() {
        return this.rgb;
    }

    @Override
    public ColorPickerWidget setRgb(boolean rgb) {
        this.rgb = rgb;
        return this;
    }
}
