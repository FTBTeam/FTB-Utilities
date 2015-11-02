package latmod.ftbu.world;

import latmod.ftbu.util.LMSecurityLevel;
import net.minecraft.nbt.NBTTagCompound;

public class PersonalSettings
{
	public final LMPlayer owner;
	
	public boolean chatLinks;
	public boolean renderBadge;
	public boolean explosions;
	public LMSecurityLevel blocks;
	
	public PersonalSettings(LMPlayer p)
	{
		owner = p;
		chatLinks = true;
		renderBadge = true;
		explosions = true;
		blocks = LMSecurityLevel.FRIENDS;
	}
	
	public void readFromServer(NBTTagCompound tag)
	{
		chatLinks = tag.hasKey("ChatLinks") ? tag.getBoolean("ChatLinks") : true;
		renderBadge = tag.hasKey("Badge") ? tag.getBoolean("Badge") : true;
		explosions = tag.hasKey("Explosions") ? tag.getBoolean("Explosions") : true;
		blocks = tag.hasKey("Blocks") ? blocks = LMSecurityLevel.VALUES_3[tag.getByte("Blocks")] : LMSecurityLevel.FRIENDS;
	}
	
	public void writeToServer(NBTTagCompound tag)
	{
		tag.setBoolean("ChatLinks", chatLinks);
		tag.setBoolean("Badge", renderBadge);
		tag.setBoolean("Explosions", explosions);
		tag.setByte("Blocks", (byte)blocks.ID);
	}
	
	public void readFromNet(NBTTagCompound tag, boolean self)
	{
		renderBadge = tag.getBoolean("B");
		
		if(self)
		{
			chatLinks = tag.getBoolean("CL");
			explosions = tag.getBoolean("E");
			blocks = LMSecurityLevel.VALUES_3[tag.getByte("BL")];
		}
	}
	
	public void writeToNet(NBTTagCompound tag, boolean self)
	{
		tag.setBoolean("B", renderBadge);
		
		if(self)
		{
			tag.setBoolean("CL", chatLinks);
			tag.setBoolean("E", explosions);
			tag.setByte("BL", (byte)blocks.ID);
		}
	}
	
	public void update()
	{ if(owner.isServer) owner.toPlayerMP().sendUpdate(); }
}