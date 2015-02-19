package latmod.core.client.playerdeco;

import latmod.core.ParticleHelper;
import latmod.core.util.*;
import net.minecraftforge.client.event.RenderPlayerEvent;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class PDParticles extends PlayerDecorator
{
	public String part = null;
	public int rarity_still;
	public int rarity_moving;
	public int quantity_still;
	public int quantity_moving;
	
	public double parA = 0D, parB = 0D, parC = 0D;
	
	public void onDataLoaded(FastMap<String, String> data)
	{
		part = getS(data, "id", null);
		
		if(part != null)
		{
			rarity_still = getN(data, "rs", 10).intValue();
			rarity_moving = getN(data, "rm", 3).intValue();
			quantity_still = getN(data, "qs", 1).intValue();
			quantity_moving = getN(data, "qm", 1).intValue();
			
			parA = getN(data, "pA", 0D).doubleValue();
			parB = getN(data, "pB", 0D).doubleValue();
			parC = getN(data, "pC", 0D).doubleValue();
		}
	}
	
	public void onPlayerRender(RenderPlayerEvent.Specials.Post e)
	{
		if(part != null)
		{
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
					ParticleHelper.spawnPart(e.entity.worldObj, part, x, y, z, parA, parB, parC);
				}
			}
		}
	}
}