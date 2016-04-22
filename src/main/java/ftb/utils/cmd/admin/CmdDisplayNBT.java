package ftb.utils.cmd.admin;

import ftb.lib.api.cmd.*;
import ftb.lib.api.info.InfoPage;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;

public class CmdDisplayNBT extends CommandLM
{
	public CmdDisplayNBT()
	{ super("display_nbt", CommandLevel.OP); }
	
	@Override
	public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		
		InfoPage file = new InfoPage("NBT");
		
		ItemStack is = ep.inventory.getCurrentItem();
		if(is == null) is = new ItemStack(Blocks.air);
		
		NBTTagCompound tag = new NBTTagCompound();
		is.writeToNBT(tag);
		writeToGuide(file, tag);
		file.displayGuide(ep);
	}
	
	private void writeToGuide(InfoPage cat, NBTBase tag)
	{
		if(tag == null) return;
		else if(tag instanceof NBTTagCompound)
		{
			NBTTagCompound tag1 = (NBTTagCompound) tag;
			for(String s : tag1.getKeySet())
			{
				writeToGuide(cat.getSub(s), tag1.getTag(s));
			}
		}
		else if(tag instanceof NBTTagList)
		{
			NBTTagList list = (NBTTagList) tag.copy();
			
			for(int i = list.tagCount() - 1; i >= 0; i--)
			{
				writeToGuide(cat.getSub("[" + (i + 1) + "]"), list.removeTag(i));
			}
		}
		else
		{
			cat.printlnText(tag.toString());
		}
	}
}