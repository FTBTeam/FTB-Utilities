package latmod.ftbu.mod.cmd.admin;

import latmod.ftbu.core.api.FTBUReloadableRegistry;
import latmod.ftbu.core.cmd.*;
import latmod.ftbu.core.net.*;
import net.minecraft.command.ICommandSender;
import cpw.mods.fml.relauncher.Side;

public class CmdAdminReload extends SubCommand
{
	public String onCommand(ICommandSender ics, String[] args)
	{
		FTBUReloadableRegistry.reload(Side.SERVER, ics);
		LMNetHelper.sendTo(null, new MessageReload());
		
		return CommandLM.FINE + "LatvianModders's mods reloaded (Server)";
	}
}