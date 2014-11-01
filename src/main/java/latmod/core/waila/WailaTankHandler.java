package latmod.core.waila;

import java.util.List;

import latmod.latcore.LCEventHandler;
import mcp.mobius.waila.api.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

public class WailaTankHandler extends BasicWailaHandler
{
	public WailaTankHandler(WailaHelper.RegisterHandlersEvent e)
	{
		super(e);
		registerBody = true;
	}
	
	public List<String> getWailaBody(ItemStack is, List<String> l, IWailaDataAccessor data, IWailaConfigHandler config)
	{
		if(config.getConfig(LCEventHandler.WAILA_TANK, false))
		{
			FluidTankInfo[] info = ((IFluidHandler)data.getTileEntity()).getTankInfo(ForgeDirection.UNKNOWN);
			
			if(info != null && info.length > 0) for(FluidTankInfo i : info) if(i != null && i.fluid != null)
			{
				int mb = i.fluid.amount;
				String fluid = i.fluid.getFluid().getLocalizedName(i.fluid);
				l.add("Tank: " + mb + " mB of " + fluid);
			}
		}
		
		return l;
	}
}