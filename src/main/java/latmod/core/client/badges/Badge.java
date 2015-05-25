package latmod.core.client.badges;

import latmod.core.LatCoreMC;
import latmod.core.mod.LC;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class Badge // Badges
{
	public final String ID;
	private ResourceLocation texture;
	public boolean isGlowing = true;
	
	public Badge(String id)
	{
		ID = id;
		texture = LC.mod.getLocation("textures/badges/" + ID + ".png");
	}
	
	public Badge register()
	{ Badges.registry.put(ID, this); return this; }
	
	public Badge setNotGlowing()
	{ isGlowing = false; return this; }
	
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
		
		if(isGlowing) LatCoreMC.Client.pushMaxBrightness();
		
		GL11.glPushMatrix();
		GL11.glTranslated(0.04D, 0.01D, 0.86D);
		
		
		if(ep.getEquipmentInSlot(3) != null && ep.getEquipmentInSlot(3).getItem() instanceof ItemArmor)
				GL11.glTranslated(0D, 0D, -0.0625D);
		
		if(ep.isSneaking())
			GL11.glRotatef(25F, 1F, 0F, 0F);
		
		float s = 0.20F;
		GL11.glScalef(s, s, 1F);
		GL11.glTranslated(0D, 0D, -1D);
		
		GL11.glColor4f(1F, 1F, 1F, 1F);
		GL11.glBegin(GL11.GL_QUADS);
		
		GL11.glTexCoord2f(0F, 0F);
		GL11.glVertex3f(0F, 0F, 0F);
		GL11.glTexCoord2f(1F, 0F);
		GL11.glVertex3f(1F, 0F, 0F);
		GL11.glTexCoord2f(1F, 1F);
		GL11.glVertex3f(1F, 1F, 0F);
		GL11.glTexCoord2f(0F, 1F);
		GL11.glVertex3f(0F, 1F, 0F);
		GL11.glEnd();
		
		if(isGlowing) LatCoreMC.Client.popMaxBrightness();
		
		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}
}