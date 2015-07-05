package latmod.ftbu.core.cmd;

import latmod.ftbu.core.world.LMWorld;

public enum NameType
{
	NONE,
	ON,
	OFF;
	
	public boolean isOnline()
	{ return this == ON; }
	
	public String[] getUsernames()
	{ return LMWorld.getWorld().getAllNames(this); }
}