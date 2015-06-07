package latmod.ftbu.client;

import latmod.ftbu.FTBU;
import latmod.ftbu.core.gui.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;

import org.lwjgl.opengl.*;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class GuiDisplayItem extends GuiLM
{
	public static final ResourceLocation texture = FTBU.mod.getLocation("textures/gui/displayitem.png");
	
	public ItemStack item;
	public String title;
	public String desc;
	public float scale;
	
	public GuiDisplayItem(EntityPlayer ep, ItemStack is, String t, String d, float s)
	{
		super(new ContainerEmpty(ep, null), texture);
		xSize = 144;
		ySize = 182;
		item = is;
		title = t;
		desc = d;
		scale = MathHelper.clamp_float(s, 1F, 8F);
	}
	
	public void drawGuiContainerBackgroundLayer(float f, int mx, int my)
	{
		super.drawGuiContainerBackgroundLayer(f, mx, my);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		GL11.glPushMatrix();
		GL11.glTranslatef(guiLeft + xSize / 2F, guiTop + ySize / 2F, 32F);
		GL11.glEnable(GL11.GL_LIGHTING);
		RenderHelper.enableGUIStandardItemLighting();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		zLevel = 200;
		itemRender.zLevel = 200F;
		FontRenderer font = item.getItem().getFontRenderer(item);
		if (font == null) font = fontRendererObj;
		
		GL11.glScalef(scale, scale, 1F);
		
		itemRender.renderItemAndEffectIntoGUI(font, Minecraft.getMinecraft().getTextureManager(), item, -8, -8);
		
		zLevel = 0;
		itemRender.zLevel = 0F;
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
		
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public void drawText(int mx, int my)
	{
		drawCenteredString(fontRendererObj, title.isEmpty() ? item.getDisplayName() : title, guiLeft + xSize / 2, guiTop + 6, 0xFFFFFFFF);
		if(!desc.isEmpty()) drawCenteredString(fontRendererObj, desc, guiLeft + xSize / 2, guiTop + ySize - 14, 0xFFFFFFFF);
		super.drawText(mx, my);
	}
}