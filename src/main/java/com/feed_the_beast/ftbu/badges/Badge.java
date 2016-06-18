package com.feed_the_beast.ftbu.badges;

import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbl.api.client.gui.GuiLM;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.latmod.lib.FinalIDObject;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

public class Badge extends FinalIDObject
{
    public static final ResourceLocation defTex = new ResourceLocation(FTBUFinals.MOD_ID, "textures/failed_badge.png");

    // -- //

    public final String imageURL;
    private ResourceLocation textureURL = null;

    public Badge(String id, String url)
    {
        super(id);
        imageURL = url;
    }

    @Nonnull
    @Override
    public String toString()
    {
        return getID() + '=' + imageURL;
    }

    @SideOnly(Side.CLIENT)
    public ResourceLocation getTexture()
    {
        if(imageURL == null)
        {
            return null;
        }

        if(textureURL == null)
        {
            textureURL = new ResourceLocation(FTBUFinals.MOD_ID, "badges/" + getID() + ".png");
            FTBLibClient.getDownloadImage(textureURL, imageURL, defTex, null);
        }

        return textureURL;
    }

    @SideOnly(Side.CLIENT)
    public void onPlayerRender(EntityPlayer ep)
    {
        ResourceLocation texture = getTexture();
        if(texture == null)
        {
            return;
        }

        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        FTBLibClient.setTexture(texture);
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
        GuiLM.drawTexturedRect(0D, 0D, 0.2D, 0.2D, 0D, 0D, 1D, 1D);

        FTBLibClient.popMaxBrightness();
        GlStateManager.popMatrix();
    }
}