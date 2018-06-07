package com.feed_the_beast.ftbutilities.command.ranks;

import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.command.CommandUtils;
import com.feed_the_beast.ftblib.lib.config.RankConfigAPI;
import com.feed_the_beast.ftblib.lib.config.RankConfigValueInfo;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.util.misc.Node;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.ranks.FTBUtilitiesPermissionHandler;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * @author LatvianModder
 */
public class CmdGetPermission extends CmdBase
{
	public CmdGetPermission()
	{
		super("get_permission", Level.OP);
	}

	@Override
	public List<String> getAliases()
	{
		return Collections.singletonList("getp");
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
	{
		if (args.length == 2)
		{
			return getListOfStringsMatchingLastWord(args, Ranks.isActive() ? Ranks.INSTANCE.getPermissionNodes() : FTBUtilitiesPermissionHandler.INSTANCE.getRegisteredNodes());
		}

		return super.getTabCompletions(server, sender, args, pos);
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		return index == 0;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		checkArgs(sender, args, 2);
		ForgePlayer player = CommandUtils.getForgePlayer(sender, args[0]);
		Node node = Node.get(args[1]);

		ITextComponent nodeText = new TextComponentString(node.toString());
		nodeText.getStyle().setColor(TextFormatting.GOLD);

		ITextComponent nameText = player.getDisplayName();
		nameText.getStyle().setColor(TextFormatting.DARK_GREEN);

		RankConfigValueInfo info = RankConfigAPI.getHandler().getInfo(node);
		ITextComponent valueText;

		if (info != null)
		{
			valueText = new TextComponentString(player.getRankConfig(node).toString());
			valueText.getStyle().setColor(TextFormatting.BLUE);
		}
		else
		{
			boolean value = player.hasPermission(args[1]);
			valueText = new TextComponentString(value ? "true" : "false");
			valueText.getStyle().setColor(value ? TextFormatting.GREEN : TextFormatting.RED);
		}

		sender.sendMessage(FTBUtilities.lang(sender, "commands.ranks.get_permission.text", nodeText, nameText, valueText));
	}
}