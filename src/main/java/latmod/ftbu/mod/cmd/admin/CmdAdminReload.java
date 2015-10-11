package latmod.ftbu.mod.cmd.admin;

import cpw.mods.fml.relauncher.Side;
import latmod.ftbu.api.EventFTBUReload;
import latmod.ftbu.api.config.ConfigListRegistry;
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
		ConfigListRegistry.reloadAll();
		new MessageSyncConfig(null).sendTo(null);
		
		if(FTBUConfigGeneral.restartTimer.get() > 0)
		{
			if(prevRRTimer != FTBUConfigGeneral.restartTimer.get())
				FTBUTicks.serverStarted();
		}
		
		new EventFTBUReload(Side.SERVER, ics).post();
		new MessageReload().sendTo(null);
		LatCoreMC.printChat(BroadcastSender.inst, "FTBU reloaded (Server)");
		return null;
	}
}