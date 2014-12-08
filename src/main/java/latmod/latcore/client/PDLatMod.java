package latmod.latcore.client;

import cpw.mods.fml.relauncher.*;
import latmod.core.ParticleHelper;
import latmod.core.util.*;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.RenderPlayerEvent;

@SideOnly(Side.CLIENT)
public class PDLatMod extends PlayerDecorator
{
	public PDLatMod()
	{
		super("latmod");
	}
	
	public void onPlayerRender(RenderPlayerEvent.Specials.Post e)
	{
		AxisAlignedBB bb = e.entity.getBoundingBox();
		
		if(bb != null && !new Vertex(e.entity.prevPosX, e.entity.prevPosY, e.entity.prevPosZ).equalsPos(new Vertex(e.entity)))
		{
			for(int i = 0; i < 20; i++)
			{
				double x = MathHelper.randomDouble(ParticleHelper.rand, bb.minX, bb.maxX);
				double y = MathHelper.randomDouble(ParticleHelper.rand, bb.minY, bb.maxY);
				double z = MathHelper.randomDouble(ParticleHelper.rand, bb.minZ, bb.maxZ);
				e.entity.worldObj.spawnParticle("reddust", x, y, z, 0D, 0D, 0D);
			}
		}
	}
}