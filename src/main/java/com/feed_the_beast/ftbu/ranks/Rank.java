package com.feed_the_beast.ftbu.ranks;

import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbl.lib.FinalIDObject;
import com.feed_the_beast.ftbu.api.IRank;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.IJsonSerializable;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Rank extends FinalIDObject implements IRank, IJsonSerializable
{
    private IRank parent;
    private Boolean allPermissions;
    final Map<String, Boolean> permissions;
    private final Map<String, Event.Result> cachedPermissions;
    final Map<String, IConfigValue> config;
    private final Map<String, IConfigValue> cachedConfig;
    private String displayName, prefix;
    private TextFormatting color;

    public Rank(String id)
    {
        super(id);
        permissions = new LinkedHashMap<>();
        cachedPermissions = new HashMap<>();
        config = new LinkedHashMap<>();
        cachedConfig = new HashMap<>();
        displayName = "";
        color = null;
        prefix = "";
    }

    @Override
    public IRank getParent()
    {
        return parent == null ? EmptyRank.INSTANCE : parent;
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
            r = getParent().hasPermission(permission);
        }

        cachedPermissions.put(permission, r);
        return r;
    }

    @Override
    public IConfigValue getConfig(String id)
    {
        IConfigValue e = cachedConfig.get(id);

        if(e == null)
        {
            e = config.get(id);

            if(e == null || e.isNull())
            {
                e = getParent().getConfig(id);
            }
        }

        cachedConfig.put(id, e);
        return e;
    }

    @Override
    public JsonElement getSerializableElement()
    {
        JsonObject o = new JsonObject();

        o.add("parent", new JsonPrimitive(getParent().getName()));
        o.add("display_name", new JsonPrimitive(displayName));
        o.add("color", new JsonPrimitive(color == null ? "" : color.getFriendlyName()));
        o.add("prefix", new JsonPrimitive(prefix));

        if(!permissions.isEmpty())
        {
            JsonArray a1 = new JsonArray();

            for(Map.Entry<String, Boolean> e : permissions.entrySet())
            {
                a1.add(new JsonPrimitive((e.getValue() ? "" : "-") + e.getKey()));
            }

            o.add("permissions", a1);
        }

        if(!config.isEmpty())
        {
            JsonObject o1 = new JsonObject();
            config.forEach((key, value) -> o1.add(key, value.getSerializableElement()));
            o.add("config", o1);
        }

        return o;
    }

    @Override
    public void fromJson(JsonElement e)
    {
        parent = null;
        allPermissions = null;
        permissions.clear();
        config.clear();
        cachedPermissions.clear();
        cachedConfig.clear();
        displayName = "";
        color = null;
        prefix = "";

        if(!e.isJsonObject())
        {
            return;
        }

        JsonObject o = e.getAsJsonObject();

        if(o.has("parent"))
        {
            parent = Ranks.RANKS.get(o.get("parent").getAsString());
        }

        if(o.has("display_name"))
        {
            displayName = o.get("display_name").getAsString();
        }

        if(o.has("color"))
        {
            color = TextFormatting.getValueByName(o.get("color").getAsString());
        }

        if(o.has("prefix"))
        {
            prefix = o.get("prefix").getAsString();
        }

        if(o.has("permissions"))
        {
            JsonArray a = o.get("permissions").getAsJsonArray();

            for(int i = 0; i < a.size(); i++)
            {
                String id = a.get(i).getAsString();
                char firstChar = id.charAt(0);
                boolean not_allowed = firstChar == '-';
                String key = (firstChar == '-' || firstChar == '+') ? id.substring(1) : id;
                permissions.put(key, !not_allowed);
            }
        }

        allPermissions = permissions.get("*");

        if(o.has("config"))
        {
            for(Map.Entry<String, JsonElement> entry : o.get("config").getAsJsonObject().entrySet())
            {

            }
        }
    }

    @Override
    public String getDisplayName()
    {
        return displayName.isEmpty() ? getParent().getDisplayName() : displayName;
    }

    @Override
    public TextFormatting getColor()
    {
        return color == null ? getParent().getColor() : color;
    }

    @Override
    public String getPrefix()
    {
        return prefix.isEmpty() ? getParent().getPrefix() : prefix;
    }
}