package latmod.core.waila;

import java.util.List;

import latmod.latcore.*;
import mcp.mobius.waila.api.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

public class WailaTankHandler extends BasicWailaHandler
{
	public static final String CONFIG_TANK = "latcoremc.tank";
	
	public WailaTankHandler(RegisterWailaEvent e)
	{
		super(e);
		e.registry.addConfig(LC.mod.modID, CONFIG_TANK, false);
	}
	
	public List<String> getWailaBody(ItemStack is, List<String> l, IWailaDataAccessor data, IWailaConfigHandler config)
	{
		if(LCConfig.General.addWailaTanks && config.getConfig(CONFIG_TANK, false))
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