package ftb.utils.mod.handlers;

import ftb.lib.*;
import ftb.lib.api.notification.Notification;
import ftb.lib.api.players.*;
import ftb.utils.mod.FTBU;
import ftb.utils.mod.config.FTBUConfigGeneral;
import ftb.utils.world.*;
import latmod.lib.MathHelperLM;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FTBUPlayerEventHandler
{
	@SubscribeEvent
	public void onChunkChanged(EntityEvent.EnteringChunk e)
	{
		if(e.entity.worldObj.isRemote || !(e.entity instanceof EntityPlayerMP)) return;
		
		EntityPlayerMP ep = (EntityPlayerMP) e.entity;
		ForgePlayerMP player = ForgeWorldMP.inst.getPlayer(ep);
		if(player == null || !player.isOnline()) return;
		
		player.lastPos = new EntityPos(ep).toBlockDimPos();
		
		ChunkType type = FTBUWorldDataMP.inst.getType(player, ep.dimension, e.newChunkX, e.newChunkZ);
		FTBUPlayerDataMP d = FTBUPlayerDataMP.get(player);
		
		if(d.lastChunkType == null || !d.lastChunkType.equals(type))
		{
			d.lastChunkType = type;
			IChatComponent msg;
			
			if(type.asClaimed() != null) msg = new ChatComponentText(String.valueOf(type.asClaimed().chunk.getOwner()));
			else msg = FTBU.mod.chatComponent(type.lang);
			
			msg.getChatStyle().setColor(EnumChatFormatting.WHITE);
			msg.getChatStyle().setBold(true);
			
			Notification n = new Notification("chunk_changed", msg, 3000);
			n.setColor(type.getAreaColor(player));
			FTBLib.notifyPlayer(ep, n);
		}
	}
	
	@SubscribeEvent
	public void onPlayerAttacked(LivingAttackEvent e)
	{
		if(e.entity.worldObj.isRemote) return;
		
		int dim = e.entity.dimension;
		if(dim != 0 || !(e.entity instanceof EntityPlayerMP) || e.entity instanceof FakePlayer) return;
		
		Entity entity = e.source.getSourceOfDamage();
		
		if(entity != null && (entity instanceof EntityPlayerMP || entity instanceof IMob))
		{
			if(entity instanceof FakePlayer) return;
			else if(entity instanceof EntityPlayerMP && ForgeWorldMP.inst.getPlayer(entity).allowCreativeInteractSecure())
				return;
			
			int cx = MathHelperLM.chunk(e.entity.posX);
			int cz = MathHelperLM.chunk(e.entity.posZ);
			
			if((FTBUConfigGeneral.safe_spawn.get() && FTBUWorldDataMP.isInSpawn(dim, cx, cz))) e.setCanceled(true);
			/*else
			{
				ClaimedChunk c = Claims.get(dim, cx, cz);
				if(c != null && c.claims.settings.isSafe()) e.setCanceled(true);
			}*/
		}
	}
}