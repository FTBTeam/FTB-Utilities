package ftb.utils.badges;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ftb.lib.api.client.FTBLibClient;
import ftb.utils.mod.client.FTBUClient;
import ftb.utils.world.LMPlayerClient;
import ftb.utils.world.LMWorldClient;
import net.minecraftforge.client.event.RenderPlayerEvent;

@SideOnly(Side.CLIENT)
public class BadgeRenderer
{
	public static final BadgeRenderer instance = new BadgeRenderer();
	
	@SubscribeEvent
	public void onPlayerRender(RenderPlayerEvent.Specials.Post e)
	{
		if(FTBLibClient.isIngameWithFTBU() && FTBUClient.render_badges.getAsBoolean() && !e.entityPlayer.isInvisible())
		{
			Badge b = ClientBadges.getClientBadge(e.entityPlayer.getGameProfile().getId());
			
			if(b != null && b != Badge.emptyBadge)
			{
				LMPlayerClient pc = LMWorldClient.inst.getPlayer(e.entityPlayer);
				
				if(pc != null && pc.renderBadge)
				{
					b.onPlayerRender(e.entityPlayer);
				}
			}
		}
	}
}