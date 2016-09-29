package com.feed_the_beast.ftbu.ranks;

import com.feed_the_beast.ftbl.api.config.IConfigKey;
import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbl.lib.FinalIDObject;
import com.feed_the_beast.ftbl.lib.config.ConfigTree;
import com.feed_the_beast.ftbl.lib.config.PropertyNull;
import com.feed_the_beast.ftbu.api.IRank;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.IJsonSerializable;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Rank extends FinalIDObject implements IRank, IJsonSerializable
{
    private IRank parent;
    private Boolean allPermissions;
    final Map<String, Boolean> permissions;
    private final Map<String, Event.Result> cachedPermissions;
    final ConfigTree config;
    private final Map<IConfigKey, IConfigValue> cachedConfig;

    public Rank(String id)
    {
        super(id);
        permissions = new LinkedHashMap<>();
        cachedPermissions = new HashMap<>();
        config = new ConfigTree(true);
        cachedConfig = new HashMap<>();
    }

    @Override
    @Nullable
    public IRank getParent()
    {
        return parent;
    }

    public void setParent(IRank r)
    {
        parent = r;
    }

    @Override
    public Event.Result hasPermission(String permission)
    {
        if(allPermissions != null)
        {
            return allPermissions ? Event.Result.ALLOW : Event.Result.DENY;
        }

        Event.Result r = cachedPermissions.get(permission);

        if(r != null)
        {
            return r;
        }

        //TODO: Allow '*' values
        if(permissions.containsKey(permission))
        {
            r = permissions.get(permission) ? Event.Result.ALLOW : Event.Result.DENY;
        }

        if(r == null)
        {
            r = parent != null ? parent.hasPermission(permission) : Event.Result.DEFAULT;
        }

        cachedPermissions.put(permission, r);
        return r;
    }

    @Override
    public IConfigValue getConfig(IConfigKey id)
    {
        IConfigValue e = cachedConfig.get(id);

        if(e == null)
        {
            e = config.get(id);

            if(e == PropertyNull.INSTANCE && parent != null)
            {
                e = parent.getConfig(id);
            }
        }

        cachedConfig.put(id, e);
        return e;
    }

    @Override
    public JsonElement getSerializableElement()
    {
        JsonObject o = new JsonObject();

        o.add("parent", new JsonPrimitive(parent == null ? "" : parent.getName()));

        if(!permissions.isEmpty())
        {
            JsonArray a1 = new JsonArray();

            for(Map.Entry<String, Boolean> e : permissions.entrySet())
            {
                a1.add(new JsonPrimitive((e.getValue() ? "+" : "-") + e.getKey()));
            }

            o.add("permissions", a1);
        }

        if(!config.isEmpty())
        {
            o.add("config", config.getSerializableElement());
        }

        return o;
    }

    @Override
    public void fromJson(JsonElement e)
    {
        JsonObject o = e.getAsJsonObject();
        parent = o.has("parent") ? Ranks.INSTANCE.RANKS.get(o.get("parent").getAsString()) : null;
        permissions.clear();
        config.getTree().clear();
        cachedPermissions.clear();
        cachedConfig.clear();

        if(o.has("permissions"))
        {
            JsonArray a = o.get("permissions").getAsJsonArray();

            for(int i = 0; i < a.size(); i++)
            {
                String id = a.get(i).getAsString();
                char firstChar = id.charAt(0);
                boolean b = firstChar == '-';
                permissions.put((firstChar == '-' || firstChar == '+') ? id.substring(1) : id, b);
            }
        }

        allPermissions = permissions.get("*");

        if(o.has("config"))
        {
            config.fromJson(o.get("config"));
        }
    }
}