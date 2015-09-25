package latmod.ftbu.mod;

import cpw.mods.fml.common.Loader;

public class FTBUFinals // FTBU
{
	public static final String MOD_ID = "FTBU";
	public static final String MOD_VERSION = "@VERSION@";
	public static final String MOD_NAME = "FTBUtilities";
	public static final String MOD_DEP = "required-after:Forge@[10.13.4.1448,);after:Baubles;after:NotEnoughItems;after:Waila";
	
	public static final boolean DEV = MOD_VERSION.indexOf('@') != -1;
	public static final String MC_VERSION = Loader.MC_VERSION;
	public static final String MOD_VERSION_DISPLAY = DEV ? "Dev" : MOD_VERSION;
}