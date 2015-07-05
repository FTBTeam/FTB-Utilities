package latmod.ftbu.core.event;

import latmod.ftbu.core.util.FastList;
import latmod.ftbu.core.world.LMPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.relauncher.Side;

public abstract class LMPlayerEvent extends EventLM
{
	public final LMPlayer player;
	
	public LMPlayerEvent(LMPlayer p)
	{ player = p; }
	
	public static class DataChanged extends LMPlayerEvent
	{
		public final String action;
		
		public DataChanged(LMPlayer p, Side s, String b)
		{ super(p); action = b; }
		
		public boolean isAction(String b)
		{ return action == b || action.equals(b); }
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
	
	public static class LoggedInClient extends LMPlayerEvent
	{
		public final boolean firstTime;
		
		public LoggedInClient(LMPlayer p, boolean b)
		{ super(p); firstTime = b; }
	}
	
	public static class LoggedOut extends LMPlayerEvent
	{
		public final EntityPlayerMP playerMP;
		
		public LoggedOut(LMPlayer p, EntityPlayerMP ep)
		{ super(p); playerMP = ep; }
	}
	
	public static class LoggedOutClient extends LMPlayerEvent
	{
		public LoggedOutClient(LMPlayer p)
		{ super(p); }
	}
	
	public static class CustomInfo extends LMPlayerEvent
	{
		public final FastList<String> info;
		public final Side side;
		
		public CustomInfo(LMPlayer p, Side s, FastList<String> l)
		{ super(p); side = s; info = l; }
	}
}