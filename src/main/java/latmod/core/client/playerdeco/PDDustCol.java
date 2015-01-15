package latmod.core.client.playerdeco;

import java.awt.Color;

import latmod.core.*;
import latmod.core.mod.LC;
import net.minecraftforge.client.event.RenderPlayerEvent;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class PDDustCol extends PlayerDecorator
{
	public int rarity_still, rarity_moving;
	public int quantity_still, quantity_moving;
	public float red, green, blue, alpha;
	public int color;
	
	public void onDataLoaded(FastMap<String, String> data)
	{
		rarity_still = getN(data, "rs", 10).intValue();
		rarity_moving = getN(data, "rm", 3).intValue();
		quantity_still = getN(data, "qs", 1).intValue();
		quantity_moving = getN(data, "qm", 1).intValue();
		
		red = getN(data, "r", 1F).floatValue();
		green = getN(data, "g", 1F).floatValue();
		blue = getN(data, "b", 1F).floatValue();
		alpha = getN(data, "a", 1F).floatValue();
		
		color = new Color((int)(red * 255F), (int)(green * 255F), (int)(blue * 255F), (int)(alpha * 255F)).getRGB();
	}
	
	public void onPlayerRender(RenderPlayerEvent.Specials.Post e)
	{
		if(!LC.proxy.inGameHasFocus()) return;
		
		boolean hasMoved = hasMoved(e.entity);
		boolean spawnPart = false;
		
		if(hasMoved && rarity_moving > 0 && quantity_moving > 0) spawnPart = rarity_moving == 1 || ParticleHelper.rand.nextInt(rarity_moving) == 0;
		if(!hasMoved && rarity_still > 0 && quantity_still > 0) spawnPart = rarity_still == 1 || ParticleHelper.rand.nextInt(rarity_still) == 0;
		
		if(spawnPart)
		{
			for(int i = 0; i < (hasMoved ? quantity_moving : quantity_still); i++)
			{
				double w = e.entity.width / 2D;
				double x = MathHelperLM.randomDouble(ParticleHelper.rand, e.entity.posX - w, e.entity.posX + w);
				double y = MathHelperLM.randomDouble(ParticleHelper.rand, e.entity.boundingBox.minY, e.entity.boundingBox.maxY);
				double z = MathHelperLM.randomDouble(ParticleHelper.rand, e.entity.posZ - w, e.entity.posZ + w);
				LC.proxy.spawnDust(e.entity.worldObj, x, y, z, color);
			}
		}
	}
}