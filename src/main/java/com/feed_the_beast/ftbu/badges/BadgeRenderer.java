package com.feed_the_beast.ftbu.badges;

import com.feed_the_beast.ftbl.api.ForgePlayerSP;
import com.feed_the_beast.ftbl.api.ForgeWorldSP;
import com.feed_the_beast.ftbu.client.FTBUClient;
import com.feed_the_beast.ftbu.world.data.FTBUPlayerData;
import com.feed_the_beast.ftbu.world.data.FTBUWorldData;
import com.feed_the_beast.ftbu.world.data.FTBUWorldDataSP;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public enum BadgeRenderer implements LayerRenderer<AbstractClientPlayer>
{
    INSTANCE;

    @Override
    public void doRenderLayer(@Nonnull AbstractClientPlayer ep, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        if(FTBUWorldData.isLoadedW(ForgeWorldSP.inst) && FTBUClient.render_badges.getAsBoolean() && !ep.isInvisible())
        {
            Badge b = FTBUWorldDataSP.getClientBadge(ep.getGameProfile().getId());

            if(b != null)
            {
                ForgePlayerSP pc = ForgeWorldSP.inst.getPlayer(ep);

                if(pc != null && FTBUPlayerData.get(pc).renderBadge.getAsBoolean())
                {
                    b.onPlayerRender(ep);
                }
            }
        }
    }

    @Override
    public boolean shouldCombineTextures()
    {
        return false;
    }
}