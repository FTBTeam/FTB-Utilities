package com.feed_the_beast.ftbu.world;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class JsonTable
{
    public static class TableEntry
    {
        private final Map<String, JsonElement> values;

        public TableEntry()
        {
            values = new HashMap<>();
        }

        public TableEntry set(String s, JsonElement e)
        {
            values.put(s, e);
            return this;
        }

        public JsonElement get(String s)
        {
            JsonElement e = values.get(s);
            return (e == null) ? JsonNull.INSTANCE : e;
        }
    }

    private final Map<String, String> ids;
    private final List<TableEntry> entries;

    public JsonTable()
    {
        ids = new LinkedHashMap<>();
        entries = new ArrayList<>();
    }

    public void setTitle(String id, String translated)
    {
        if(!translated.isEmpty())
        {
            ids.put(id, translated);
        }
    }

    public void addEntry(TableEntry e)
    {
        entries.add(e);
    }

    public JsonObject toJson()
    {
        JsonObject o = new JsonObject();
        String[] ids1 = ids.keySet().toArray(new String[ids.size()]);

        o.add("columns", new JsonPrimitive(ids1.length));
        o.add("rows", new JsonPrimitive(entries.size()));

        JsonObject o1 = new JsonObject();

        for(int i = 0; i < ids1.length; i++)
        {
            o1.add(ids1[i], new JsonPrimitive(i));
        }

        o.add("ids", o1);

        JsonArray a = new JsonArray();

        for(String s : ids1)
        {
            a.add(new JsonPrimitive(ids.get(s)));
        }

        o.add("ids_loc", a);

        a = new JsonArray();
        JsonArray a1;

        for(TableEntry entry : entries)
        {
            a1 = new JsonArray();

            for(String s : ids1)
            {
                a1.add(entry.get(s));
            }

            a.add(a1);
        }

        o.add("table", a);

        return o;
    }

    @Override
    public String toString()
    {
        return toJson().toString();
    }
}