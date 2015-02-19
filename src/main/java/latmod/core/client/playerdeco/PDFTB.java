package latmod.core.client.playerdeco;

import latmod.core.util.FastMap;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.client.event.RenderPlayerEvent;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class PDFTB extends PlayerDecorator
{
	public static final PDFTB instance = new PDFTB();
	
	public static int[] pixels =
	{
		1, 1, 1, 0, 0, 0,
		1, 2, 2, 2, 0, 0,
		1, 1, 2, 3, 3, 0,
		1, 0, 2, 3, 0, 3,
		1, 0, 2, 3, 3, 0,
		0, 0, 2, 3, 0, 3,
		0, 0, 0, 3, 3, 0,
	};
	
	public PDFTB()
	{
	}
	
	public void onDataLoaded(FastMap<String, String> data)
	{
	}
	
	public void onPlayerRender(RenderPlayerEvent.Specials.Post e)
	{
		// RenderPlayer //
		float s = 0.035F;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		
		GL11.glPushMatrix();
		GL11.glTranslated(-0.23D, 0.03D, -0.126D);
		
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
			int x = i % 6;
			int y = i / 6;
			
			double b = (secs + x * 0.3D + y * 0.7D) % 2D;
			if(b > 1D) b = 2D - b;
			
			double cm = b * 0.3D + 0.7D;
			
			int red = 0;
			int green = 148;
			int blue = 255;
			
			if(pixels[i] == 2)
			{
				red = 60;
				green = 255;
				blue = 53;
			}
			else if(pixels[i] == 3)
			{
				red = 255;
				green = 62;
				blue = 56;
			}
			
			GL11.glColor4d((red / 255D) * cm, (green / 255D) * cm, (blue / 255D) * cm, 1D);
			
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