package latmod.core;

public enum EnumToolClass
{
	PICKAXE("pickaxe"),
	SHOVEL("shovel"),
	AXE("axe");
	
	public final String toolClass;
	
	EnumToolClass(String s)
	{ toolClass = s; }
	
	public static final int LEVEL_STONE = 1;
	public static final int LEVEL_IRON = 2;
	public static final int LEVEL_DIAMOND = 3;
}