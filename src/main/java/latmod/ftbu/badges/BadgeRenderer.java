package latmod.ftbu.badges;

import ftb.lib.client.FTBLibClient;
import latmod.ftbu.mod.client.FTBUClient;
import latmod.ftbu.world.*;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class BadgeRenderer
{
	public static final BadgeRenderer instance = new BadgeRenderer();
	
	@SubscribeEvent
	public void onPlayerRender(RenderPlayerEvent.Specials.Post e)
	{
		if(FTBLibClient.isPlayingWithFTBU() && FTBUClient.render_badges.get() && !e.entityPlayer.isInvisible())
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