package latmod.core;

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
	public static boolean travelEntity(Entity entity, double x, double y, double z, int dim)
	{
		if(!DimensionManager.isDimensionRegistered(dim)) return false;
		MinecraftServer mcs = MinecraftServer.getServer();
		if (mcs == null || (dim != 0 && !mcs.getAllowNether()))
			return false;
		
		WorldServer w1 = mcs.worldServerForDimension(dim);
		if (w1 == null)
		{
			System.err.println("Cannot Link Entity to Dimension: Could not get World for Dimension " + dim);
			return false;
		}
		
		teleportEntity(w1, entity, x, y, z, dim);
		return true;
	}
	
	private static Entity teleportEntity(WorldServer w1, Entity e, double x, double y, double z, int dim)
	{
		WorldServer w0 = (WorldServer)e.worldObj;
		Entity mount = e.ridingEntity;
		
		if(e.ridingEntity != null)
		{
			e.mountEntity(null);
			mount = teleportEntity(w1, mount, x, y, z, dim);
		}
		
		boolean chw = w0 != w1;
		
		w0.updateEntityWithOptionalForce(e, false);
		
		if(e instanceof EntityPlayerMP)
		{
			EntityPlayerMP ep = (EntityPlayerMP) e;
			ep.closeScreen();
			if (chw)
			{
				ep.dimension = dim;
				ep.playerNetServerHandler.sendPacket(new S07PacketRespawn(ep.dimension, ep.worldObj.difficultySetting, w1.getWorldInfo().getTerrainType(), ep.theItemInWorldManager.getGameType()));
				w0.getPlayerManager().removePlayer(ep);
			}
		}
		
		if(chw) removeEntityFromWorld(w0, e);
		e.setLocationAndAngles(x, y, z, e.rotationYaw, e.rotationPitch);
		w1.theChunkProviderServer.loadChunk(MathHelper.floor_double(x) >> 4, MathHelper.floor_double(z) >> 4);
		
		if(chw)
		{
			if(!(e instanceof EntityPlayer))
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
		
		if(e instanceof EntityPlayerMP)
		{
			EntityPlayerMP ep = (EntityPlayerMP) e;
			if (chw) ep.mcServer.getConfigurationManager().func_72375_a(ep, w1);
			ep.playerNetServerHandler.setPlayerLocation(x, y, z, ep.rotationYaw, ep.rotationPitch);
		}
		
		w1.updateEntityWithOptionalForce(e, false);
		
		if(e instanceof EntityPlayerMP && chw)
		{
			EntityPlayerMP ep = (EntityPlayerMP)e;
			ep.theItemInWorldManager.setWorld(w1);
			ep.mcServer.getConfigurationManager().updateTimeAndWeatherForPlayer(ep, w1);
			ep.mcServer.getConfigurationManager().syncPlayerInventory(ep);
			for (Object o : ep.getActivePotionEffects())
				ep.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(ep.getEntityId(), (PotionEffect)o));
			ep.playerNetServerHandler.sendPacket(new S1FPacketSetExperience(ep.experience, ep.experienceTotal, ep.experienceLevel));
		}
		
		e.setLocationAndAngles(x, y, z, e.rotationYaw, e.rotationPitch);
		
		if(mount != null)
		{
			if (e instanceof EntityPlayerMP)
				w1.updateEntityWithOptionalForce(e, true);
			e.mountEntity(mount);
		}
		
		return e;
	}
	
	private static void removeEntityFromWorld(World w, Entity e)
	{
		if(e instanceof EntityPlayer)
		{
			EntityPlayer ep = (EntityPlayer) e;
			ep.closeScreen();
			w.playerEntities.remove(ep);
			w.updateAllPlayersSleepingFlag();
			int i = e.chunkCoordX;
			int j = e.chunkCoordZ;
			if ((e.addedToChunk)
					&& (w.getChunkProvider().chunkExists(i, j))) {
				w.getChunkFromChunkCoords(i, j).removeEntity(e);
				w.getChunkFromChunkCoords(i, j).isModified = true;
			}
			w.loadedEntityList.remove(e);
			w.onEntityRemoved(e);
		}
	}
}