package latmod.ftbu.world;

import ftb.lib.*;
import ftb.lib.item.*;
import latmod.ftbu.net.MessageMailUpdate;
import latmod.lib.FastList;
import net.minecraft.nbt.*;

public class Mail
{
	public final int mailID;
	public final LMPlayer receiver;
	public long timeSent;
	public LMPlayer sender;
	public final FastList<String> text;
	public final BasicInventory items;
	
	public Mail(int id, LMPlayer p)
	{
		mailID = id;
		receiver = p;
		text = new FastList<String>();
		items = new BasicInventory(9)
		{
			public void markDirty()
			{
				//FIXME: Mail items
				if(!FTBLib.isServer()) new MessageMailUpdate(Mail.this).sendToServer();
			}
		};
	}
	
	public int hashCode()
	{ return mailID; }
	
	public boolean equals(Object o)
	{ return o.hashCode() == hashCode(); }
	
	public void readFromNBT(NBTTagCompound tag)
	{
		timeSent = tag.getLong("Time");
		sender = LMWorld.getWorld().getPlayer(tag.getInteger("Sender"));
		
		text.clear();
		NBTTagList textList = tag.getTagList("Text", LMNBTUtils.STRING);
		for(int i = 0; i < textList.tagCount(); i++)
			text.add(textList.getStringTagAt(i));
		
		StringIDInvLoader.readItemsFromNBT(items.items, tag, "Items");
	}
	
	public void writeToNBT(NBTTagCompound tag)
	{
		tag.setInteger("ID", mailID);
		tag.setLong("Time", timeSent);
		tag.setInteger("Sender", (sender == null) ? 0 : sender.playerID);
		
		NBTTagList textList = new NBTTagList();
		for(String s : text) textList.appendTag(new NBTTagString(s));
		tag.setTag("Text", textList);
		
		StringIDInvLoader.writeItemsToNBT(items.items, tag, "Items");
	}
	
	public static void readFromNBT(LMPlayer player, NBTTagCompound tag, String s)
	{
		player.mail.clear();
		NBTTagList list = tag.getTagList(s, LMNBTUtils.MAP);
		for(int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound tag1 = list.getCompoundTagAt(i);
			Mail m = new Mail(tag1.getInteger("ID"), player);
			m.readFromNBT(tag1);
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