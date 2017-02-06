package com.feed_the_beast.ftbu.ranks;

import com.feed_the_beast.ftbl.api.IRankConfig;
import com.feed_the_beast.ftbl.api.config.IConfigContainer;
import com.feed_the_beast.ftbl.api.config.IConfigTree;
import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbl.lib.config.ConfigTree;
import com.feed_the_beast.ftbl.lib.config.PropertyString;
import com.feed_the_beast.ftbl.lib.config.SimpleConfigKey;
import com.feed_the_beast.ftbl.lib.util.LMFileUtils;
import com.feed_the_beast.ftbl.lib.util.LMJsonUtils;
import com.feed_the_beast.ftbl.lib.util.LMStringUtils;
import com.feed_the_beast.ftbl.lib.util.LMUtils;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.FTBUCommon;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.api.IRank;
import com.feed_the_beast.ftbu.api.NodeEntry;
import com.feed_the_beast.ftbu.api_impl.FTBUtilitiesAPI_Impl;
import com.feed_the_beast.ftbu.config.FTBUConfigRanks;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.server.permission.DefaultPermissionHandler;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Ranks
{
    private static final Map<String, Rank> RANKS = new LinkedHashMap<>();
    public static final Map<UUID, IRank> PLAYER_MAP = new HashMap<>();
    public static final List<NodeEntry> ALL_NODES = new ArrayList<>();
    public static IRank defaultPlayerRank = DefaultPlayerRank.INSTANCE, defaultOPRank = DefaultOPRank.INSTANCE;
    private static IConfigTree ranksConfigTree;

    public static final IConfigContainer RANKS_CONFIG_CONTAINER = new IConfigContainer()
    {
        private final ITextComponent TITLE = new TextComponentString("Ranks");

        @Override
        public IConfigTree getConfigTree()
        {
            return ranksConfigTree;
        }

        @Override
        public ITextComponent getTitle()
        {
            return TITLE;
        }

        @Override
        public void saveConfig(ICommandSender sender, @Nullable NBTTagCompound nbt, JsonObject json)
        {
            ranksConfigTree.fromJson(json);
            saveRanks();
        }
    };

    @Nullable
    public static Rank getRank(String id)
    {
        if(id.equals(DefaultPlayerRank.INSTANCE.getName()))
        {
            return DefaultPlayerRank.INSTANCE;
        }
        else if(id.equals(DefaultOPRank.INSTANCE.getName()))
        {
            return DefaultOPRank.INSTANCE;
        }
        return RANKS.get(id);
    }

    public static Collection<String> getRankNames()
    {
        return RANKS.keySet();
    }

    public static void reload()
    {
        FTBUFinals.LOGGER.info("Loadeding ranks..");

        RANKS.clear();
        RANKS.put(DefaultPlayerRank.INSTANCE.getName(), DefaultPlayerRank.INSTANCE);
        RANKS.put(DefaultOPRank.INSTANCE.getName(), DefaultOPRank.INSTANCE);
        PLAYER_MAP.clear();
        defaultPlayerRank = null;
        defaultOPRank = null;

        if(FTBUConfigRanks.ENABLED.getBoolean())
        {
            JsonElement e = LMJsonUtils.fromJson(new File(LMUtils.folderLocal, "ftbu/ranks.json"));

            if(e.isJsonObject())
            {
                JsonObject o = e.getAsJsonObject();

                if(o.has("default_ranks") && o.has("ranks"))
                {
                    for(Map.Entry<String, JsonElement> entry : o.get("ranks").getAsJsonObject().entrySet())
                    {
                        RANKS.put(entry.getKey(), new Rank(entry.getKey()));
                    }

                    for(Map.Entry<String, JsonElement> entry : o.get("ranks").getAsJsonObject().entrySet())
                    {
                        RANKS.get(entry.getKey()).fromJson(entry.getValue());
                    }

                    JsonObject dr = o.get("default_ranks").getAsJsonObject();
                    defaultPlayerRank = RANKS.get(dr.get("player").getAsString());
                    defaultOPRank = RANKS.get(dr.get("op").getAsString());
                }
            }

            try
            {
                e = LMJsonUtils.fromJson(new File(LMUtils.folderLocal, "ftbu/player_ranks.json"));

                if(e.isJsonObject())
                {
                    for(Map.Entry<String, JsonElement> entry : e.getAsJsonObject().entrySet())
                    {
                        UUID id = LMStringUtils.fromString(entry.getKey());
                        if(id != null)
                        {
                            String s = entry.getValue().getAsString();

                            if(RANKS.containsKey(s))
                            {
                                PLAYER_MAP.put(id, RANKS.get(s));
                            }
                        }
                    }
                }
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
                defaultPlayerRank = null;
                defaultOPRank = null;
            }
        }

        if(defaultPlayerRank == null)
        {
            defaultPlayerRank = DefaultPlayerRank.INSTANCE;
        }

        if(defaultOPRank == null)
        {
            defaultOPRank = DefaultOPRank.INSTANCE;
        }

        ranksConfigTree = new ConfigTree();
        ranksConfigTree.add(new SimpleConfigKey("default_rank.player"), new PropertyString("player"));
        ranksConfigTree.add(new SimpleConfigKey("default_rank.op"), new PropertyString("op"));

        saveRanks();
    }

    public static void saveRanks()
    {
        JsonObject o = new JsonObject();
        JsonObject o1 = new JsonObject();
        o1.add("player", new JsonPrimitive(defaultPlayerRank.getName()));
        o1.add("op", new JsonPrimitive(defaultOPRank.getName()));
        o.add("default_ranks", o1);
        o1 = new JsonObject();

        for(Rank r : RANKS.values())
        {
            JsonElement e = r.getSerializableElement();

            if(!e.isJsonNull())
            {
                o1.add(r.getName(), e);
            }
        }

        o.add("ranks", o1);

        LMJsonUtils.toJson(new File(LMUtils.folderLocal, "ftbu/ranks.json"), o);

        final JsonObject o2 = new JsonObject();
        PLAYER_MAP.forEach((key, value) -> o2.add(LMStringUtils.fromUUID(key), new JsonPrimitive(value.getName())));
        LMJsonUtils.toJson(new File(LMUtils.folderLocal, "ftbu/player_ranks.json"), o2);
    }

    public static void generateExampleFiles()
    {
        LMFileUtils.delete(new File(LMUtils.folderLocal, "ftbu/readme.txt"));
        LMFileUtils.delete(new File(LMUtils.folderLocal, "ftbu/ranks_example.json"));
        LMFileUtils.delete(new File(LMUtils.folderLocal, "ftbu/default_rank_config.json"));

        ALL_NODES.clear();

        for(NodeEntry node : FTBUCommon.CUSTOM_PERM_PREFIX_REGISTRY)
        {
            ALL_NODES.add(new NodeEntry(node.getName() + "*", node.getLevel(), node.getDescription()));
        }

        for(String s : FTBUtilitiesAPI_Impl.INSTANCE.getRegisteredNodes())
        {
            DefaultPermissionLevel level = DefaultPermissionHandler.INSTANCE.getDefaultPermissionLevel(s);
            String desc = FTBUtilitiesAPI_Impl.INSTANCE.getNodeDescription(s);

            boolean printNode = true;

            for(NodeEntry cprefix : FTBUCommon.CUSTOM_PERM_PREFIX_REGISTRY)
            {
                if(s.startsWith(cprefix.getName()))
                {
                    if(level == cprefix.getLevel() && desc.isEmpty())
                    {
                        printNode = false;
                    }

                    break;
                }
            }

            if(printNode)
            {
                ALL_NODES.add(new NodeEntry(s, level, desc));
            }
        }

        Collections.sort(ALL_NODES, LMStringUtils.ID_COMPARATOR);

        Map<DefaultPermissionLevel, String> colorMap = new EnumMap<>(DefaultPermissionLevel.class);

        List<String> list = new ArrayList<>();

        try
        {
            list.add("<html><head><title>Permissions</title><style>");
            list.add("table{font-family:arial, sans-serif;border-collapse:collapse;}");
            list.add("td,th{border:1px solid #666666;text-align:left;padding:8px;}");
            list.add("td.all{background-color:#72FF85;}");
            list.add("td.op{background-color:#42A3FF;}");
            list.add("td.none{background-color:#FF4242;}");
            list.add("</style></head><body><h1>Permissions</h1><table>");
            list.add("<tr><th>Permission Node</th><th></th><th>Info</th></tr>");

            for(NodeEntry entry : ALL_NODES)
            {
                list.add("<tr><td>" + entry.getName() + "</td><td class='" + entry.getLevel().name().toLowerCase() + "'>" + entry.getLevel() + "</td><td>");

                if(entry.getDescription() != null)
                {
                    for(String s1 : entry.getDescription().split("\n"))
                    {
                        list.add("<p>" + s1 + "</p>");
                    }
                }

                list.add("</td></tr>");
            }

            list.add("</table></body></html>");
            LMFileUtils.save(new File(LMUtils.folderLocal, "ftbu/all_permissions.html"), list);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        List<IRankConfig> sortedRankConfigs = new ArrayList<>(FTBLibIntegration.API.getRankConfigRegistry().values());
        Collections.sort(sortedRankConfigs, LMStringUtils.ID_COMPARATOR);

        try
        {
            list.clear();
            list.add("<html><head><title>Rank Configs</title>");
            list.add("<style>table{font-family: arial, sans-serif;border-collapse: collapse;}td,th{border:1px solid #666666;text-align: left;padding:8px;}</style>");
            list.add("</head><body><h1>Rank Configs</h1><table>");
            list.add("<tr><th>Rank Config</th><th>Def Value</th><th>Def OP Value</th><th>Info</th></tr>");

            for(IRankConfig p : sortedRankConfigs)
            {
                IConfigValue value = p.getDefValue();
                list.add("<tr><td>" + p.getName() + "</td><td>");

                String min = value.getMinValueString();
                String max = value.getMaxValueString();
                List<String> variants = value.getVariants();

                if(min != null || max != null || variants != null)
                {
                    list.add("<ul><li>Default: ");
                    list.add(value.getSerializableElement() + "</li>");

                    if(min != null)
                    {
                        list.add("<li>Min: " + min + "</li>");
                    }

                    if(max != null)
                    {
                        list.add("<li>Max: " + max + "</li>");
                    }

                    if(variants != null)
                    {
                        list.add("<li>Variants:<ul>");
                        variants = new ArrayList<>(variants);
                        Collections.sort(variants, LMStringUtils.IGNORE_CASE_COMPARATOR);

                        for(String s : variants)
                        {
                            list.add("<li>" + s + "</li>");
                        }

                        list.add("</ul></li>");
                    }

                    list.add("</ul>");
                }
                else
                {
                    list.add("Default: " + value.getSerializableElement());
                }

                list.add("</td><td>" + p.getDefOPValue().getSerializableElement() + "</td><td>");

                if(!p.getInfo().isEmpty())
                {
                    String[] info = p.getInfo().split("\n");

                    if(info.length > 1)
                    {
                        Arrays.sort(info);
                        list.add("<ul>");
                        for(String s1 : info)
                        {
                            list.add("<li>" + s1 + "</li>");
                        }
                        list.add("</ul>");
                    }
                    else
                    {
                        list.add(info[0]);
                    }
                }

                list.add("</td></tr>");
            }

            list.add("</table></body></html>");
            LMFileUtils.save(new File(LMUtils.folderLocal, "ftbu/all_configs.html"), list);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
}