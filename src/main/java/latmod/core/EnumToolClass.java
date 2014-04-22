package latmod.core;

public enum EnumToolClass
{
	PICKAXE("pickaxe"),
	SHOVEL("shovel"),
	AXE("axe");
	
	public final String toolClass;
	
	EnumToolClass(String s)
	{ toolClass = s; }
}