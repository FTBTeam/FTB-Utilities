package latmod.core.event;

import latmod.core.*;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.relauncher.Side;

public abstract class LMPlayerEvent extends EventLM
{
	public final LMPlayer player;
	
	public LMPlayerEvent(LMPlayer p)
	{ player = p; }
	
	public static class DataChanged extends LMPlayerEvent
	{
		public final Side side;
		public final String action;
		
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
		public final Side side;
		public final EntityPlayer entityPlayer;
		public final boolean firstTime;
		
		public LoggedIn(LMPlayer p, Side s, EntityPlayer ep, boolean b)
		{ super(p); side = s; entityPlayer = ep; firstTime = b; }
	}
	
	public static class LoggedOut extends LMPlayerEvent
	{
		public final Side side;
		public final EntityPlayer entityPlayer;
		
		public LoggedOut(LMPlayer p, Side s, EntityPlayer ep)
		{ super(p); side = s; entityPlayer = ep; }
	}
}