package com.feed_the_beast.ftbu.handlers;

import com.feed_the_beast.ftbu.api.IRank;
import com.feed_the_beast.ftbu.api_impl.FTBUtilitiesAPI_Impl;
import com.feed_the_beast.ftbu.config.FTBUConfigGeneral;
import com.feed_the_beast.ftbu.config.FTBUConfigRanks;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FTBUServerEventHandler
{
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

        if(FTBUConfigRanks.OVERRIDE_CHAT.getBoolean())
        {
            IRank rank = FTBUtilitiesAPI_Impl.INSTANCE.getRank(event.getPlayer().getGameProfile());

            ITextComponent main = new TextComponentString("");
            ITextComponent name = new TextComponentString(rank.getPrefix() + event.getPlayer().getDisplayNameString() + rank.getSuffix());

            name.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + event.getPlayer().getName() + " "));

            NBTTagCompound hoverNBT = new NBTTagCompound();
            String s = EntityList.getEntityString(event.getPlayer());
            hoverNBT.setString("id", event.getPlayer().getCachedUniqueIdString());

            if(s != null)
            {
                hoverNBT.setString("type", s);
            }

            hoverNBT.setString("name", event.getPlayer().getName());

            name.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new TextComponentString(hoverNBT.toString())));
            name.getStyle().setInsertion(event.getPlayer().getName());

            main.appendSibling(name);
            main.appendSibling(ForgeHooks.newChatWithLinks(msg));
            event.setComponent(main);
        }
    }
}