package latmod.ftbu.core.event;

import latmod.ftbu.core.LMPlayer;
import latmod.ftbu.core.util.FastList;
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
		public final Side side;
		
		public DataChanged(LMPlayer p, Side s, String b)
		{ super(p); side = s; action = b; }
		
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
		public final Side side;
		
		public LoggedIn(LMPlayer p, Side s, EntityPlayerMP ep, boolean b)
		{ super(p); side = s; playerMP = ep; firstTime = b; }
	}
	
	public static class LoggedOut extends LMPlayerEvent
	{
		public final EntityPlayerMP playerMP;
		public final Side side;
		
		public LoggedOut(LMPlayer p, Side s, EntityPlayerMP ep)
		{ super(p); side = s; playerMP = ep; }
	}
	
	public static class CustomInfo extends LMPlayerEvent
	{
		public final FastList<String> info;
		public final Side side;
		
		public CustomInfo(LMPlayer p, Side s, FastList<String> l)
		{ super(p); side = s; info = l; }
	}
}