package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import com.feed_the_beast.ftbl.lib.math.MathUtils;
import com.feed_the_beast.ftbu.net.MessageEditNBT;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class CmdEditNBT extends CmdBase
{
	public static Map<UUID, NBTTagCompound> EDITING = new HashMap<>();

	public CmdEditNBT()
	{
		super("nbtedit", Level.OP);
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, "tile", "player", "entity");
		}

		return super.getTabCompletions(server, sender, args, pos);
	}

	@Override
	public boolean isUsernameIndex(String[] args, int i)
	{
		return i == 1 && args[0].equals("player");
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		NBTTagCompound info = new NBTTagCompound(), mainNbt = null;

		if (args.length == 0)
		{
			RayTraceResult ray = MathUtils.rayTrace(player, false);

			if (ray != null)
			{
				switch (ray.typeOfHit)
				{
					case BLOCK:
					{
						TileEntity tile = player.getEntityWorld().getTileEntity(ray.getBlockPos());

						if (tile != null)
						{
							info.setString("type", "tile");
							info.setInteger("x", tile.getPos().getX());
							info.setInteger("y", tile.getPos().getY());
							info.setInteger("z", tile.getPos().getZ());
							mainNbt = tile.serializeNBT();
							mainNbt.removeTag("x");
							mainNbt.removeTag("y");
							mainNbt.removeTag("z");
							info.setString("id", mainNbt.getString("id"));
							mainNbt.removeTag("id");
						}

						break;
					}
					case ENTITY:
					{
						info.setString("type", "entity");
						info.setInteger("id", ray.entityHit.getEntityId());
						mainNbt = ray.entityHit.serializeNBT();
						break;
					}
				}
			}
		}
		else
		{
			switch (args[0])
			{
				case "me":
				{
					info.setString("type", "player");
					info.setUniqueId("id", player.getGameProfile().getId());
					mainNbt = new NBTTagCompound();
					player.writeToNBT(mainNbt);
					break;
				}
				case "tile":
				{
					checkArgs(args, 4, "/ftb nbtedit tile <x> <y> <z>");
					int x = parseInt(args[1]);
					int y = parseInt(args[2]);
					int z = parseInt(args[3]);

					TileEntity tile = player.getEntityWorld().getTileEntity(new BlockPos(x, y, z));

					if (tile != null)
					{
						info.setString("type", "tile");
						info.setInteger("x", x);
						info.setInteger("y", y);
						info.setInteger("z", z);
						mainNbt = tile.serializeNBT();
						mainNbt.removeTag("x");
						mainNbt.removeTag("y");
						mainNbt.removeTag("z");
						info.setString("id", mainNbt.getString("id"));
						mainNbt.removeTag("id");
					}

					break;
				}
				case "entity":
				{
					checkArgs(args, 2, "/ftb nbtedit entity <id>");
					int id = parseInt(args[1]);
					Entity entity = player.getEntityWorld().getEntityByID(id);

					if (entity != null)
					{
						info.setString("type", "entity");
						info.setInteger("id", id);
						mainNbt = entity.serializeNBT();
					}

					break;
				}
				case "player":
				{
					checkArgs(args, 2, "/ftb nbtedit entity <player>");
					EntityPlayerMP player1 = getPlayer(server, sender, args[1]);
					info.setString("type", "player");
					info.setUniqueId("id", player1.getGameProfile().getId());
					mainNbt = player1.serializeNBT();
					break;
				}
			}
		}

		if (mainNbt != null)
		{
			info.setLong("random", MathUtils.RAND.nextLong());
			EDITING.put(player.getGameProfile().getId(), info);
			new MessageEditNBT(info, mainNbt).sendTo(player);
		}
	}
}