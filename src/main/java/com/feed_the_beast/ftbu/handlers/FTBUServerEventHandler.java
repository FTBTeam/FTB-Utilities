package com.feed_the_beast.ftbu.handlers;

import com.feed_the_beast.ftbl.api.EventHandler;
import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.feed_the_beast.ftbu.api.FTBUtilitiesAPI;
import com.feed_the_beast.ftbu.api.IRank;
import com.feed_the_beast.ftbu.config.FTBUConfigGeneral;
import com.feed_the_beast.ftbu.config.FTBUConfigRanks;
import com.feed_the_beast.ftbu.config.PropertyChatSubstitute;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
@EventHandler
public class FTBUServerEventHandler
{
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onServerChatEvent(ServerChatEvent event)
	{
		String msg = event.getMessage().trim();
		String chatSubstitutePrefix = FTBUConfigGeneral.CHAT_SUBSTITUTE_PREFIX.getString();

		if (FTBUConfigRanks.OVERRIDE_CHAT.getBoolean() || msg.startsWith(chatSubstitutePrefix))
		{
			IRank rank = FTBUtilitiesAPI.API.getRank(event.getPlayer().getGameProfile());

			ITextComponent main = StringUtils.text("");
			ITextComponent name = StringUtils.text(rank.getFormattedName(event.getPlayer().getDisplayNameString()));

			name.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + event.getPlayer().getName() + " "));

			NBTTagCompound hoverNBT = new NBTTagCompound();
			String s = EntityList.getEntityString(event.getPlayer());
			hoverNBT.setString("id", event.getPlayer().getCachedUniqueIdString());

			if (s != null)
			{
				hoverNBT.setString("type", s);
			}

			hoverNBT.setString("name", event.getPlayer().getName());

			name.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ENTITY, StringUtils.text(hoverNBT.toString())));
			name.getStyle().setInsertion(event.getPlayer().getName());

			main.appendSibling(name);

			if (msg.startsWith(chatSubstitutePrefix))
			{
				String key = msg.substring(chatSubstitutePrefix.length());

				for (IConfigValue value : FTBUConfigGeneral.CHAT_SUBSTITUTES)
				{
					PropertyChatSubstitute sub = (PropertyChatSubstitute) value;

					if (sub.key.equals(key))
					{
						main.appendSibling(sub.value);
						event.setComponent(main);
						return;
					}
				}
			}

			main.appendSibling(ForgeHooks.newChatWithLinks(msg));
			event.setComponent(main);
		}
	}
}