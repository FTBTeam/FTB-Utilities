package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import com.feed_the_beast.ftbl.lib.util.CommonUtils;
import com.feed_the_beast.ftbl.lib.util.FileUtils;
import com.feed_the_beast.ftbu.gui.guide.GuidePage;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;

import java.io.File;
import java.util.Arrays;

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
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		//FIXME: Replace with custom gui
		EntityPlayerMP ep = getCommandSenderAsPlayer(sender);

		if (args.length == 0)
		{
			GuidePage page = new GuidePage("crash_report_list", null);

			try
			{
				String[] crashReports = new File(CommonUtils.folderMinecraft, "crash-reports").list();

				if (crashReports != null)
				{
					Arrays.sort(crashReports);

					for (String s : crashReports)
					{
						if (s.endsWith(".txt"))
						{
							ITextComponent textComponent = new TextComponentString(s);
							textComponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ftb view_crash " + s));
							page.println(textComponent);
						}
					}
				}
			}
			catch (Exception ex)
			{
				page.println("Failed to read crash-reports folder!");
			}
			return;
		}

		GuidePage page = new GuidePage("crash_report", null);

		try
		{
			File file = new File(CommonUtils.folderMinecraft, "crash-reports/" + args[0]);

			for (String s : FileUtils.load(file))
			{
				page.println(s);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			page.println("Failed to load crash report!");
		}
	}
}