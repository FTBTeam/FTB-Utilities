package latmod.ftbu.mod.cmd.admin;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.cmd.*;
import latmod.ftbu.mod.FTBUGuiHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class CmdAdminDisplayItem extends SubCommand
{
	public String onCommand(ICommandSender ics, String[] args)
	{
		EntityPlayerMP ep = CommandLM.getCommandSenderAsPlayer(ics);
		ItemStack is = ep.inventory.getCurrentItem();
		
		if(ep.inventory.getCurrentItem() != null)
		{
			NBTTagCompound data = new NBTTagCompound();
			
			NBTTagCompound item = new NBTTagCompound();
			is.writeToNBT(item);
			data.setTag("I", item);
			data.setString("T", is.getDisplayName());
			if(is.hasDisplayName()) data.setString("D", is.getItem().getItemStackDisplayName(is));
			
			LatCoreMC.openGui(ep, FTBUGuiHandler.DISPLAY_ITEM, data);
			return null;
		}
		
		return "Invalid item!";
	}
}