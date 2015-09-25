package latmod.ftbu.cmd;

import latmod.ftbu.world.LMWorld;

public enum NameType
{
	NONE,
	ON,
	OFF;
	
	public boolean isOnline()
	{ return this == ON; }
	
	public String[] getUsernames()
	{ return LMWorld.getWorld().getAllPlayerNames(this); }
}