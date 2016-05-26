package com.feed_the_beast.ftbu.api.guide;

import com.feed_the_beast.ftbl.FTBLibLang;
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
import com.feed_the_beast.ftbu.api.EventFTBUServerInfo;
import com.feed_the_beast.ftbu.config.FTBUConfigGeneral;
import com.feed_the_beast.ftbu.config.FTBUConfigModules;
import com.feed_the_beast.ftbu.world.Backups;
import com.feed_the_beast.ftbu.world.FTBUPlayerData;
import com.feed_the_beast.ftbu.world.FTBUWorldData;
import com.google.gson.JsonPrimitive;
import latmod.lib.LMFileUtils;
import latmod.lib.LMStringUtils;
import net.minecraft.command.ICommand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ServerInfoFile extends InfoPage
{
    public static class CachedInfo
    {
        public static final InfoPage main = new InfoPage("ServerInfo").setTitle(new TextComponentTranslation("player_action.ftbu:server_info"));

        public static void reload()
        {
            main.clear();

            //categoryServer.println(new ChatComponentTranslation("ftbl:worldID", FTBWorld.server.getWorldID()));

            File file = new File(FTBLib.folderLocal, "guide/");
            if(file.exists() && file.isDirectory())
            {
                File[] f = file.listFiles();
                if(f != null && f.length > 0)
                {
                    Arrays.sort(f, LMFileUtils.fileComparator);
                    for(int i = 0; i < f.length; i++)
                    {
                        //FIXME: loadFromFiles(main, f[i]);
                    }
                }
            }

            file = new File(FTBLib.folderLocal, "guide_cover.txt");
            if(file.exists() && file.isFile())
            {
                try
                {
                    String text = LMFileUtils.loadAsText(file);
                    if(text != null && !text.isEmpty())
                    {
                        main.printlnText(text.replace("\r", ""));
                    }
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
            }

            main.cleanup();
        }
    }

    private List<ForgePlayerMP> players = null;
    private ForgePlayerMP self;
    private InfoPage categoryTops = null;

    public ServerInfoFile(ForgePlayerMP pself)
    {
        super(CachedInfo.main.getID());
        setTitle(CachedInfo.main.getTitleComponent());

        if((self = pself) == null)
        {
            return;
        }
        boolean isDedi = FTBLib.getServer().isDedicatedServer();
        boolean isOP = !isDedi || PermissionAPI.hasPermission(self.getProfile(), FTBUPermissions.display_admin_info, false, Context.EMPTY);

        copyFrom(CachedInfo.main);

        categoryTops = getSub("Tops").setTitle(Top.langTopTitle.textComponent());

        players = new ArrayList<>();
        players.addAll(ForgeWorldMP.inst.getServerPlayers());

        for(ForgePlayerMP p : players)
        {
            p.refreshStats();
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
            println(FTBLibLang.difficulty.textComponent(LMStringUtils.firstUppercase(pself.getPlayer().worldObj.getDifficulty().toString().toLowerCase())));
        }

        if(FTBUConfigGeneral.server_info_mode.getAsBoolean())
        {
            println(FTBLibLang.mode_current.textComponent(LMStringUtils.firstUppercase(ForgeWorldMP.inst.getMode().toString().toLowerCase())));
        }

        for(Top t : Top.registry.values())
        {
            InfoPage thisTop = categoryTops.getSub(t.getID()).setTitle(t.langKey.textComponent());

            Collections.sort(players, t);

            int size = Math.min(players.size(), 250);

            for(int j = 0; j < size; j++)
            {
                ForgePlayerMP p = players.get(j);

                Object data = t.getData(p);
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
            for(ICommand c : FTBLib.getAllCommands(self.getPlayer()))
            {
                try
                {
                    InfoPage cat = new InfoPage('/' + c.getCommandName());

                    List<String> al = c.getCommandAliases();
                    if(al != null && !al.isEmpty())
                    {
                        for(String s : al)
                        {
                            cat.printlnText('/' + s);
                        }
                    }

                    if(c instanceof ICustomCommandInfo)
                    {
                        List<ITextComponent> list = new ArrayList<>();
                        ((ICustomCommandInfo) c).addInfo(list, self.getPlayer());

                        for(ITextComponent c1 : list)
                        {
                            cat.println(c1);
                        }
                    }
                    else
                    {
                        String usage = c.getCommandUsage(self.getPlayer());

                        if(usage != null)
                        {
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

        for(String s : FTBUWorldData.getW(ForgeWorldMP.inst).toMP().warps.list())
        {
            line = new InfoExtendedTextLine(page, new TextComponentString(s));
            line.setClickAction(new ClickAction(ClickActionType.CMD, new JsonPrimitive("warp " + s)));
            page.text.add(line);
        }

        page = getSub("homes").setTitle(new TextComponentString("Homes")); //TODO: LANG

        for(String s : FTBUPlayerData.get(self).toMP().homes.list())
        {
            line = new InfoExtendedTextLine(page, new TextComponentString(s));
            line.setClickAction(new ClickAction(ClickActionType.CMD, new JsonPrimitive("home " + s)));
            page.text.add(line);
        }

        cleanup();
        sortAll();
    }
}