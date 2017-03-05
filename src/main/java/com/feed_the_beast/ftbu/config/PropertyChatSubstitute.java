package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbl.lib.config.PropertyBase;
import com.feed_the_beast.ftbl.lib.util.LMJsonUtils;
import com.feed_the_beast.ftbl.lib.util.LMNetUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import javax.annotation.Nullable;

/**
 * Created by LatvianModder on 24.09.2016.
 */
public class PropertyChatSubstitute extends PropertyBase
{
    public static final String ID = "ftbu_chat_substitute";

    public String key;
    public ITextComponent value;

    public PropertyChatSubstitute()
    {
    }

    public PropertyChatSubstitute(String s, ITextComponent t)
    {
        key = s;
        value = t;
    }

    @Override
    public String getName()
    {
        return ID;
    }

    @Nullable
    @Override
    public Object getValue()
    {
        return value;
    }

    @Override
    public String getString()
    {
        return value.toString();
    }

    @Override
    public boolean getBoolean()
    {
        return true;
    }

    @Override
    public int getInt()
    {
        return 1;
    }

    @Override
    public IConfigValue copy()
    {
        return new PropertyChatSubstitute(key, value);
    }

    @Override
    public boolean isNull()
    {
        return key.isEmpty() || value == null;
    }

    @Override
    public void fromJson(JsonElement o)
    {
        key = "";
        value = null;

        if(o.isJsonObject())
        {
            JsonObject o1 = o.getAsJsonObject();

            if(o1.has("key") && o1.has("val"))
            {
                key = o1.get("key").getAsString();
                value = LMJsonUtils.deserializeTextComponent(o1.get("val"));
            }
        }
    }

    @Override
    public JsonElement getSerializableElement()
    {
        JsonObject o = new JsonObject();
        o.add("key", new JsonPrimitive(key));
        o.add("val", LMJsonUtils.serializeTextComponent(value));
        return o;
    }

    @Override
    public void writeData(ByteBuf data)
    {
        ByteBufUtils.writeUTF8String(data, key);
        LMNetUtils.writeTextComponent(data, value);
    }

    @Override
    public void readData(ByteBuf data)
    {
        key = ByteBufUtils.readUTF8String(data);
        value = LMNetUtils.readTextComponent(data);
    }
}