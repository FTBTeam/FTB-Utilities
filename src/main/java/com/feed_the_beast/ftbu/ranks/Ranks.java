package com.feed_the_beast.ftbu.ranks;

import com.feed_the_beast.ftbl.api.IRankConfig;
import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbl.lib.info.InfoPage;
import com.feed_the_beast.ftbl.lib.internal.FTBLibLang;
import com.feed_the_beast.ftbl.lib.util.LMFileUtils;
import com.feed_the_beast.ftbl.lib.util.LMJsonUtils;
import com.feed_the_beast.ftbl.lib.util.LMServerUtils;
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
import com.mojang.authlib.GameProfile;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.server.permission.DefaultPermissionHandler;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Ranks
{
    private static final Map<String, Rank> RANKS = new LinkedHashMap<>();
    private static final Collection<String> RANK_NAMES = new ArrayList<>();
    private static final Map<UUID, IRank> PLAYER_MAP = new HashMap<>();
    public static final InfoPage INFO_PAGE = new InfoPage("ranks_info").setTitle(FTBLibLang.ALL_PERMISSIONS.textComponent());
    private static IRank defaultPlayerRank, defaultOPRank;

    @Nullable
    public static IRank getRank(String id, @Nullable IRank nullrank)
    {
        if(id.equals(DefaultPlayerRank.INSTANCE.getName()))
        {
            return DefaultPlayerRank.INSTANCE;
        }
        else if(id.equals(DefaultOPRank.INSTANCE.getName()))
        {
            return DefaultOPRank.INSTANCE;
        }

        IRank r = RANKS.get(id);
        return r == null ? nullrank : r;
    }

    public static IRank getRank(GameProfile profile)
    {
        IRank r = FTBUConfigRanks.ENABLED.getBoolean() ? PLAYER_MAP.get(profile.getId()) : null;
        return (r == null) ? (LMServerUtils.isOP(profile) ? defaultOPRank : defaultPlayerRank) : r;
    }

    public static void setRank(UUID id, IRank r)
    {
        PLAYER_MAP.put(id, r);
        saveRanks();
    }

    public static Collection<String> getRankNames()
    {
        return RANK_NAMES;
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
        RANK_NAMES.clear();

        if(FTBUConfigRanks.ENABLED.getBoolean())
        {
            JsonElement e = LMJsonUtils.fromJson(new File(LMUtils.folderLocal, "ftbu/ranks.json"));

            if(e.isJsonObject())
            {
                JsonObject o = e.getAsJsonObject();

                if(o.has("default_rank"))
                {
                    LMFileUtils.delete(new File(LMUtils.folderLocal, "ftbu/readme.txt"));
                    LMFileUtils.delete(new File(LMUtils.folderLocal, "ftbu/ranks_example.json"));
                    LMFileUtils.delete(new File(LMUtils.folderLocal, "ftbu/default_rank_config.json"));
                }
                else if(o.has("default_ranks") && o.has("ranks"))
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
                    defaultPlayerRank = getRank(dr.get("player").getAsString(), DefaultPlayerRank.INSTANCE);
                    defaultOPRank = getRank(dr.get("op").getAsString(), DefaultOPRank.INSTANCE);
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
            }
        }

        if(defaultPlayerRank == null)
        {
            Rank r = new Rank("player");
            r.setParent(DefaultPlayerRank.INSTANCE);
            RANKS.put(r.getName(), r);
            defaultPlayerRank = r;
        }

        if(defaultOPRank == null)
        {
            Rank r = new Rank("op");
            r.setParent(DefaultOPRank.INSTANCE);
            RANKS.put(r.getName(), r);
            defaultOPRank = r;
        }

        RANK_NAMES.addAll(RANKS.keySet());
        RANK_NAMES.remove(DefaultPlayerRank.INSTANCE.getName());
        RANK_NAMES.remove(DefaultOPRank.INSTANCE.getName());

        saveRanks();
    }

    private static void saveRanks()
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

    static boolean checkCommandPermission(MinecraftServer server, ICommandSender sender, ICommand parent, String permission)
    {
        if(sender instanceof EntityPlayerMP)
        {
            Event.Result result = FTBUtilitiesAPI_Impl.INSTANCE.getRank(((EntityPlayerMP) sender).getGameProfile()).hasPermission(permission);
            return result == Event.Result.DEFAULT ? parent.checkPermission(server, sender) : (result == Event.Result.ALLOW);
        }

        return parent.checkPermission(server, sender);
    }

    public static void generateExampleFiles()
    {
        List<NodeEntry> allNodes = new ArrayList<>();

        for(NodeEntry node : FTBUCommon.CUSTOM_PERM_PREFIX_REGISTRY)
        {
            allNodes.add(new NodeEntry(node.getName() + "*", node.getLevel(), node.getDescription()));
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
                allNodes.add(new NodeEntry(s, level, desc));
            }
        }

        Collections.sort(allNodes, LMStringUtils.ID_COMPARATOR);
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

            for(NodeEntry entry : allNodes)
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

        INFO_PAGE.clear();
        ITextComponent txt = new TextComponentString("");
        ITextComponent txt1 = new TextComponentString("NONE");
        txt1.getStyle().setColor(TextFormatting.DARK_RED);
        txt.appendSibling(txt1);
        txt.appendText(" | ");
        txt1 = new TextComponentString("ALL");
        txt1.getStyle().setColor(TextFormatting.DARK_GREEN);
        txt.appendSibling(txt1);
        txt.appendText(" | ");
        txt1 = new TextComponentString("OP");
        txt1.getStyle().setColor(TextFormatting.BLUE);
        txt.appendSibling(txt1);
        INFO_PAGE.println(txt);
        INFO_PAGE.println(null);

        for(NodeEntry node : allNodes)
        {
            txt = new TextComponentString(node.getName());

            switch(node.getLevel())
            {
                case ALL:
                    txt.getStyle().setColor(TextFormatting.DARK_GREEN);
                    break;
                case OP:
                    txt.getStyle().setColor(TextFormatting.BLUE);
                    break;
                default:
                    txt.getStyle().setColor(TextFormatting.DARK_RED);
            }

            if(node.getDescription() != null && !node.getDescription().isEmpty())
            {
                txt.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(node.getDescription())));
            }

            INFO_PAGE.println(txt);
        }

        /*
        page = getSub("rank_configs").setTitle(new TextComponentString("Rank Configs")); //TODO: Lang

        for(IRankConfig key : RankConfigAPI.getRegisteredRankConfigs().values())
        {
            page.println(key.getName() + ": " + RankConfigAPI.getRankConfig(ep, key).getSerializableElement());
        }

        Collections.sort(page.getText(), (o1, o2) -> o1.getUnformattedText().compareTo(o2.getUnformattedText()));
        */
    }
}