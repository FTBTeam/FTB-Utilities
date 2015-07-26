package latmod.ftbu.mod.cmd.admin;

import latmod.ftbu.core.*;
import latmod.ftbu.core.cmd.*;
import latmod.ftbu.core.event.ReloadEvent;
import latmod.ftbu.core.net.*;
import latmod.ftbu.mod.FTBUTickHandler;
import latmod.ftbu.mod.config.FTBUConfig;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.relauncher.Side;

public class CmdAdminReload extends SubCommand
{
	public String onCommand(ICommandSender ics, String[] args)
	{
		float prevRRTimer = FTBUConfig.general.restartTimer.floatValue();
		
		FTBUConfig.instance.load();
		if(FTBUConfig.general.isDedi())
		{
			for(EntityPlayerMP ep : LatCoreMC.getAllOnlinePlayers())
				IServerConfig.Registry.updateConfig(ep, null);
			
			if(prevRRTimer != FTBUConfig.general.restartTimer.floatValue())
				FTBUTickHandler.resetTimer(true);
		}
		
		new ReloadEvent(Side.SERVER, ics).post();
		LMNetHelper.sendTo(null, new MessageReload());
		
		return CommandLM.FINE + "LatvianModders's mods reloaded (Server)";
	}
}