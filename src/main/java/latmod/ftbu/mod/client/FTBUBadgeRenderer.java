package latmod.ftbu.mod.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.*;
import latmod.ftbu.badges.*;
import latmod.ftbu.net.ClientAction;
import latmod.ftbu.util.client.LatCoreMCClient;
import latmod.ftbu.world.*;
import net.minecraftforge.client.event.RenderPlayerEvent;

@SideOnly(Side.CLIENT)
public class FTBUBadgeRenderer
{
	public static final FTBUBadgeRenderer instance = new FTBUBadgeRenderer();
	
	@SubscribeEvent
	public void onPlayerRender(RenderPlayerEvent.Specials.Post e)
	{
		if(LatCoreMCClient.isPlaying() && FTBUClient.render_badges.get() && !e.entityPlayer.isInvisible())
		{
			LMPlayerClient pc = LMWorldClient.inst.getPlayer(e.entityPlayer);
			
			if(pc != null && pc.renderBadge)
			{
				Integer id = Integer.valueOf(pc.playerID);
				if(ClientBadges.playerBadges.containsKey(id))
				{
					Badge b = ClientBadges.playerBadges.get(id);
					if(b != null) b.onPlayerRender(e.entityPlayer);
				}
				else
				{
					ClientBadges.playerBadges.put(id, Badge.emptyBadge);
					ClientAction.ACTION_REQUEST_BADGE.send(pc.playerID);
				}
			}
		}
	}
}