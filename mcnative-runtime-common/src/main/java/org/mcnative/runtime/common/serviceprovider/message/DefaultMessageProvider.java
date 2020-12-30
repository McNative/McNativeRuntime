/*
 * (C) Copyright 2020 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 22.01.20, 20:25
 *
 * The McNative Project is under the Apache License, version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.mcnative.runtime.common.serviceprovider.message;

import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.DocumentRegistry;
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.message.MessagePack;
import net.pretronic.libraries.message.MessageProvider;
import net.pretronic.libraries.message.bml.DefaultMessageProcessor;
import net.pretronic.libraries.message.bml.Message;
import net.pretronic.libraries.message.bml.MessageProcessor;
import net.pretronic.libraries.message.bml.function.defaults.math.RandomNumberFunction;
import net.pretronic.libraries.message.bml.function.defaults.math.SumFunction;
import net.pretronic.libraries.message.bml.function.defaults.operation.ConditionFunction;
import net.pretronic.libraries.message.bml.function.defaults.operation.LoopFunction;
import net.pretronic.libraries.message.bml.function.defaults.text.RandomTextFunction;
import net.pretronic.libraries.message.bml.indicate.IndicateBuilder;
import net.pretronic.libraries.message.bml.parser.MessageParser;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.message.language.Language;
import net.pretronic.libraries.message.language.LanguageAble;
import net.pretronic.libraries.message.repository.MessageRepository;
import net.pretronic.libraries.plugin.Plugin;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import net.pretronic.libraries.utility.map.caseintensive.CaseIntensiveHashMap;
import net.pretronic.libraries.utility.parser.ParserException;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.plugin.MinecraftPlugin;
import org.mcnative.runtime.api.text.context.MinecraftTextBuildContext;
import org.mcnative.runtime.api.text.context.TextBuildType;
import org.mcnative.runtime.common.serviceprovider.message.builder.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DefaultMessageProvider implements MessageProvider {

    private static final String MESSAGE_NOT_FOUND = "The message %key% was not found.";

    private final MessageProcessor processor;
    private final List<MessagePack> packs;
    private final Map<String,Map<Language, Message>> messages;

    private Language defaultLanguage;

    public DefaultMessageProvider() {
        this(Language.ENGLISH);
    }

    public DefaultMessageProvider(Language defaultLanguage) {
        Validate.notNull(defaultLanguage);
        this.processor = new DefaultMessageProcessor();
        this.packs = new ArrayList<>();
        this.messages = new CaseIntensiveHashMap<>();
        this.defaultLanguage = defaultLanguage;

        registerDefaultIndicates();
        registerDefaultFunctions();
    }

    @Override
    public MessageProcessor getProcessor() {
        return processor;
    }

    @Override
    public Language getDefaultLanguage() {
        return defaultLanguage;
    }

    @Override
    public void setDefaultLanguage(Language language) {
        Validate.notNull(language);
        this.defaultLanguage = language;
    }

    @Override
    public Collection<MessageRepository> getRepositories() {
        throw new IllegalArgumentException("Message repositories are currently not supported");
    }

    @Override
    public MessageRepository getRepository(String s) {
        throw new IllegalArgumentException("Message repositories are currently not supported");
    }

    @Override
    public MessageRepository addRepository(String s, String s1) {
        throw new IllegalArgumentException("Message repositories are currently not supported");
    }

    @Override
    public Collection<MessagePack> getPacks() {
        return packs;
    }

    @Override
    public Collection<MessagePack> getPacks(String module) {
        return Iterators.filter(this.packs, pack -> pack.getMeta().getModule().equalsIgnoreCase(module));
    }

    @Override
    public Collection<MessagePack> getPacks(Language language) {
        return Iterators.filter(this.packs, pack -> pack.getMeta().getLanguage().equals(language));
    }

    @Override
    public MessagePack getPack(String name) {
        return Iterators.findOne(this.packs, pack -> pack.getMeta().getName().equals(name));
    }

    @Override
    public void addPack(MessagePack pack) {
        if(getPack(pack.getMeta().getName()) != null) throw new IllegalArgumentException("A pack with the name "+pack.getMeta().getName()+" is already available");
        this.packs.add(pack);
    }

    @Override
    public List<MessagePack> loadPacks(String module) {
        Validate.notNull(module);
        MinecraftPlugin plugin = findModuleOwner(module);
        if(plugin != null){
            File folder = plugin.getDataFolder();
            if(folder != null){
                File messageFolder = new File(folder, "messages/");
                if(messageFolder.exists()){
                    File[] content = messageFolder.listFiles();
                    if(content != null){
                        List<MessagePack> result = new ArrayList<>();
                        for (File file : content) {
                            DocumentFileType type = DocumentRegistry.findType(file);
                            if(type != null){
                                try{
                                    FileMessagePack pack = type.getReader().read(file, StandardCharsets.UTF_8).getAsObject(FileMessagePack.class);
                                    pack.setFile(file);
                                    addPack(pack);
                                    result.add(pack);
                                    plugin.getLogger().info("(Message-Provider) Loaded message pack {}",pack.getMeta().getName());
                                    plugin.getLogger().info("(Message-Provider) Loaded {} messages",pack.getMessages().size());
                                }catch (Exception exception){
                                    plugin.getLogger().info("(Message-Provider) Could not load message pack {}",file.getName());
                                    plugin.getLogger().error(exception);
                                }
                            }
                        }
                        return result;
                    }
                }
            }
        }
        return Collections.emptyList();
    }

    @Override
    public void unloadPacks(String module) {
        Validate.notNull(module);
        Iterators.removeSilent(this.packs, messagePack -> messagePack.getMeta().getModule().equalsIgnoreCase(module));
    }

    @Override
    public MessagePack importPack(MessagePack pack) {
        MinecraftPlugin owner = findModuleOwner(pack.getMeta().getModule());
        if(owner == null) throw new IllegalArgumentException("Packet owner is missing");

        addPack(pack);

        File location = getFile(pack, owner);
        DocumentFileType.YAML.getWriter().write(location, StandardCharsets.UTF_8,Document.newDocument(pack),true);
        owner.getLogger().info("(Message-Provider) Imported message pack {}",pack.getMeta().getName());
        owner.getLogger().info("(Message-Provider) Loaded {} messages",pack.getMessages().size());
        return pack;
    }

    @Override
    public MessagePack addPack(Document document) {
        MessagePack pack = MessagePack.fromDocument(document);
        addPack(pack);
        return pack;
    }

    @Override
    public void updatePack(MessagePack pack, int updateCount) {
        MinecraftPlugin owner = findModuleOwner(pack.getMeta().getModule());
        if(pack instanceof FileMessagePack){
            DocumentFileType.YAML.getWriter().write(((FileMessagePack) pack).getFile(), StandardCharsets.UTF_8,Document.newDocument(pack),true);
        }else{
            if(owner == null) throw new IllegalArgumentException("Packet owner is missing");
            File location = getFile(pack, owner);
            DocumentFileType.YAML.getWriter().write(location, StandardCharsets.UTF_8,Document.newDocument(pack),true);
        }

        if(owner != null){
            owner.getLogger().info("(Message-Provider) Updated message pack {}",pack.getMeta().getName());
            owner.getLogger().info("(Message-Provider) Updated {} messages",updateCount);
        }else{
            McNative.getInstance().getLogger().info("["+pack.getMeta().getModule()+"] (Message-Provider) Updated message pack {}",pack.getMeta().getName());
            McNative.getInstance().getLogger().info("["+pack.getMeta().getModule()+"] (Message-Provider) Updated {} messages",updateCount);
        }
    }

    private MinecraftPlugin findModuleOwner(String module){
        for (Plugin<?> plugin : McNative.getInstance().getPluginManager().getPlugins()) {
            if (plugin instanceof MinecraftPlugin
                    && plugin.getDescription().getMessageModule() != null
                    && plugin.getDescription().getMessageModule().equalsIgnoreCase(module)) {
                return (MinecraftPlugin) plugin;
            }
        }
        return null;
    }

    private File getFile(MessagePack pack, MinecraftPlugin owner) {
        File folder = new File(owner.getDataFolder(), "messages/");
        folder.mkdirs();
        String name = pack.getMeta().getName() + "-" + pack.getMeta().getLanguage().getCode() + ".yml";
        name = name.toLowerCase().replace(" ", "-");
        return new File(folder, name);
    }

    @Override
    public Message getMessage(String key) {
        return getMessage(key, (Language) null);
    }

    @Override
    public Message getMessage(String key, Language language) {
        if(language == null) language = defaultLanguage;
        Map<Language,Message> node = this.messages.get(key);
        if(node != null && !node.isEmpty()){
            Message result = node.get(language);
            if(result != null) return result;
            result = node.get(defaultLanguage);
            if(result != null) return result;
            return node.entrySet().iterator().next().getValue();
        }
        return Message.ofStaticText(MESSAGE_NOT_FOUND.replace("%key%",key));
    }

    @Override
    public Message getMessage(String key, LanguageAble obj) {
        return getMessage(key,obj != null ? obj.getLanguage() : null);
    }

    @Override
    public String buildMessage(String key, VariableSet variables) {
        return buildMessage(key,variables,null);
    }

    @Override
    public String buildMessage(String key, Language language) {
        return buildMessage(key,VariableSet.newEmptySet(),language);
    }

    @Override
    public String buildMessage(String key, VariableSet variables, Language language) {
        return getMessage(key,language)
                .build(new MinecraftTextBuildContext(language,variables,McNative.getInstance().getPlatform().getProtocolVersion(),null, TextBuildType.PLAIN))
                .toString();
    }

    @Override
    public void calculateMessages() {
        this.messages.clear();
        for (MessagePack pack : this.packs) {
            pack.getMessages().forEach((key, message) -> {
                Map<Language,Message> node = messages.computeIfAbsent(key, key1 -> new HashMap<>());

                try{
                    MessageParser parser = new MessageParser(processor,message);
                    Message result = parser.parse();
                    node.put(pack.getMeta().getLanguage(),result);
                }catch (ParserException exception){
                    McNative.getInstance().getLogger().error("[McNative] (Message-Provider) Failed parsing message "+key);
                    McNative.getInstance().getLogger().error("[McNative] (Message-Provider) Error: "+exception.getMessage());
                    String[] result = exception.getParser().buildExceptionContent(exception.getLine(),exception.getIndex(),"[McNative] (Message-Provider) ");
                    McNative.getInstance().getLogger().error(result[0]);
                    McNative.getInstance().getLogger().error(result[1]);
                }catch (Exception exception){
                    McNative.getInstance().getLogger().error("[McNative] (Message-Provider) Failed parsing message "+key);
                    McNative.getInstance().getLogger().error("[McNative] (Message-Provider) Error: "+exception.getMessage());
                }
            });
        }
    }

    public void registerDefaultIndicates(){
        processor.addIgnoredChar(' ');
        processor.addIgnoredChar('\t');
        processor.addIgnoredChar('\r');

        processor.registerIndicate(ObjectOwner.SYSTEM, IndicateBuilder.newBuilder()
                .define('{','}')
                .builder(new VariableBuilder())
                .create());

        processor.registerIndicate(ObjectOwner.SYSTEM, IndicateBuilder.newBuilder()
                .prefix('$')
                .define('{','}')
                .factory(name -> new IncludeMessageBuilder(DefaultMessageProvider.this))
                .create());

        processor.registerIndicate(ObjectOwner.SYSTEM, IndicateBuilder.newBuilder()
                .prefix('%')
                .define('{','}')
                .builder(new PlaceholderVariableBuilder())
                .create());

        processor.registerIndicate(ObjectOwner.SYSTEM, IndicateBuilder.newBuilder()
                .prefix('@')
                .define('(',')')
                .parameter(',')
                .hasName()
                .hasOperation()
                .isSubIndicateAble()
                .factory(new FunctionFactory(processor))
                .create());

        processor.registerIndicate(ObjectOwner.SYSTEM, IndicateBuilder.newBuilder()
                .prefix('!')
                .define('[',']')
                .parameter(';')
                .extension('(',')')
                .isSubIndicateAble()
                .isExtensionSubIndicateAble()
                .builder(new ActionTextBuilder())
                .create());
        processor.setTextBuilderFactory(new TextBuilder.Factory());
    }

    public void registerDefaultFunctions(){
        processor.registerFunction(ObjectOwner.SYSTEM,"for",new LoopFunction());
        processor.registerFunction(ObjectOwner.SYSTEM,"foreach",new LoopFunction());
        processor.registerFunction(ObjectOwner.SYSTEM,"if",new ConditionFunction());

        processor.registerFunction(ObjectOwner.SYSTEM,"random",new RandomNumberFunction());
        processor.registerFunction(ObjectOwner.SYSTEM,"randomText",new RandomTextFunction());
        processor.registerFunction(ObjectOwner.SYSTEM,"sum",new SumFunction());
    }
}
