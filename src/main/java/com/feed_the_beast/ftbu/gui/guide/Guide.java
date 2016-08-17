package com.feed_the_beast.ftbu.gui.guide;

import com.feed_the_beast.ftbl.api.info.IResourceProvider;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.latmod.lib.IIDObject;
import net.minecraft.util.IJsonSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by LatvianModder on 06.05.2016.
 */
@SideOnly(Side.CLIENT)
public abstract class Guide implements IIDObject, IJsonSerializable
{
    private final String ID;
    private final GuideType type;
    private String name;
    private List<String> authors;
    private List<String> guideAuthors;
    private List<String> modes;

    public Guide(String id, GuideType t)
    {
        ID = id;
        type = t;
    }

    @Override
    @Nonnull
    public String getID()
    {
        return ID;
    }

    public GuideType getType()
    {
        return type;
    }

    public List<String> getAuthors()
    {
        return authors;
    }

    public List<String> getGuideAuthors()
    {
        return guideAuthors;
    }

    public List<String> getModes()
    {
        return modes;
    }

    @Override
    public void fromJson(@Nonnull JsonElement json)
    {
        JsonObject o = json.getAsJsonObject();

        name = o.get("name").getAsString();

        List<String> l = new ArrayList<>();

        for(JsonElement e : o.get("authors").getAsJsonArray())
        {
            l.add(e.getAsString());
        }

        authors = Collections.unmodifiableList(l);

        l = new ArrayList<>();

        for(JsonElement e : o.get("guide_authors").getAsJsonArray())
        {
            l.add(e.getAsString());
        }

        guideAuthors = Collections.unmodifiableList(l);

        l = new ArrayList<>();

        for(JsonElement e : o.get("modes").getAsJsonArray())
        {
            l.add(e.getAsString());
        }

        modes = Collections.unmodifiableList(l);
    }

    @Override
    @Nonnull
    public JsonElement getSerializableElement()
    {
        JsonObject o = new JsonObject();

        o.add("id", new JsonPrimitive(getID()));
        o.add("name", new JsonPrimitive(name));

        JsonArray a = new JsonArray();

        for(String s : authors)
        {
            a.add(new JsonPrimitive(s));
        }

        o.add("authors", a);

        a = new JsonArray();

        for(String s : guideAuthors)
        {
            a.add(new JsonPrimitive(s));
        }

        o.add("guide_authors", a);

        a = new JsonArray();

        for(String s : modes)
        {
            a.add(new JsonPrimitive(s));
        }

        o.add("modes", a);

        return o;
    }

    public String toString()
    {
        return getSerializableElement().toString();
    }

    public abstract boolean isLocal();

    public abstract IResourceProvider getResourceProvider();

    @SideOnly(Side.CLIENT)
    public abstract ResourceLocation getIcon();
}