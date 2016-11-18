package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbl.lib.config.PropertyBase;
import com.feed_the_beast.ftbl.lib.util.LMJsonUtils;
import com.feed_the_beast.ftbl.lib.util.LMNetUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LatvianModder on 24.09.2016.
 */
public class PropertyChatSubstituteList extends PropertyBase
{
    public static final String ID = "ftbu_chat_substitutes";

    public final Map<String, ITextComponent> value;

    public PropertyChatSubstituteList(Map<String, ITextComponent> l)
    {
        value = new HashMap<>(l);
    }

    @Override
    public String getID()
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
        return !value.isEmpty();
    }

    @Override
    public int getInt()
    {
        return value.size();
    }

    @Override
    public IConfigValue copy()
    {
        return new PropertyChatSubstituteList(value);
    }

    @Override
    public NBTBase serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        value.forEach((key, value) -> nbt.setString(key, ITextComponent.Serializer.componentToJson(value)));
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTBase nbt)
    {
        value.clear();
        NBTTagCompound nbt1 = (NBTTagCompound) nbt;

        for(String s : nbt1.getKeySet())
        {
            value.put(s, ITextComponent.Serializer.jsonToComponent(nbt1.getString(s)));
        }
    }

    @Override
    public void fromJson(JsonElement o)
    {
        value.clear();

        JsonObject o1 = o.getAsJsonArray().get(0).getAsJsonObject();

        for(Map.Entry<String, JsonElement> e : o1.entrySet())
        {
            value.put(e.getKey(), LMJsonUtils.deserializeTextComponent(e.getValue()));
        }
    }

    @Override
    public JsonElement getSerializableElement()
    {
        JsonObject o = new JsonObject();
        value.forEach((key, value1) -> o.add(key, LMJsonUtils.serializeTextComponent(value1)));
        JsonArray a = new JsonArray();
        a.add(o);
        return a;
    }

    @Override
    public void writeData(ByteBuf data)
    {
        data.writeShort(value.size());
        value.forEach((key, value1) ->
        {
            LMNetUtils.writeString(data, key);
            LMNetUtils.writeTextComponent(data, value1);
        });
    }

    @Override
    public void readData(ByteBuf data)
    {
        int s = data.readUnsignedShort();
        value.clear();

        while(--s >= 0)
        {
            String key = LMNetUtils.readString(data);
            ITextComponent value1 = LMNetUtils.readTextComponent(data);
            value.put(key, value1);
        }
    }
}
