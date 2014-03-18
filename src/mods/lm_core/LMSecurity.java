package mods.lm_core;
import mods.lm_core.mod.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;

public class LMSecurity
{
	public static final int PUBLIC = 0;
	public static final int PRIVATE = 1;
	public static final int RESTRICED = 2;
	
	public int owner = 0;
	public int level = PUBLIC;
	public int[] friends = new int[0];
	
	public LMSecurity(int ownerID)
	{ owner = ownerID; }
	
	public LMSecurity(EntityPlayer ep)
	{ this(PlayerID.inst.get(ep)); }
	
	public void readFromNBT(NBTTagList tag)
	{
		if(tag == null || tag.tagCount() < 2)
		{ level = PUBLIC; friends = new int[0]; }
		
		owner = getShort(tag, 0);
		level = getShort(tag, 1);
		friends = new int[tag.tagCount() - 2];
		
		for(int i = 0; i < friends.length; i++)
		friends[i] = getShort(tag, i + 2);
	}
	
	public void writeToNBT(NBTTagList tag)
	{
		addShort(tag, owner);
		addShort(tag, level);
		for(int i = 0; i < friends.length; i++)
		addShort(tag, friends[i]);
	}
	
	private int getShort(NBTTagList tag, int i)
	{ return ((NBTTagShort)tag.tagAt(i)).data; }
	
	private void addShort(NBTTagList tag, int i)
	{ tag.appendTag(new NBTTagShort(null, (short)i)); }
	
	public boolean canPlayerInteract(int eid)
	{
		if(level == 0) return true;
		if(eid == owner) return true;
		for(int i = 0; i < friends.length; i++)
		if(eid == friends[i]) return true;
		return false;
	}
	
	public boolean canPlayerInteract(EntityPlayer ep)
	{ return canPlayerInteract(PlayerID.inst.get(ep)); }
}