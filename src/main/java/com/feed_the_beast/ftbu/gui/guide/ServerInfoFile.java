package com.feed_the_beast.ftbu.gui.guide;

import com.feed_the_beast.ftbl.FTBLibLang;
import com.feed_the_beast.ftbl.api.ForgePlayer;
import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbl.api.ForgeWorldMP;
import com.feed_the_beast.ftbl.api.cmd.ICustomCommandInfo;
import com.feed_the_beast.ftbl.api.info.InfoExtendedTextLine;
import com.feed_the_beast.ftbl.api.info.InfoPage;
import com.feed_the_beast.ftbl.api.notification.ClickAction;
import com.feed_the_beast.ftbl.api.notification.ClickActionType;
import com.feed_the_beast.ftbl.api.permissions.Context;
import com.feed_the_beast.ftbl.api.permissions.PermissionAPI;
import com.feed_the_beast.ftbl.util.FTBLib;
import com.feed_the_beast.ftbu.FTBULang;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.FTBUTops;
import com.feed_the_beast.ftbu.api.EventFTBUServerInfo;
import com.feed_the_beast.ftbu.api.TopRegistry;
import com.feed_the_beast.ftbu.client.FTBUActions;
import com.feed_the_beast.ftbu.config.FTBUConfigGeneral;
import com.feed_the_beast.ftbu.config.FTBUConfigModules;
import com.feed_the_beast.ftbu.world.Backups;
import com.feed_the_beast.ftbu.world.FTBUPlayerData;
import com.feed_the_beast.ftbu.world.FTBUWorldData;
import com.google.gson.JsonPrimitive;
import com.latmod.lib.util.LMStringUtils;
import net.minecraft.command.ICommand;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatBase;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerInfoFile extends InfoPage
{
    public static class CachedInfo
    {
        public static final InfoPage main = new InfoPage("server_info").setTitle(FTBUActions.SERVER_INFO.displayName);

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

    public ServerInfoFile(@Nonnull ForgePlayerMP self)
    {
        super(CachedInfo.main.getID());
        setTitle(CachedInfo.main.getTitleComponent());

        MinecraftServer server = FTBLib.getServer();

        boolean isDedi = server.isDedicatedServer();
        boolean isOP = !isDedi || PermissionAPI.hasPermission(self.getProfile(), FTBUPermissions.DISPLAY_ADMIN_INFO, false, new Context(self.getPlayer()));

        copyFrom(CachedInfo.main);

        List<ForgePlayerMP> players = new ArrayList<>(ForgeWorldMP.inst.playerMap.size());

        for(ForgePlayer p : ForgeWorldMP.inst.playerMap.values())
        {
            players.add(p.toMP());
        }

        if(FTBUConfigModules.auto_restart.getAsBoolean())
        {
            println(FTBULang.timer_restart.textComponent(LMStringUtils.getTimeString(FTBUWorldData.getW(ForgeWorldMP.inst).toMP().restartMillis - System.currentTimeMillis())));
        }

        if(FTBUConfigModules.backups.getAsBoolean())
        {
            println(FTBULang.timer_backup.textComponent(LMStringUtils.getTimeString(Backups.nextBackup - System.currentTimeMillis())));
        }

        if(FTBUConfigGeneral.server_info_difficulty.getAsBoolean())
        {
            println(FTBLibLang.difficulty.textComponent(LMStringUtils.firstUppercase(self.getPlayer().worldObj.getDifficulty().toString().toLowerCase())));
        }

        if(FTBUConfigGeneral.server_info_mode.getAsBoolean())
        {
            println(FTBLibLang.mode_current.textComponent(LMStringUtils.firstUppercase(ForgeWorldMP.inst.getMode().toString().toLowerCase())));
        }

        InfoPage topsPage = getSub("tops").setTitle(FTBUTops.LANG_TOP_TITLE.textComponent());

        for(StatBase stat : TopRegistry.getKeys())
        {
            InfoPage thisTop = topsPage.getSub(stat.statId).setTitle(stat.getStatName());

            Collections.sort(players, TopRegistry.getComparator(stat));

            int size = Math.min(players.size(), 250);

            for(int j = 0; j < size; j++)
            {
                ForgePlayerMP p = players.get(j);
                Object data = null;
                TopRegistry.DataSupplier dataSupplier = TopRegistry.getDataSuppier(stat);

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

        InfoPage page = getSub("commands").setTitle(FTBLibLang.commands.textComponent());
        page.clear();

        try
        {
            for(ICommand c : FTBLib.getAllCommands(server, self.getPlayer()))
            {
                try
                {
                    InfoPage cat = new InfoPage('/' + c.getCommandName());

                    List<String> al = c.getCommandAliases();
                    if(!al.isEmpty())
                    {
                        for(String s : al)
                        {
                            cat.printlnText('/' + s);
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
                                cat.printlnText(s1);
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

                    cat.setParent(page);
                    page.addSub(cat);
                }
                catch(Exception ex1)
                {
                    ITextComponent cc = new TextComponentString('/' + c.getCommandName());
                    cc.getStyle().setColor(TextFormatting.DARK_RED);
                    page.getSub('/' + c.getCommandName()).setTitle(cc).printlnText("Errored");

                    if(FTBLib.DEV_ENV)
                    {
                        ex1.printStackTrace();
                    }
                }
            }
        }
        catch(Exception ex)
        {
        }

        page = getSub("warps").setTitle(new TextComponentString("Warps")); //TODO: LANG
        InfoExtendedTextLine line;

        for(String s : FTBUWorldData.getW(ForgeWorldMP.inst).toMP().listWarps())
        {
            line = new InfoExtendedTextLine(page, new TextComponentString(s));
            line.setClickAction(new ClickAction(ClickActionType.CMD, new JsonPrimitive("ftb warp " + s)));
            page.text.add(line);
        }

        page = getSub("homes").setTitle(new TextComponentString("Homes")); //TODO: LANG

        for(String s : FTBUPlayerData.get(self).toMP().listHomes())
        {
            line = new InfoExtendedTextLine(page, new TextComponentString(s));
            line.setClickAction(new ClickAction(ClickActionType.CMD, new JsonPrimitive("ftb home " + s)));
            page.text.add(line);
        }

        cleanup();
        sortAll();
    }
}