package com.feed_the_beast.ftbu.handlers;

import com.feed_the_beast.ftbl.api.EventHandler;
import com.feed_the_beast.ftbl.api.events.team.ForgeTeamConfigEvent;
import com.feed_the_beast.ftbl.api.events.team.ForgeTeamDeletedEvent;
import com.feed_the_beast.ftbl.api.events.team.ForgeTeamOwnerChangedEvent;
import com.feed_the_beast.ftbl.api.events.team.ForgeTeamPlayerJoinedEvent;
import com.feed_the_beast.ftbl.api.events.team.ForgeTeamPlayerLeftEvent;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunks;
import com.feed_the_beast.ftbu.util.FTBUTeamData;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
@EventHandler
public class FTBUTeamEventHandler
{
	/*@SubscribeEvent
	public static void onDataSynced(ForgeTeamEvent.Sync event)
    {
        if(event.team.hasCapability(FTBUCapabilities.FTBU_TEAM_DATA, null))
        {
            FTBUTeamData data = event.team.getCapability(FTBUCapabilities.FTBU_TEAM_DATA, null);

            if(event.team.world.getSide().isServer())
            {
                NBTTagCompound tag = new NBTTagCompound();
                data.toMP().writeSyncData(event.team, tag, event.player);
                event.data.setTag("FTBU", tag);
            }
            else
            {
                data.toSP().readSyncData(event.team, event.data.getCompoundTag("FTBU"), event.player);
            }
        }
    }*/

	/*
	public void printMessage(@Nullable IForgePlayer from, ITextComponent message)
	{
		ITextComponent name = StringUtils.color(new TextComponentString(Universe.INSTANCE.getPlayer(message.getSender()).getProfile().getName()), color.getValue().getTextFormatting());
		ITextComponent msg = FTBLibLang.TEAM_CHAT_MESSAGE.textComponent(name, message);
		msg.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, FTBLibLang.CLICK_HERE.textComponent()));
		msg.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/team msg "));

		for (EntityPlayerMP ep : getOnlineTeamPlayers(EnumTeamStatus.MEMBER))
		{
			ep.sendMessage(msg);
		}
	}*/

	@SubscribeEvent
	public static void getSettings(ForgeTeamConfigEvent event)
	{
		FTBUTeamData.get(event.getTeam()).addConfig(event);
	}

	@SubscribeEvent
	public static void onTeamDeleted(ForgeTeamDeletedEvent event)
	{
		//printMessage(FTBLibLang.TEAM_DELETED.textComponent(getTitle()));
	}

	@SubscribeEvent
	public static void onPlayerJoined(ForgeTeamPlayerJoinedEvent event)
	{
		//printMessage(FTBLibLang.TEAM_MEMBER_JOINED.textComponent(player.getName()));
	}

	@SubscribeEvent
	public static void onPlayerLeft(ForgeTeamPlayerLeftEvent event)
	{
		//printMessage(FTBLibLang.TEAM_MEMBER_LEFT.textComponent(player.getName()));
		ClaimedChunks.INSTANCE.unclaimAllChunks(event.getPlayer(), null);
	}

	@SubscribeEvent
	public static void onOwnerChanged(ForgeTeamOwnerChangedEvent event)
	{
		//printMessage(FTBLibLang.TEAM_TRANSFERRED_OWNERSHIP.textComponent(p1.getName()));
	}
}