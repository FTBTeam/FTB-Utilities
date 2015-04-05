package latmod.core.event;

import latmod.core.LMPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.relauncher.*;

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
		public final EntityPlayerMP playerMP;
		public final boolean firstTime;
		
		public LoggedIn(LMPlayer p, EntityPlayerMP ep, boolean b)
		{ super(p); playerMP = ep; firstTime = b; }
	}
	
	@SideOnly(Side.CLIENT)
	public static class LoggedInClient extends LMPlayerEvent
	{
		public final EntityPlayerSP playerSP;
		
		public LoggedInClient(LMPlayer p, EntityPlayerSP ep)
		{
			super(p);
			playerSP = ep;
		}
	}
	
	public static class LoggedOut extends LMPlayerEvent
	{
		public final EntityPlayerMP playerMP;
		
		public LoggedOut(LMPlayer p, EntityPlayerMP ep)
		{ super(p); playerMP = ep; }
	}
	
	@SideOnly(Side.CLIENT)
	public static class LoggedOutClient extends LMPlayerEvent
	{
		public final EntityPlayerSP playerSP;
		
		public LoggedOutClient(LMPlayer p, EntityPlayerSP ep)
		{
			super(p);
			playerSP = ep;
		}
	}
}