package com.feed_the_beast.ftbutilities.command.ranks;

import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.config.ConfigBoolean;
import com.feed_the_beast.ftblib.lib.config.ConfigValue;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.ranks.FTBUtilitiesPermissionHandler;
import com.feed_the_beast.ftbutilities.ranks.Rank;
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
		Rank rank = Ranks.INSTANCE.getRank(server, sender, args[0]);

		ConfigValue value = rank.getPermissionValue(args[1], args[1], true);

		ITextComponent valueText;

		if (value.isNull())
		{
			valueText = FTBUtilities.lang(sender, "commands.ranks.none");
			valueText.getStyle().setColor(TextFormatting.DARK_GRAY);
		}
		else if (value instanceof ConfigBoolean)
		{
			valueText = new TextComponentString(value.getString());
			valueText.getStyle().setColor(value.getBoolean() ? TextFormatting.GREEN : TextFormatting.RED);
		}
		else
		{
			valueText = new TextComponentString(value.getString());
			valueText.getStyle().setColor(TextFormatting.BLUE);
		}

		ITextComponent nodeText = new TextComponentString(args[1]);
		nodeText.getStyle().setColor(TextFormatting.GOLD);

		ITextComponent nameText = rank.getDisplayName().createCopy();
		nameText.getStyle().setColor(TextFormatting.DARK_GREEN);

		sender.sendMessage(FTBUtilities.lang(sender, "commands.ranks.get_permission.text", nodeText, nameText, valueText));
	}
}