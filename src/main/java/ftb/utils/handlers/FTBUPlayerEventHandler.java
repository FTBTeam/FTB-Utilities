package ftb.utils.handlers;

import ftb.lib.ChunkDimPos;
import ftb.lib.EntityDimPos;
import ftb.lib.FTBLib;
import ftb.lib.api.ForgePlayerMP;
import ftb.lib.api.ForgeWorldMP;
import ftb.lib.api.notification.Notification;
import ftb.lib.api.permissions.ForgePermissionRegistry;
import ftb.lib.mod.FTBLibPermissions;
import ftb.utils.config.FTBUConfigGeneral;
import ftb.utils.world.ChunkType;
import ftb.utils.world.FTBUPlayerDataMP;
import ftb.utils.world.FTBUWorldDataMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FTBUPlayerEventHandler
{
	@SubscribeEvent
	public void onChunkChanged(EntityEvent.EnteringChunk e)
	{
		if(e.getEntity().worldObj.isRemote || !(e.getEntity() instanceof EntityPlayerMP)) { return; }
		
		EntityPlayerMP ep = (EntityPlayerMP) e.getEntity();
		ForgePlayerMP player = ForgeWorldMP.inst.getPlayer(ep);
		if(player == null || !player.isOnline()) { return; }
		
		player.lastPos = new EntityDimPos(ep).toBlockDimPos();
		
		ChunkType type = FTBUWorldDataMP.get().getType(player, new ChunkDimPos(DimensionType.getById(ep.dimension), e.getNewChunkX(), e.getNewChunkZ()));
		FTBUPlayerDataMP d = FTBUPlayerDataMP.get(player);
		
		if(d.lastChunkType == null || !d.lastChunkType.equals(type))
		{
			d.lastChunkType = type;
			
			ITextComponent msg = type.getTitleComponent();
			msg.getStyle().setColor(TextFormatting.WHITE);
			msg.getStyle().setBold(true);
			Notification n = new Notification("chunk_changed", msg, 3000);
			n.setColor(type.getAreaColor(player));
			FTBLib.notifyPlayer(ep, n);
		}
	}
	
	@SubscribeEvent
	public void onPlayerAttacked(LivingAttackEvent e)
	{
		if(e.getEntity().worldObj.isRemote) { return; }
		
		DimensionType dim = DimensionType.getById(e.getEntity().dimension);
		if(dim != DimensionType.OVERWORLD || !(e.getEntity() instanceof EntityPlayerMP) || e.getEntity() instanceof FakePlayer)
		{ return; }
		
		Entity entity = e.getSource().getSourceOfDamage();
		
		if(entity != null && (entity instanceof EntityPlayerMP || entity instanceof IMob))
		{
			if(entity instanceof FakePlayer) { return; }
			else if(entity instanceof EntityPlayerMP && ForgePermissionRegistry.hasPermission(FTBLibPermissions.interact_secure, ((EntityPlayerMP) entity).getGameProfile()))
			{
				return;
			}
			
			if((FTBUConfigGeneral.safe_spawn.getAsBoolean() && FTBUWorldDataMP.isInSpawnD(dim, e.getEntity().posX, e.getEntity().posZ)))
			{
				e.setCanceled(true);
			}
			/*else
			{
				ClaimedChunk c = Claims.get(dim, cx, cz);
				if(c != null && c.claims.settings.isSafe()) e.setCanceled(true);
			}*/
		}
	}
}