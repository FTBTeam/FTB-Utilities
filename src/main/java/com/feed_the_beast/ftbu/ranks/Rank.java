package com.feed_the_beast.ftbu.ranks;

import com.feed_the_beast.ftbl.api.permissions.ForgePermissionRegistry;
import com.feed_the_beast.ftbl.api.permissions.RankConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import latmod.lib.util.FinalIDObject;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IJsonSerializable;
import net.minecraft.util.text.TextFormatting;

import java.util.LinkedHashMap;
import java.util.Map;

public final class Rank extends FinalIDObject implements IJsonSerializable
{
    public final Map<String, Boolean> permissions;
    public final Map<RankConfig, JsonElement> config;
    public Rank parent = null;
    public TextFormatting color = TextFormatting.WHITE;
    public String prefix = "";
    public String badge = "";

    public Rank(String id)
    {
        super(id);
        permissions = new LinkedHashMap<>();
        config = new LinkedHashMap<>();
    }

    public Boolean handlePermission(String permission)
    {
        if(permissions.containsKey("*"))
        {
            return permissions.get("*");
        }

        return permissions.get(permission);
    }

    public JsonElement handleRankConfig(RankConfig permission)
    {
        if(this == Ranks.PLAYER)
        {
            return permission.getDefaultValue(false);
        }
        else if(this == Ranks.ADMIN)
        {
            return permission.getDefaultValue(true);
        }

        JsonElement e = config.get(permission);
        return (e == null) ? ((parent != null) ? parent.handleRankConfig(permission) : null) : e;
    }

    @Override
    public JsonElement getSerializableElement()
    {
        JsonObject o = new JsonObject();

        o.add("parent", new JsonPrimitive(parent == null ? "" : parent.getID()));
        o.add("color", new JsonPrimitive(color.getFriendlyName()));
        o.add("prefix", new JsonPrimitive(prefix));
        o.add("badge", new JsonPrimitive(badge));

        if(!permissions.isEmpty())
        {
            JsonArray a1 = new JsonArray();

            for(Map.Entry<String, Boolean> e : permissions.entrySet())
            {
                a1.add(new JsonPrimitive((e.getValue().booleanValue() ? "+" : "-") + e.getKey()));
            }

            o.add("permissions", a1);
        }

        if(!config.isEmpty())
        {
            JsonObject o1 = new JsonObject();

            for(Map.Entry<RankConfig, JsonElement> e : config.entrySet())
            {
                o1.add(e.getKey().getID().toString(), e.getValue());
            }

            o.add("config", o1);
        }

        return o;
    }

    @Override
    public void fromJson(JsonElement e)
    {
        JsonObject o = e.getAsJsonObject();
        parent = o.has("parent") ? Ranks.instance().ranks.get(o.get("parent").getAsString()) : null;
        color = o.has("color") ? TextFormatting.getValueByName(o.get("color").getAsString()) : TextFormatting.WHITE;
        prefix = o.has("prefix") ? o.get("prefix").getAsString() : "";
        badge = o.has("badge") ? o.get("badge").getAsString() : "";
        permissions.clear();
        config.clear();

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

        if(o.has("config"))
        {
            for(Map.Entry<String, JsonElement> entry : o.get("config").getAsJsonObject().entrySet())
            {
                RankConfig c = ForgePermissionRegistry.getConfig(entry.getKey());

                if(c != null && !entry.getValue().isJsonNull())
                {
                    config.put(c, entry.getValue());
                }
            }
        }
    }

    public boolean allowCommand(MinecraftServer server, ICommandSender sender, ICommand command)
    {
        Boolean b = handlePermission("command." + command.getCommandName());
        if(b != null)
        {
            return b.booleanValue();
        }
        if(parent == null)
        {
            return command.checkPermission(server, sender);
        }
        return parent.allowCommand(server, sender, command);
    }
}