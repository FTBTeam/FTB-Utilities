package latmod.ftbu.core.gui;
import latmod.ftbu.FTBU;
import latmod.ftbu.client.FTBUClient;
import latmod.ftbu.core.client.LMRenderHelper;
import latmod.ftbu.core.event.LoadLMIconsEvent;
import latmod.ftbu.core.util.FastList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public abstract class GuiLM extends GuiContainer
{
	// General IIcons //
	
	public static class Icons
	{
		public static IIcon
		button,
		pressed,
		toggle_off,
		toggle_on,
		help,
		settings,
		up,
		down,
		left,
		right,
		accept,
		accept_gray,
		back,
		cancel,
		add,
		remove;
		
		public static final IIcon[] security = new IIcon[4];
		public static IIcon security_whitelist, security_blacklist;
		
		public static final IIcon[] inv = new IIcon[4];
		public static final IIcon[] redstone = new IIcon[4];
		
		public static class Friends
		{
			public static IIcon
			add,
			remove,
			groups,
			//groups_gray,
			//mail,
			//mail_gray,
			//trade,
			//trade_gray,
			view,
			view_gray,
			online;
			
			public static void load(LoadLMIconsEvent e)
			{
				add = e.load("friends/add");
				remove = e.load("friends/remove");
				groups = e.load("friends/groups");
				//groups_gray = e.load("friends/groups_gray");
				//mail = e.load("friends/mail");
				//mail_gray = e.load("friends/mail_gray");
				//trade = e.load("friends/trade");
				//trade_gray = e.load("friends/trade_gray");
				view = e.load("friends/view");
				view_gray = e.load("friends/view_gray");
				online = e.load("friends/online");
			}
		}
		
		public static void load(LoadLMIconsEvent e)
		{
			button = e.load("button");
			pressed = e.load("pressed");
			toggle_off = e.load("toggle_off");
			toggle_on = e.load("toggle_on");
			help = e.load("help");
			settings = e.load("settings");
			up = e.load("arrows/up");
			down = e.load("arrows/down");
			left = e.load("arrows/left");
			right = e.load("arrows/right");
			accept = e.load("accept");
			accept_gray = e.load("accept_gray");
			back = left;
			cancel = e.load("cancel");
			add = e.load("add");
			remove = e.load("remove");
			
			security[0] = e.load("security/public");
			security[1] = e.load("security/private");
			security[2] = e.load("security/friends");
			security[3] = e.load("security/group");
			
			security_blacklist = e.load("security/black");
			security_whitelist = e.load("security/white");
			
			inv[0] = e.load("inv/io");
			inv[1] = e.load("inv/in");
			inv[2] = e.load("inv/out");
			inv[3] = e.load("inv/off");
			
			redstone[0] = e.load("rs/off");
			redstone[1] = e.load("rs/high");
			redstone[2] = e.load("rs/low");
			redstone[3] = e.load("rs/pulse");
			
			Friends.load(e);
		}
	}
	
	// GuiLM //
	
	public final ContainerLM container;
	public final ResourceLocation texture;
	public final FastList<WidgetLM> widgets;
	
	public GuiLM(ContainerLM c, ResourceLocation tex)
	{
		super(c);
		
		FTBU.mod.getLocation("textures/gui/icons/button.png");
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
	
	public final float getZLevel()
	{ return zLevel; }
	
	public final void setZLevel(float z)
	{ zLevel = z; }
	
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
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		LMRenderHelper.recolor();
		setTexture(texture);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}
	
	public void drawScreen(int mx, int my, float f)
	{
		super.drawScreen(mx, my, f);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		drawText(mx, my);
		GL11.glDisable(GL11.GL_LIGHTING);
	}
	
	public void drawText(int mx, int my)
	{
		FastList<String> l = new FastList<String>();
		
		addMouseText(mx, my, l);
		
		for(int i = 0; i < widgets.size(); i++)
		{
			WidgetLM w = widgets.get(i);
			if(w.mouseOver(mx, my))
				w.addMouseOverText(l);
		}
		
		if(!l.isEmpty()) drawHoveringText(l, mx, my, fontRendererObj);
	}
	
	public void addMouseText(int mx, int my, FastList<String> l)
	{
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
	{ drawTexturedModalRectD(x, y, u, v, w, h); }
	
	public void drawTexturedModalRectD(double x, double y, double u, double v, double w, double h)
	{ drawTexturedModalRectD(x, y, u, v, w, h, 256, 256, zLevel); }
	
	public static void drawTexturedModalRectD(double x, double y, double u, double v, double w, double h, int width, int height, double z)
	{
		double scX = 1D / (double)width;
		double scY = 1D / (double)height;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x + 0, y + h, z, (u + 0) * scX, (v + h) * scY);
		tessellator.addVertexWithUV(x + w, y + h, z, (u + w) * scX, (v + h) * scY);
		tessellator.addVertexWithUV(x + w, y + 0, z, (u + w) * scX, (v + 0) * scY);
		tessellator.addVertexWithUV(x + 0, y + 0, z, (u + 0) * scX, (v + 0) * scY);
		tessellator.draw();
	}
	
	public void playSoundFX(String s, float pitch)
	{ mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation(s), pitch)); }
	
	public void playClickSound()
	{ playSoundFX("gui.button.press", 1F); }
	
	public FontRenderer getFontRenderer()
	{ return fontRendererObj; }
	
	public static void drawPlayerHead(String username, double x, double y, double w, double h, double z)
	{
		Minecraft.getMinecraft().getTextureManager().bindTexture(FTBUClient.getSkinTexture(username));
		
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