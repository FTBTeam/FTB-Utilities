package latmod.ftbu.world;

import java.util.UUID;

import ftb.lib.*;
import ftb.lib.item.LMInvUtils;
import latmod.ftbu.api.tile.ISecureTile;
import latmod.ftbu.mod.config.*;
import latmod.lib.*;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.util.FakePlayer;

public class Claims
{
	public final LMPlayerServer owner;
	private final FastList<ClaimedChunk> chunks;
	public final FastMap<Integer, ForgeChunkManager.Ticket> ticketMap;
	
	public Claims(LMPlayerServer p)
	{
		owner = p;
		chunks = new FastList<ClaimedChunk>();
		ticketMap = new FastMap<Integer, ForgeChunkManager.Ticket>();
	}
	
	public void readFromNBT(NBTTagCompound serverData)
	{
		chunks.clear();
		
		NBTTagCompound tag = serverData.getCompoundTag("Claims");
		
		NBTTagList list = tag.getTagList("Chunks", LMNBTUtils.INT_ARRAY);
		
		if(list != null) for(int i = 0; i < list.tagCount(); i++)
		{
			int[] ai = list.func_150306_c(i);
			chunks.add(new ClaimedChunk(this, ai[0], ai[1], ai[2]));
		}
		
		int[] loadedChunks = tag.getIntArray("CLoaded");
		
		for(int i = 0; i < loadedChunks.length; i++)
		{
			int j = loadedChunks[i];
			if(j >= 0 && j < chunks.size())
				chunks.get(j).isChunkloaded = true;
		}
	}
	
	public void writeToNBT(NBTTagCompound serverData)
	{
		NBTTagCompound tag = new NBTTagCompound();
		
		NBTTagList list = new NBTTagList();
		IntList loadedChunks = new IntList();
		
		for(int j = 0; j < chunks.size(); j++)
		{
			ClaimedChunk c = chunks.get(j);
			list.appendTag(new NBTTagIntArray(new int[] { c.dim, c.posX, c.posZ }));
			if(c.isChunkloaded) loadedChunks.add(j);
		}
		
		tag.setTag("Chunks", list);
		tag.setIntArray("CLoaded", loadedChunks.toArray());
		serverData.setTag("Claims", tag);
	}
	
	public ClaimedChunk getLocal(int dim, int cx, int cz)
	{
		for(int i = 0; i < chunks.size(); i++)
		{
			ClaimedChunk c = chunks.get(i);
			if(c.equalsChunk(dim, cx, cz)) return c;
		}
		
		return null;
	}
	
	public void claim(int dim, int cx, int cz)
	{
		if(FTBUConfigClaims.dimensionBlacklist.get().contains(dim)) return;
		
		int max = owner.getMaxClaimPower();
		if(max == 0) return;
		if(getClaimedChunks() >= max) return;
		
		ChunkType t = ChunkType.get(dim, cx, cz);
		if(!t.isClaimed() && t.canClaimChunk(owner))
			chunks.add(new ClaimedChunk(this, dim, cx, cz));
		
		owner.sendUpdate();
	}
	
	public void unclaim(int dim, int cx, int cz)
	{
		if(!chunks.isEmpty() && chunks.remove(new ClaimedChunk(this, dim, cx, cz)))
			owner.sendUpdate();
	}
	
	public void unclaimAll(int dim)
	{
		if(chunks.isEmpty()) return;
		int size0 = getClaimedChunks();
		
		/*
		FastList<ClaimedChunk> l = new FastList<ClaimedChunk>();
		
		for(int i = 0; i < chunks.size(); i++)
		{
			ClaimedChunk c = chunks.get(i);
			if(c.dim != dim) l.add(c);
		}
		
		*/
		
		for(int i = chunks.size() - 1; i >= 0; i--)
		{
			ClaimedChunk c = chunks.get(i);
			if(c.dim == dim) chunks.remove(i);
		}
		
		if(size0 != getClaimedChunks())
			owner.sendUpdate();
	}
	
	public void unclaimAll()
	{
		int i = chunks.size();
		chunks.clear();
		if(i > 0) owner.sendUpdate();
	}
	
