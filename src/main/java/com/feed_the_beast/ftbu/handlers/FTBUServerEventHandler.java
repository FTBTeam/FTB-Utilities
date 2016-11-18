package com.feed_the_beast.ftbu.handlers;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.util.LMUtils;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.config.FTBUConfigGeneral;
import com.feed_the_beast.ftbu.world.FTBUPlayerData;
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

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onServerChatEvent(ServerChatEvent event)
    {
        String msg = event.getMessage().trim();

        if(msg.startsWith(FTBUConfigGeneral.CHAT_SUBSTITUTE_PREFIX.getString()))
        {
            ITextComponent replacement = FTBUConfigGeneral.CHAT_SUBSTITUTES.value.get(msg.substring(FTBUConfigGeneral.CHAT_SUBSTITUTE_PREFIX.getString().length()));

            if(replacement != null)
            {
                event.setComponent(new TextComponentString(event.getUsername() + ": ").appendSibling(replacement.createCopy()));
                return;
            }
        }

        if(!FTBUConfigGeneral.ENABLE_LINKS.getBoolean())
        {
            return;
        }

        String[] splitMsg = LMUtils.removeFormatting(msg).split(" ");

        List<String> links = new ArrayList<>();

        for(String s : splitMsg)
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

            FTBLibIntegration.API.addServerCallback(1, () ->
            {
                for(IForgePlayer p : FTBLibIntegration.API.getUniverse().getPlayers())
                {
                    if(p.isOnline() && FTBUPlayerData.get(p).chatLinks())
                    {
                        p.getPlayer().addChatMessage(line);
                    }
                }
            });
        }
    }
}