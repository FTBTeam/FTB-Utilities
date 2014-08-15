package latmod.core.mod;

public class LCGuis
{
	private static int nextGuiID = 0;
	public static final int nextGuiID()
	{ return ++nextGuiID; }
	
	public static final int STORAGE_UNIT = nextGuiID();
	
	public static class Buttons
	{
		public static final String SECURITY = "security";
		public static final String REDSTONE = "redstone";
		public static final String INV_MODE = "inv_mode";
	}
}