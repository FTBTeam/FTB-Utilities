package com.feed_the_beast.ftbu.client;

import com.feed_the_beast.ftbl.api.gui.IDrawableObject;
import com.feed_the_beast.ftbl.lib.Color4I;
import com.feed_the_beast.ftbl.lib.client.FTBLibClient;
import com.feed_the_beast.ftbl.lib.client.ImageProvider;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.UUID;

public enum LayerBadge implements LayerRenderer<AbstractClientPlayer>
{
	INSTANCE;

	@Override
	public void doRenderLayer(AbstractClientPlayer ep, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		if (FTBUClientConfig.RENDER_BADGES.getBoolean() && !ep.isInvisible())
		{
			UUID id = ep.getGameProfile().getId();
			IDrawableObject tex = CachedClientData.getBadge(id);

			if (tex == ImageProvider.NULL)
			{
				return;
			}

			GlStateManager.disableLighting();
			GlStateManager.disableCull();
			GlStateManager.enableTexture2D();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			FTBLibClient.pushMaxBrightness();
			GlStateManager.pushMatrix();

			GlStateManager.translate(0.04F, 0.01F, 0.86F);

			if (ep.isSneaking())
			{
				GlStateManager.rotate(25F, 1F, 0F, 0F);
				GlStateManager.translate(0F, -0.18F, 0F);
			}

			ItemStack armor = ep.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
			if (!armor.isEmpty() && armor.getItem().isValidArmor(armor, EntityEquipmentSlot.CHEST, ep))
			{
				GlStateManager.translate(0F, 0F, -0.0625F);
			}
			else if (ep.isWearing(EnumPlayerModelParts.JACKET))
			{
				GlStateManager.translate(0F, 0F, -0.02125F);
			}

			GlStateManager.translate(0F, 0F, -1F);
			GlStateManager.scale(0.2D, 0.2D, 0.125D);
			GlStateManager.color(1F, 1F, 1F, 1F);
			GlStateManager.disableCull();
			tex.draw(0, 0, 1, 1, Color4I.NONE);
			GlStateManager.enableCull();
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