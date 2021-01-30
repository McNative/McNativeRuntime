package org.mcnative.runtime.common;

import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.entry.DocumentEntry;
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;
import org.mcnative.runtime.api.loader.LoaderConfiguration;
import org.mcnative.runtime.api.loader.ResourceConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class DefaultLoaderConfiguration implements LoaderConfiguration {

    private final String endpoint;
    private final String template;
    private final String profile;
    private final Collection<ResourceConfig> configs;

    public DefaultLoaderConfiguration(String endpoint, String template, String profile, Collection<ResourceConfig> configs) {
        this.endpoint = endpoint;
        this.template = template;
        this.profile = profile;
        this.configs = configs;
    }

    @Override
    public String getEndpoint() {
        return endpoint;
    }

    @Override
    public String getTemplate() {
        return template;
    }

    @Override
    public String getProfile() {
        return profile;
    }

    @Override
    public Collection<ResourceConfig> getResourceConfig() {
        return configs;
    }

    @Override
    public ResourceConfig getResourceConfig(UUID uuid) {
        Validate.notNull(uuid);
        return Iterators.findOne(this.configs, config -> config.getId() != null && config.getId().equals(uuid));
    }

    @Override
    public ResourceConfig getResourceConfig(String name) {
        Validate.notNull(name);
        return Iterators.findOne(this.configs, config -> config.getName() != null && config.getName().equalsIgnoreCase(name));
    }

    @Override
    public void pullProfile() {
        throw new UnsupportedOperationException("Currently not implemented");
    }

    public static DefaultLoaderConfiguration load(File location){
        if(location.exists()){
            Document document = DocumentFileType.YAML.getReader().read(location);
            String endpoint = document.getString("endpoint");
            String profile = document.getString("profile");
            String template = document.getString("template");
            Collection<ResourceConfig> configs = new ArrayList<>();

            Document localProfile = document.getDocument("localProfile");
            if(localProfile != null){
                for (DocumentEntry entry : localProfile.entries()) {
                    Document profileEntry = entry.toDocument();
                    configs.add(new ResourceConfig(entry.getKey(),null
                            ,profileEntry.getString("qualifier")
                            ,profileEntry.getString("version")));
                }
            }

            return new DefaultLoaderConfiguration(endpoint,template,profile,configs);
        }
        return new DefaultLoaderConfiguration("mirror.mcnative.org","local","local",new ArrayList<>());
    }
}
