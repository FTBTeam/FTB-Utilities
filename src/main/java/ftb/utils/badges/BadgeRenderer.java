package ftb.utils.badges;

import ftb.lib.api.client.FTBLibClient;
import ftb.lib.api.friends.*;
import ftb.utils.mod.client.FTBUClient;
import ftb.utils.mod.handlers.ftbl.FTBUPlayerData;
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
			LMPlayerSP pc = LMWorldSP.inst.getPlayer(ep);
			
			if(pc != null && ((FTBUPlayerData) pc.customData.get("ftbu")).getFlag(FTBUPlayerData.RENDER_BADGE))
			{
				Badge b = ClientBadges.getClientBadge(pc.getPlayerID());
				b.onPlayerRender(ep);
			}
		}
	}
	
	public boolean shouldCombineTextures()
	{ return false; }
}