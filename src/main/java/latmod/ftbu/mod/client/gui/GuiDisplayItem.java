package latmod.ftbu.mod.client.gui;

import latmod.ftbu.core.gui.*;
import latmod.ftbu.core.inv.ItemDisplay;
import latmod.ftbu.core.util.FastList;
import latmod.ftbu.mod.FTBU;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.*;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class GuiDisplayItem extends GuiLM
{
	public static final ResourceLocation texture = FTBU.mod.getLocation("textures/gui/displayitem.png");
	
	public ItemDisplay itemDisplay;
	
	public GuiDisplayItem(ItemDisplay i)
	{
		super(null, texture);
		xSize = 144;
		ySize = 182;
		itemDisplay = i;
	}
	
	public void addWidgets(FastList<WidgetLM> l)
	{
	}
	
	public void drawBackground()
	{
		super.drawBackground();
		
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
		FontRenderer font = itemDisplay.item.getItem().getFontRenderer(itemDisplay.item);
		if (font == null) font = fontRendererObj;
		
		GL11.glScalef(itemDisplay.scale, itemDisplay.scale, 1F);
		
		itemRender.renderItemAndEffectIntoGUI(font, Minecraft.getMinecraft().getTextureManager(), itemDisplay.item, -8, -8);
		
		zLevel = 0;
		itemRender.zLevel = 0F;
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
		
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public void drawText(FastList<String> l)
	{
		if(itemDisplay.title != null && !itemDisplay.title.isEmpty()) drawCenteredString(fontRendererObj, itemDisplay.title, guiLeft + xSize / 2, guiTop + 6, 0xFFFFFFFF);
		if(itemDisplay.desc != null && !itemDisplay.desc.isEmpty()) l.addAll(itemDisplay.desc);
		super.drawText(l);
	}
	
	public boolean handleDragNDrop(GuiContainer g, int x, int y, ItemStack is, int b)
	{
		if(is != null && x > guiLeft && x < guiLeft + xSize && y > guiTop && y < guiTop + ySize)
		{
			itemDisplay = new ItemDisplay(is.copy(), is.getDisplayName(), null, itemDisplay.scale);
			is.stackSize = 0;
			return true;
		}
		
		return false;
	}
}