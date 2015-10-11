package latmod.ftbu.mod.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.*;
import latmod.ftbu.badges.*;
import latmod.ftbu.util.EventBusHelper;
import latmod.ftbu.world.*;
import net.minecraftforge.client.event.RenderPlayerEvent;

@SideOnly(Side.CLIENT)
public class FTBUBadgeRenderer
{
	public static final FTBUBadgeRenderer instance = new FTBUBadgeRenderer();
	public static boolean isEnabled = false;
	
	public void enable(boolean enable)
	{
		if(isEnabled != enable)
		{
			isEnabled = true;
			
			if(enable) EventBusHelper.register(this);
			else EventBusHelper.unregister(this);
		}
	}
	
	@SubscribeEvent
	public void onPlayerRender(RenderPlayerEvent.Specials.Post e)
	{
		if(!Badge.badges.isEmpty() && FTBUClient.renderBadges.getB() && !e.entityPlayer.isInvisible())
		{
			LMPlayerClient pc = LMWorldClient.inst.getPlayer(e.entityPlayer);
			
			if(pc != null && pc.settings.renderBadge)
			{
				if(pc.cachedBadge == null)
				{
					pc.cachedBadge = Badge.badges.get(pc.getUUID());
					if(pc.cachedBadge == null) pc.cachedBadge = new BadgeEmpty();
				}
				
				pc.cachedBadge.onPlayerRender(e.entityPlayer);
			}
		}
	}
}