package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.FTBLibLang;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.info.IGuiInfoPage;
import com.feed_the_beast.ftbl.api.rankconfig.IRankConfig;
import com.feed_the_beast.ftbl.api.rankconfig.RankConfigAPI;
import com.feed_the_beast.ftbl.lib.info.InfoPage;
import com.feed_the_beast.ftbl.lib.util.LMServerUtils;
import com.feed_the_beast.ftbl.lib.util.LMStringUtils;
import com.feed_the_beast.ftbl.lib.util.LMUtils;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.FTBULeaderboards;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.api.Leaderboard;
import com.feed_the_beast.ftbu.api.guide.ServerInfoEvent;
import com.feed_the_beast.ftbu.api_impl.FTBUtilitiesAPI_Impl;
import com.feed_the_beast.ftbu.client.FTBUActions;
import com.feed_the_beast.ftbu.config.FTBUConfigBackups;
import com.feed_the_beast.ftbu.config.FTBUConfigGeneral;
import com.feed_the_beast.ftbu.world.backups.Backups;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.server.command.CommandTreeBase;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.IContext;
import net.minecraftforge.server.permission.context.PlayerContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ServerInfoFile extends InfoPage
{
    public static class CachedInfo
    {
        public static final InfoPage main = new InfoPage("server_info"); //TODO: Lang

        public static void reload()
        {
            main.clear();

            /*
            //categoryServer.println(new ChatComponentTranslation("ftbl:worldID", FTBWorld.server.getWorldID()));
            File file = new File(FTBLib.folderLocal, "guide/");
            if(file.exists())
            {
            }
            
            main.cleanup();
            */
        }
    }

    public ServerInfoFile(EntityPlayerMP ep)
    {
        super(CachedInfo.main.getName());
        setTitle(new TextComponentTranslation(FTBUActions.SERVER_INFO.getPath()));
        IForgePlayer self = FTBLibIntegration.API.getUniverse().getPlayer(ep);

        MinecraftServer server = LMServerUtils.getServer();

        boolean isDedi = server.isDedicatedServer();
        boolean isOP = !isDedi || PermissionAPI.hasPermission(ep, FTBUPermissions.DISPLAY_ADMIN_INFO);
        FTBUUniverseData ftbuUniverseData = FTBUUniverseData.get();

        copyFrom(CachedInfo.main);

        List<IForgePlayer> players = new ArrayList<>();
        players.addAll(FTBLibIntegration.API.getUniverse().getPlayers());

        if(PermissionAPI.hasPermission(ep, FTBUPermissions.DISPLAY_RANK))
        {
            println("Your Rank: " + FTBUtilitiesAPI_Impl.INSTANCE.getRank(ep.getGameProfile())); //TODO: Lang
        }

        if(FTBUConfigGeneral.AUTO_RESTART.getBoolean())
        {
            println(FTBULang.TIMER_RESTART.textComponent(LMStringUtils.getTimeString(ftbuUniverseData.restartMillis - System.currentTimeMillis())));
        }

        if(FTBUConfigBackups.ENABLED.getBoolean())
        {
            println(FTBULang.TIMER_BACKUP.textComponent(LMStringUtils.getTimeString(Backups.INSTANCE.nextBackup - System.currentTimeMillis())));
        }

        if(FTBUConfigGeneral.SERVER_INFO_DIFFICULTY.getBoolean())
        {
            println(FTBLibLang.DIFFICULTY.textComponent(LMStringUtils.firstUppercase(ep.worldObj.getDifficulty().toString().toLowerCase())));
        }

        if(FTBUConfigGeneral.SERVER_INFO_MODE.getBoolean())
        {
            println(FTBLibLang.MODE_CURRENT.textComponent(LMStringUtils.firstUppercase(FTBLibIntegration.API.getSharedData(Side.SERVER).getPackMode().getID())));
        }

        IGuiInfoPage page = getSub("leaderboards").setTitle(FTBULeaderboards.LANG_TITLE.textComponent());

        for(Leaderboard leaderboard : FTBUtilitiesAPI_Impl.INSTANCE.LEADERBOARDS.values())
        {
            IGuiInfoPage thisTop = page.getSub(leaderboard.getStat().statId).setTitle(leaderboard.getName());
            Collections.sort(players, leaderboard.getComparator());

            int size = Math.min(players.size(), 250);

            for(int j = 0; j < size; j++)
            {
                IForgePlayer p = players.get(j);
                Object data = leaderboard.getData(p);

                if(data == null)
                {
                    data = "[null]";
                }

                StringBuilder sb = new StringBuilder();
                sb.append('[');
                sb.append(j + 1);
                sb.append(']');
                sb.append(' ');
                sb.append(p.getProfile().getName());
                sb.append(':');
                sb.append(' ');
                if(!(data instanceof ITextComponent))
                {
                    sb.append(data);
                }

                ITextComponent c = new TextComponentString(sb.toString());
                if(p == self)
                {
                    c.getStyle().setColor(TextFormatting.DARK_GREEN);
                }
                else if(j < 3)
                {
                    c.getStyle().setColor(TextFormatting.LIGHT_PURPLE);
                }
                if(data instanceof ITextComponent)
                {
                    c.appendSibling(LMServerUtils.getChatComponent(data));
                }

                thisTop.println(c);
            }
        }

        MinecraftForge.EVENT_BUS.post(new ServerInfoEvent(this, self, isOP));

        page = getSub("commands").setTitle(FTBLibLang.COMMANDS.textComponent());

        try
        {
            for(ICommand c : LMServerUtils.getAllCommands(server, ep))
            {
                try
                {
                    IGuiInfoPage cat = page.getSub('/' + c.getCommandName());

                    List<String> al = c.getCommandAliases();
                    if(!al.isEmpty())
                    {
                        for(String s : al)
                        {
                            cat.println('/' + s);
                        }
                    }

                    if(c instanceof CommandTreeBase)
                    {
                        List<ITextComponent> list = new ArrayList<>();
                        list.add(new TextComponentString('/' + c.getCommandName()));
                        list.add(null);
                        addCommandUsage(ep, list, 0, (CommandTreeBase) c);

                        for(ITextComponent c1 : list)
                        {
                            cat.println(c1);
                        }
                    }
                    else
                    {
                        String usage = c.getCommandUsage(ep);

                        if(usage.indexOf('\n') != -1)
                        {
                            String[] usageL = usage.split("\n");
                            for(String s1 : usageL)
                            {
                                cat.println(s1);
                            }
                        }
                        else
                        {
                            if(usage.indexOf('%') != -1 || usage.indexOf('/') != -1)
                            {
                                cat.println(new TextComponentString(usage));
                            }
                            else
                            {
                                cat.println(new TextComponentTranslation(usage));
                            }
                        }
                    }
                }
                catch(Exception ex1)
                {
                    ITextComponent cc = new TextComponentString('/' + c.getCommandName());
                    cc.getStyle().setColor(TextFormatting.DARK_RED);
                    page.getSub('/' + c.getCommandName()).setTitle(cc).println("Errored");

                    if(LMUtils.DEV_ENV)
                    {
                        ex1.printStackTrace();
                    }
                }
            }
        }
        catch(Exception ex)
        {
            page.println("Failed to load commands");
        }

        page = getSub("warps").setTitle(new TextComponentString("Warps")); //TODO: LANG
        ITextComponent t;

        for(String s : ftbuUniverseData.listWarps())
        {
            t = new TextComponentString(s);
            t.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ftb warp " + s));
            page.println(t);
        }

        page = getSub("homes").setTitle(new TextComponentString("Homes")); //TODO: LANG

        for(String s : FTBUPlayerData.get(self).listHomes())
        {
            t = new TextComponentString(s);
            t.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ftb home " + s));
            page.println(t);
        }

        if(PermissionAPI.hasPermission(ep, FTBUPermissions.DISPLAY_PERMISSIONS))
        {
            page = getSub("permissions").setTitle(FTBLibLang.MY_PERMISSIONS.textComponent());

            IContext context = new PlayerContext(ep);

            for(String s : PermissionAPI.getPermissionHandler().getRegisteredNodes())
            {
                if(PermissionAPI.hasPermission(self.getProfile(), s, context))
                {
                    page.println(s);
                }
            }

            Collections.sort(page.getText(), (o1, o2) -> o1.getUnformattedText().compareTo(o2.getUnformattedText()));

            page = getSub("rank_configs").setTitle(new TextComponentString("Rank Configs")); //TODO: Lang

            for(IRankConfig key : RankConfigAPI.getRegistredRankConfigs().values())
            {
                page.println(key.getName() + ": " + RankConfigAPI.getRankConfig(ep, key).getSerializableElement());
            }

            Collections.sort(page.getText(), (o1, o2) -> o1.getUnformattedText().compareTo(o2.getUnformattedText()));
        }

        cleanup();
        sortAll();
    }

    private void addCommandUsage(ICommandSender sender, List<ITextComponent> list, int level, CommandTreeBase treeCommand)
    {
        for(ICommand c : treeCommand.getSubCommands())
        {
            if(c instanceof CommandTreeBase)
            {
                list.add(tree(new TextComponentString('/' + c.getCommandName()), level));
                addCommandUsage(sender, list, level + 1, (CommandTreeBase) c);
            }
            else
            {
                String usage = c.getCommandUsage(sender);
                if(usage.indexOf('/') != -1 || usage.indexOf('%') != -1)
                {
                    list.add(tree(new TextComponentString(usage), level));
                }
                else
                {
                    list.add(tree(new TextComponentTranslation(usage), level));
                }
            }
        }
    }

    private ITextComponent tree(ITextComponent sibling, int level)
    {
        if(level == 0)
        {
            return sibling;
        }
        char[] chars = new char[level * 2];
        Arrays.fill(chars, ' ');
        return new TextComponentString(new String(chars)).appendSibling(sibling);
    }
}