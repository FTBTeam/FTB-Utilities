package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.IUniverse;
import com.feed_the_beast.ftbl.lib.info.InfoPage;
import com.feed_the_beast.ftbl.lib.internal.FTBLibLang;
import com.feed_the_beast.ftbl.lib.util.LMUtils;
import com.feed_the_beast.ftbl.lib.util.ServerUtils;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.api.guide.ServerInfoEvent;
import com.feed_the_beast.ftbu.config.FTBUConfigBackups;
import com.feed_the_beast.ftbu.config.FTBUConfigGeneral;
import com.feed_the_beast.ftbu.ranks.Ranks;
import com.feed_the_beast.ftbu.world.FTBUUniverseData;
import com.feed_the_beast.ftbu.world.backups.Backups;
import com.google.common.base.Preconditions;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.server.command.CommandTreeBase;
import net.minecraftforge.server.permission.PermissionAPI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerInfoPage
{
    private static final InfoPage CACHED_PAGE = new InfoPage("server_info").setTitle(new TextComponentTranslation("sidebar_button.ftbu.server_info"));

    public static void reloadCachedInfo()
    {
        CACHED_PAGE.clear();
        
        
    }

    public static InfoPage getPageForPlayer(EntityPlayer ep)
    {
        IUniverse universe = FTBLibIntegration.API.getUniverse();
        Preconditions.checkNotNull(universe, "World can't be null!");
        IForgePlayer self = universe.getPlayer(ep);
        Preconditions.checkNotNull(self, "Player can't be null!");

        InfoPage page = new InfoPage(CACHED_PAGE.getName());
        page.setTitle(CACHED_PAGE.getTitle());
        page.copyFrom(CACHED_PAGE);

        MinecraftServer server = ServerUtils.getServer();

        boolean isDedi = server.isDedicatedServer();
        boolean isOP = !isDedi || PermissionAPI.hasPermission(ep, FTBUPermissions.DISPLAY_ADMIN_INFO);
        FTBUUniverseData ftbuUniverseData = FTBUUniverseData.get();

        List<IForgePlayer> players = new ArrayList<>();
        players.addAll(universe.getPlayers());

        if(FTBUConfigGeneral.AUTO_RESTART.getBoolean())
        {
            page.println(FTBULang.TIMER_RESTART.textComponent(StringUtils.getTimeString(ftbuUniverseData.restartMillis - System.currentTimeMillis())));
        }

        if(FTBUConfigBackups.ENABLED.getBoolean())
        {
            page.println(FTBULang.TIMER_BACKUP.textComponent(StringUtils.getTimeString(Backups.INSTANCE.nextBackup - System.currentTimeMillis())));
        }

        if(FTBUConfigGeneral.SERVER_INFO_DIFFICULTY.getBoolean())
        {
            page.println(FTBLibLang.DIFFICULTY.textComponent(StringUtils.firstUppercase(ep.world.getDifficulty().toString().toLowerCase())));
        }

        if(FTBUConfigGeneral.SERVER_INFO_MODE.getBoolean())
        {
            page.println(FTBLibLang.MODE_CURRENT.textComponent(StringUtils.firstUppercase(FTBLibIntegration.API.getServerData().getPackMode().getName())));
        }

        if(FTBUConfigGeneral.SERVER_INFO_ADMIN_QUICK_ACCESS.getBoolean())
        {
            //FIXME: SERVER_INFO_ADMIN_QUICK_ACCESS
        }

        InfoPage page1 = page.getSub("leaderboards").setTitle(FTBULeaderboards.LANG_LEADERBOARD_TITLE.textComponent());

        for(Leaderboard leaderboard : FTBUCommon.LEADERBOARDS)
        {
            InfoPage thisTop = page1.getSub(leaderboard.stat.statId).setTitle(leaderboard.name);
            Collections.sort(players, leaderboard.comparator);

            int size = Math.min(players.size(), 250);

            for(int j = 0; j < size; j++)
            {
                IForgePlayer p = players.get(j);
                Object data = leaderboard.data.getData(p);

                if(data == null)
                {
                    data = "[null]";
                }

                StringBuilder sb = new StringBuilder();
                sb.append('[');
                sb.append(j + 1);
                sb.append(']');
                sb.append(' ');
                sb.append(p.getName());
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
                    c.appendSibling(ServerUtils.getChatComponent(data));
                }

                thisTop.println(c);
            }
        }

        MinecraftForge.EVENT_BUS.post(new ServerInfoEvent(page, self, isOP));

        page1 = page.getSub("commands").setTitle(FTBLibLang.COMMANDS.textComponent());

        try
        {
            for(ICommand c : ServerUtils.getAllCommands(server, ep))
            {
                try
                {
                    addCommandUsage(ep, page1.getSub(c.getName()), 0, c);
                }
                catch(Exception ex1)
                {
                    ITextComponent cc = new TextComponentString('/' + c.getName());
                    cc.getStyle().setColor(TextFormatting.DARK_RED);
                    page1.getSub('/' + c.getName()).setTitle(cc).println("Errored");

                    if(LMUtils.DEV_ENV)
                    {
                        ex1.printStackTrace();
                    }
                }
            }

            page1.sort(true);
        }
        catch(Exception ex)
        {
            page1.println("Failed to load commands");
        }

        if(PermissionAPI.hasPermission(ep, FTBUPermissions.DISPLAY_PERMISSIONS))
        {
            page.addSub(Ranks.INFO_PAGE);
        }

        page.cleanup();
        page.sort(false);
        return page;
    }

    private static void addCommandUsage(ICommandSender sender, InfoPage page, int level, ICommand c)
    {
        page.println('/' + c.getName());

        for(String s : c.getAliases())
        {
            page.println('/' + s);
        }

        page.println(null);

        for(String s : c.getUsage(sender).split("\n"))
        {
            if(s.indexOf('%') != -1 || s.indexOf('/') != -1)
            {
                page.println(new TextComponentTranslation("commands.generic.usage", s));
            }
            else
            {
                page.println(new TextComponentTranslation("commands.generic.usage", new TextComponentTranslation(s)));
            }
        }

        if(c instanceof CommandTreeBase)
        {
            CommandTreeBase treeCommand = (CommandTreeBase) c;

            for(ICommand command : treeCommand.getSubCommands())
            {
                addCommandUsage(sender, page.getSub(command.getName()), level + 1, command);
            }
        }
    }
}