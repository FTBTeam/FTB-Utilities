package latmod.core.event;

import latmod.core.LMPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public abstract class LMPlayerEvent extends EventLM
{
	public final LMPlayer player;
	
	public LMPlayerEvent(LMPlayer p)
	{ player = p; }
	
	public static class DataChanged extends LMPlayerEvent
	{
		public final String action;
		
		public DataChanged(LMPlayer p, String b)
		{ super(p); action = b; }
		
		public boolean isAction(String b)
		{ return action == b; }
	}
	
	public static class DataLoaded extends LMPlayerEvent
	{
		public DataLoaded(LMPlayer p)
		{ super(p); }
	}
	
	public static class DataSaved extends LMPlayerEvent
	{
		public DataSaved(LMPlayer p)
		{ super(p); }
	}
	
	public static class LoggedIn extends LMPlayerEvent
	{
		public final EntityPlayerMP playerMP;
		public final boolean firstTime;
		
		public LoggedIn(LMPlayer p, EntityPlayerMP ep, boolean b)
		{ super(p); playerMP = ep; firstTime = b; }
	}
	
	public static class LoggedOut extends LMPlayerEvent
	{
		public final EntityPlayerMP playerMP;
		
		public LoggedOut(LMPlayer p, EntityPlayerMP ep)
		{ super(p); playerMP = ep; }
	}
}