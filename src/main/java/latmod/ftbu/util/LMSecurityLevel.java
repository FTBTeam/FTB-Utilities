package latmod.ftbu.util;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.mod.FTBU;

public enum LMSecurityLevel
{
	PUBLIC("public"),
	PRIVATE("private"),
	FRIENDS("friends");
	
	public static final LMSecurityLevel[] VALUES = values();
	public static final LMSecurityLevel[] VALUES_2 = new LMSecurityLevel[] { PUBLIC, PRIVATE };
	
	public final int ID;
	private String uname;
	
	LMSecurityLevel(String s)
	{
		ID = ordinal();
		uname = s;
	}
	
	public boolean isPublic()
	{ return this == PUBLIC; }
	
	public boolean isRestricted()
	{ return this == FRIENDS; }
	
	public LMSecurityLevel next(LMSecurityLevel[] l)
	{ return l[(ID + 1) % l.length]; }
	
	public LMSecurityLevel prev(LMSecurityLevel[] l)
	{
		int id = ID - 1;
		if(id < 0) id = l.length - 1;
		return l[id];
	}
	
	@SideOnly(Side.CLIENT)
	public String getText()
	{ return FTBU.mod.translateClient("security." + uname); }
	
	@SideOnly(Side.CLIENT)
	public String getTitle()
	{ return FTBU.mod.translateClient("security"); }
}