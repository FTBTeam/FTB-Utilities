package latmod.ftbu.core.api;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.core.util.FastList;
import latmod.ftbu.core.world.*;
import net.minecraft.util.IChatComponent;

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
	
	public static class DataChanged extends LMPlayerClientEvent
	{
		public DataChanged(LMPlayerClient p)
		{ super(p); }
	}
	
	public static class LoggedIn extends LMPlayerClientEvent
	{
		public final boolean firstTime;
		
		public LoggedIn(LMPlayerClient p, boolean b)
		{ super(p); firstTime = b; }
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
		public final FastList<IChatComponent> info;
		
		public CustomInfo(LMPlayerClient p, FastList<IChatComponent> l)
		{ super(p); info = l; }
	}
	
	public static class PlayerDied extends LMPlayerClientEvent
	{
		public PlayerDied(LMPlayerClient p)
		{ super(p); }
	}
}