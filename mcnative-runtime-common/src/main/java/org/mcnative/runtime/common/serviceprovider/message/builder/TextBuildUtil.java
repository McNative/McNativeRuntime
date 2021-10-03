package org.mcnative.runtime.common.serviceprovider.message.builder;

import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.entry.DocumentEntry;
import org.mcnative.runtime.api.connection.MinecraftConnection;
import org.mcnative.runtime.api.protocol.MinecraftProtocolVersion;
import org.mcnative.runtime.api.text.Text;
import org.mcnative.runtime.api.text.components.MessageComponent;
import org.mcnative.runtime.api.text.components.MessageKeyComponent;
import org.mcnative.runtime.api.text.context.MinecraftTextBuildContext;
import org.mcnative.runtime.api.text.context.TextBuildType;
import org.mcnative.runtime.api.text.format.ColoredString;
import org.mcnative.runtime.api.text.format.TextColor;
import org.mcnative.runtime.api.text.format.TextStyle;

import java.lang.reflect.Array;
import java.util.Arrays;

public class TextBuildUtil {

    public static Object buildUnformattedText(Object input,Object next) {
        if(next == null) return input;
        else if(input == null) return "null";
        else return input.toString()+next;
    }

    public static String buildPlainText(Object input,Object nextComp){
        if(input instanceof ColoredString){
            String content = input.toString();

            StringBuilder builder = new StringBuilder(content);
            for (int i = 0; i < builder.length(); i++) {
                char char0 = builder.charAt(i);
                if((char0 == Text.FORMAT_CHAR || char0 == Text.DEFAULT_ALTERNATE_COLOR_CHAR) && builder.length() > ++i){
                    builder.delete(i-1,i+1);
                    i -= 2;
                }
            }

            if(nextComp != null)builder.append(buildPlainText(nextComp,null));

            return builder.toString();
        }else if(input == null){
            if(nextComp != null) return buildPlainText(nextComp,null);
            return "";
        }else if(input.getClass().isArray()){
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < Array.getLength(input); i++) {
                builder.append(Array.get(input,i));
            }
            builder.append(buildPlainText(nextComp,null));
            return builder.toString();
        }else{
            String content = input.toString();
            if(nextComp != null) content += buildPlainText(nextComp,null);
            return content;
        }
    }

    private static void resetDocument(Document document){
        for (TextStyle value : TextStyle.values()) {
            if(value != TextStyle.RESET){
                document.set(value.getName(),false);
            }
        }
    }

    protected static Document buildCompileText(MinecraftTextBuildContext context, Object input, Object nextComp){
        if(input instanceof MessageKeyComponent){
            Document result = ((MessageKeyComponent) input).compile(context);
            return buildCompileText(context, result, nextComp);
        }else if(input instanceof MessageComponent){
            Document result = ((MessageComponent<?>) input).compile((MinecraftConnection)null,context.getVariables(),context.getLanguage());
            return buildCompileText(context, result, nextComp);
        }else if(input instanceof Document){
            return buildCompileText(context, (Document)input, nextComp);
        }else{
            Document root = Document.factory().newArrayEntry(null);
            Document current = Document.newDocument();
            if(input instanceof ColoredString){
                char[] chars = input.toString().toCharArray();
                StringBuilder builder = new StringBuilder();
                for ( int i = 0; i < chars.length; i++ ) {
                    char c = chars[i];
                    if ((c == Text.FORMAT_CHAR || c == Text.DEFAULT_ALTERNATE_COLOR_CHAR)) {
                        if ( ++i >= chars.length) break;
                        c = chars[i];
                        if (c >= 'A' && c <= 'Z' ) c += 32;

                        TextColor color;
                        if(chars[i] == '#' && chars.length>(i+6)){
                            color = TextColor.make(new String(Arrays.copyOfRange(chars,i,i+7)));
                            i += 6;
                        } else color = TextColor.of(chars[i]);

                        if (builder.length() > 0 ){
                            Document old = current ;
                            current = old.copy();
                            old.set("text",builder.toString());
                            builder = new StringBuilder();
                            root.addEntry(old);
                        }

                        if(color != null){
                            current = Document.newDocument();
                            resetDocument(current);
                            current.set("color",color.compileColor(context.getVersion()));
                            continue;
                        }

                        TextStyle style = TextStyle.of(c);
                        if (style == TextStyle.RESET ) {
                            current = Document.newDocument();
                            current.set("color",TextColor.WHITE.compileColor(context.getVersion()));
                        } else if(style != null){
                            current.set(style.getName().toLowerCase(),true);
                        }
                        continue;
                    }
                    builder.append( c );
                }
                current.set("text",builder.toString());
                root.addEntry(current);
            }else{
                String content = input != null ? input.toString() : "null";
                current.set("text",content);
                root.addEntry(current);
            }
            if(nextComp != null){
                current = root.getLast().toDocument();
                if(nextComp instanceof DocumentEntry){
                    if(((DocumentEntry) nextComp).isArray()){
                        if(!((DocumentEntry) nextComp).toDocument().isEmpty()){
                            current.set("extra",nextComp);
                        }
                    }else{
                        current.set("extra",new Object[]{nextComp});
                    }
                }else if(nextComp.getClass().isArray()){
                    int length = Array.getLength(nextComp);
                    if(length >= 0){
                        current.set("extra",nextComp);
                    }
                }else{
                    Document result = buildCompileText(context, nextComp, null);
                    current.set("extra",new Object[]{result});
                }
            }
            return root;
        }
    }

    protected static String buildLegacyText(Object input,Object nextComp){
        StringBuilder builder = new StringBuilder();
        buildLegacyText(builder,input,nextComp);
        return builder.toString();
    }

    private static void buildLegacyText(StringBuilder builder,Object input,Object nextComp){
        if(input instanceof ColoredString){
            int start = builder.length();
            builder.append(input);
            for(int i = start; i < builder.length()-1; i++) {
                if(builder.charAt(i) == '&' && Text.ALL_CODES.indexOf(builder.charAt(i+1)) > -1){
                    builder.setCharAt(i,Text.FORMAT_CHAR);
                }
            }
        }else{
            builder.append(input.toString());
        }
        if(nextComp != null){
            buildLegacyText(builder, nextComp, null);
        }
    }

    protected static String buildCompileTextRaw(MinecraftTextBuildContext context,Object input,Object nextComp){
        StringBuilder builder = new StringBuilder();
        buildCompileTextRaw(context,builder,input,nextComp);
        return builder.toString();
    }

    private static void buildCompileTextRaw(MinecraftTextBuildContext context,StringBuilder builder,Object input,Object nextComp){
        if(input instanceof ColoredString){
            int start = builder.length();
            builder.append(input);
            for(int i = start; i < builder.length()-1; i++) {

                if (builder.charAt(i) == Text.FORMAT_CHAR || builder.charAt(i) == Text.DEFAULT_ALTERNATE_COLOR_CHAR) {
                    if ( ++i >= builder.length()) break;
                    if(builder.charAt(i) == '#' && builder.length()>(i+6)){
                        TextColor color = TextColor.make(builder.substring(i,i+7));
                        builder.replace(i-1,i+7,color.toFormatCode(context.getVersion()));
                        i += 6;
                    }else if(TextColor.of(builder.charAt(i)) != null || TextStyle.of(builder.charAt(i)) != null ){
                        builder.setCharAt(i-1,Text.FORMAT_CHAR);
                    }
                }
            }
        }else{
            builder.append(input.toString());
        }
        if(nextComp != null){
            buildCompileTextRaw(context,builder, nextComp, null);
        }
    }

    private static Document buildCompileText(MinecraftTextBuildContext context,Document document,Object nextComp){
        if(nextComp != null){
            Document root = Document.newDocument();
            root.set("text","");

            if(nextComp instanceof DocumentEntry){
                root.set("extra",new Object[]{document,nextComp});
            }else if(nextComp.getClass().isArray()){
                int length = Array.getLength(nextComp);
                if(length >= 0){
                    root.set("extra",nextComp);
                    root.getDocument("extra").entries().add(0,document);
                }
            }else{
                Document result = buildCompileText(context,nextComp,null);
                root.set("extra",new Object[]{document,result});
            }

            return root;
        }
        return document;
    }

    protected static Object buildTextData(Object input, Object input2){
        if(input2 == null && input == null){
            return null;
        }else if(input == null){
            if(input2.getClass().isArray()) return Array.getLength(input2) > 0 ? input2 : null;
            else return new Object[]{input2};
        }else if(input2 == null){
            if(input.getClass().isArray()) return Array.getLength(input) > 0 ? input : null;
            else return new Object[]{input};
        }
        if(input.getClass().isArray() && input2.getClass().isArray() ){
            int input2Length = Array.getLength(input);
            int input3Length = Array.getLength(input2);
            Object[] result = Arrays.copyOf((Object[])input,input2Length+input3Length);
            int index = input2Length+1;
            for (int i = 0; i < input3Length; i++) {
                result[index] = Array.get(input2,i);
            }
            return result;
        }else if(input.getClass().isArray()){
            Object[] result = Arrays.copyOf((Object[])input,Array.getLength(input)+1);
            result[result.length-1] = input2;
            return result;
        }else if(input2.getClass().isArray()){
            Object[] result = Arrays.copyOf((Object[])input2,Array.getLength(input2)+1);
            result[result.length-1] = input;
            return result;
        }else{
            return new Object[]{
                    input instanceof DocumentEntry ? input : input.toString()
                    ,input2 instanceof DocumentEntry ? input2 : input2.toString()};
        }
    }

}
