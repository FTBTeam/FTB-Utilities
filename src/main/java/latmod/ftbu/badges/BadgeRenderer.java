package latmod.ftbu.badges;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.*;
import latmod.ftbu.mod.client.FTBUClient;
import latmod.ftbu.util.client.LatCoreMCClient;
import latmod.ftbu.world.*;
import net.minecraftforge.client.event.RenderPlayerEvent;

@SideOnly(Side.CLIENT)
public class BadgeRenderer
{
	public static final BadgeRenderer instance = new BadgeRenderer();
	
	@SubscribeEvent
	public void onPlayerRender(RenderPlayerEvent.Specials.Post e)
	{
		if(LatCoreMCClient.isPlaying() && FTBUClient.render_badges.get() && !e.entityPlayer.isInvisible())
		{
			LMPlayerClient pc = LMWorldClient.inst.getPlayer(e.entityPlayer);
			
			if(pc != null && pc.renderBadge)
			{
				Badge b = ClientBadges.getClientBadge(pc.playerID);
				b.onPlayerRender(e.entityPlayer);
			}
		}
	}
}