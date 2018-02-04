package com.feed_the_beast.ftbutilities.cmd;

import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.cmd.CmdTreeBase;
import com.feed_the_beast.ftblib.lib.math.MathUtils;
import com.feed_the_beast.ftbutilities.net.MessageEditNBT;
import com.feed_the_beast.ftbutilities.net.MessageEditNBTRequest;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class CmdEditNBT extends CmdTreeBase
{
	public static Map<UUID, NBTTagCompound> EDITING = new HashMap<>();

	public CmdEditNBT()
	{
		super("nbtedit");
		addSubcommand(new CmdTile());
		addSubcommand(new CmdEntity());
		addSubcommand(new CmdPlayer());
	}

	public static class CmdNBT extends CmdBase
	{
		private CmdNBT(String id)
		{
			super(id, Level.OP);
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
		{
			EntityPlayerMP player = getCommandSenderAsPlayer(sender);
			NBTTagCompound info = new NBTTagCompound();
			NBTTagCompound nbt = new NBTTagCompound();
			editNBT(player, info, nbt, args);

			if (info.hasKey("type"))
			{
				info.setLong("random", MathUtils.RAND.nextLong());
				EDITING.put(player.getGameProfile().getId(), info);
				new MessageEditNBT(info, nbt).sendTo(player);
			}
		}

		public void editNBT(EntityPlayerMP player, NBTTagCompound info, NBTTagCompound nbt, String[] args) throws CommandException
		{
		}
	}

	private static class CmdTile extends CmdNBT
	{
		private CmdTile()
		{
			super("tile");
		}

		@Override
		public void editNBT(EntityPlayerMP player, NBTTagCompound info, NBTTagCompound nbt, String[] args) throws CommandException
		{
			checkArgs(player, args, 3);
			int x = parseInt(args[0]);
			int y = parseInt(args[1]);
			int z = parseInt(args[2]);

			TileEntity tile = player.getEntityWorld().getTileEntity(new BlockPos(x, y, z));

			if (tile != null)
			{
				info.setString("type", "tile");
				info.setInteger("x", x);
				info.setInteger("y", y);
				info.setInteger("z", z);
				tile.writeToNBT(nbt);
				nbt.removeTag("x");
				nbt.removeTag("y");
				nbt.removeTag("z");
				info.setString("id", nbt.getString("id"));
				nbt.removeTag("id");
			}
		}
	}

	private static class CmdEntity extends CmdNBT
	{
		private CmdEntity()
		{
			super("entity");
		}

		@Override
		public void editNBT(EntityPlayerMP player, NBTTagCompound info, NBTTagCompound nbt, String[] args) throws CommandException
		{
			checkArgs(player, args, 1);
			int id = parseInt(args[0]);
			Entity entity = player.getEntityWorld().getEntityByID(id);

			if (entity != null)
			{
				info.setString("type", "entity");
				info.setInteger("id", id);
				entity.writeToNBT(nbt);
			}
		}
	}

	private static class CmdPlayer extends CmdNBT
	{
		private CmdPlayer()
		{
			super("player");
		}

		@Override
		public boolean isUsernameIndex(String[] args, int i)
		{
			return i == 0;
		}

		@Override
		public void editNBT(EntityPlayerMP player, NBTTagCompound info, NBTTagCompound nbt, String[] args) throws CommandException
		{
			checkArgs(player, args, 1);
			EntityPlayerMP player1 = getPlayer(player.mcServer, player, args[0]);
			info.setString("type", "player");
			info.setUniqueId("id", player1.getUniqueID());
			player1.writeToNBT(nbt);
		}
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length == 0)
		{
			new MessageEditNBTRequest().sendTo(getCommandSenderAsPlayer(sender));
			return;
		}

		if (args.length == 1 && args[0].equals("me"))
		{
			getCommandMap().get("player").execute(server, sender, new String[] {getCommandSenderAsPlayer(sender).getName()});
			return;
		}

		super.execute(server, sender, args);
	}
}