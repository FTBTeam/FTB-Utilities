package latmod.latcore.client;

import latmod.core.*;
import net.minecraftforge.client.event.RenderPlayerEvent;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class PDParticles extends PlayerDecorator
{
	public final String part;
	
	public PDParticles(String s)
	{
		part = s;
	}
	
	public void onPlayerRender(RenderPlayerEvent.Specials.Post e)
	{
		if(hasMoved(e.entity) ? (ParticleHelper.rand.nextInt(3) == 0) : (ParticleHelper.rand.nextInt(10) == 0))
		{
			double w = e.entity.width / 2D;
			double x = MathHelper.randomDouble(ParticleHelper.rand, e.entity.posX - w, e.entity.posX + w);
			double y = MathHelper.randomDouble(ParticleHelper.rand, e.entity.posY - e.entity.getYOffset(), e.entity.posY);
			double z = MathHelper.randomDouble(ParticleHelper.rand, e.entity.posZ - w, e.entity.posZ + w);
			ParticleHelper.spawnPart(e.entity.worldObj, part, x, y, z);
		}
	}
}