package latmod.ftbu.mod.cmd;

import latmod.ftbu.cmd.*;
import latmod.ftbu.mod.config.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.*;

public class CmdRules extends CommandLM
{
	public CmdRules()
	{ super("rules", CommandLevel.ALL); }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{
		if(!printRules(getCommandSenderAsPlayer(ics)))
			throw new FeatureDisabledException();
		return null;
	}
	
	public static boolean printRules(EntityPlayerMP ep)
	{
		if(!FTBUConfigGeneral.isDedi() || FTBUConfigLogin.rules.get().isEmpty()) return false;
		
		IChatComponent c = new ChatComponentText("[Click here to open rules]");//LANG
		c.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, FTBUConfigLogin.rules.get()));
		c.getChatStyle().setColor(EnumChatFormatting.GOLD);
		ep.addChatMessage(c);
		return true;
	}
}