package latmod.ftbu.world;

import latmod.ftbu.util.LMSecurityLevel;
import latmod.lib.IntMap;
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
		IntMap map = IntMap.fromIntArrayS(tag.getIntArray("B"));
		
		renderBadge = map.get(0) == 1;
		
		if(self)
		{
			chatLinks = map.get(1) == 1;
			explosions = map.get(2) == 1;
			blocks = LMSecurityLevel.VALUES_3[map.get(3)];
		}
	}
	
	public void writeToNet(NBTTagCompound tag, boolean self)
	{
		IntMap map = new IntMap();
		
		if(renderBadge) map.put(0, 1);
		
		if(self)
		{
			if(chatLinks) map.put(1, 1);
			if(explosions) map.put(2, 1);
			map.put(3, blocks.ID);
		}
		
		tag.setIntArray("B", map.toIntArray());
	}
	
	public void update()
	{ if(owner.isServer) owner.toPlayerMP().sendUpdate(); }
}