package latmod.ftbu.mod.cmd.admin;

import ftb.lib.cmd.CommandLevel;
import latmod.ftbu.mod.FTBUGuiHandler;
import latmod.ftbu.util.CommandFTBU;
import latmod.ftbu.world.*;
import latmod.lib.MathHelperLM;
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
		LMPlayerServer p = LMWorldServer.inst.getPlayer(ep);
		NBTTagCompound data = new NBTTagCompound();
		p.adminToken = MathHelperLM.rand.nextLong();
		data.setLong("T", p.adminToken);
		FTBUGuiHandler.instance.openGui(ep, FTBUGuiHandler.ADMIN_CLAIMS, data);
		return null;
	}
}