package com.feed_the_beast.ftbutilities.net;

import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbutilities.data.FTBUPlayerData;
import com.feed_the_beast.ftbutilities.data.FTBUUniverseData;
import com.feed_the_beast.ftbutilities.gui.GuiWarps;
import net.minecraft.command.ICommand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.ArrayList;
import java.util.List;

public class MessageSendWarpList extends MessageToClient<MessageSendWarpList>
{
	public static class WarpItem implements Comparable<WarpItem>
	{
		public static final int TYPE_SPECIAL_IN = 0;
		public static final int TYPE_SPECIAL_OUT = 1;
		public static final int TYPE_WARP = 2;
		public static final int TYPE_HOME = 3;

		public static final WarpItem CANCEL = new WarpItem("Cancel", "", TYPE_SPECIAL_IN);

		private static final DataOut.Serializer<WarpItem> SERIALIZER = (data, w) ->
		{
			data.writeString(w.name);
			data.writeString(w.cmd);
			data.writeByte(w.type);
		};

		private static final DataIn.Deserializer<WarpItem> DESERIALIZER = data ->
		{
			String n = data.readString();
			String c = data.readString();
			return new WarpItem(n, c, data.readUnsignedByte());
		};

		public final String name;
		public final String cmd;
		public final int type;

		private WarpItem(String n, String c, int t)
		{
			name = n;
			cmd = c;
			type = t;
		}

		public boolean isSpecial()
		{
			return type == TYPE_SPECIAL_IN || type == TYPE_SPECIAL_OUT;
		}

		public boolean innerCircle()
		{
			return type == TYPE_SPECIAL_IN || type == TYPE_HOME;
		}

		@Override
		public int compareTo(WarpItem o)
		{
			return name.compareToIgnoreCase(o.name);
		}
	}

	private List<WarpItem> warps;

	public MessageSendWarpList()
	{
	}

	private static String command(EntityPlayerMP player, String name, String backup)
	{
		ICommand command = player.mcServer.getCommandManager().getCommands().get(name);
		return "/" + ((command != null && command.checkPermission(player.mcServer, player)) ? name : backup);
	}

	public MessageSendWarpList(EntityPlayerMP player)
	{
		warps = new ArrayList<>();

		warps.add(new WarpItem("Spawn", command(player, "spawn", "ftb spawn"), WarpItem.TYPE_SPECIAL_OUT));
		warps.add(new WarpItem("Back", command(player, "back", "ftb back"), WarpItem.TYPE_SPECIAL_IN));

		String cmd = command(player, "warp", "ftb warp") + " ";

		for (String s : FTBUUniverseData.WARPS.list())
		{
			warps.add(new WarpItem(s, cmd + s, WarpItem.TYPE_WARP));
		}

		cmd = command(player, "home", "ftb home") + " ";

		for (String s : FTBUPlayerData.get(Universe.get().getPlayer(player)).homes.list())
		{
			warps.add(new WarpItem(s, cmd + s, WarpItem.TYPE_HOME));
		}
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBUNetHandler.GENERAL;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeCollection(warps, WarpItem.SERIALIZER);
	}

	@Override
	public void readData(DataIn data)
	{
		warps = new ArrayList<>();
		data.readCollection(warps, WarpItem.DESERIALIZER);
	}

	@Override
	public void onMessage(MessageSendWarpList m, EntityPlayer player)
	{
		if (GuiWarps.INSTANCE != null)
		{
			GuiWarps.INSTANCE.setData(m.warps);
		}
	}
}