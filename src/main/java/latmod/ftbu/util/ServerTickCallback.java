package latmod.ftbu.util;

public abstract class ServerTickCallback
{
	private int ticks = 0;
	public final int maxTick;
	
	public ServerTickCallback(int i)
	{ maxTick = Math.max(1, i); }
	
	public ServerTickCallback()
	{ this(1); }
	
	public boolean incAndCheck()
	{
		ticks++;
		if(ticks == maxTick)
		{
			onCallback();
			return true;
		}
		
		return false;
	}
	
	public abstract void onCallback();
}