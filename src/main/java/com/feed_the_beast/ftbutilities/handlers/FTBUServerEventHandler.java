package com.feed_the_beast.ftbutilities.handlers;

import com.feed_the_beast.ftblib.FTBLibConfig;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftbutilities.FTBUConfig;
import com.feed_the_beast.ftbutilities.FTBUFinals;
import com.feed_the_beast.ftbutilities.data.FTBUTeamData;
import com.feed_the_beast.ftbutilities.gui.ClientClaimedChunks;
import com.feed_the_beast.ftbutilities.ranks.Rank;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBUFinals.MOD_ID)
public class FTBUServerEventHandler
{
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onServerChatEvent(ServerChatEvent event)
	{
		String msg = event.getMessage().trim();

		if (FTBUConfig.ranks.override_chat)
		{
			Rank rank = Ranks.getRank(event.getPlayer().mcServer, event.getPlayer().getGameProfile());

			ITextComponent main = new TextComponentString("");

			// FIXME: Find a better way getting tag
			ForgePlayer player = Universe.get().getPlayer(event.getPlayer());
			FTBUTeamData teamData = FTBUTeamData.get(player.team);
			String tag = teamData.tag.getString();

			ITextComponent name = new TextComponentString(rank.getFormattedName(event.getPlayer().getDisplayNameString(), tag));

			name.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + event.getPlayer().getName() + " "));

			NBTTagCompound hoverNBT = new NBTTagCompound();
			String s = EntityList.getEntityString(event.getPlayer());
			hoverNBT.setString("id", event.getPlayer().getCachedUniqueIdString());

			if (s != null)
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