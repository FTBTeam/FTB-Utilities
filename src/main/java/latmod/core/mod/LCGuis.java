package latmod.core.mod;

public class LCGuis
{
	private static int nextGuiID = 0;
	public static final int nextGuiID()
	{ return ++nextGuiID; }
	
	public static final int SECURITY = nextGuiID();
	public static final int STORAGE_UNIT = nextGuiID();
	
	public static class Buttons
	{
		private static int nextButtonID = 0;
		public static final int nextButtonID()
		{ return ++nextButtonID; }
		
		public static final int SECURITY = nextButtonID();
	}
}