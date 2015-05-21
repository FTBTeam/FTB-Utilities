package latmod.core.mod.client;

import latmod.core.gui.*;
import latmod.core.mod.LC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.*;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class GuiDisplayBlock extends GuiLM
{
	public static final ResourceLocation texture = LC.mod.getLocation("textures/gui/block.png");
	
	public final ItemStack item;
	public final String title;
	public final String desc;
	
	public GuiDisplayBlock(EntityPlayer ep, ItemStack is, String t, String d)
	{
		super(new ContainerEmpty(ep, null), texture);
		System.out.println("Test! " + is);
		xSize = 128;
		ySize = 128;
		item = is;
		title = t;
		desc = d;
	}
	
	public void drawGuiContainerBackgroundLayer(float f, int mx, int my)
	{
		super.drawGuiContainerBackgroundLayer(f, mx, my);
		
		GL11.glPushMatrix();
		GL11.glTranslatef(0F, 0F, 32F);
		GL11.glEnable(GL11.GL_LIGHTING);
		RenderHelper.enableGUIStandardItemLighting();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		zLevel = 200;
		itemRender.zLevel = 200F;
		FontRenderer font = item.getItem().getFontRenderer(item);
		if (font == null) font = fontRendererObj;
		
		GL11.glPushMatrix();
		GL11.glTranslatef(guiLeft + xSize / 4F, guiTop + ySize / 4F - 2F, 0F);
		GL11.glScalef(4F, 4F, 1F);
		itemRender.renderItemAndEffectIntoGUI(font, Minecraft.getMinecraft().getTextureManager(), item, 0, 0);
		GL11.glPopMatrix();
		
		zLevel = 0;
		itemRender.zLevel = 0F;
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}
	
	public void drawText(int mx, int my)
	{
		drawCenteredString(fontRendererObj, title.isEmpty() ? item.getDisplayName() : title, guiLeft + xSize / 2, guiTop + 6, 0xFFFFFFFF);
		if(!desc.isEmpty()) drawCenteredString(fontRendererObj, desc, guiLeft + xSize / 2, guiTop + ySize - 14, 0xFFFFFFFF);
		super.drawText(mx, my);
	}
}