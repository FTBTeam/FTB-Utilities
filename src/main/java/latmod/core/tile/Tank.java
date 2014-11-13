package latmod.core.tile;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

public class Tank
{
	public final String name;
	public FluidTank fluidTank;
	
	public Tank(String s, double buckets)
	{
		name = s;
		fluidTank = new FluidTank((int)(buckets * FluidContainerRegistry.BUCKET_VOLUME));
	}
	
	public boolean hasFluid()
	{ return fluidTank.getFluidAmount() > 0; }
	
	public FluidStack getFluidStack()
	{ return fluidTank.getFluid(); }
	
	public Fluid getFluid()
	{ return hasFluid() ? getFluidStack().getFluid() : null; }
	
	public int getAmount()
	{ return fluidTank.getFluidAmount(); }
	
	public int getCapacity()
	{ return fluidTank.getCapacity(); }
	
	public void readFromNBT(NBTTagCompound tag)
	{
		NBTTagCompound tankTag = tag.getCompoundTag(name);
		fluidTank.readFromNBT(tankTag);
		if(tankTag.hasKey("Empty"))
		fluidTank.setFluid(null);
	}
	
	public void writeToNBT(NBTTagCompound tag)
	{
		NBTTagCompound tankTag = new NBTTagCompound();
		fluidTank.writeToNBT(tankTag);
		tag.setTag(name, tankTag);
	}
	
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{ if(canFill(from, resource.getFluid())) return fluidTank.fill(resource, doFill); return 0; }
	
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
	{
		if(fluidTank.getFluidAmount() <= 0) return null;
		if(!fluidTank.getFluid().isFluidEqual(resource)) return null;
		if(!canDrain(from, resource.getFluid())) return null;
		return fluidTank.drain(resource.amount, doDrain);
	}
	
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{ return fluidTank.drain(maxDrain, doDrain); }
	
	public boolean canFill(ForgeDirection from, Fluid fluid)
	{ return true; }
	
	public boolean canDrain(ForgeDirection from, Fluid fluid)
	{ return true; }
	
	public FluidTankInfo[] getTankInfo(ForgeDirection from)
	{ return new FluidTankInfo[] { fluidTank.getInfo() }; }
}