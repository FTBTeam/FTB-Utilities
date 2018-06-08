package com.feed_the_beast.ftbutilities.client;

import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftbutilities.FTBUtilitiesItems;
import com.feed_the_beast.ftbutilities.handlers.FTBUtilitiesClientEventHandler;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.UUID;

public enum LayerBadge implements LayerRenderer<AbstractClientPlayer>
{
	INSTANCE;

	@Override
	public void doRenderLayer(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		if (FTBUtilitiesClientConfig.general.render_badges && !player.isInvisible())
		{
			UUID id = player.getGameProfile().getId();
			Icon tex = FTBUtilitiesClientEventHandler.getBadge(id);

			if (tex.isEmpty())
			{
				return;
			}

			GlStateManager.disableLighting();
			GlStateManager.disableCull();
			GlStateManager.enableTexture2D();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			ClientUtils.pushMaxBrightness();
			GlStateManager.pushMatrix();

			GlStateManager.translate(0.04F, 0.01F, 0.86F);

			if (player.isSneaking())
			{
				GlStateManager.rotate(25F, 1F, 0F, 0F);
				GlStateManager.translate(0F, -0.18F, 0F);
			}

			ItemStack armor = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
			if (FTBUtilitiesItems.SILENTGEMS_CHESTPLATE != Items.AIR && armor.getItem() == FTBUtilitiesItems.SILENTGEMS_CHESTPLATE)
			{
				GlStateManager.translate(0F, 0F, -0.1F);
			}
			else if (!armor.isEmpty() && armor.getItem().isValidArmor(armor, EntityEquipmentSlot.CHEST, player))
			{
				GlStateManager.translate(0F, 0F, -0.0625F);
			}
			else if (player.isWearing(EnumPlayerModelParts.JACKET))
			{
				GlStateManager.translate(0F, 0F, -0.02125F);
			}

			GlStateManager.translate(0F, 0F, -1F);
			GlStateManager.scale(0.2D, 0.2D, 0.125D);
			GlStateManager.color(1F, 1F, 1F, 1F);
			GlStateManager.disableCull();
			tex.draw(0, 0, 1, 1);
			GlStateManager.enableCull();
			ClientUtils.popBrightness();
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean shouldCombineTextures()
	{
		return false;
	}
}