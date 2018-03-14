package com.feed_the_beast.ftbutilities.cmd;

import com.feed_the_beast.ftblib.FTBLibLang;
import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.io.HttpConnection;
import com.feed_the_beast.ftblib.lib.io.RequestMethod;
import com.feed_the_beast.ftblib.lib.io.Response;
import com.feed_the_beast.ftblib.lib.util.CommonUtils;
import com.feed_the_beast.ftblib.lib.util.FileUtils;
import com.feed_the_beast.ftblib.lib.util.StringJoiner;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.FTBUtilitiesLang;
import com.feed_the_beast.ftbutilities.net.MessageViewCrash;
import com.feed_the_beast.ftbutilities.net.MessageViewCrashList;
import com.google.gson.JsonElement;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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
	public boolean checkPermission(MinecraftServer server, ICommandSender sender)
	{
		return !server.isDedicatedServer() || super.checkPermission(server, sender);
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
	{
		if (args.length == 1)
		{
			File folder = new File(CommonUtils.folderMinecraft, "crash-reports");
			if (folder.exists() && folder.isDirectory())
			{
				String[] files = folder.list();

				if (files != null && files.length > 0)
				{
					if (files.length > 1)
					{
						Arrays.sort(files, StringUtils.IGNORE_CASE_COMPARATOR.reversed());
					}

					return getListOfStringsMatchingLastWord(args, files);
				}
			}
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
				if (args.length >= 2 && args[1].equals("upload"))
				{
					new ThreadUploadCrash(file, sender).start();
					return;
				}

				try
				{
					new MessageViewCrash(file.getName(), StringUtils.readStringList(new FileReader(file))).sendTo(player);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public static class ThreadUploadCrash extends Thread
	{
		private final File file;
		private final ICommandSender sender;

		public ThreadUploadCrash(File f, ICommandSender s)
		{
			file = f;
			sender = s;
		}

		@Override
		public void run()
		{
			try
			{
				File urlFile = new File(CommonUtils.folderLocal, "ftbutilities/uploaded_crash_reports/" + file.getName());
				String url = "";

				if (urlFile.exists())
				{
					url = FileUtils.loadAsText(urlFile);
				}
				else
				{
					List<String> text = StringUtils.readStringList(new FileInputStream(file));
					HttpConnection connection = HttpConnection.connection("https://hastebin.com/documents", RequestMethod.POST, "text/plain; charset=utf-8");
					connection.data = StringJoiner.with('\n').join(text).getBytes(StandardCharsets.UTF_8);
					Response response = connection.connect(sender.getServer().getServerProxy());
					JsonElement json = response.asJson();

					if (json.isJsonObject() && json.getAsJsonObject().has("key"))
					{
						url = "https://hastebin.com/" + json.getAsJsonObject().get("key").getAsString() + ".md";
						FileUtils.saveSafe(urlFile, url);
					}

					response.close();
				}

				if (!url.isEmpty())
				{
					ITextComponent link = FTBLibLang.CLICK_HERE.textComponent(sender);
					link.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(url)));
					link.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
					FTBUtilitiesLang.UPLOADED_CRASH.sendMessage(sender, link);
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
}