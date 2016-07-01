package com.feed_the_beast.ftbu.api;

import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbl.api.LangKey;
import com.latmod.lib.FinalIDObject;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public abstract class Top extends FinalIDObject implements Comparator<ForgePlayerMP>
{
    public static final LangKey LANG_TOP_TITLE = new LangKey("ftbu.top.title");
    public static final Map<String, Top> REGISTRY = new HashMap<>();
    public final LangKey langKey;

    public Top(String s)
    {
        super(s);
        langKey = new LangKey("ftbu.top." + getID());
    }

    public static void register(Top t)
    {
        REGISTRY.put(t.getID(), t);
    }

    public abstract Object getData(ForgePlayerMP p);
}