package com.feed_the_beast.ftbutilities.command.ranks;


import com.feed_the_beast.ftblib.lib.command.CmdTreeBase;
import com.feed_the_beast.ftblib.lib.command.CmdTreeHelp;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;

/**
 * @author LatvianModder
 */
public class CmdRanks extends CmdTreeBase
{
	public CmdRanks()
	{
		super("ranks");
		addSubcommand(new CmdAdd());
		addSubcommand(new CmdGet());
		addSubcommand(new CmdSet());
		addSubcommand(new CmdGetPermission());
		addSubcommand(new CmdSetPermission());
		addSubcommand(new CmdDelete());
		addSubcommand(new CmdTreeHelp(this));
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (Loader.isModLoaded("spongeforge"))
		{
			ITextComponent component = new TextComponentString("FTB Utilities Ranks aren't going to work with Sponge installed!");
			component.getStyle().setColor(TextFormatting.DARK_RED);
			sender.sendMessage(component);
			return;
		}

		super.execute(server, sender, args);
	}
}