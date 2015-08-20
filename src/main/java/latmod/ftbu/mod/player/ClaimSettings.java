package latmod.ftbu.mod.player;

import net.minecraft.nbt.NBTTagCompound;
import latmod.ftbu.core.LMSecurity;
import latmod.ftbu.core.world.LMPlayerServer;

public class ClaimSettings
{
	private boolean safe;
	private LMSecurity.Level breakLevel;
	private LMSecurity.Level interactLevel;
	
	public ClaimSettings()
	{ setDefaults(); }
	
	public void readFromNBT(NBTTagCompound tag)
	{
		safe = tag.getBoolean("Safe");
	}
	
	public void writeToNBT(NBTTagCompound tag)
	{
		tag.setBoolean("Safe", safe);
	}
	
	public void setSafe(LMPlayerServer p, boolean b)
	{
		if(safe != b)
		{
			safe = b;
			p.sendUpdate(true);
		}
	}
	
	public void setBreakLevel(LMPlayerServer p, LMSecurity.Level l)
	{
		if(l != null && breakLevel != l)
		{
			breakLevel = l;
			p.sendUpdate(true);
		}
	}
	
	public void setInteractLevel(LMPlayerServer p, LMSecurity.Level l)
	{
		if(l != null && interactLevel != l)
		{
			interactLevel = l;
			p.sendUpdate(true);
		}
	}
	
	public boolean isSafe()
	{ return safe; }
	
	public LMSecurity.Level getBreakLevel()
	{ return breakLevel; }
	
	public LMSecurity.Level getInteractLevel()
	{ return interactLevel; }
	
	public boolean setDefaults()
	{
		boolean b = shouldSend();
		
		safe = false;
		breakLevel = LMSecurity.Level.FRIENDS;
		interactLevel = LMSecurity.Level.FRIENDS;
		
		return b;
	}
	
	public boolean shouldSend()
	{ return safe || breakLevel != LMSecurity.Level.FRIENDS || interactLevel != LMSecurity.Level.FRIENDS; }
}