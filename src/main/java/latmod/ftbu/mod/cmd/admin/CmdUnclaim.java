package latmod.ftbu.mod.cmd.admin;

import ftb.lib.AdminToken;
import ftb.lib.cmd.*;
import latmod.ftbu.mod.FTBUGuiHandler;
import latmod.ftbu.mod.config.FTBUConfigCmd;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;

public class CmdUnclaim extends CommandLM
{
	public CmdUnclaim()
	{ super(FTBUConfigCmd.name_unclaim.get(), CommandLevel.OP); }

	public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		NBTTagCompound data = new NBTTagCompound();
		data.setLong("T", AdminToken.generate(ep));
		FTBUGuiHandler.instance.openGui(ep, FTBUGuiHandler.ADMIN_CLAIMS, data);
		return null;
	}
}