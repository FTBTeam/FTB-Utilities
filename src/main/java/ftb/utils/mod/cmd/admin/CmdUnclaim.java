package ftb.utils.mod.cmd.admin;

import ftb.lib.LMAccessToken;
import ftb.lib.api.cmd.CommandLM;
import ftb.lib.api.cmd.CommandLevel;
import ftb.utils.mod.FTBUGuiHandler;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

public class CmdUnclaim extends CommandLM
{
	public CmdUnclaim()
	{ super("unclaim", CommandLevel.OP); }
	
	@Override
	public void processCommand(ICommandSender ics, String[] args) throws CommandException
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		NBTTagCompound data = new NBTTagCompound();
		data.setLong("T", LMAccessToken.generate(ep));
		FTBUGuiHandler.instance.openGui(ep, FTBUGuiHandler.ADMIN_CLAIMS, data);
	}
}