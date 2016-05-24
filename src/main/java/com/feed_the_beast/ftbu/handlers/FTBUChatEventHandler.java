package com.feed_the_beast.ftbu.handlers;

import com.feed_the_beast.ftbl.api.ForgePlayer;
import com.feed_the_beast.ftbl.api.ForgeWorldMP;
import com.feed_the_beast.ftbl.api.ServerTickCallback;
import com.feed_the_beast.ftbl.util.FTBLib;
import com.feed_the_beast.ftbu.world.FTBUPlayerData;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

public class FTBUChatEventHandler
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

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatEvent(net.minecraftforge.event.ServerChatEvent e)
    {
        String[] msg = FTBLib.removeFormatting(e.getMessage()).split(" "); // https://github.com/LatvianModder

        ArrayList<String> links = new ArrayList<>();

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
                        if(FTBUPlayerData.get(p.toMP()).getFlag(FTBUPlayerData.CHAT_LINKS))
                        {
                            p.getPlayer().addChatMessage(line);
                        }
                    }
                }
            });
        }
    }
}