package org.mcnative.runtime.common.plugin.configuration;

import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.utility.Convert;
import net.pretronic.libraries.utility.GeneralUtil;
import net.pretronic.libraries.utility.Validate;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.Setting;
import org.mcnative.runtime.api.player.data.PlayerDataProvider;
import org.mcnative.runtime.api.plugin.configuration.ConfigurationProvider;

public class DefaultSetting implements Setting {

    private final int id;
    private final String owner;
    private final String key;
    private Object value;
    private final long created;
    private long updated;

    public DefaultSetting(int id, String owner, String key, Object value, long created, long updated) {
        this.id = id;
        this.owner = owner;
        this.key = key;
        this.value = value;
        this.created = created;
        this.updated = updated;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getOwner() {
        return owner;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getValue() {
        return value.toString();
    }

    @Override
    public Object getObjectValue() {
        return value;
    }

    @Override
    public byte getByteValue() {
        return Convert.toByte(value);
    }

    @Override
    public int getIntValue() {
        return Convert.toInteger(value);
    }

    @Override
    public long getLongValue() {
        return Convert.toLong(value);
    }

    @Override
    public double getDoubleValue() {
        return Convert.toDouble(value);
    }

    @Override
    public float getFloatValue() {
        return Convert.toFloat(value);
    }

    @Override
    public boolean getBooleanValue() {
        return Convert.toBoolean(value);
    }

    @Override
    public Document getDocumentValue() {
        if(value instanceof  Document) return (Document) value;
        return DocumentFileType.JSON.getReader().read(value.toString());
    }

    @Override
    public void setValue(Object value) {
        Validate.notNull(value);
        this.value = value;
        McNative.getInstance().getRegistry().getService(ConfigurationProvider.class).updateSetting(this);
    }

    @Override
    public long getCreated() {
        return this.created;
    }

    @Override
    public long getUpdated() {
        return this.updated;
    }

    @Override
    public void setUpdated(long updated) {
        this.updated = updated;
    }

    @Override
    public boolean equalsValue(Object value) {
        if(this.value == value) return true;
        if(this.value.equals(value)) return true;
        else if(this.value instanceof String){
            String stringValue = this.value.toString();
            if(value instanceof Byte){
                return GeneralUtil.isNaturalNumber(stringValue) && Byte.parseByte(stringValue) == (byte)value;
            }else if(value instanceof Integer){
                return GeneralUtil.isNaturalNumber(stringValue) && Integer.parseInt(stringValue) == (int)value;
            }else if(value instanceof Long){
                return GeneralUtil.isNaturalNumber(stringValue) && Long.parseLong(stringValue) == (long)value;
            }else if(value instanceof Double){
                return GeneralUtil.isNumber(stringValue) && Double.parseDouble(stringValue) == (double)value;
            }else if(value instanceof Boolean){
                return (stringValue.equalsIgnoreCase("true") && (boolean)value)
                        || (stringValue.equalsIgnoreCase("false") && !(boolean) value);
            }
        }
        return false;
    }
}
