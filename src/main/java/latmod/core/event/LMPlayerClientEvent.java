package latmod.core.event;

import latmod.core.LMPlayer;
import latmod.core.util.FastList;
import net.minecraft.client.entity.EntityPlayerSP;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class LMPlayerClientEvent
{
	public static class DataChanged extends LMPlayerEvent
	{
		public final String action;
		
		public DataChanged(LMPlayer p, String b)
		{ super(p); action = b; }
		
		public boolean isAction(String b)
		{ return action == b; }
	}
	
	public static class LoggedIn extends LMPlayerEvent
	{
		public final EntityPlayerSP playerSP;
		
		public LoggedIn(LMPlayer p, EntityPlayerSP ep)
		{
			super(p);
			playerSP = ep;
		}
	}
	
	public static class LoggedOut extends LMPlayerEvent
	{
		public final EntityPlayerSP playerSP;
		
		public LoggedOut(LMPlayer p, EntityPlayerSP ep)
		{
			super(p);
			playerSP = ep;
		}
	}
	
	public static class CustomInfo extends LMPlayerEvent
	{
		public final FastList<String> info;
		
		public CustomInfo(LMPlayer p, FastList<String> l)
		{ super(p); info = l; }
	}
}