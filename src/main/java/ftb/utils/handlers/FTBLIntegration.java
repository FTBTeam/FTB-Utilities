package ftb.utils.handlers;

import com.google.gson.JsonArray;
import ftb.lib.FTBLib;
import ftb.lib.api.*;
import ftb.lib.api.events.ReloadEvent;
import ftb.lib.api.item.LMInvUtils;
import ftb.lib.api.permissions.ForgePermissionRegistry;
import ftb.lib.mod.*;
import ftb.utils.FTBUPermissions;
import ftb.utils.api.guide.ServerGuideFile;
import ftb.utils.badges.ServerBadges;
import ftb.utils.config.FTBUConfigGeneral;
import ftb.utils.ranks.Ranks;
import ftb.utils.world.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public class FTBLIntegration implements FTBUIntegration // FTBLIntegrationClient
{
	public void onReloaded(ReloadEvent e)
	{
		FTBUConfigGeneral.onReloaded(e.world.side);
		
		if(e.world.side.isServer())
		{
			ServerGuideFile.CachedInfo.reload();
			Ranks.instance().reload();
			ServerBadges.reload();
			
			if(FTBLib.getServerWorld() != null) FTBUChunkEventHandler.instance.markDirty(null);
		}
	}
	
	public void renderWorld(float pt)
	{
	}
	
	public void onTooltip(ItemTooltipEvent e)
	{
	}
	
	public boolean canPlayerInteract(EntityPlayerMP ep, BlockPos pos, boolean leftClick)
	{
		ForgePlayerMP p = ForgeWorldMP.inst.getPlayer(ep);
		
		if(p == null) return true;
		else if(!p.isFake() && ForgePermissionRegistry.hasPermission(FTBLibPermissions.interact_secure, ep.getGameProfile()))
		{
			return true;
		}
		
		//TODO: World border
		
		if(leftClick)
		{
			JsonArray a = FTBUPermissions.claims_break_whitelist.get(p.getProfile()).getAsJsonArray();
			
			for(int i = 0; i < a.size(); i++)
			{
				if(a.get(i).getAsString().equals(LMInvUtils.getRegName(ep.worldObj.getBlockState(pos).getBlock())))
					return true;
			}
		}
		
		ChunkType type = FTBUWorldDataMP.get().getTypeD(p, DimensionType.getById(ep.dimension), pos);
		return type.canInteract(p.toPlayerMP(), leftClick);
	}
}