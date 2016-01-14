package latmod.ftbu.util;

import ftb.lib.api.gui.GuiIcons;
import ftb.lib.client.TextureCoords;
import latmod.ftbu.mod.FTBU;
import net.minecraft.util.StatCollector;

public enum LMSecurityLevel
{
	PUBLIC("public"),
	PRIVATE("private"),
	FRIENDS("friends");
	
	public static final LMSecurityLevel[] VALUES_3 = new LMSecurityLevel[] {PUBLIC, PRIVATE, FRIENDS};
	public static final LMSecurityLevel[] VALUES_2 = new LMSecurityLevel[] {PUBLIC, PRIVATE};
	
	public final int ID;
	public final String uname;
	public final String langKey;
	
	LMSecurityLevel(String s)
	{
		ID = ordinal();
		uname = s;
		langKey = FTBU.mod.assets + "security." + uname;
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
	
	public String getText()
	{ return StatCollector.translateToLocal(langKey); }
	
	public String getTitle()
	{ return StatCollector.translateToLocal(FTBU.mod.assets + "security"); }
	
	public TextureCoords getIcon()
	{ return GuiIcons.security[ID]; }
	
	public static String[] getNames()
	{
		String[] s = new String[VALUES_3.length];
		for(int i = 0; i < VALUES_3.length; i++)
			s[i] = VALUES_3[i].uname;
		return s;
	}
	
	public static LMSecurityLevel get(String s)
	{
		for(LMSecurityLevel l : VALUES_3)
		{
			if(l.uname.equalsIgnoreCase(s)) return l;
		}
		return null;
	}
}