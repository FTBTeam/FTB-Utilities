package latmod.latcore.client;

import latmod.core.*;
import net.minecraftforge.client.event.RenderPlayerEvent;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class PDSmoke extends PlayerDecorator
{
	public void onPlayerRender(RenderPlayerEvent.Specials.Post e)
	{
		if(hasMoved(e.entity))
		{
			double w = e.entity.width / 2D;
			double x = MathHelper.randomDouble(ParticleHelper.rand, e.entity.posX - w, e.entity.posX + w);
			double y = MathHelper.randomDouble(ParticleHelper.rand, e.entity.posY - e.entity.getYOffset(), e.entity.posY);
			double z = MathHelper.randomDouble(ParticleHelper.rand, e.entity.posZ - w, e.entity.posZ + w);
			e.entity.worldObj.spawnParticle("townaura", x, y, z, 0D, 0D, 0D);
		}
	}
}