package com.feed_the_beast.ftbutilities.command;

import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.util.NBTUtils;
import com.feed_the_beast.ftbutilities.net.MessageUpdateTabName;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

/**
 * @author LatvianModder
 */
public class CmdRec extends CmdBase
{
	public CmdRec()
	{
		super("rec", Level.ALL);
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		return index == 0;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		NBTTagCompound nbt = NBTUtils.getPersistedData(player, true);

		if (nbt.getBoolean("recording"))
		{
			nbt.removeTag("recording");
			ITextComponent component = new TextComponentTranslation("commands.rec.not_recording", player.getDisplayName());
			component.getStyle().setColor(TextFormatting.GRAY);
			component.getStyle().setItalic(true);
			server.getPlayerList().sendMessage(component);
		}
		else
		{
			nbt.setBoolean("recording", true);
			ITextComponent component = new TextComponentTranslation("commands.rec.recording", player.getDisplayName());
			component.getStyle().setColor(TextFormatting.GRAY);
			component.getStyle().setItalic(true);
			server.getPlayerList().sendMessage(component);
		}

		Universe.get().getPlayer(player).clearCache();
		new MessageUpdateTabName(player).sendToAll();
	}
}