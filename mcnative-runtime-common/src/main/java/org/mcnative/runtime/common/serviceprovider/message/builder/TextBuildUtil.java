package org.mcnative.runtime.common.serviceprovider.message.builder;

import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.entry.DocumentEntry;
import org.mcnative.runtime.api.connection.MinecraftConnection;
import org.mcnative.runtime.api.text.Text;
import org.mcnative.runtime.api.text.components.MessageComponent;
import org.mcnative.runtime.api.text.components.MessageKeyComponent;
import org.mcnative.runtime.api.text.context.MinecraftTextBuildContext;
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
            Document root = Document.newDocument();
            Document current = root;
            if(input instanceof ColoredString){
                String content = input.toString();
                char[] chars = content.toCharArray();
                current.set("text","");

                int textIndex = 0;
                int lastColor = 0;
                for (int i = 0; i < chars.length; i++) {
                    char char0 = chars[i];
                    if((char0 == Text.FORMAT_CHAR || char0 == Text.DEFAULT_ALTERNATE_COLOR_CHAR) && chars.length > ++i){
                        TextColor color;

                        int skip = 1;
                        if(chars[i] == '#' && chars.length>(i+6)){
                            color = TextColor.make(new String(Arrays.copyOfRange(chars,i,i+7)));
                            skip = 7;
                        } else color = TextColor.of(chars[i]);

                        if(color != null){
                            Document next = Document.newDocument();
                            current.set("extra",new Document[]{next});
                            next.set("color",color.compileColor(context.getVersion()));
                            next.set("text","");
                            resetDocument(next);
                            if(textIndex < i){
                                current.set("text",new String(Arrays.copyOfRange(chars,textIndex,i-1)));
                            }
                            current = next;
                            textIndex = i+skip;
                            lastColor = i;
                        }

                        TextStyle style = TextStyle.of(chars[i]);
                        if(style != null){
                            if(i-lastColor != 2){
                                Document next = Document.newDocument();
                                current.set("extra",new Document[]{next});
                                resetDocument(next);
                                if(textIndex < i){
                                    current.set("text",new String(Arrays.copyOfRange(chars,textIndex,i-1)));
                                }
                                current = next;
                            }
                            current.set(style.getName().toLowerCase(),true);
                            textIndex = i+1;
                        }
                    }
                }
                if(textIndex < chars.length){
                    current.set("text",new String(Arrays.copyOfRange(chars,textIndex,chars.length)));
                }
            }else{
                String content = input != null ? input.toString() : "null";
                root.set("text",content);
            }
            if(nextComp != null){
                if(nextComp instanceof DocumentEntry){
                    current.set("extra",new Object[]{nextComp});
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
            builder.append(input.toString());
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
