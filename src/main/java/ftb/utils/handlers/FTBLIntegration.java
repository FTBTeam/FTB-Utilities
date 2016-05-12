package ftb.utils.handlers;

import com.google.gson.JsonElement;
import ftb.lib.FTBLib;
import ftb.lib.api.ForgePlayerMP;
import ftb.lib.api.events.ReloadEvent;
import ftb.lib.api.item.LMInvUtils;
import ftb.lib.api.permissions.ForgePermissionRegistry;
import ftb.lib.mod.FTBLibPermissions;
import ftb.lib.mod.FTBUIntegration;
import ftb.utils.FTBUPermissions;
import ftb.utils.api.guide.ServerGuideFile;
import ftb.utils.badges.ServerBadges;
import ftb.utils.ranks.Ranks;
import ftb.utils.world.ChunkType;
import ftb.utils.world.FTBUWorldDataMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public class FTBLIntegration implements FTBUIntegration // FTBLIntegrationClient
{
	@Override
	public void onReloaded(ReloadEvent e)
	{
		if(e.world.side.isServer())
		{
			ServerGuideFile.CachedInfo.reload();
			Ranks.instance().reload();
			ServerBadges.reload();
			
			if(FTBLib.getServerWorld() != null) { FTBUChunkEventHandler.instance.markDirty(null); }
		}
	}
	
	@Override
	public void renderWorld(float pt)
	{
	}
	
	@Override
	public void onTooltip(ItemTooltipEvent e)
	{
	}
	
	@Override
	public boolean canPlayerInteract(ForgePlayerMP player, BlockPos pos, boolean leftClick)
	{
		if(player == null) { return true; }
		else if(!player.isFake() && ForgePermissionRegistry.hasPermission(FTBLibPermissions.interact_secure, player.getProfile()))
		{
			return true;
		}
		
		//TODO: World border
		
		if(leftClick)
		{
			for(JsonElement e : FTBUPermissions.claims_break_whitelist.get(player.getProfile()).getAsJsonArray())
			{
				if(e.getAsString().equals(LMInvUtils.getRegName(player.getPlayer().worldObj.getBlockState(pos).getBlock()).toString()))
				{
					return true;
				}
			}
		}
		
		ChunkType type = FTBUWorldDataMP.get().getTypeD(player, DimensionType.getById(player.getPlayer().dimension), pos);
		return type.canInteract(player, leftClick);
	}
}