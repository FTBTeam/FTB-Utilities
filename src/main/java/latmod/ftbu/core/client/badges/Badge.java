package latmod.ftbu.core.client.badges;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.client.LatCoreMCClient;
import latmod.ftbu.core.util.FastMap;
import latmod.ftbu.mod.FTBU;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class Badge
{
	public final String ID;
	private ResourceLocation texture;
	public boolean isGlowing = true;
	private double scaleX, scaleY, offsetX, offsetY;
	
	public Badge(String id)
	{
		ID = id;
		texture = FTBU.mod.getLocation("textures/badges/" + ID + ".png");
		scaleX = scaleY = 1D;
	}
	
	public String toString()
	{ return ID; }
	
	public boolean equals(Object o)
	{ return o != null && (o == this || o.toString().equals(toString())); }
	
	public Badge setNotGlowing()
	{ isGlowing = false; return this; }
	
	public Badge setScale(double x, double y)
	{ scaleX = x; scaleY = y; return this; }
	
	public Badge setOffset(double x, double y)
	{ offsetX = x; offsetY = y; return this; }
	
	public double scaleX()
	{ return scaleX; }
	
	public double scaleY()
	{ return scaleY; }
	
	public double offsetX()
	{ return offsetX; }
	
	public double offsetY()
	{ return offsetY; }
	
	public ResourceLocation getTexture()
	{ return texture; }
	
	public void onPlayerRender(EntityPlayer ep) // RenderPlayer
	{
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(getTexture());
		
		if(isGlowing) LatCoreMCClient.pushMaxBrightness();
		
		GL11.glPushMatrix();
		
		if(ep.isSneaking())
			GL11.glRotatef(25F, 1F, 0F, 0F);
		
		GL11.glTranslated(0.04D, 0.01D, 0.86D);
		
		if(ep.getEquipmentInSlot(3) != null && ep.getEquipmentInSlot(3).getItem() instanceof ItemArmor)
				GL11.glTranslated(0D, 0D, -0.0625D);
		
		float s = 0.20F;
		GL11.glScalef(s, s, 1F);
		GL11.glTranslated(offsetX(), offsetY(), -1D);
		Tessellator t = Tessellator.instance;
		t.startDrawingQuads();
		t.setColorRGBA(255, 255, 255, 255);
		t.addVertexWithUV(0D, 0D, 0D, 0D, 0D);
		t.addVertexWithUV(scaleX(), 0D, 0D, 1D, 0D);
		t.addVertexWithUV(scaleX(), scaleY(), 0D, 1D, 1D);
		t.addVertexWithUV(0D, scaleY(), 0D, 0D, 1D);
		t.draw();
		
		if(isGlowing) LatCoreMCClient.popMaxBrightness();
		
		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}
	
	// Static //
	
	public static boolean reloading = false;
	private static final FastMap<String, Badge> registry = new FastMap<String, Badge>();
	private static final Badge none = new Badge("none") { public void onPlayerRender(EntityPlayer ep) { } };
	
	public static final void init()
	{
		registry.clear();
		register(new Badge("admin"));
		register(new Badge("curse"));
		register(new Badge("donator"));
		
		register(new Badge("enki")
		{
			public double scaleX()
			{ return 2D; }
			
			public double scaleY()
			{ return 0.5D; }
			
			public double offsetX()
			{ return -0.5D; }
			
			public double offsetY()
			{ return 0.2D; }
		});
		
		register(new Badge("ftb"));
		register(new Badge("gamer"));
		register(new Badge("latmod"));
		register(new Badge("mods_pink"));
		register(new Badge("mods"));
		register(new Badge("packs"));
		register(new Badge("swords"));
		register(new Badge("tester"));
		register(new Badge("twitch"));
		register(new Badge("yt"));
	}
	
	public static final void register(Badge b)
	{ registry.put(b.ID, b); }
	
	public static final Badge getBadge(String s)
	{
		Badge b = registry.get(s);
		if(b != null) return b;
		b = new Badge(s);
		if(LatCoreMC.resourceExists(b.getTexture()))
		{ registry.put(s, b); return b; }
		registry.put(s, none);
		return none;
	}
}