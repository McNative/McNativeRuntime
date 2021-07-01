package org.mcnative.runtime.client.integrations.labymod.widget;

import org.mcnative.runtime.api.player.client.labymod.widget.Widget;
import org.mcnative.runtime.api.player.client.labymod.widget.types.*;
import org.mcnative.runtime.client.integrations.labymod.widget.types.DefaultButtonWidget;
import org.mcnative.runtime.client.integrations.labymod.widget.types.DefaultColorPickerWidget;
import org.mcnative.runtime.client.integrations.labymod.widget.types.DefaultTextFieldWidget;

public enum Widgets {

    BUTTON(DefaultButtonWidget.class),
    TEXT_FIELD(DefaultTextFieldWidget.class),
    LABEL(null),
    COLOR_PICKER(DefaultColorPickerWidget.class),
    IMAGE(null);
    /*
    LABEL(DefaultLab.class),
    COLOR_PICKER(ColorPickerWidget.class),
    IMAGE(ImageWidget.class);
     */

    private final Class<? extends DefaultWidget<?>> clazz;

    Widgets(Class<? extends DefaultWidget<?>> clazz) {
        this.clazz = clazz;
    }

    public Class<? extends DefaultWidget<?>> getClazz() {
        return this.clazz;
    }

    public static Widgets getTypeOf(Class<? extends DefaultWidget<?>> clazz) {
        for (Widgets type : values()) {
            if (type.clazz == clazz) {
                return type;
            }
        }
        throw new IllegalArgumentException("Can't get widget for class " + clazz);
    }
}
