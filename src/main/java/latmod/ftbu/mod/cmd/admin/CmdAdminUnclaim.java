package latmod.ftbu.mod.cmd.admin;

import ftb.lib.AdminToken;
import ftb.lib.cmd.CommandLevel;
import latmod.ftbu.mod.FTBUGuiHandler;
import latmod.ftbu.util.CommandFTBU;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;

public class CmdAdminUnclaim extends CommandFTBU
{
	public CmdAdminUnclaim(String s)
	{ super(s, CommandLevel.OP); }

	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		NBTTagCompound data = new NBTTagCompound();
		data.setLong("T", AdminToken.generate(ep));
		FTBUGuiHandler.instance.openGui(ep, FTBUGuiHandler.ADMIN_CLAIMS, data);
		return null;
	}
}