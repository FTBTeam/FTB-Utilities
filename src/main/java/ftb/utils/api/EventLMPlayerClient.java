package ftb.utils.api;

import cpw.mods.fml.relauncher.Side;
import ftb.utils.world.LMPlayer;
import ftb.utils.world.LMPlayerClient;

import java.util.List;

public abstract class EventLMPlayerClient extends EventLMPlayer
{
	public final LMPlayerClient player;
	
	public EventLMPlayerClient(LMPlayerClient p)
	{ player = p; }
	
	@Override
	public LMPlayer getPlayer()
	{ return player; }
	
	@Override
	public Side getSide()
	{ return Side.CLIENT; }
	
	// Events //
	
	public static class DataChanged extends EventLMPlayerClient
	{
		public DataChanged(LMPlayerClient p)
		{ super(p); }
	}
	
	public static class LoggedIn extends EventLMPlayerClient
	{
		public final boolean firstTime;
		
		public LoggedIn(LMPlayerClient p, boolean b)
		{
			super(p);
			firstTime = b;
		}
	}
	
	public static class LoggedOut extends EventLMPlayerClient
	{
		public LoggedOut(LMPlayerClient p)
		{ super(p); }
	}
	
	public static class DataLoaded extends EventLMPlayerClient
	{
		public DataLoaded(LMPlayerClient p)
		{ super(p); }
	}
	
	public static class CustomInfo extends EventLMPlayerClient
	{
		public final List<String> info;
		
		public CustomInfo(LMPlayerClient p, List<String> l)
		{
			super(p);
			info = l;
		}
	}
	
	public static class PlayerDied extends EventLMPlayerClient
	{
		public PlayerDied(LMPlayerClient p)
		{ super(p); }
	}
}