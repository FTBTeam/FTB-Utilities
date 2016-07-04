package com.feed_the_beast.ftbu.handlers;

import com.feed_the_beast.ftbl.api.ForgePlayer;
import com.feed_the_beast.ftbl.api.ForgeWorldMP;
import com.feed_the_beast.ftbl.api.ServerTickCallback;
import com.feed_the_beast.ftbl.api.events.RegisterFTBCommandsEvent;
import com.feed_the_beast.ftbl.api.events.ReloadEvent;
import com.feed_the_beast.ftbl.util.FTBLib;
import com.feed_the_beast.ftbu.FTBU;
import com.feed_the_beast.ftbu.cmd.CmdAdminHome;
import com.feed_the_beast.ftbu.cmd.CmdBack;
import com.feed_the_beast.ftbu.cmd.CmdBackup;
import com.feed_the_beast.ftbu.cmd.CmdChunks;
import com.feed_the_beast.ftbu.cmd.CmdDelHome;
import com.feed_the_beast.ftbu.cmd.CmdDelWarp;
import com.feed_the_beast.ftbu.cmd.CmdGetRank;
import com.feed_the_beast.ftbu.cmd.CmdHome;
import com.feed_the_beast.ftbu.cmd.CmdInv;
import com.feed_the_beast.ftbu.cmd.CmdRestart;
import com.feed_the_beast.ftbu.cmd.CmdServerInfo;
import com.feed_the_beast.ftbu.cmd.CmdSetHome;
import com.feed_the_beast.ftbu.cmd.CmdSetRank;
import com.feed_the_beast.ftbu.cmd.CmdSetWarp;
import com.feed_the_beast.ftbu.cmd.CmdSpawn;
import com.feed_the_beast.ftbu.cmd.CmdTplast;
import com.feed_the_beast.ftbu.cmd.CmdTrashCan;
import com.feed_the_beast.ftbu.cmd.CmdWarp;
import com.feed_the_beast.ftbu.gui.guide.ServerInfoFile;
import com.feed_the_beast.ftbu.ranks.Ranks;
import com.feed_the_beast.ftbu.world.FTBUPlayerData;
import com.feed_the_beast.ftbu.world.FTBUWorldDataMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class FTBUServerEventHandler
{
    private static final String[] LINK_PREFIXES = {"http://", "https://"};

    private static int getFirstLinkIndex(String s)
    {
        for(String s1 : LINK_PREFIXES)
        {
            int idx = s.indexOf(s1);
            if(idx != -1)
            {
                return idx;
            }
        }

        return -1;
    }

    @SubscribeEvent
    public void onReloaded(ReloadEvent e)
    {
        if(e.world.getSide().isServer())
        {
            ServerInfoFile.CachedInfo.reload();
            Ranks.instance().reload();

            FTBUWorldDataMP.reloadServerBadges();

            if(FTBLib.getServerWorld() != null)
            {
                FTBUChunkEventHandler.instance.markDirty(null);
            }
        }
        else
        {
            FTBU.proxy.onReloadedClient();
        }
    }

    @SubscribeEvent
    public void registerAdminCmds(RegisterFTBCommandsEvent event)
    {
        if(event.isDedi)
        {
            event.add(new CmdRestart());
        }

        event.add(new CmdInv());
        event.add(new CmdSetWarp());
        event.add(new CmdDelWarp());
        event.add(new CmdBackup());
        event.add(new CmdAdminHome());
        event.add(new CmdServerInfo());
        event.add(new CmdTplast());
        event.add(new CmdTrashCan());
        event.add(new CmdBack());
        event.add(new CmdSpawn());
        event.add(new CmdWarp());
        event.add(new CmdHome());
        event.add(new CmdSetHome());
        event.add(new CmdDelHome());
        event.add(new CmdGetRank());
        event.add(new CmdSetRank());
        event.add(new CmdChunks());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatEvent(ServerChatEvent e)
    {
        String[] msg = FTBLib.removeFormatting(e.getMessage()).split(" "); // https://github.com/LatvianModder

        List<String> links = new ArrayList<>();

        for(String s : msg)
        {
            int index = getFirstLinkIndex(s);
            if(index != -1)
            {
                links.add(s.substring(index).trim());
            }
        }

        if(!links.isEmpty())
        {
            final ITextComponent line = new TextComponentString("");
            boolean oneLink = links.size() == 1;

            for(int i = 0; i < links.size(); i++)
            {
                String link = links.get(i);
                ITextComponent c = new TextComponentString(oneLink ? "[ Link ]" : ("[ Link #" + (i + 1) + " ]"));
                c.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(link)));
                c.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link));
                line.appendSibling(c);
                if(!oneLink)
                {
                    line.appendSibling(new TextComponentString(" "));
                }
            }

            line.getStyle().setColor(TextFormatting.GOLD);

            FTBLib.addCallback(new ServerTickCallback()
            {
                @Override
                public void onCallback()
                {
                    for(ForgePlayer p : ForgeWorldMP.inst.getOnlinePlayers())
                    {
                        if(FTBUPlayerData.get(p).toMP().chatLinks.getAsBoolean())
                        {
                            p.getPlayer().addChatMessage(line);
                        }
                    }
                }
            });
        }
    }
}