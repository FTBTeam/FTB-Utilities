package com.feed_the_beast.ftbu.ranks;

import com.feed_the_beast.ftbl.api.IRankConfig;
import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbl.lib.client.DrawableItem;
import com.feed_the_beast.ftbl.lib.guide.GuidePage;
import com.feed_the_beast.ftbl.lib.internal.FTBLibLang;
import com.feed_the_beast.ftbl.lib.util.FileUtils;
import com.feed_the_beast.ftbl.lib.util.JsonUtils;
import com.feed_the_beast.ftbl.lib.util.LMUtils;
import com.feed_the_beast.ftbl.lib.util.ServerUtils;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
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
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.server.permission.DefaultPermissionHandler;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
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
    public static final GuidePage INFO_PAGE = new GuidePage("ranks_info").setTitle(FTBLibLang.ALL_PERMISSIONS.textComponent()).setIcon(new DrawableItem(new ItemStack(Items.DIAMOND_SWORD)));
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
        return (r == null) ? (ServerUtils.isOP(profile) ? defaultOPRank : defaultPlayerRank) : r;
    }

    public static void addRank(Rank rank)
    {
        RANKS.put(rank.getName(), rank);
        updateRankNames();
        saveRanks();
    }

    public static void setRank(UUID id, @Nullable IRank r)
    {
        if(r == null)
        {
            PLAYER_MAP.remove(id);
        }
        else
        {
            PLAYER_MAP.put(id, r);
        }

        saveRanks();
    }

    public static Collection<String> getRankNames()
    {
        return RANK_NAMES;
    }

    public static void updateRankNames()
    {
        RANK_NAMES.clear();
        RANK_NAMES.addAll(RANKS.keySet());
        RANK_NAMES.add("none");
        RANK_NAMES.remove(DefaultPlayerRank.INSTANCE.getName());
        RANK_NAMES.remove(DefaultOPRank.INSTANCE.getName());
    }

    public static void reload()
    {
        FTBUFinals.LOGGER.info("Loading ranks..");

        RANKS.clear();
        RANKS.put(DefaultPlayerRank.INSTANCE.getName(), DefaultPlayerRank.INSTANCE);
        RANKS.put(DefaultOPRank.INSTANCE.getName(), DefaultOPRank.INSTANCE);
        PLAYER_MAP.clear();
        defaultPlayerRank = null;
        defaultOPRank = null;

        if(FTBUConfigRanks.ENABLED.getBoolean())
        {
            JsonElement e = JsonUtils.fromJson(new File(LMUtils.folderLocal, "ftbu/ranks.json"));

            if(e.isJsonObject())
            {
                JsonObject o = e.getAsJsonObject();

                if(o.has("default_rank"))
                {
                    FileUtils.delete(new File(LMUtils.folderLocal, "ftbu/readme.txt"));
                    FileUtils.delete(new File(LMUtils.folderLocal, "ftbu/ranks_example.json"));
                    FileUtils.delete(new File(LMUtils.folderLocal, "ftbu/default_rank_config.json"));
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
                    defaultPlayerRank = dr.has("player") ? RANKS.get(dr.get("player").getAsString()) : null;
                    defaultOPRank = dr.has("op") ? RANKS.get(dr.get("op").getAsString()) : null;
                }
            }

            try
            {
                e = JsonUtils.fromJson(new File(LMUtils.folderLocal, "ftbu/player_ranks.json"));

                if(e.isJsonObject())
                {
                    for(Map.Entry<String, JsonElement> entry : e.getAsJsonObject().entrySet())
                    {
                        UUID id = StringUtils.fromString(entry.getKey());
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
            r.parent = DefaultPlayerRank.INSTANCE;
            RANKS.put(r.getName(), r);
            defaultPlayerRank = r;
        }

        if(defaultOPRank == null)
        {
            Rank r = new Rank("op");
            r.parent = DefaultOPRank.INSTANCE;
            r.syntax = "<" + TextFormatting.DARK_GREEN + "$name" + TextFormatting.RESET + "> ";
            RANKS.put(r.getName(), r);
            defaultOPRank = r;
        }

        updateRankNames();
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

        JsonUtils.toJson(new File(LMUtils.folderLocal, "ftbu/ranks.json"), o);

        final JsonObject o2 = new JsonObject();
        PLAYER_MAP.forEach((key, value) -> o2.add(StringUtils.fromUUID(key), new JsonPrimitive(value.getName())));
        JsonUtils.toJson(new File(LMUtils.folderLocal, "ftbu/player_ranks.json"), o2);
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

        Collections.sort(allNodes, StringUtils.ID_COMPARATOR);
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
            FileUtils.save(new File(LMUtils.folderLocal, "ftbu/all_permissions.html"), list);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        List<IRankConfig> sortedRankConfigs = new ArrayList<>(FTBLibIntegration.API.getRankConfigRegistry().values());
        Collections.sort(sortedRankConfigs, StringUtils.ID_COMPARATOR);

        try
        {
            list.clear();
            list.add("<html><head><title>Rank Configs</title>");
            list.add("<style>table{font-family: arial, sans-serif;border-collapse: collapse;}td,th{border:1px solid #666666;text-align: left;padding:8px;}</style>");
            list.add("</head><body><h1>Rank Configs</h1><table>");
            list.add("<tr><th>Rank Config</th><th>Def Value</th><th>Def OP Value</th><th>Info</th></tr>");

            List<String> infoList = new ArrayList<>();

            for(IRankConfig p : sortedRankConfigs)
            {
                IConfigValue value = p.getDefValue();
                list.add("<tr><td>" + p.getName() + "</td><td>");

                value.addInfo(p, infoList);
                List<String> variants = value.getVariants();

                if(!infoList.isEmpty() || !variants.isEmpty())
                {
                    list.add("<ul><li>Default: " + value.getSerializableElement() + "</li>");
                    list.add("<ul><li>OP Default: " + p.getDefOPValue().getSerializableElement() + "</li>");

                    for(String s : infoList)
                    {
                        list.add("<li>" + StringUtils.removeFormatting(s) + "</li>");
                    }

                    infoList.clear();

                    if(!variants.isEmpty())
                    {
                        list.add("<li>Variants:<ul>");
                        variants = new ArrayList<>(variants);
                        Collections.sort(variants, StringUtils.IGNORE_CASE_COMPARATOR);

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

                list.add("</td><td>");

                String info = p.getInfo();

                if(!info.isEmpty())
                {
                    String[] s = info.split("\\\\n");

                    if(s.length > 1)
                    {
                        list.add("<ul>");
                        for(String s1 : s)
                        {
                            list.add("<li>" + s1 + "</li>");
                        }
                        list.add("</ul>");
                    }
                    else
                    {
                        list.add(info);
                    }
                }

                list.add("</td></tr>");
            }

            list.add("</table></body></html>");
            FileUtils.save(new File(LMUtils.folderLocal, "ftbu/all_configs.html"), list);

            list.clear();
            list.add(PermissionAPI.getPermissionHandler().getRegisteredNodes().size() + " nodes in total");
            list.add("");

            for(String node : PermissionAPI.getPermissionHandler().getRegisteredNodes())
            {
                list.add(node + ": " + DefaultPermissionHandler.INSTANCE.getDefaultPermissionLevel(node));
            }

            Collections.sort(list);
            FileUtils.save(new File(LMUtils.folderLocal, "ftbu/all_permissions_full_list.txt"), list);
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