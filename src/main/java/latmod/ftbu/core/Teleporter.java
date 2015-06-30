package latmod.ftbu.core;

import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.*;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.*;
import net.minecraftforge.common.DimensionManager;

/** Made by XCompWiz */
public class Teleporter
{
	public static boolean travelEntity(Entity entity, EntityPos pos)
	{ return travelEntity(entity, pos.x, pos.y, pos.z, pos.dim); }
	
	public static boolean travelEntity(Entity entity, double x, double y, double z, int dim)
	{
		if(entity == null) return false;
		
		EntityPlayerMP ep = (entity instanceof EntityPlayerMP) ? (EntityPlayerMP)entity : null;
		
		if(entity.worldObj.provider.dimensionId == dim)
		{
			if(ep != null) ep.playerNetServerHandler.setPlayerLocation(x, y, z, ep.rotationYaw, ep.rotationPitch);
			else entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
			return true;
		}
		
		if(!DimensionManager.isDimensionRegistered(dim)) return false;
		MinecraftServer mcs = MinecraftServer.getServer();
		if (mcs == null || (dim != 0 && !mcs.getAllowNether()))
			return false;
		
		WorldServer w1 = mcs.worldServerForDimension(dim);
		if (w1 == null)
		{
			System.err.println("Cannot Teleport Entity to Dimension: Could not get World for Dimension " + dim);
			return false;
		}
		
		teleportEntity(w1, entity, x, y, z, dim, ep);
		return true;
	}
	
	private static Entity teleportEntity(WorldServer w1, Entity e, double x, double y, double z, int dim, EntityPlayerMP ep)
	{
		WorldServer w0 = (WorldServer)e.worldObj;
		Entity mount = e.ridingEntity;
		
		if(e.ridingEntity != null)
		{
			e.mountEntity(null);
			mount = teleportEntity(w1, mount, x, y, z, dim, ep);
		}
		
		boolean chw = w0 != w1;
		
		w0.updateEntityWithOptionalForce(e, false);
		
		if(ep != null)
		{
			ep.closeScreen();
			
			if(chw)
			{
				ep.dimension = dim;
				ep.playerNetServerHandler.sendPacket(new S07PacketRespawn(ep.dimension, ep.worldObj.difficultySetting, w1.getWorldInfo().getTerrainType(), ep.theItemInWorldManager.getGameType()));
				w0.getPlayerManager().removePlayer(ep);
			}
		}
		
		if(chw && ep != null)
		{
			ep.closeScreen();
			w0.playerEntities.remove(ep);
			w0.updateAllPlayersSleepingFlag();
			int i = ep.chunkCoordX;
			int j = ep.chunkCoordZ;
			
			if(ep.addedToChunk && w0.getChunkProvider().chunkExists(i, j))
			{
				w0.getChunkFromChunkCoords(i, j).removeEntity(ep);
				w0.getChunkFromChunkCoords(i, j).isModified = true;
			}
			
			w0.loadedEntityList.remove(ep);
			w0.onEntityRemoved(ep);
		}
		
		e.setLocationAndAngles(x, y, z, e.rotationYaw, e.rotationPitch);
		w1.theChunkProviderServer.loadChunk(MathHelper.floor_double(x) >> 4, MathHelper.floor_double(z) >> 4);
		
		if(chw)
		{
			if(ep == null)
			{
				NBTTagCompound entityNBT = new NBTTagCompound();
				e.isDead = false;
				entityNBT.setString("id", EntityList.getEntityString(e));
				e.writeToNBT(entityNBT);
				e.isDead = true;
				e = EntityList.createEntityFromNBT(entityNBT, w1);
				if (e == null) return null;
				
				e.dimension = w1.provider.dimensionId;
			}
			
			w1.spawnEntityInWorld(e);
			e.setWorld(w1);
		}
		
		e.setLocationAndAngles(x, y, z, e.rotationYaw, e.rotationPitch);
		w1.updateEntityWithOptionalForce(e, false);
		e.setLocationAndAngles(x, y, z, e.rotationYaw, e.rotationPitch);
		
		if(ep != null)
		{
			if(chw) ep.mcServer.getConfigurationManager().func_72375_a(ep, w1);
			ep.playerNetServerHandler.setPlayerLocation(x, y, z, ep.rotationYaw, ep.rotationPitch);
		}
		
		w1.updateEntityWithOptionalForce(e, false);
		
		if(ep != null && chw)
		{
			ep.theItemInWorldManager.setWorld(w1);
			ep.mcServer.getConfigurationManager().updateTimeAndWeatherForPlayer(ep, w1);
			ep.mcServer.getConfigurationManager().syncPlayerInventory(ep);
			for(Object o : ep.getActivePotionEffects())
				ep.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(ep.getEntityId(), (PotionEffect)o));
			ep.playerNetServerHandler.sendPacket(new S1FPacketSetExperience(ep.experience, ep.experienceTotal, ep.experienceLevel));
		}
		
		e.setLocationAndAngles(x, y, z, e.rotationYaw, e.rotationPitch);
		
		if(mount != null)
		{
			if(ep != null)
				w1.updateEntityWithOptionalForce(e, true);
			e.mountEntity(mount);
		}
		
		return e;
	}
}