	public int getClaimedChunks()
	{ return chunks.size(); }
	
	public void loadChunks(World w)
	{
		if(w.isRemote) return;
		unloadChunks(w);
		
		//FTBLib.logger.info("FTBU Chunks loaded for " + owner);
	}
	
	public void unloadChunks(World w)
	{
		if(w.isRemote) return;
		
		ForgeChunkManager.Ticket ticket = ticketMap.get(w.provider.dimensionId);
		if(ticket != null)
		{
		}
		
		//FTBLib.logger.info("FTBU Chunks unloaded for " + owner);
	}
	
	// Static //
	
	/** Server side */
	public static ClaimedChunk get(int dim, int cx, int cz)
	{
		for(int i = 0; i < LMWorldServer.inst.players.size(); i++)
		{
			ClaimedChunk c = LMWorldServer.inst.players.get(i).toPlayerMP().claims.getLocal(dim, cx, cz);
			if(c != null) return c;
		}
		
		return null;
	}
	
	/** Server side */
	public static boolean isInSpawn(int dim, int cx, int cz)
	{
		if(dim != 0) return false;
		//if(!LatCoreMC.isDedicatedServer()) return false;
		int radius = FTBLib.getServer().getSpawnProtectionSize();
		if(radius <= 0) return false;
		ChunkCoordinates c = LMDimUtils.getSpawnPoint(0);
		int minX = MathHelperLM.chunk(c.posX + 0.5D - radius);
		int minZ = MathHelperLM.chunk(c.posZ + 0.5D - radius);
		int maxX = MathHelperLM.chunk(c.posX + 0.5D + radius);
		int maxZ = MathHelperLM.chunk(c.posZ + 0.5D + radius);
		return cx >= minX && cx <= maxX && cz >= minZ && cz <= maxZ;
	}
	
	/** Server side */
	public static boolean isInSpawnF(int dim, double x, double z)
	{ return dim == 0 && isInSpawn(dim, MathHelperLM.chunk(x), MathHelperLM.chunk(z)); }
	
	/** Server side */
	public static boolean allowExplosion(int dim, int cx, int cz)
	{
		if(dim == 0 && FTBUConfigGeneral.safeSpawn.get() && isInSpawn(dim, cx, cz))
			return false;
		else if(LMWorldServer.inst.settings.isOutside(dim, cx, cz))
			return false;
		else
		{
			EnumEnabled fe = FTBUConfigClaims.forcedExplosions.get();
			
			ClaimedChunk c = get(dim, cx, cz);
			if(c != null)
			{
				if(fe == null) return c.claims.owner.settings.explosions;
				else return fe.isEnabled();
			}
		}
		
		return true;
	}
	
	public static boolean canPlayerInteract(EntityPlayer ep, int x, int y, int z, boolean leftClick)
	{
		if(ep instanceof FakePlayer) return true;
		
		World w = ep.worldObj;
		boolean server = !w.isRemote;
		if(server && LMWorldServer.inst.settings.isOutsideF(w.provider.dimensionId, x, z)) return false;
		
		if(!server || FTBUConfigGeneral.allowCreativeInteractSecure(ep)) return true;
		
		Block block = w.getBlock(x, y, z);
		
		if(block.hasTileEntity(w.getBlockMetadata(x, y, z)))
		{
			TileEntity te = w.getTileEntity(x, y, z);
			if(te instanceof ISecureTile && !te.isInvalid() && !((ISecureTile)te).canPlayerInteract(ep, leftClick))
			{ ((ISecureTile)te).onPlayerNotOwner(ep, leftClick); return false; }
		}
		
		return canInteract(ep.getGameProfile().getId(), w, x, y, z, leftClick);
	}
	
	public static boolean canInteract(UUID playerID, World w, int x, int y, int z, boolean leftClick)
	{
		if(leftClick && FTBUConfigClaims.breakWhitelist.get().contains(LMInvUtils.getRegName(w.getBlock(x, y, z)))) return true;
		ChunkType type = ChunkType.getD(w.provider.dimensionId, x, z);
		return type.canInteract(LMWorldServer.inst.getPlayer(playerID), leftClick);
	}
}