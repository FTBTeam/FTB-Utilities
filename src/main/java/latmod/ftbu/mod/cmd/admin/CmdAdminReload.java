package latmod.ftbu.mod.cmd.admin;

import cpw.mods.fml.relauncher.Side;
import latmod.ftbu.api.EventFTBUReload;
import latmod.ftbu.api.config.ConfigFileRegistry;
import latmod.ftbu.cmd.*;
import latmod.ftbu.mod.FTBUTicks;
import latmod.ftbu.mod.config.FTBUConfigGeneral;
import latmod.ftbu.net.*;
import latmod.ftbu.util.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;

public class CmdAdminReload extends CommandLM
{
	public CmdAdminReload(String s)
	{ super(s, CommandLevel.OP); }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{
		float prevRRTimer = FTBUConfigGeneral.restartTimer.get();
		
		ConfigFileRegistry.reloadAll();
		ConfigFileRegistry.syncWithClient(null);
		
		if(FTBUConfigGeneral.isDedi())
		{
			if(prevRRTimer != FTBUConfigGeneral.restartTimer.get())
				FTBUTicks.serverStarted();
		}
		
		new EventFTBUReload(Side.SERVER, ics).post();
		LMNetHelper.sendTo(null, new MessageReload());
		LatCoreMC.printChat(BroadcastSender.inst, "FTBU reloaded (Server)");
		return null;
	}
}