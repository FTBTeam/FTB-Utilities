package latmod.ftbu.core;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.*;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraftforge.common.DimensionManager;

public class LMDimUtils
{
	public static boolean teleportPlayer(EntityPlayerMP ep, EntityPos pos)
	{ return teleportPlayer(ep, pos.x, pos.y, pos.z, pos.dim); }
	
	/*
	public static boolean teleportPlayer(EntityPlayerMP ep, double x, double y, double z, int dim)
	{
		if(ep == null) return false;
		
		ep.fallDistance = 0F;
		
		if(ep.worldObj.provider.dimensionId == dim)
		{
			ep.playerNetServerHandler.setPlayerLocation(x, y, z, ep.rotationYaw, ep.rotationPitch);
			return true;
		}
		
		if(!DimensionManager.isDimensionRegistered(dim)) return false;
		MinecraftServer mcs = MinecraftServer.getServer();
		WorldServer newWorldServer = mcs.worldServerForDimension(dim);
		mcs.getConfigurationManager().transferPlayerToDimension(ep, dim, new CustomTeleporter(newWorldServer));
		ep.setPositionAndUpdate(x, y, z);
		return true;
	}
	*/
	
	public static boolean teleportPlayer(EntityPlayerMP ep, double x, double y, double z, int dim)
	{
		if(ep == null) return false;
		ep.fallDistance = 0F;
		
		if(ep.worldObj.provider.dimensionId == dim)
		{
			ep.playerNetServerHandler.setPlayerLocation(x, y, z, ep.rotationYaw, ep.rotationPitch);
			return true;
		}
		
		if(!DimensionManager.isDimensionRegistered(dim)) return false;
		MinecraftServer mcs = MinecraftServer.getServer();
		if (mcs == null || (dim != 0 && !mcs.getAllowNether()))
			return false;
		
		WorldServer w1 = mcs.worldServerForDimension(dim);
		if (w1 == null)
		{
			System.err.println("Cannot teleport " + ep.getCommandSenderName() + " to Dimension " + dim + ": Missing WorldServer");
			return false;
		}
		
		WorldServer w0 = (WorldServer)ep.worldObj;
		
		if(ep.ridingEntity != null)
		{
			ep.mountEntity(null);
		}
		
		boolean chw = w0 != w1;
		
		w0.updateEntityWithOptionalForce(ep, false);
		
		ep.closeScreen();
		
		if(chw)
		{
			ep.dimension = dim;
			ep.playerNetServerHandler.sendPacket(new S07PacketRespawn(ep.dimension, ep.worldObj.difficultySetting, w1.getWorldInfo().getTerrainType(), ep.theItemInWorldManager.getGameType()));
			w0.getPlayerManager().removePlayer(ep);
			
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
		
		ep.setLocationAndAngles(x, y, z, ep.rotationYaw, ep.rotationPitch);
		w1.theChunkProviderServer.loadChunk(MathHelper.floor_double(x) >> 4, MathHelper.floor_double(z) >> 4);
		
		if(chw)
		{
			w1.spawnEntityInWorld(ep);
			ep.setWorld(w1);
		}
		
		ep.setLocationAndAngles(x, y, z, ep.rotationYaw, ep.rotationPitch);
		w1.updateEntityWithOptionalForce(ep, false);
		ep.setLocationAndAngles(x, y, z, ep.rotationYaw, ep.rotationPitch);
		
		if(chw) ep.mcServer.getConfigurationManager().func_72375_a(ep, w1);
		ep.playerNetServerHandler.setPlayerLocation(x, y, z, ep.rotationYaw, ep.rotationPitch);
		
		w1.updateEntityWithOptionalForce(ep, false);
		
		ep.theItemInWorldManager.setWorld(w1);
		ep.mcServer.getConfigurationManager().updateTimeAndWeatherForPlayer(ep, w1);
		ep.mcServer.getConfigurationManager().syncPlayerInventory(ep);
		for(Object o : ep.getActivePotionEffects())
			ep.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(ep.getEntityId(), (PotionEffect)o));
		ep.playerNetServerHandler.sendPacket(new S1FPacketSetExperience(ep.experience, ep.experienceTotal, ep.experienceLevel));
		
		ep.setLocationAndAngles(x, y, z, ep.rotationYaw, ep.rotationPitch);
		return true;
	}
	
	/*
	public static class CustomTeleporter extends net.minecraft.world.Teleporter
	{
		public CustomTeleporter(WorldServer ws)
		{ super(ws); }
		
		public boolean makePortal(Entity entity)
		{ return true; }
		
		public void placeInPortal(Entity entity, double x, double y, double z, float yaw)
		{
			//((EntityPlayerMP)entity).setPositionAndUpdate(x, y, z);
		}
	}*/
	
	public static World getWorld(int dim)
	{ return DimensionManager.getWorld(dim); }
	
	public static String getDimName(World w)
	{ return (w == null) ? "" : w.provider.getDimensionName(); }
	
	public static double getMovementFactor(World w)
	{
		if(w == null) return 1D;
		if(w.provider.dimensionId == 0) return 1D;
		if(w.provider.dimensionId == 1) return 1D;
		if(w.provider.dimensionId == -1) return 8D;
		return w.provider.getMovementFactor();
	}
	
	public static double getWorldScale(World w)
	{ return 1D / getMovementFactor(w); }
	
	public static ChunkCoordinates getSpawnPoint(int dim)
	{
		WorldServer w = DimensionManager.getWorld(dim);
		if(w == null) return null;
		return w.getSpawnPoint();
	}
	
	public static EntityPos getEntitySpawnPoint(int dim)
	{
		ChunkCoordinates c = getSpawnPoint(dim);
		EntityPos p = new EntityPos();
		p.x = c.posX + 0.5D;
		p.y = c.posY + 0.5D;
		p.z = c.posZ + 0.5D;
		p.dim = dim;
		return p;
	}
	
	public static ChunkCoordinates getPlayerSpawnPoint(EntityPlayerMP ep, int dim)
	{
		ChunkCoordinates c = ep.getBedLocation(dim);
		return (c == null) ? getSpawnPoint(dim) : c;
	}
	
	public static EntityPos getPlayerEntitySpawnPoint(EntityPlayerMP ep, int dim)
	{
		ChunkCoordinates c = getPlayerSpawnPoint(ep, dim);
		EntityPos p = new EntityPos();
		p.x = c.posX + 0.5D;
		p.y = c.posY + 0.5D;
		p.z = c.posZ + 0.5D;
		p.dim = dim;
		return p;
	}
}