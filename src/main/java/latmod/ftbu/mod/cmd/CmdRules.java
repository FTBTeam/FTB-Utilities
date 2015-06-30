package latmod.ftbu.mod.cmd;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.cmd.*;
import latmod.ftbu.mod.FTBUConfig;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.*;

public class CmdRules extends CommandLM
{
	public CmdRules()
	{ super("rules", CommandLevel.ALL); }
	
	public String onCommand(ICommandSender ics, String[] args)
	{ if(!printRules(getCommandSenderAsPlayer(ics))) return "Rules link not set!"; return null; }
	
	public static boolean printRules(EntityPlayerMP ep)
	{
		if(!LatCoreMC.isDedicatedServer() || FTBUConfig.Login.rules == null || FTBUConfig.Login.rules.isEmpty()) return false;
		
		IChatComponent c = new ChatComponentText("[Click here to open rules]");
		c.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, FTBUConfig.Login.rules));
		c.getChatStyle().setColor(EnumChatFormatting.GOLD);
		ep.addChatMessage(c);
		return true;
	}
}