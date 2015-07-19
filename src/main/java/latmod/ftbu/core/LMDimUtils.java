package latmod.ftbu.core;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.*;
import net.minecraftforge.common.DimensionManager;

public class LMDimUtils
{
	public static boolean teleportPlayer(EntityPlayerMP ep, EntityPos pos)
	{ return teleportPlayer(ep, pos.x, pos.y, pos.z, pos.dim); }
	
	public static boolean teleportPlayer(EntityPlayerMP ep, double x, double y, double z, int dim)
	{
		if(ep == null) return false;
		
		if(ep.worldObj.provider.dimensionId == dim)
		{
			ep.playerNetServerHandler.setPlayerLocation(x, y, z, ep.rotationYaw, ep.rotationPitch);
			return true;
		}
		
		if(!DimensionManager.isDimensionRegistered(dim)) return false;
		MinecraftServer mcs = LatCoreMC.getServer();
		WorldServer newWorldServer = mcs.worldServerForDimension(dim);
		mcs.getConfigurationManager().transferPlayerToDimension(ep, dim, new CustomTeleporter(newWorldServer));
		ep.setPositionAndUpdate(x, y, z);
		return true;
	}
	
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
	}
	
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
		return getSpawnPoint(dim);
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