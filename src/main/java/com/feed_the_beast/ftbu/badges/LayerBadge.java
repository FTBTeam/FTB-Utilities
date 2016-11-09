package com.feed_the_beast.ftbu.badges;

import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.client.CachedClientData;
import com.feed_the_beast.ftbu.client.FTBUClientConfig;
import com.feed_the_beast.ftbu.net.MessageRequestBadge;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SideOnly(Side.CLIENT)
public enum LayerBadge implements LayerRenderer<AbstractClientPlayer>
{
    INSTANCE;

    public static final ResourceLocation DEF_TEX = new ResourceLocation(FTBUFinals.MOD_ID, "textures/failed_badge.png");
    public static final Map<UUID, ResourceLocation> CACHE = new HashMap<>();

    @Override
    public void doRenderLayer(AbstractClientPlayer ep, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        if(FTBUClientConfig.RENDER_BADGES.getBoolean() && !ep.isInvisible())
        {
            UUID id = ep.getGameProfile().getId();
            ResourceLocation tex = CACHE.get(id);

            if(tex == null)
            {
                CACHE.put(id, DEF_TEX);
                new MessageRequestBadge(id).sendToServer();
                return;
            }
            else if(tex == DEF_TEX)
            {
                String url = CachedClientData.LOCAL_BADGES.map.get(id);

                if(url != null)
                {
                    tex = new ResourceLocation(FTBUFinals.MOD_ID, "badges/" + url.replace(':', '.'));
                    FTBLibClient.getDownloadImage(tex, url, DEF_TEX, null);
                    CACHE.put(id, tex);
                }

                return;
            }

            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GlStateManager.enableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            FTBLibClient.setTexture(tex);
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