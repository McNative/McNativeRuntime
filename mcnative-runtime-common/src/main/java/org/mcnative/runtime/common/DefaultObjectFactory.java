package org.mcnative.runtime.common;

import net.pretronic.libraries.utility.Validate;
import org.mcnative.runtime.api.ObjectFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class DefaultObjectFactory implements ObjectFactory {

    private static final String[] EMPTY = new String[0];

    private final Map<Class<?>,Function<?,?>> creators;

    public DefaultObjectFactory() {
        this.creators = new HashMap<>();
    }

    @Override
    public <T> T createObject(Class<?> clazz) {
        return createObject(clazz,EMPTY);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T createObject(Class<?> clazz, Object... parameters) {
        Validate.notNull(clazz,parameters);
        Function creator = creators.get(clazz);
        if(creator == null) throw new UnsupportedOperationException("No object creator for "+clazz+" found");
        return (T) creator.apply(parameters);
    }

    @Override
    public <T> void registerCreator(Class<T> clazz, Function<Object[], T> creator) {
        Validate.notNull(clazz,creator);
        creators.put(clazz,creator);
    }
}
