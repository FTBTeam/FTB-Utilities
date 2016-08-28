package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.FTBLibLang;
import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.cmd.ICustomCommandInfo;
import com.feed_the_beast.ftbl.api.info.impl.InfoPage;
import com.feed_the_beast.ftbl.api.permissions.PermissionAPI;
import com.feed_the_beast.ftbl.api.permissions.context.PlayerContext;
import com.feed_the_beast.ftbl.util.FTBLib;
import com.feed_the_beast.ftbu.FTBULang;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.FTBUTops;
import com.feed_the_beast.ftbu.api.EventFTBUServerInfo;
import com.feed_the_beast.ftbu.api.TopRegistry;
import com.feed_the_beast.ftbu.config.FTBUConfigBackups;
import com.feed_the_beast.ftbu.config.FTBUConfigGeneral;
import com.feed_the_beast.ftbu.config.FTBUConfigWorld;
import com.feed_the_beast.ftbu.world.backups.Backups;
import com.feed_the_beast.ftbu.world.data.FTBUPlayerData;
import com.feed_the_beast.ftbu.world.data.FTBUWorldData;
import com.latmod.lib.util.LMStringUtils;
import net.minecraft.command.ICommand;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatBase;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
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

        MinecraftServer server = FTBLib.getServer();

        boolean isDedi = server.isDedicatedServer();
        boolean isOP = !isDedi || PermissionAPI.hasPermission(self.getProfile(), FTBUPermissions.DISPLAY_ADMIN_INFO, false, new PlayerContext(self.getPlayer()));

        copyFrom(CachedInfo.main);

        List<IForgePlayer> players = new ArrayList<>();
        players.addAll(FTBLibAPI.get().getWorld().getPlayers());

        if(FTBUConfigWorld.auto_restart.getAsBoolean())
        {
            println(FTBULang.TIMER_RESTART.textComponent(LMStringUtils.getTimeString(FTBUWorldData.getW(self.getWorld()).toMP().restartMillis - System.currentTimeMillis())));
        }

        if(FTBUConfigBackups.enabled.getAsBoolean())
        {
            println(FTBULang.TIMER_BACKUP.textComponent(LMStringUtils.getTimeString(Backups.INSTANCE.nextBackup - System.currentTimeMillis())));
        }

        if(FTBUConfigGeneral.server_info_difficulty.getAsBoolean())
        {
            println(FTBLibLang.DIFFICULTY.textComponent(LMStringUtils.firstUppercase(self.getPlayer().worldObj.getDifficulty().toString().toLowerCase())));
        }

        if(FTBUConfigGeneral.server_info_mode.getAsBoolean())
        {
            println(FTBLibLang.MODE_CURRENT.textComponent(LMStringUtils.firstUppercase(FTBLibAPI.get().getSharedData(Side.SERVER).getMode().getID())));
        }

        InfoPage topsPage = getSub("tops").setTitle(FTBUTops.LANG_TOP_TITLE.textComponent());

        for(StatBase stat : TopRegistry.INSTANCE.getKeys())
        {
            InfoPage thisTop = topsPage.getSub(stat.statId).setTitle(TopRegistry.INSTANCE.getName(stat));

            Collections.sort(players, TopRegistry.INSTANCE.getComparator(stat));

            int size = Math.min(players.size(), 250);

            for(int j = 0; j < size; j++)
            {
                IForgePlayer p = players.get(j);
                Object data = null;
                TopRegistry.DataSupplier dataSupplier = TopRegistry.INSTANCE.getDataSuppier(stat);

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
                    c.appendSibling(FTBLib.getChatComponent(data));
                }

                thisTop.println(c);
            }
        }

        MinecraftForge.EVENT_BUS.post(new EventFTBUServerInfo(this, self, isOP));

        InfoPage page = getSub("commands").setTitle(FTBLibLang.COMMANDS.textComponent());
        page.clear();

        try
        {
            for(ICommand c : FTBLib.getAllCommands(server, self.getPlayer()))
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

                    if(c instanceof ICustomCommandInfo)
                    {
                        List<ITextComponent> list = new ArrayList<>();
                        ((ICustomCommandInfo) c).addInfo(server, self.getPlayer(), list);

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

                    if(FTBLib.DEV_ENV)
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

        for(String s : FTBUWorldData.getW(self.getWorld()).toMP().listWarps())
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
}