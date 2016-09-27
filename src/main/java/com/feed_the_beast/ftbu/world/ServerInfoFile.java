package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.FTBLibLang;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.info.InfoPage;
import com.feed_the_beast.ftbl.lib.util.LMServerUtils;
import com.feed_the_beast.ftbl.lib.util.LMStringUtils;
import com.feed_the_beast.ftbl.lib.util.LMUtils;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.FTBUTops;
import com.feed_the_beast.ftbu.api.EventFTBUServerInfo;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.api.ILeaderboardDataProvider;
import com.feed_the_beast.ftbu.api.ILeaderboardRegistry;
import com.feed_the_beast.ftbu.api_impl.FTBUtilitiesAPI_Impl;
import com.feed_the_beast.ftbu.config.FTBUConfigBackups;
import com.feed_the_beast.ftbu.config.FTBUConfigGeneral;
import com.feed_the_beast.ftbu.world.backups.Backups;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatBase;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.server.command.CommandTreeBase;
import net.minecraftforge.server.permission.PermissionAPI;

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

    public ServerInfoFile(IForgePlayer self)
    {
        super(CachedInfo.main.getName());
        setTitle(CachedInfo.main.getTitle());

        MinecraftServer server = LMServerUtils.getServer();

        boolean isDedi = server.isDedicatedServer();
        boolean isOP = !isDedi || PermissionAPI.hasPermission(self.getPlayer(), FTBUPermissions.DISPLAY_ADMIN_INFO);

        copyFrom(CachedInfo.main);

        List<IForgePlayer> players = new ArrayList<>();
        players.addAll(FTBLibIntegration.API.getUniverse().getPlayers());

        if(FTBUConfigGeneral.AUTO_RESTART.getBoolean())
        {
            println(FTBULang.TIMER_RESTART.textComponent(LMStringUtils.getTimeString(FTBUUniverseData.get(self.getUniverse()).restartMillis - System.currentTimeMillis())));
        }

        if(FTBUConfigBackups.ENABLED.getBoolean())
        {
            println(FTBULang.TIMER_BACKUP.textComponent(LMStringUtils.getTimeString(Backups.INSTANCE.nextBackup - System.currentTimeMillis())));
        }

        if(FTBUConfigGeneral.SERVER_INFO_DIFFICULTY.getBoolean())
        {
            println(FTBLibLang.DIFFICULTY.textComponent(LMStringUtils.firstUppercase(self.getPlayer().worldObj.getDifficulty().toString().toLowerCase())));
        }

        if(FTBUConfigGeneral.SERVER_INFO_MODE.getBoolean())
        {
            println(FTBLibLang.MODE_CURRENT.textComponent(LMStringUtils.firstUppercase(FTBLibIntegration.API.getSharedData(Side.SERVER).getPackMode().getID())));
        }

        InfoPage topsPage = getSub("tops").setTitle(FTBUTops.LANG_TOP_TITLE.textComponent());

        ILeaderboardRegistry leaderboardRegistry = FTBUtilitiesAPI_Impl.INSTANCE.getLeaderboardRegistry();

        for(StatBase stat : leaderboardRegistry.getRegistred())
        {
            InfoPage thisTop = topsPage.getSub(stat.statId).setTitle(leaderboardRegistry.getName(stat));

            Collections.sort(players, leaderboardRegistry.getComparator(stat));

            int size = Math.min(players.size(), 250);

            for(int j = 0; j < size; j++)
            {
                IForgePlayer p = players.get(j);
                Object data = null;
                ILeaderboardDataProvider dataSupplier = leaderboardRegistry.getDataProvider(stat);

                if(dataSupplier != null)
                {
                    data = dataSupplier.getData(p);
                }

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

        MinecraftForge.EVENT_BUS.post(new EventFTBUServerInfo(this, self, isOP));

        InfoPage page = getSub("commands").setTitle(FTBLibLang.COMMANDS.textComponent());
        page.clear();

        try
        {
            for(ICommand c : LMServerUtils.getAllCommands(server, self.getPlayer()))
            {
                try
                {
                    InfoPage cat = page.getSub('/' + c.getCommandName());

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
                        addCommandUsage(self.getPlayer(), list, 0, (CommandTreeBase) c);

                        for(ITextComponent c1 : list)
                        {
                            cat.println(c1);
                        }
                    }
                    else
                    {
                        String usage = c.getCommandUsage(self.getPlayer());

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

        for(String s : FTBUUniverseData.get(self.getUniverse()).listWarps())
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