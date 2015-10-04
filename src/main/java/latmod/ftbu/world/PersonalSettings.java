package latmod.ftbu.world;

import latmod.ftbu.util.LMSecurity;
import net.minecraft.nbt.NBTTagCompound;

public class PersonalSettings
{
	public final LMPlayer owner;
	
	public boolean chatLinks;
	public boolean renderBadge;
	public boolean safeClaims;
	public LMSecurity.Level breakLevel;
	public LMSecurity.Level interactLevel;
	
	public PersonalSettings(LMPlayer p)
	{
		owner = p;
		chatLinks = true;
		renderBadge = true;
		safeClaims = false;
		breakLevel = LMSecurity.Level.FRIENDS;
		interactLevel = LMSecurity.Level.FRIENDS;
	}
	
	public void readFromServer(NBTTagCompound tag)
	{
		chatLinks = tag.getBoolean("ChatLinks");
		renderBadge = tag.getBoolean("Badge");
		safeClaims = tag.getBoolean("SafeClaims");
	}
	
	public void writeToServer(NBTTagCompound tag)
	{
		tag.setBoolean("ChatLinks", chatLinks);
		tag.setBoolean("Badge", renderBadge);
		tag.setBoolean("SafeClaims", safeClaims);
	}
	
	public void readFromNet(NBTTagCompound tag, boolean self)
	{
		renderBadge = tag.getBoolean("B");
		
		if(self)
		{
			chatLinks = tag.getBoolean("CL");
			safeClaims = tag.getBoolean("SC");
		}
	}
	
	public void writeToNet(NBTTagCompound tag, boolean self)
	{
		tag.setBoolean("B", renderBadge);
		
		if(self)
		{
			tag.setBoolean("CL", chatLinks);
			tag.setBoolean("SC", safeClaims);
		}
	}
	
	public void update()
	{ if(owner.isServer) owner.toPlayerMP().sendUpdate(); }
}