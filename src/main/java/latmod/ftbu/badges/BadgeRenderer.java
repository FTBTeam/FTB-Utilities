package latmod.ftbu.badges;

import ftb.lib.FTBLibClient;
import latmod.ftbu.mod.client.FTBUClient;
import latmod.ftbu.world.*;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraftforge.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class BadgeRenderer implements LayerRenderer<AbstractClientPlayer>
{
	public static final BadgeRenderer instance = new BadgeRenderer();
	
	public void doRenderLayer(AbstractClientPlayer ep, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale)
	{
		if(FTBLibClient.isIngameWithFTBU() && FTBUClient.render_badges.get() && !ep.isInvisible())
		{
			LMPlayerClient pc = LMWorldClient.inst.getPlayer(ep);
			
			if(pc != null && pc.renderBadge)
			{
				Badge b = ClientBadges.getClientBadge(pc.playerID);
				b.onPlayerRender(ep);
			}
		}
	}
	
	public boolean shouldCombineTextures()
	{ return false; }
}