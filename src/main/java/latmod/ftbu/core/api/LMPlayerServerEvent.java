package latmod.ftbu.core.api;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.relauncher.Side;
import latmod.ftbu.core.util.FastList;
import latmod.ftbu.core.world.*;
import net.minecraft.entity.player.EntityPlayerMP;

public abstract class LMPlayerServerEvent extends LMPlayerEvent // LMPlayerClientEvent
{
	public final LMPlayerServer player;
	
	public LMPlayerServerEvent(LMPlayerServer p)
	{ player = p; }
	
	public LMPlayer getPlayer()
	{ return player; }
	
	public Side getSide()
	{ return Side.SERVER; }
	
	// Events //
	
	public static class DataChanged extends LMPlayerServerEvent
	{
		public DataChanged(LMPlayerServer p)
		{ super(p); }
	}
	
	public static class GroupsChanged extends LMPlayerServerEvent
	{
		public GroupsChanged(LMPlayerServer p)
		{ super(p); }
	}
	
	public static class DataLoaded extends LMPlayerServerEvent
	{
		public DataLoaded(LMPlayerServer p)
		{ super(p); }
	}
	
	public static class DataSaved extends LMPlayerServerEvent
	{
		public DataSaved(LMPlayerServer p)
		{ super(p); }
	}
	
	public static class LoggedIn extends LMPlayerServerEvent
	{
		public final EntityPlayerMP playerMP;
		public final boolean firstTime;
		
		public LoggedIn(LMPlayerServer p, EntityPlayerMP ep, boolean b)
		{ super(p); playerMP = ep; firstTime = b; }
	}
	
	public static class LoggedOut extends LMPlayerServerEvent
	{
		public final EntityPlayerMP playerMP;
		
		public LoggedOut(LMPlayerServer p, EntityPlayerMP ep)
		{ super(p); playerMP = ep; }
	}
	
	public static class CustomInfo extends LMPlayerServerEvent
	{
		public final FastList<String> info;
		
		public CustomInfo(LMPlayerServer p, FastList<String> l)
		{ super(p); info = l; }
	}
	
	@Cancelable
	public static class GetMaxClaimPower extends LMPlayerServerEvent
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