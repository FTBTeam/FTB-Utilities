package latmod.core.mod.gui;
import java.util.Map;

import latmod.core.LatCoreMC;
import latmod.core.client.LMRenderer;
import latmod.core.mod.LC;
import latmod.core.util.FastList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public abstract class GuiLM extends GuiContainer
{
	public static final ResourceLocation icons_lm = LatCoreMC.getLocation(LC.MOD_ID, "textures/gui/icons_lm.png");
	private static final int BS = 19;
	public static final TextureCoords
	
	button_basic = new TextureCoords(icons_lm, BS * 0, BS * 0),
	button_pressed = new TextureCoords(icons_lm, BS * 0, BS * 1),
	button_inner = new TextureCoords(icons_lm, BS * 1, BS * 0),
	button_inner_pressed = new TextureCoords(icons_lm, BS * 1, BS * 1),
	button_back = new TextureCoords(icons_lm, BS * 2, BS * 0),
	button_help = new TextureCoords(icons_lm, BS * 2, BS * 1),
	
	button_security_public = new TextureCoords(icons_lm, BS * 3, BS * 0),
	button_security_private = new TextureCoords(icons_lm, BS * 3, BS * 1),
	button_security_whitelist = new TextureCoords(icons_lm, BS * 4, BS * 0),
	button_security_blacklist = new TextureCoords(icons_lm, BS * 4, BS * 1),
	
	button_inv_both = new TextureCoords(icons_lm, BS * 5, BS * 0),
	button_inv_in = new TextureCoords(icons_lm, BS * 6, BS * 0),
	button_inv_out = new TextureCoords(icons_lm, BS * 5, BS * 1),
	button_inv_none = new TextureCoords(icons_lm, BS * 6, BS * 1),
	
	button_redstone_none = new TextureCoords(icons_lm, BS * 7, BS * 0),
	button_redstone_high = new TextureCoords(icons_lm, BS * 8, BS * 0),
	button_redstone_low = new TextureCoords(icons_lm, BS * 8, BS * 1),
	button_redstone_pulse = new TextureCoords(icons_lm, BS * 7, BS * 1);
	
	public static final TextureCoords[] button_security =
	{ button_security_public, button_security_private, button_security_whitelist, button_security_blacklist };
	
	public static final TextureCoords[] button_inv =
	{ button_inv_both, button_inv_in, button_inv_out, button_inv_none };
	
	public static final TextureCoords[] button_redstone =
	{ button_redstone_none, button_redstone_high, button_redstone_low };
	
	public static final TextureCoords[] button_redstone_wp =
	{ button_redstone_none, button_redstone_high, button_redstone_low, button_redstone_pulse };
	
	public final ContainerLM container;
	public final ResourceLocation texture;
	public final FastList<WidgetLM> widgets;
	public int textureWidth = 256;
	public int textureHeight = 256;
	
	public GuiLM(ContainerLM c, ResourceLocation tex)
	{
		super(c);
		container = c;
		texture = tex;
		widgets = new FastList<WidgetLM>();
	}
	
	public ItemStack getHeldItem()
	{ return container.player.inventory.getItemStack(); }
	
	public final int getPosX()
	{ return guiLeft; }
	
	public final int getPosY()
	{ return guiTop; }
	
	public final double getZLevel()
	{ return zLevel; }
	
	public final void setTexture(ResourceLocation tex)
	{ mc.getTextureManager().bindTexture(tex); }
	
	protected void mouseClicked(int mx, int my, int b)
	{
		for(int i = 0; i < widgets.size(); i++)
			widgets.get(i).voidMousePressed(mx, my, b);
		
		for(int i = 0; i < widgets.size(); i++)
		{
			if(widgets.get(i).mousePressed(mx, my, b))
				return;
		}
		
		super.mouseClicked(mx, my, b);
	}
	
	protected void keyTyped(char keyChar, int key)
	{
		for(int i = 0; i < widgets.size(); i++)
		{
			if(widgets.get(i).keyPressed(key, keyChar))
				return;
		}
		
		super.keyTyped(keyChar, key);
	}
	
	public void drawGuiContainerBackgroundLayer(float f, int mx, int my)
	{
		LMRenderer.recolor();
		setTexture(texture);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}
	
	public void drawWrappedIcon(IIcon i, float x, float y, float w, float h)
	{
		float minU = i.getMinU();
		float minV = i.getMinV();
		float maxU = i.getMaxU();
		float maxV = i.getMaxV();
		
		Tessellator t = Tessellator.instance;
		t.startDrawingQuads();
		t.addVertexWithUV(x + 0, y + h, zLevel, minU, maxV);
		t.addVertexWithUV(x + w, y + h, zLevel, maxU, maxV);
		t.addVertexWithUV(x + w, y + 0, zLevel, maxU, minV);
		t.addVertexWithUV(x + 0, y + 0, zLevel, minU, minV);
		t.draw();
	}
	
	public void drawTexturedModalRect(int x, int y, int u, int v, int w, int h)
	{
		double scX = 1D / (double)textureWidth;
		double scY = 1D / (double)textureHeight;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x + 0, y + h, zLevel, (u + 0) * scX, (v + h) * scY);
		tessellator.addVertexWithUV(x + w, y + h, zLevel, (u + w) * scX, (v + h) * scY);
		tessellator.addVertexWithUV(x + w, y + 0, zLevel, (u + w) * scX, (v + 0) * scY);
		tessellator.addVertexWithUV(x + 0, y + 0, zLevel, (u + 0) * scX, (v + 0) * scY);
		tessellator.draw();
	}
	
	public void drawTexturedModalRectD(double x, double y, double u, double v, double w, double h)
	{
		double scX = 1D / (double)textureWidth;
		double scY = 1D / (double)textureHeight;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x + 0, y + h, zLevel, (u + 0) * scX, (v + h) * scY);
		tessellator.addVertexWithUV(x + w, y + h, zLevel, (u + w) * scX, (v + h) * scY);
		tessellator.addVertexWithUV(x + w, y + 0, zLevel, (u + w) * scX, (v + 0) * scY);
		tessellator.addVertexWithUV(x + 0, y + 0, zLevel, (u + 0) * scX, (v + 0) * scY);
		tessellator.draw();
	}
	
	public void playSoundFX(String s, float pitch)
	{ mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation(s), pitch)); }
	
	public void playClickSound()
	{ playSoundFX("gui.button.press", 1F); }
	
	public FontRenderer getFontRenderer()
	{ return fontRendererObj; }
	
	public void drawPlayerHead(GameProfile profile, double x, double y, double w, double h)
	{
		ResourceLocation resourcelocation = AbstractClientPlayer.locationStevePng;
		
		if (profile != null)
		{
			Minecraft minecraft = Minecraft.getMinecraft();
			
			@SuppressWarnings("rawtypes")
			Map map = minecraft.func_152342_ad().func_152788_a(profile);
			
			if (map.containsKey(MinecraftProfileTexture.Type.SKIN))
				resourcelocation = minecraft.func_152342_ad().func_152792_a((MinecraftProfileTexture)map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
		}
		
		setTexture(resourcelocation);
		
		double z = getZLevel();
		Tessellator tessellator = Tessellator.instance;
		
		double minU = 1D / 8D;
		double minV = 1D / 4D;
		double maxU = 2D / 8D;
		double maxV = 2D / 4D;
		
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x + 0, y + h, z, minU, maxV);
		tessellator.addVertexWithUV(x + w, y + h, z, maxU, maxV);
		tessellator.addVertexWithUV(x + w, y + 0, z, maxU, minV);
		tessellator.addVertexWithUV(x + 0, y + 0, z, minU, minV);
		tessellator.draw();
		
		double minU2 = 5D / 8D;
		double minV2 = 1D / 4D;
		double maxU2 = 6D / 8D;
		double maxV2 = 2D / 4D;
		
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x + 0, y + h, z, minU2, maxV2);
		tessellator.addVertexWithUV(x + w, y + h, z, maxU2, maxV2);
		tessellator.addVertexWithUV(x + w, y + 0, z, maxU2, minV2);
		tessellator.addVertexWithUV(x + 0, y + 0, z, minU2, minV2);
		tessellator.draw();
	}
}