package latmod.core.client.playerdeco;

import latmod.core.FastMap;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.client.event.RenderPlayerEvent;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class PDLatMod extends PlayerDecorator
{
	public static final PDLatMod instance = new PDLatMod();
	
	public static int[] pixels =
	{
		0, 1, 1, 1, 0,
		1, 0, 0, 0, 1,
		1, 0, 1, 0, 1,
		1, 0, 0, 1, 0,
		0, 1, 0, 0, 0,
		0, 1, 1, 1, 0,
		1, 0, 0, 0, 1,
		1, 0, 1, 0, 1,
		1, 0, 0, 0, 1,
		0, 1, 1, 1, 0,
	};
	
	public PDLatMod()
	{
	}
	
	public void onDataLoaded(FastMap<String, String> data)
	{
	}
	
	public void onPlayerRender(RenderPlayerEvent.Specials.Post e)
	{
		// RenderPlayer //
		float s = 0.028F;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		
		GL11.glPushMatrix();
		GL11.glTranslated(0.08D, 0.03D, -0.126D);
		
		if(e.entityPlayer.isSneaking())
			GL11.glRotatef(25F, 1F, 0F, 0F);
		
		if(e.entityPlayer.inventory.armorInventory[2] != null && e.entityPlayer.inventory.armorInventory[2].getItem() instanceof ItemArmor)
			GL11.glTranslated(0D, 0D, -0.0625D);
		
		GL11.glScalef(s, s, s);
		GL11.glTranslated(0D, 0D, -1D);
		
		double secs = System.currentTimeMillis() / 1500D;
		
		for(int i = 0; i < pixels.length; i++)
		if(pixels[i] != 0)
		{
			int x = i % 5;
			int y = i / 5;
			
			double b = (secs + x * 0.3D + y * 0.7D) % 2D;
			if(b > 1D) b = 2D - b;
			
			double cm = b * 0.35D + 0.65D;
			
			GL11.glColor4d(1D * cm, 0.8D * cm, 0.1D * cm, 1D);
			
			GL11.glBegin(GL11.GL_QUADS);
			
			for(float h = 0; h <= 0.5F; h += 0.1F)
			{
				GL11.glVertex3f(x, y, h);
				GL11.glVertex3f(x + 1, y, h);
				GL11.glVertex3f(x + 1, y + 1, h);
				GL11.glVertex3f(x, y + 1, h);
			}
			
			GL11.glEnd();
		}
		
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LIGHTING);
	}
}