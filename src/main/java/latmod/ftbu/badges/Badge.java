package latmod.ftbu.badges;

import java.util.UUID;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.util.client.LatCoreMCClient;
import latmod.lib.FastMap;
import latmod.lib.util.FinalIDObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class Badge extends FinalIDObject
{
	public static boolean isReloading = true;
	public static final FastMap<UUID, Badge> badges = new FastMap<UUID, Badge>();
	public static final ResourceLocation defTex = FTBU.mod.getLocation("textures/failed_badge.png");
	
	public static void init()
	{
		badges.clear();
		badges.put(new UUID(6556003398745671694L, -8612738752993846641L), new Badge("http://i.imgur.com/t1qZ58U.png"));
	}
	
	private ResourceLocation textureURL = null;
	public boolean isGlowing = true;
	
	public Badge(String id)
	{ super(id); }
	
	public Badge setNotGlowing()
	{ isGlowing = false; return this; }
	
	public ResourceLocation getTexture()
	{
		if(textureURL == null)
		{
			textureURL = FTBU.mod.getLocation("textures/badges/" + toString());
			LatCoreMCClient.getDownloadImage(textureURL, toString(), defTex, null);
		}
		
		return textureURL;
	}
	
	public void onPlayerRender(EntityPlayer ep)
	{
		ResourceLocation texture = getTexture();
		if(texture == null) return;
		
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		LatCoreMCClient.mc.getTextureManager().bindTexture(texture);
		
		if(isGlowing) LatCoreMCClient.pushMaxBrightness();
		
		GL11.glPushMatrix();
		
		if(ep.isSneaking())
			GL11.glRotatef(25F, 1F, 0F, 0F);
		
		GL11.glTranslated(0.04D, 0.01D, 0.86D);
		
		if(ep.getEquipmentInSlot(3) != null && ep.getEquipmentInSlot(3).getItem() instanceof ItemArmor)//TODO: Change to isValidArmor
				GL11.glTranslated(0D, 0D, -0.0625D);
		
		float s = 0.2F;
		GL11.glTranslated(0D, 0D, -1D);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		GL11.glTexCoord2f(0F, 0F);
		GL11.glVertex3f(0F, 0F, 0F);
		GL11.glTexCoord2f(1F, 0F);
		GL11.glVertex3f(s, 0F, 0F);
		GL11.glTexCoord2f(1F, 1F);
		GL11.glVertex3f(s, s, 0F);
		GL11.glTexCoord2f(0F, 1F);
		GL11.glVertex3f(0F, s, 0F);
		GL11.glEnd();
		
		if(isGlowing) LatCoreMCClient.popMaxBrightness();
		
		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}
}