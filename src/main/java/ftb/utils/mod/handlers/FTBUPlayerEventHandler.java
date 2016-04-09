package ftb.utils.mod.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import ftb.lib.*;
import ftb.lib.api.notification.Notification;
import ftb.utils.api.EventLMPlayerServer;
import ftb.utils.mod.FTBU;
import ftb.utils.mod.config.FTBUConfigGeneral;
import ftb.utils.net.*;
import ftb.utils.world.*;
import ftb.utils.world.claims.*;
import latmod.lib.MathHelperLM;
import latmod.lib.util.Pos2I;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.*;

public class FTBUPlayerEventHandler
{
	@SubscribeEvent
	public void playerLoggedOut(PlayerEvent.PlayerLoggedOutEvent e)
	{ if(e.player instanceof EntityPlayerMP) playerLoggedOut((EntityPlayerMP) e.player); }
	
	public static void playerLoggedOut(EntityPlayerMP ep)
	{
		LMPlayerServer p = LMWorldServer.inst.getPlayer(ep);
		if(p == null) return;
		p.refreshStats();
		
		for(int i = 0; i < 4; i++)
			p.lastArmor[i] = ep.inventory.armorInventory[i];
		p.lastArmor[4] = ep.inventory.getCurrentItem();
		
		new EventLMPlayerServer.LoggedOut(p, ep).post();
		new MessageLMPlayerLoggedOut(p).sendTo(null);
		
		p.setPlayer(null);
		//Backups.shouldRun = true;
		
		FTBUChunkEventHandler.instance.markDirty(null);
	}
	
	@SubscribeEvent
	public void onChunkChanged(EntityEvent.EnteringChunk e)
	{
		if(e.entity.worldObj.isRemote || !(e.entity instanceof EntityPlayerMP)) return;
		
		EntityPlayerMP ep = (EntityPlayerMP) e.entity;
		LMPlayerServer player = LMWorldServer.inst.getPlayer(ep);
		if(player == null || !player.isOnline()) return;
		
		player.lastPos = new EntityPos(ep).toLinkedPos();
		
		if(LMWorldServer.inst.settings.getWB(ep.dimension).isOutsideD(ep.posX, ep.posZ))
		{
			ep.motionX = ep.motionY = ep.motionZ = 0D;
			IChatComponent warning = FTBU.mod.chatComponent(ChunkType.WORLD_BORDER.lang + ".warning");
			warning.getChatStyle().setColor(EnumChatFormatting.WHITE);
			Notification n = new Notification("world_border", warning, 3000);
			n.color = ChunkType.WORLD_BORDER.getAreaColor(player);
			FTBLib.notifyPlayer(ep, n);
			
			if(LMWorldServer.inst.settings.getWB(player.lastPos.dim).isOutsideD(player.lastPos.x, player.lastPos.z))
			{
				FTBLib.printChat(ep, FTBU.mod.chatComponent("cmd.spawn_tp"));
				World w = LMDimUtils.getWorld(0);
				Pos2I pos = LMWorldServer.inst.settings.getWB(0).pos;
				int posY = w.getTopSolidOrLiquidBlock(pos.x, pos.y);
				LMDimUtils.teleportEntity(ep, pos.x + 0.5D, posY + 1.25D, pos.y + 0.5D, 0);
			}
			else LMDimUtils.teleportEntity(ep, player.lastPos);
			ep.worldObj.playSoundAtEntity(ep, "random.fizz", 1F, 1F);
		}
		
		int currentChunkType = LMWorldServer.inst.claimedChunks.getType(ep.dimension, e.newChunkX, e.newChunkZ).ID;
		
		if(player.lastChunkType == -99 || player.lastChunkType != currentChunkType)
		{
			player.lastChunkType = currentChunkType;
			
			ChunkType type = ClaimedChunks.getChunkTypeFromI(currentChunkType);
			IChatComponent msg = null;
			
			if(type.isClaimed())
				msg = new ChatComponentText(String.valueOf(LMWorldServer.inst.getPlayer(currentChunkType)));
			else msg = FTBU.mod.chatComponent(type.lang);
			
			msg.getChatStyle().setColor(EnumChatFormatting.WHITE);
			msg.getChatStyle().setBold(true);
			
			Notification n = new Notification("chunk_changed", msg, 3000);
			n.setColor(type.getAreaColor(player));
			
			FTBLib.notifyPlayer(ep, n);
		}
	}
	
	@SubscribeEvent
	public void onPlayerDeath(LivingDeathEvent e)
	{
		if(e.entity instanceof EntityPlayerMP)
		{
			LMPlayerServer p = LMWorldServer.inst.getPlayer(e.entity);
			p.lastDeath = new EntityPos(e.entity).toLinkedPos();
			
			p.refreshStats();
			new MessageLMPlayerDied(p).sendTo(null);
		}
	}
	
	@SubscribeEvent
	public void onPlayerAttacked(LivingAttackEvent e)
	{
		if(e.entity == null || e.entity.worldObj == null || e.entity.worldObj.isRemote) return;
		
		int dim = e.entity.dimension;
		if(dim != 0 || !(e.entity instanceof EntityPlayerMP) || e.entity instanceof FakePlayer) return;
		
		Entity entity = e.source.getSourceOfDamage();
		
		if(entity != null && (entity instanceof EntityPlayerMP || entity instanceof IMob))
		{
			if(entity instanceof FakePlayer) return;
			else if(entity instanceof EntityPlayerMP && LMWorldServer.inst.getPlayer(entity).allowInteractSecure())
				return;
			
			int cx = MathHelperLM.chunk(e.entity.posX);
			int cz = MathHelperLM.chunk(e.entity.posZ);
			
			if((FTBUConfigGeneral.safe_spawn.getAsBoolean() && ClaimedChunks.isInSpawn(dim, cx, cz)))
				e.setCanceled(true);
			/*else
			{
				ClaimedChunk c = Claims.get(dim, cx, cz);
				if(c != null && c.claims.settings.isSafe()) e.setCanceled(true);
			}*/
		}
	}
}