package latmod.ftbu.mod.cmd.all;

import latmod.ftbu.core.cmd.*;
import latmod.ftbu.core.world.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.*;
import net.minecraft.util.*;

public class CmdFTBUUUID extends SubCommand
{
	public NameType getUsername(String[] args, int i)
	{
		if(i == 0) return NameType.OFF;
		return NameType.NONE;
	}
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		LMPlayer p = LMWorld.server.getPlayer(args.length > 0 ? args[0] : ics);
		IChatComponent toPrint = new ChatComponentText("UUID for " + p.getName() + ": ");
		IChatComponent uuid = new ChatComponentText(p.uuidString);
		uuid.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Copy to chat")));
		uuid.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, p.uuidString));
		uuid.getChatStyle().setColor(EnumChatFormatting.GOLD);
		toPrint.appendSibling(uuid);
		ics.addChatMessage(toPrint);
		return null;
	}
}