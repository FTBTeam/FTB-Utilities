package latmod.ftbu.mod.cmd.admin;

import latmod.ftbu.core.cmd.*;
import latmod.ftbu.core.inv.ItemDisplay;
import latmod.ftbu.core.util.FastList;
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
			ItemDisplay itemDisplay = new ItemDisplay(is, is.getDisplayName(), is.hasDisplayName() ? FastList.asList(is.getItem().getItemStackDisplayName(is)) : null, 8F);
			NBTTagCompound data = new NBTTagCompound();
			itemDisplay.writeToNBT(data);
			FTBUGuiHandler.instance.openGui(ep, FTBUGuiHandler.DISPLAY_ITEM, data);
			return null;
		}
		
		return "Invalid item!";
	}
}