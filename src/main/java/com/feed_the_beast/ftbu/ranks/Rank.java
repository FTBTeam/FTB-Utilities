package com.feed_the_beast.ftbu.ranks;

import com.feed_the_beast.ftbl.api.IRankConfig;
import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbl.lib.FinalIDObject;
import com.feed_the_beast.ftbl.lib.util.LMStringUtils;
import com.feed_the_beast.ftbl.lib.util.LMUtils;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.api.IRank;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.IJsonSerializable;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Rank extends FinalIDObject implements IRank, IJsonSerializable
{
    private static final String[] EVENT_RESULT_PREFIX = {"-", "~", "+"};

    private IRank parent;
    private Event.Result allPermissions;
    private final Map<String, Event.Result> permissions;
    private final Map<String, Event.Result> cachedPermissions;
    private final Map<String, IConfigValue> config;
    private final Map<String, IConfigValue> cachedConfig;
    private String prefix, suffix;

    public Rank(String id)
    {
        super(id);
        permissions = new LinkedHashMap<>();
        cachedPermissions = new HashMap<>();
        config = new LinkedHashMap<>();
        cachedConfig = new HashMap<>();
        prefix = null;
        suffix = null;
    }

    @Override
    public IRank getParent()
    {
        return parent == null ? DefaultPlayerRank.INSTANCE : parent;
    }

    @Override
    public void setParent(IRank r)
    {
        parent = r;
    }

    private Event.Result hasPermissionRaw(String permission)
    {
        if(permissions.containsKey(permission))
        {
            return permissions.get(permission);
        }
        else
        {
            String[] splitPermission = permission.split("\\.");

            for(Map.Entry<String, Event.Result> entry : permissions.entrySet())
            {
                if(LMStringUtils.matchesNode(splitPermission, entry.getKey().split("\\.")))
                {
                    return entry.getValue();
                }
            }
        }

        if(allPermissions != null)
        {
            return allPermissions;
        }

        return getParent().hasPermission(permission);
    }

    @Override
    public Event.Result hasPermission(String permission)
    {
        Event.Result r = cachedPermissions.get(permission);

        if(r == null)
        {
            r = hasPermissionRaw(permission);
            cachedPermissions.put(permission, r);
        }

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

        if(prefix != null)
        {
            o.add("prefix", new JsonPrimitive(prefix.replace(LMUtils.FORMATTING_CHAR, '&')));
        }

        if(suffix != null)
        {
            o.add("suffix", new JsonPrimitive(suffix.replace(LMUtils.FORMATTING_CHAR, '&')));
        }

        JsonArray a1 = new JsonArray();

        for(Map.Entry<String, Event.Result> e : permissions.entrySet())
        {
            a1.add(new JsonPrimitive(EVENT_RESULT_PREFIX[e.getValue().ordinal()] + e.getKey()));
        }

        o.add("permissions", a1);

        JsonObject o1 = new JsonObject();
        config.forEach((key, value) -> o1.add(key, value.getSerializableElement()));
        o.add("config", o1);

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
        prefix = null;
        suffix = null;

        if(!e.isJsonObject())
        {
            return;
        }

        JsonObject o = e.getAsJsonObject();

        if(o.has("parent"))
        {
            parent = Ranks.getRank(o.get("parent").getAsString(), null);
        }

        if(o.has("prefix"))
        {
            prefix = o.get("prefix").getAsString().replace('&', LMUtils.FORMATTING_CHAR);
        }

        if(o.has("suffix"))
        {
            suffix = o.get("suffix").getAsString().replace('&', LMUtils.FORMATTING_CHAR);
        }

        if(o.has("permissions"))
        {
            JsonArray a = o.get("permissions").getAsJsonArray();

            for(int i = 0; i < a.size(); i++)
            {
                String id = a.get(i).getAsString();
                char firstChar = id.charAt(0);
                String key = (firstChar == '-' || firstChar == '+' || firstChar == '~') ? id.substring(1) : id;
                permissions.put(key, firstChar == '-' ? Event.Result.DENY : (firstChar == '~' ? Event.Result.DEFAULT : Event.Result.ALLOW));
            }
        }

        allPermissions = permissions.get("*");

        if(o.has("config"))
        {
            for(Map.Entry<String, JsonElement> entry : o.get("config").getAsJsonObject().entrySet())
            {
                IRankConfig rconfig = FTBLibIntegration.API.getRankConfigRegistry().get(entry.getKey());

                if(rconfig != null)
                {
                    IConfigValue value = rconfig.getDefValue().copy();
                    value.fromJson(entry.getValue());
                    config.put(rconfig.getName(), value);
                }
            }
        }
    }

    @Override
    public String getPrefix()
    {
        return prefix == null ? getParent().getPrefix() : prefix;
    }

    @Override
    public String getSuffix()
    {
        return suffix == null ? getParent().getSuffix() : suffix;
    }
}