package com.feed_the_beast.ftbutilities.net;

import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftblib.lib.util.NBTUtils;
import com.feed_the_beast.ftbutilities.FTBUtilitiesConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public class MessageUpdateTabName extends MessageToClient
{
	private UUID playerId;
	private ITextComponent displayName;
	private boolean afk, rec;

	public MessageUpdateTabName()
	{
	}

	public MessageUpdateTabName(EntityPlayerMP player)
	{
		playerId = player.getUniqueID();
		displayName = player.getDisplayName();
		afk = (System.currentTimeMillis() - player.getLastActiveTime()) >= FTBUtilitiesConfig.afk.getNotificationTimer();
		rec = NBTUtils.getPersistedData(player, false).getBoolean("recording");
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBUtilitiesNetHandler.GENERAL;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeUUID(playerId);
		data.writeTextComponent(displayName);
		data.writeBoolean(afk);
		data.writeBoolean(rec);
	}

	@Override
	public void readData(DataIn data)
	{
		playerId = data.readUUID();
		displayName = data.readTextComponent();
		afk = data.readBoolean();
		rec = data.readBoolean();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onMessage()
	{
		NetworkPlayerInfo info = Minecraft.getMinecraft().player.connection.getPlayerInfo(playerId);

		if (info == null)
		{
			return;
		}

		ITextComponent component = new TextComponentString("");

		if (rec)
		{
			ITextComponent component1 = new TextComponentString("[REC]");
			component1.getStyle().setColor(TextFormatting.RED);
			component1.getStyle().setBold(true);
			component.appendSibling(component1);
		}

		if (afk)
		{
			ITextComponent component1 = new TextComponentString("[AFK]");
			component1.getStyle().setColor(TextFormatting.GRAY);
			component.appendSibling(component1);
		}

		if (afk || rec)
		{
			component.appendText(" ");
		}

		component.appendSibling(displayName);
		info.setDisplayName(component);
	}
}