package latmod.ftbu.mod;

import java.util.UUID;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import latmod.ftbu.core.*;
import latmod.ftbu.core.api.readme.ReadmeFile;
import latmod.ftbu.core.net.*;
import latmod.ftbu.core.tile.TileLM;
import latmod.ftbu.core.world.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent;

public class FTBUCommon // FTBUClient
{
	public void preInit()
	{
	}
	
	public void postInit()
	{
	}
	
	public void onReadmeEvent(ReadmeFile file)
	{
	}
	
	public boolean isShiftDown() { return false; }
	public boolean isCtrlDown() { return false; }
	public boolean isTabDown() { return false; }
	public boolean inGameHasFocus() { return true; }
	
	public EntityPlayer getClientPlayer()
	{ return null; }
	
	public EntityPlayer getClientPlayer(UUID id)
	{ return null; }
	
	public World getClientWorld()
	{ return null; }
	
	public LMWorld<?> getClientWorldLM()
	{ return null; }
	
	public double getReachDist(EntityPlayer ep)
	{
		if(ep != null && ep instanceof EntityPlayerMP)
			return ((EntityPlayerMP)ep).theItemInWorldManager.getBlockReachDistance();
		return 0D;
	}
	
	public void spawnDust(World w, double x, double y, double z, int col) { }
	public boolean openClientGui(EntityPlayer ep, String mod, int id, NBTTagCompound data) { return false; }
	public <M extends MessageLM<?>> void handleClientMessage(IClientMessageLM<M> m, MessageContext ctx) { }
	public void readTileData(TileLM t, S35PacketUpdateTileEntity p) { }
	
	public void chunkChanged(EntityEvent.EnteringChunk e)
	{
		if(e.entity instanceof EntityPlayerMP)
		{
			EntityPlayerMP ep = (EntityPlayerMP)e.entity;
			LMPlayerServer p = LMWorldServer.inst.getPlayer(ep);
			if(p == null) return;
			
			if(p.lastPos == null) p.lastPos = new EntityPos(ep);
			
			else if(!p.lastPos.equalsPos(ep))
			{
				if(Claims.isOutsideWorldBorderD(ep.dimension, ep.posX, ep.posZ))
				{
					ep.motionX = ep.motionY = ep.motionZ = 0D;
					IChatComponent warning = new ChatComponentTranslation("ftbu:" + ChunkType.WORLD_BORDER.lang + ".warning");
					warning.getChatStyle().setColor(EnumChatFormatting.RED);
					LatCoreMC.notifyPlayer(ep, new Notification("world_border", warning, 3000));
					
					if(Claims.isOutsideWorldBorderD(p.lastPos.dim, p.lastPos.x, p.lastPos.z))
					{
						LatCoreMC.printChat(ep, "Teleporting to spawn!");
						FTBUEventHandler.instance.teleportToSpawn(ep);
					}
					else LMDimUtils.teleportPlayer(ep, p.lastPos);
					ep.worldObj.playSoundAtEntity(ep, "random.fizz", 1F, 1F);
				}
				
				p.lastPos.set(ep);
			}
			
			int currentChunkType = ChunkType.getChunkTypeI(ep.dimension, e.newChunkX, e.newChunkZ, p);
			
			if(p.lastChunkType == -99 || p.lastChunkType != currentChunkType)
			{
				p.lastChunkType = currentChunkType;
				
				ChunkType type = ChunkType.getChunkTypeFromI(currentChunkType, p);
				IChatComponent msg = null;
				
				if(type.isClaimed())
					msg = new ChatComponentText("" + LMWorldServer.inst.getPlayer(currentChunkType));
				else
					msg = new ChatComponentTranslation("ftbu:" + type.lang);
				
				Notification n = new Notification("chunk_changed", msg, 3000);
				n.setColor(type.areaColor);
				
				LatCoreMC.notifyPlayer(ep, n);
				LMNetHelper.sendTo(ep, new MessageAreaUpdate(e.newChunkX, e.newChunkZ, ep.dimension, currentChunkType));
			}
		}
	}
}