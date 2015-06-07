package latmod.ftbu.core.cmd;

import latmod.ftbu.core.LMPlayer;

public enum NameType
{
	NONE,
	ON,
	OFF;
	
	public boolean isOnline()
	{ return this == ON; }
	
	public String[] getUsernames()
	{ return LMPlayer.getAllNames(this); }
}