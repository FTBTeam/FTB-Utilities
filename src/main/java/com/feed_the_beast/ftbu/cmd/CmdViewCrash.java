package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import com.feed_the_beast.ftbl.lib.util.CommonUtils;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.feed_the_beast.ftbu.net.MessageViewCrash;
import com.feed_the_beast.ftbu.net.MessageViewCrashList;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileReader;
import java.util.List;

/**
 * @author LatvianModder
 */
public class CmdViewCrash extends CmdBase
{
	public CmdViewCrash()
	{
		super("view_crash", Level.OP);
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, new File(CommonUtils.folderMinecraft, "crash-reports").list());
		}

		return super.getTabCompletions(server, sender, args, pos);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);

		if (args.length == 0)
		{
			new MessageViewCrashList(new File(CommonUtils.folderMinecraft, "crash-reports")).sendTo(player);
		}
		else
		{
			checkArgs(sender, args, 1);

			File file = new File(CommonUtils.folderMinecraft, "crash-reports/" + (args[0].endsWith(".txt") ? args[0] : ("crash-" + args[0] + ".txt")));

			if (file.exists())
			{
				try
				{
					new MessageViewCrash(StringUtils.readStringList(new FileReader(file))).sendTo(player);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}