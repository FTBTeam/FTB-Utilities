package com.feed_the_beast.ftbutilities.command;

import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.command.CmdTreeBase;
import com.feed_the_beast.ftblib.lib.command.CmdTreeHelp;
import com.feed_the_beast.ftblib.lib.command.CommandUtils;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.math.MathUtils;
import com.feed_the_beast.ftblib.lib.util.NBTUtils;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.net.MessageEditNBT;
import com.feed_the_beast.ftbutilities.net.MessageEditNBTRequest;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
		addSubcommand(new CmdBlock());
		addSubcommand(new CmdEntity());
		addSubcommand(new CmdPlayer());
		addSubcommand(new CmdItem());
		addSubcommand(new CmdTreeHelp(this));
	}

	private static void addInfo(NBTTagList list, ITextComponent key, ITextComponent value)
	{
		list.appendTag(new NBTTagString(ITextComponent.Serializer.componentToJson(StringUtils.color(key, TextFormatting.BLUE).appendText(": ").appendSibling(StringUtils.color(value, TextFormatting.GOLD)))));
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length < 1)
		{
			new MessageEditNBTRequest().sendTo(getCommandSenderAsPlayer(sender));
		}
		else
		{
			super.execute(server, sender, args);
		}
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

			long size = NBTUtils.getSizeInBytes(nbt, false);

			if (size >= 30000L)
			{
				throw FTBUtilities.error(sender, "commands.nbtedit.too_large");
			}
			else if (info.hasKey("type"))
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

	private static class CmdBlock extends CmdNBT
	{
		private CmdBlock()
		{
			super("block");
		}

		@Override
		public NBTTagCompound editNBT(EntityPlayerMP player, NBTTagCompound info, String[] args) throws CommandException
		{
			checkArgs(player, args, 3);
			BlockPos pos = parseBlockPos(player, args, 0, false);

			if (!player.world.isBlockLoaded(pos))
			{
				throw new CommandException("commands.clone.outOfWorld");
			}

			TileEntity tile = player.world.getTileEntity(pos);
			NBTTagCompound nbt = new NBTTagCompound();

			if (tile != null)
			{
				info.setString("type", "block");
				info.setInteger("x", pos.getX());
				info.setInteger("y", pos.getY());
				info.setInteger("z", pos.getZ());
				tile.writeToNBT(nbt);
				nbt.removeTag("x");
				nbt.removeTag("y");
				nbt.removeTag("z");
				info.setString("id", nbt.getString("id"));
				nbt.removeTag("id");

				NBTTagList list = new NBTTagList();
				addInfo(list, new TextComponentString("Class"), new TextComponentString(tile.getClass().getName()));
				ResourceLocation key = TileEntity.getKey(tile.getClass());
				addInfo(list, new TextComponentString("ID"), new TextComponentString(key == null ? "null" : key.toString()));
				addInfo(list, new TextComponentString("Block"), new TextComponentString(tile.getBlockType().getRegistryName().toString()));
				addInfo(list, new TextComponentString("Block Class"), new TextComponentString(tile.getBlockType().getClass().getName()));
				addInfo(list, new TextComponentString("Position"), new TextComponentString("[" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "]"));

				ModContainer mod = key == null ? null : Loader.instance().getIndexedModList().get(key.getNamespace());
				addInfo(list, new TextComponentString("Mod"), new TextComponentString(mod == null ? "null" : mod.getName()));
				addInfo(list, new TextComponentString("Ticking"), new TextComponentString(tile instanceof ITickable ? "true" : "false"));
				info.setTag("text", list);

				ITextComponent title = tile.getDisplayName();

				if (title == null)
				{
					title = new TextComponentString(tile.getClass().getSimpleName());
				}

				info.setString("title", ITextComponent.Serializer.componentToJson(title));
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

				NBTTagList list = new NBTTagList();
				addInfo(list, new TextComponentString("Class"), new TextComponentString(entity.getClass().getName()));
				ResourceLocation key = EntityList.getKey(entity.getClass());
				addInfo(list, new TextComponentString("ID"), new TextComponentString(key == null ? "null" : key.toString()));
				addInfo(list, new TextComponentString("Mod"), new TextComponentString(key == null ? "null" : Loader.instance().getIndexedModList().get(key.getNamespace()).getName()));
				info.setTag("text", list);
				info.setString("title", ITextComponent.Serializer.componentToJson(entity.getDisplayName()));
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
		public List<String> getAliases()
		{
			return Collections.singletonList("me");
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

			NBTTagList list = new NBTTagList();
			addInfo(list, new TextComponentString("Name"), new TextComponentString(player.getName()));
			addInfo(list, new TextComponentString("Display Name"), player.getDisplayName());
			addInfo(list, new TextComponentString("UUID"), new TextComponentString(player.getUniqueID().toString()));
			addInfo(list, new TextComponentString("FTBLib Team"), new TextComponentString(p.team.getId()));
			info.setTag("text", list);
			info.setString("title", ITextComponent.Serializer.componentToJson(player.getDisplayName()));
			return nbt;
		}
	}

	private static class CmdItem extends CmdNBT
	{
		private CmdItem()
		{
			super("item");
		}

		@Override
		public NBTTagCompound editNBT(EntityPlayerMP player, NBTTagCompound info, String[] args)
		{
			info.setString("type", "item");
			return player.getHeldItem(EnumHand.MAIN_HAND).serializeNBT();
		}
	}
}