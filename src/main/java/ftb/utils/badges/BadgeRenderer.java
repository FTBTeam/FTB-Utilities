package ftb.utils.badges;

import ftb.lib.api.*;
import ftb.utils.client.FTBUClient;
import ftb.utils.world.*;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraftforge.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class BadgeRenderer implements LayerRenderer<AbstractClientPlayer>
{
	public static final BadgeRenderer instance = new BadgeRenderer();
	
	public void doRenderLayer(AbstractClientPlayer ep, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale)
	{
		if(FTBUWorldDataSP.exists() && FTBUClient.render_badges.get() && !ep.isInvisible())
		{
			Badge b = ClientBadges.getClientBadge(ep.getGameProfile().getId());
			
			if(b != Badge.emptyBadge)
			{
				ForgePlayerSP pc = ForgeWorldSP.inst.getPlayer(ep);
				
				if(pc != null && FTBUPlayerDataSP.get(pc).getFlag(FTBUPlayerData.RENDER_BADGE))
				{
					b.onPlayerRender(ep);
				}
			}
		}
	}
	
	public boolean shouldCombineTextures()
	{ return false; }
}