package latmod.ftbu.api;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.relauncher.Side;
import latmod.core.util.FastList;
import latmod.ftbu.world.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IChatComponent;

public abstract class EventLMPlayerServer extends EventLMPlayer // LMPlayerClientEvent
{
	public final LMPlayerServer player;
	
	public EventLMPlayerServer(LMPlayerServer p)
	{ player = p; }
	
	public LMPlayer getPlayer()
	{ return player; }
	
	public Side getSide()
	{ return Side.SERVER; }
	
	// Events //
	
	public static class DataChanged extends EventLMPlayerServer
	{
		public DataChanged(LMPlayerServer p)
		{ super(p); }
	}
	
	public static class GroupsChanged extends EventLMPlayerServer
	{
		public GroupsChanged(LMPlayerServer p)
		{ super(p); }
	}
	
	public static class DataLoaded extends EventLMPlayerServer
	{
		public DataLoaded(LMPlayerServer p)
		{ super(p); }
	}
	
	public static class DataSaved extends EventLMPlayerServer
	{
		public DataSaved(LMPlayerServer p)
		{ super(p); }
	}
	
	public static class LoggedIn extends EventLMPlayerServer
	{
		public final EntityPlayerMP playerMP;
		public final boolean firstTime;
		
		public LoggedIn(LMPlayerServer p, EntityPlayerMP ep, boolean b)
		{ super(p); playerMP = ep; firstTime = b; }
	}
	
	public static class LoggedOut extends EventLMPlayerServer
	{
		public final EntityPlayerMP playerMP;
		
		public LoggedOut(LMPlayerServer p, EntityPlayerMP ep)
		{ super(p); playerMP = ep; }
	}
	
	public static class CustomInfo extends EventLMPlayerServer
	{
		public final FastList<IChatComponent> info;
		
		public CustomInfo(LMPlayerServer p, FastList<IChatComponent> l)
		{ super(p); info = l; }
	}
	
	@Cancelable
	public static class GetMaxClaimPower extends EventLMPlayerServer
	{
		public final int config;
		public int result;
		
		public GetMaxClaimPower(LMPlayerServer p, int c)
		{
			super(p);
			config = result = c;
		}
	}
}