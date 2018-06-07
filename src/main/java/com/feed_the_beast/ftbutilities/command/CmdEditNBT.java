package com.feed_the_beast.ftbutilities.command;

import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.command.CmdTreeBase;
import com.feed_the_beast.ftblib.lib.command.CmdTreeHelp;
import com.feed_the_beast.ftblib.lib.command.CommandUtils;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.math.MathUtils;
import com.feed_the_beast.ftbutilities.net.MessageEditNBT;
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
		addSubcommand(new CmdTreeHelp(this));
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
			NBTTagCompound nbt = editNBT(player, info, args);

			if (info.hasKey("type"))
			{
				info.setLong("random", MathUtils.RAND.nextLong());
				EDITING.put(player.getGameProfile().getId(), info);
				new MessageEditNBT(info, nbt).sendTo(player);
			}
		}

		public NBTTagCompound editNBT(EntityPlayerMP player, NBTTagCompound info, String[] args) throws CommandException
		{
			return new NBTTagCompound();
		}
	}

	private static class CmdTile extends CmdNBT
	{
		private CmdTile()
		{
			super("tile");
		}

		@Override
		public NBTTagCompound editNBT(EntityPlayerMP player, NBTTagCompound info, String[] args) throws CommandException
		{
			checkArgs(player, args, 3);
			int x = parseInt(args[0]);
			int y = parseInt(args[1]);
			int z = parseInt(args[2]);
			BlockPos pos = new BlockPos(x, y, z);

			if (!player.world.isBlockLoaded(pos))
			{
				throw new CommandException("commands.clone.outOfWorld");
			}

			TileEntity tile = player.world.getTileEntity(pos);
			NBTTagCompound nbt = new NBTTagCompound();

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

			return nbt;
		}
	}

	private static class CmdEntity extends CmdNBT
	{
		private CmdEntity()
		{
			super("entity");
		}

		@Override
		public NBTTagCompound editNBT(EntityPlayerMP player, NBTTagCompound info, String[] args) throws CommandException
		{
			checkArgs(player, args, 1);
			int id = parseInt(args[0]);
			Entity entity = player.world.getEntityByID(id);
			NBTTagCompound nbt = new NBTTagCompound();

			if (entity != null)
			{
				info.setString("type", "entity");
				info.setInteger("id", id);
				entity.writeToNBT(nbt);
			}

			return nbt;
		}
	}

	private static class CmdPlayer extends CmdNBT
	{
		private CmdPlayer()
		{
			super("player");
		}

		@Override
		public boolean isUsernameIndex(String[] args, int index)
		{
			return index == 0;
		}

		@Override
		public NBTTagCompound editNBT(EntityPlayerMP player, NBTTagCompound info, String[] args) throws CommandException
		{
			ForgePlayer p = CommandUtils.getSelfOrOther(player, args, 0);
			info.setBoolean("online", p.isOnline());
			info.setString("type", "player");
			info.setUniqueId("id", p.getId());
			NBTTagCompound nbt = p.getPlayerNBT();
			nbt.removeTag("id");
			return nbt;
		}
	}
}