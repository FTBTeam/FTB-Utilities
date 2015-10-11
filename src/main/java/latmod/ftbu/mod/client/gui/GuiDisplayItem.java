package latmod.ftbu.mod.client.gui;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.inv.ItemDisplay;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.util.gui.GuiLM;
import latmod.lib.FastList;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

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
	
	public void addWidgets()
	{
	}
	
	public void drawBackground()
	{
		super.drawBackground();
		
		GL11.glColor4f(1F, 1F, 1F, 1F);
		GL11.glPushMatrix();
		GL11.glTranslatef(guiLeft + xSize / 2F, guiTop + ySize / 2F, 0F);
		GL11.glScalef(itemDisplay.scale, itemDisplay.scale, 1F);
		itemDisplay.item.stackSize = 1;
		drawItem(itemDisplay.item, -8, -8);
		GL11.glPopMatrix();
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