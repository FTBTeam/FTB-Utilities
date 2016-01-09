package latmod.ftbu.tile;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.mod.FTBU;

public enum RedstoneMode
{
	DISABLED("disabled"),
	ACTIVE_HIGH("high"),
	ACTIVE_LOW("low");
	
	public static final RedstoneMode[] VALUES = values();
	
	public final int ID;
	public final String uname;
	
	RedstoneMode(String s)
	{
		ID = ordinal();
		uname = s;
	}
	
	public boolean cancel(boolean b)
	{
		if(this == DISABLED) return false;
		if(this == ACTIVE_HIGH && !b) return true;
		if(this == ACTIVE_LOW && b) return true;
		return false;
	}
	
	public RedstoneMode next()
	{ return VALUES[(ID + 1) % VALUES.length]; }
	
	public RedstoneMode prev()
	{
		int id = ID - 1;
		if(id < 0) id = VALUES.length - 1;
		return VALUES[id];
	}
	
	@SideOnly(Side.CLIENT)
	public String getText()
	{ return FTBU.mod.translateClient("redstonemode." + uname); }
	
	@SideOnly(Side.CLIENT)
	public String getTitle()
	{ return FTBU.mod.translateClient("redstonemode"); }
}