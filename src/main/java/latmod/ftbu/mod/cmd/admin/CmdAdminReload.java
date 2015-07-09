package latmod.ftbu.mod.cmd.admin;

import latmod.ftbu.core.*;
import latmod.ftbu.core.cmd.*;
import latmod.ftbu.core.event.ReloadEvent;
import latmod.ftbu.core.net.*;
import latmod.ftbu.mod.config.FTBUConfig;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.relauncher.Side;

public class CmdAdminReload extends SubCommand
{
	public String onCommand(ICommandSender ics, String[] args)
	{
		FTBUConfig.instance.load();
		if(LatCoreMC.isDedicatedServer() && LatCoreMC.hasOnlinePlayers())
		{
			for(EntityPlayerMP ep : LatCoreMC.getAllOnlinePlayers().values)
				IServerConfig.Registry.updateConfig(ep, null);
		}
		new ReloadEvent(Side.SERVER, ics).post();
		MessageLM.NET.sendToAll(new MessageReload());
		return CommandLM.FINE + "LatvianModders's mods reloaded (Server)";
	}
}