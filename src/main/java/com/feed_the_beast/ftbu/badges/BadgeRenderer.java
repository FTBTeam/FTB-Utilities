package com.feed_the_beast.ftbu.badges;

import com.feed_the_beast.ftbl.api.ForgePlayerSP;
import com.feed_the_beast.ftbl.api.ForgeWorldSP;
import com.feed_the_beast.ftbu.client.FTBUClient;
import com.feed_the_beast.ftbu.world.FTBUPlayerData;
import com.feed_the_beast.ftbu.world.FTBUPlayerDataSP;
import com.feed_the_beast.ftbu.world.FTBUWorldDataSP;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BadgeRenderer implements LayerRenderer<AbstractClientPlayer>
{
    public static final BadgeRenderer instance = new BadgeRenderer();

    @Override
    public void doRenderLayer(AbstractClientPlayer ep, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale)
    {
        if(FTBUWorldDataSP.isLoaded() && FTBUClient.render_badges.getAsBoolean() && !ep.isInvisible())
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

    @Override
    public boolean shouldCombineTextures()
    { return false; }
}