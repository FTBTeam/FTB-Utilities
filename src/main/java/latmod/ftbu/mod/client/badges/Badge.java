package latmod.ftbu.mod.client.badges;

import java.util.UUID;

import latmod.ftbu.core.client.LatCoreMCClient;
import latmod.ftbu.core.util.FastMap;
import latmod.ftbu.mod.FTBU;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class Badge
{
	public static boolean isReloading = true;
	public static final FastMap<UUID, Badge> badges = new FastMap<UUID, Badge>();
	public static final ResourceLocation defTex = FTBU.mod.getLocation("textures/failed_badge.png");
	
	private ResourceLocation textureURL = null;
	public final String ID;
	public boolean isGlowing = true;
	
	public Badge(String id)
	{ ID = id; }
	
	public String toString()
	{ return ID; }
	
	public boolean equals(Object o)
	{ return o != null && (o == this || o.toString().equals(toString())); }
	
	public Badge setNotGlowing()
	{ isGlowing = false; return this; }
	
	public ResourceLocation getTexture()
	{
		if(textureURL == null)
		{
			textureURL = FTBU.mod.getLocation("textures/badges/" + ID);
			LatCoreMCClient.getDownloadImage(textureURL, ID, defTex, null);
		}
		
		return textureURL;
	}
	
	public void onPlayerRender(EntityPlayer ep) // RenderPlayer
	{
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		LatCoreMCClient.getMinecraft().getTextureManager().bindTexture(getTexture());
		
		if(isGlowing) LatCoreMCClient.pushMaxBrightness();
		
		GL11.glPushMatrix();
		
		if(ep.isSneaking())
			GL11.glRotatef(25F, 1F, 0F, 0F);
		
		GL11.glTranslated(0.04D, 0.01D, 0.86D);
		
		if(ep.getEquipmentInSlot(3) != null && ep.getEquipmentInSlot(3).getItem() instanceof ItemArmor)
				GL11.glTranslated(0D, 0D, -0.0625D);
		
		float s = 0.2F;
		GL11.glScalef(s, s, 1F);
		GL11.glTranslated(0D, 0D, -1D);
		Tessellator t = Tessellator.instance;
		t.startDrawingQuads();
		t.setColorRGBA(255, 255, 255, 255);
		t.addVertexWithUV(0D, 0D, 0D, 0D, 0D);
		t.addVertexWithUV(1D, 0D, 0D, 1D, 0D);
		t.addVertexWithUV(1D, 1D, 0D, 1D, 1D);
		t.addVertexWithUV(0D, 1D, 0D, 0D, 1D);
		t.draw();
		
		if(isGlowing) LatCoreMCClient.popMaxBrightness();
		
		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}
}