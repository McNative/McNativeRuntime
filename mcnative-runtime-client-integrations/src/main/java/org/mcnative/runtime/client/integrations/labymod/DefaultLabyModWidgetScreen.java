package org.mcnative.runtime.client.integrations.labymod;

import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.adapter.DocumentAdapter;
import net.pretronic.libraries.document.entry.ArrayEntry;
import net.pretronic.libraries.document.entry.DocumentBase;
import net.pretronic.libraries.document.entry.DocumentEntry;
import net.pretronic.libraries.document.simple.SimpleArrayEntry;
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.libraries.utility.reflect.TypeReference;
import org.mcnative.runtime.api.player.ConnectedMinecraftPlayer;
import org.mcnative.runtime.api.player.client.LabyModClient;
import org.mcnative.runtime.api.player.client.labymod.LabyModWidgetScreen;
import org.mcnative.runtime.api.player.client.labymod.WidgetScreenLayout;
import org.mcnative.runtime.api.player.client.labymod.widget.Widget;
import org.mcnative.runtime.api.player.client.labymod.widget.types.ButtonWidget;
import org.mcnative.runtime.api.player.client.labymod.widget.types.ColorPickerWidget;
import org.mcnative.runtime.api.player.client.labymod.widget.types.TextFieldWidget;
import org.mcnative.runtime.client.integrations.labymod.widget.DefaultWidget;
import org.mcnative.runtime.client.integrations.labymod.widget.Widgets;
import org.mcnative.runtime.client.integrations.labymod.widget.types.DefaultButtonWidget;
import org.mcnative.runtime.client.integrations.labymod.widget.types.DefaultColorPickerWidget;
import org.mcnative.runtime.client.integrations.labymod.widget.types.DefaultTextFieldWidget;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class DefaultLabyModWidgetScreen implements LabyModWidgetScreen {

    private static final Map<Class<? extends Widget<?>>, Class<? extends Widget<?>>> WIDGET_CLASS_MAPPINGS;
    private static final Map<ConnectedMinecraftPlayer, AtomicInteger> IDS = new ConcurrentHashMap<>();

    static {
        WIDGET_CLASS_MAPPINGS = new HashMap<>();
        WIDGET_CLASS_MAPPINGS.put(ButtonWidget.class, DefaultButtonWidget.class);
        WIDGET_CLASS_MAPPINGS.put(TextFieldWidget.class, DefaultTextFieldWidget.class);
        WIDGET_CLASS_MAPPINGS.put(ColorPickerWidget.class, DefaultColorPickerWidget.class);
    }

    private final LabyModClient client;
    private final ConnectedMinecraftPlayer player;

    private final int id;
    private final List<Widget<?>> widgets;

    public DefaultLabyModWidgetScreen(LabyModClient client, ConnectedMinecraftPlayer player) {
        this.client = client;
        this.player = player;

        if(!IDS.containsKey(player)) IDS.put(player, new AtomicInteger(0));
        this.id = IDS.get(player).incrementAndGet();
        this.widgets = new ArrayList<>();
    }

    @Override
    public <T extends Widget<T>> T addWidget(String alias, Class<T> widgetClass) {
        Validate.notNull(widgetClass);

        Class<T> mappedClass = (Class<T>) WIDGET_CLASS_MAPPINGS.get(widgetClass);

        T widget;
        try {
            int id = IDS.get(player).incrementAndGet();
            widget = mappedClass.getConstructor(int.class, String.class).newInstance(id, alias);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalArgumentException("Can't create labymod widget " + widgetClass, e);
        }

        this.widgets.add(widget);
        return widget;
    }

    @Override
    public LabyModWidgetScreen getLayout(Consumer<WidgetScreenLayout> consumer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Widget<?>> getWidgets() {
        return Collections.unmodifiableList(this.widgets);
    }

    @Override
    public void open() {
        Document data = Document.newDocument(this);
        this.client.sendLabyModData("screen", data);
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException();
    }

    public static class Adapter implements DocumentAdapter<DefaultLabyModWidgetScreen> {

        @Override
        public DefaultLabyModWidgetScreen read(DocumentBase base, TypeReference<DefaultLabyModWidgetScreen> type) {
            throw new UnsupportedOperationException();
        }

        @Override
        public DocumentEntry write(String key, DefaultLabyModWidgetScreen screen) {
            Document data = Document.newDocument();
            data.add("action", 0);
            data.add("id", screen.id);
            ArrayEntry widgets = new SimpleArrayEntry(null);
            for (Widget<?> widget : screen.widgets) {
                Document widgetData = Document.newDocument(widget);
                widgetData.add("type", Widgets.getTypeOf((Class<? extends DefaultWidget<?>>) widget.getClass()).ordinal());
                widgetData.add("attributes", widget);
                widgets.addEntry(widgetData);
            }
            data.add("widgets", widgets);
            return data;
        }
    }
}
