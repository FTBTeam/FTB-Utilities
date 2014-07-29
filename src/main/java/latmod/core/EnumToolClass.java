package latmod.core;

public enum EnumToolClass
{
	PICKAXE("pickaxe"),
	SHOVEL("shovel"),
	AXE("axe");
	
	public final String toolClass;
	
	EnumToolClass(String s)
	{ toolClass = s; }
	
	public static final int STONE = 1;
	public static final int IRON = 2;
	public static final int DIAMOND = 3;
	
	/** Material 4th tier, can mine Ardite & Cobalt */
	public static final int ALUMITE = 4;
}