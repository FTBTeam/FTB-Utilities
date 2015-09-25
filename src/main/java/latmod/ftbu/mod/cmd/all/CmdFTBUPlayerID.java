package latmod.ftbu.mod.cmd.all;

import latmod.ftbu.cmd.*;
import latmod.ftbu.world.LMPlayerServer;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.*;
import net.minecraft.util.*;

public class CmdFTBUPlayerID extends CommandLM
{
	public CmdFTBUPlayerID(String s)
	{ super(s, CommandLevel.ALL); }

	public NameType getUsername(String[] args, int i)
	{
		if(i == 0) return NameType.OFF;
		return NameType.NONE;
	}
	
	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{
		LMPlayerServer p = getLMPlayer(args.length > 0 ? args[0] : ics);
		IChatComponent toPrint = new ChatComponentText("");
		toPrint.getChatStyle().setColor(EnumChatFormatting.GOLD);
		toPrint.appendSibling(new ChatComponentText("[" + p.getName() + "] "));
		toPrint.appendSibling(new ChatComponentText("[" + p.playerID + "] "));
		IChatComponent uuid = new ChatComponentText("[" + p.uuidString + "]");
		uuid.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Copy to chat")));
		uuid.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, p.uuidString));
		toPrint.appendSibling(uuid);
		return toPrint;
	}
}