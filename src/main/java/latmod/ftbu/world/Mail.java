package latmod.ftbu.world;

import latmod.core.util.FastList;
import latmod.ftbu.util.LMNBTUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;

public class Mail
{
	public final LMPlayer receiver;
	public long timeSent;
	public LMPlayer sender;
	public final FastList<String> text;
	public final FastList<ItemStack> items;
	
	public Mail(LMPlayer p)
	{
		receiver = p;
		text = new FastList<String>();
		items = new FastList<ItemStack>();
	}
	
	public void readFromNBT(NBTTagCompound tag)
	{
		timeSent = tag.getLong("Time");
		sender = LMWorld.getWorld().getPlayer(tag.getInteger("Sender"));
		
		text.clear();
		NBTTagList textList = tag.getTagList("Text", LMNBTUtils.STRING);
		for(int i = 0; i < textList.tagCount(); i++)
			text.add(textList.getStringTagAt(i));
		
		items.clear();
		NBTTagList itemList = tag.getTagList("Items", LMNBTUtils.MAP);
		for(int i = 0; i < itemList.tagCount(); i++)
			items.add(ItemStack.loadItemStackFromNBT(itemList.getCompoundTagAt(i)));
	}
	
	public void writeToNBT(NBTTagCompound tag)
	{
		tag.setLong("Time", timeSent);
		tag.setInteger("Sender", (sender == null) ? 0 : sender.playerID);
		
		NBTTagList textList = new NBTTagList();
		for(String s : text) textList.appendTag(new NBTTagString(s));
		tag.setTag("Text", textList);
		
		NBTTagList itemList = new NBTTagList();
		for(ItemStack is : items)
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			is.writeToNBT(tag1);
			textList.appendTag(tag1);
		}
		tag.setTag("Items", itemList);
	}
	
	public static void readFromNBT(LMPlayer player, NBTTagCompound tag, String s)
	{
		player.mail.clear();
		NBTTagList list = tag.getTagList(s, LMNBTUtils.MAP);
		for(int i = 0; i < list.tagCount(); i++)
		{
			Mail m = new Mail(player);
			m.readFromNBT(list.getCompoundTagAt(i));
			player.mail.add(m);
		}
	}
	
	public static void writeToNBT(LMPlayer player, NBTTagCompound tag, String s)
	{
		NBTTagList list = new NBTTagList();
		
		for(Mail m : player.mail)
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			m.writeToNBT(tag1);
			list.appendTag(tag1);
		}
		
		tag.setTag(s, list);
	}
}