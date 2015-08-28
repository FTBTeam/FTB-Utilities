package latmod.ftbu.mod.cmd.admin;

import latmod.ftbu.core.api.FTBUApi;
import latmod.ftbu.core.cmd.SubCommand;
import latmod.ftbu.core.net.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.*;
import cpw.mods.fml.relauncher.Side;

public class CmdAdminReload extends SubCommand
{
	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{
		FTBUApi.reload(Side.SERVER, ics);
		LMNetHelper.sendTo(null, new MessageReload());
		return new ChatComponentText("FTBU reloaded (Server)");
	}
}