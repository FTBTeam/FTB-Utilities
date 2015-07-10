package latmod.ftbu.core.event;

import latmod.ftbu.core.util.FastList;
import latmod.ftbu.core.world.*;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public abstract class LMPlayerClientEvent extends LMPlayerEvent
{
	public final LMPlayerClient player;
	
	public LMPlayerClientEvent(LMPlayerClient p)
	{ player = p; }
	
	public LMPlayer getPlayer()
	{ return player; }
	
	public Side getSide()
	{ return Side.CLIENT; }
	
	// Events //
	
	public static class LoggedIn extends LMPlayerClientEvent
	{
		public final boolean firstTime;
		
		public LoggedIn(LMPlayerClient p, boolean b)
		{ super(p); firstTime = b; }
	}
	
	public static class DataChanged extends LMPlayerClientEvent
	{
		public final String action;
		
		public DataChanged(LMPlayerClient p, String b)
		{ super(p); action = b; }
		
		public boolean isAction(String b)
		{ return action == b || action.equals(b); }
	}
	
	public static class LoggedOut extends LMPlayerClientEvent
	{
		public LoggedOut(LMPlayerClient p)
		{ super(p); }
	}
	
	public static class DataLoaded extends LMPlayerClientEvent
	{
		public DataLoaded(LMPlayerClient p)
		{ super(p); }
	}
	
	public static class CustomInfo extends LMPlayerClientEvent
	{
		public final FastList<String> info;
		
		public CustomInfo(LMPlayerClient p, FastList<String> l)
		{ super(p); info = l; }
	}
}