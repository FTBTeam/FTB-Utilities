package com.feed_the_beast.ftbu.webapi;

import com.feed_the_beast.ftbu.config.FTBUConfigWebAPI;
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
 * Created by LatvianModder on 18.06.2016.
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
            return (s == null) ? JsonNull.INSTANCE : e;
        }
    }

    public static final class CellID
    {
        public final String x, y;

        public CellID(String px, String py)
        {
            x = px;
            y = py;
        }

        @Override
        public String toString()
        {
            return x + ':' + y;
        }

        @Override
        public boolean equals(Object o)
        {
            if(o instanceof CellID)
            {
                CellID id = (CellID) o;
                return id.x.equals(x) && id.y.equals(y);
            }

            return false;
        }

        @Override
        public int hashCode()
        {
            return 31 * x.hashCode() + y.hashCode();
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
        if(translated != null && !translated.isEmpty())
        {
            ids.put(id, translated);
        }
    }

    public void addEntry(TableEntry e)
    {
        if(e != null)
        {
            entries.add(e);
        }
    }

    public JsonObject toJson()
    {
        JsonObject o = new JsonObject();
        String[] ids1 = ids.keySet().toArray(new String[ids.size()]);

        o.add("columns", new JsonPrimitive(ids1.length));
        o.add("rows", new JsonPrimitive(entries.size()));

        JsonArray a = new JsonArray();

        if(FTBUConfigWebAPI.output_map.getAsBoolean())
        {
            JsonObject o1 = new JsonObject();

            for(String s : ids1)
            {
                o1.add(s, new JsonPrimitive(ids.get(s)));
            }

            o.add("ids", o1);

            for(TableEntry entry : entries)
            {
                o1 = new JsonObject();

                for(String s : ids1)
                {
                    o1.add(s, entry.get(s));
                }

                a.add(o1);
            }

            o.add("table", a);
        }
        else
        {
            for(String s : ids1)
            {
                a.add(new JsonPrimitive(s));
            }

            o.add("ids", a);

            a = new JsonArray();

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
        }

        return o;
    }

    @Override
    public String toString()
    {
        return toJson().toString();
    }
}