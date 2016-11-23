package com.feed_the_beast.ftbu.badges;

import com.feed_the_beast.ftbl.lib.client.FTBLibClient;
import com.feed_the_beast.ftbu.client.CachedClientData;
import com.feed_the_beast.ftbu.client.FTBUClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.UUID;

public enum LayerBadge implements LayerRenderer<AbstractClientPlayer>
{
    INSTANCE;

    @Override
    public void doRenderLayer(AbstractClientPlayer ep, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        if(FTBUClientConfig.RENDER_BADGES.getBoolean() && !ep.isInvisible())
        {
            UUID id = ep.getGameProfile().getId();
            ResourceLocation tex = CachedClientData.getBadgeTexture(id);

            if(tex.equals(CachedClientData.NO_BADGE))
            {
                return;
            }

            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GlStateManager.enableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            Minecraft.getMinecraft().getTextureManager().bindTexture(tex);
            FTBLibClient.pushMaxBrightness();
            GlStateManager.pushMatrix();

            if(ep.isSneaking())
            {
                GlStateManager.rotate(25F, 1F, 0F, 0F);
            }

            GlStateManager.translate(0.04F, 0.01F, 0.86F);

            ItemStack armor = ep.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
            if(armor != null && armor.getItem().isValidArmor(armor, EntityEquipmentSlot.CHEST, ep))
            {
                GlStateManager.translate(0F, 0F, -0.0625F);
            }

            GlStateManager.translate(0F, 0F, -1F);
            GlStateManager.color(1F, 1F, 1F, 1F);
            Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            buffer.pos(0D, 0.2D, 0D).tex(0D, 1D).endVertex();
            buffer.pos(0.2D, 0.2D, 0D).tex(1D, 1D).endVertex();
            buffer.pos(0.2D, 0D, 0D).tex(1D, 0D).endVertex();
            buffer.pos(0D, 0D, 0D).tex(0D, 0D).endVertex();
            tessellator.draw();

            FTBLibClient.popMaxBrightness();
            GlStateManager.popMatrix();
        }
    }

    @Override
    public boolean shouldCombineTextures()
    {
        return false;
    }
}