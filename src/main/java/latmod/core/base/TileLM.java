package latmod.core.base;
import net.minecraft.block.Block;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.network.*;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.*;
import net.minecraftforge.common.ForgeDirection;
import latmod.core.*;
import latmod.core.tile.ITileInterface;

public class TileLM extends TileEntity implements ITileInterface, IInventory
{
	public static final int UP = ForgeDirection.UP.ordinal();
	public static final int DOWN = ForgeDirection.DOWN.ordinal();
	public static final int[] NO_SLOTS = new int[0];
	
	public String customName = null;
	public boolean dropItems = true;
	private boolean isDirty = true;
	public boolean isLoaded = false;
	public long tick = 0L;
	public LMSecurity security = new LMSecurity((String)null);
	
	public ItemStack items[] = null;
	
	@Override
	public final TileEntity getTile()
	{ return this; }
	
	public Packet getDescriptionPacket()
	{ NBTTagCompound tag = new NBTTagCompound(); writeToNBT(tag);
	return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag); }
	
	public void onDataPacket(INetworkManager m, Packet132TileEntityData p)
	{ readFromNBT(p.data); }
	
	public void invalidate()
	{
		if(isLoaded) onUnloaded();
		super.invalidate();
	}
	
	public void onChunkUnload()
	{
		if(isLoaded) onUnloaded();
		super.onChunkUnload();
	}
	
	public void onLoaded()
	{
		isLoaded = true;
		blockType = getBlockType();
		getMeta();
	}
	
	public void onUnloaded()
	{
		isLoaded = false;
	}
	
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		
		if(tag.hasKey("Security"))
		security.readFromNBT(tag.getCompoundTag("Security"));
		
		if(items != null)
		items = InvUtils.readItemsFromNBT(items.length, tag, "Items");
		
		customName = null;
		if(tag.hasKey("CustomName")) customName = tag.getString("CustomName");
		tick = tag.getLong("Tick");
		if(tick < 0L) tick = 0L;
	}
	
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		
		NBTTagCompound securityTag = new NBTTagCompound();
		security.writeToNBT(securityTag);
		tag.setTag("Security", securityTag);
		
		if(items != null)
		InvUtils.writeItemsToNBT(items, tag, "Items");
		
		if(customName != null) tag.setString("CustomName", customName);
		if(tick < 0L) tick = 0L;
		tag.setLong("Tick", tick);
	}
	
	public final void updateEntity()
	{
		if(!isLoaded) onLoaded();
		
		onUpdate();
		
		if(isDirty)
		{
			isDirty = false;
			//super.markDirty();
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		tick++;
	}
	
	public void onUpdate() { }
	
	public void onPlaced()
	{ blockType = Block.blocksList[worldObj.getBlockId(xCoord, yCoord, zCoord)]; }
	
	public void onPlacedBy(EntityPlayer ep, ItemStack is)
	{
		security = new LMSecurity(ep);
		onInventoryChanged();
	}
	
	public void onBroken()
	{
		if(dropItems) InvUtils.dropAllItems(worldObj, xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, items);
		onInventoryChanged();
	}
	
	public boolean onRightClick(EntityPlayer ep, ItemStack is, int side, float x, float y, float z)
	{
		return true;
	}
	
	public void dropItem(ItemStack is, double ox, double oy, double oz)
	{ EntityItem ei = new EntityItem(worldObj, xCoord + 0.5D + ox, yCoord + 0.5D + oy, zCoord + 0.5D + oz, is);
	ei.delayBeforeCanPickup = 20; worldObj.spawnEntityInWorld(ei); }
	
	public void setMeta(int m)
	{ worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, m, 3); isDirty = true; }
	
	public void getMeta()
	{ blockMetadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord); }
	
	public boolean isPowered(boolean direct)
	{
		if(direct) return isPowered(false);
		return worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
	}
	
	public float getWrenchDropRate()
	{ return 1F; }
	
	public boolean recolourBlock(ForgeDirection side, int col)
	{ return false; }
	
	/** Player can be null */
	public boolean isMinable(EntityPlayer ep)
	{ return ep == null || security.canPlayerInteract(ep); }
	
	public boolean isExplosionResistant()
	{ return !security.canInteract(null); }
	
	// Inventory stuff //
	
	@Override
	public void onInventoryChanged()
	{ isDirty = true; }
	
	@Override
	public String getInvName()
	{ return isInvNameLocalized() ? customName : "Inventory"; }

	@Override
	public boolean isInvNameLocalized()
	{ return customName != null; }

	@Override
	public void openChest() { }

	@Override
	public void closeChest() { }
	
	@Override
	public ItemStack decrStackSize(int i, int j)
	{ return (items == null) ? null : InvUtils.decrStackSize((IInventory)this, i, j); }
	
	@Override
	public int getInventoryStackLimit()
	{ return 64; }
	
	@Override
	public int getSizeInventory()
	{ return (items == null) ? 0 : items.length; }
	
	@Override
	public ItemStack getStackInSlot(int i)
	{ return (items == null) ? null : items[i]; }
	
	@Override
	public ItemStack getStackInSlotOnClosing(int i)
	{ return (items == null) ? null : InvUtils.getStackInSlotOnClosing((IInventory)this, i); }
	
	@Override
	public void setInventorySlotContents(int i, ItemStack is)
	{ if(items != null) items[i] = is; }
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer ep)
	{ return items != null && security.canPlayerInteract(ep); }

	@Override
	public boolean isItemValidForSlot(int i, ItemStack is)
	{ return items != null; }
	
	public boolean addSingleItemToSlots(ItemStack is0, int[] slots, boolean doAdd)
	{
		ItemStack is = InvUtils.singleCopy(is0);
		
		for(int i = 0; i < slots.length; i++)
		{
			if(items[slots[i]] != null && InvUtils.itemsEquals(items[slots[i]], is, false, true))
			{
				if(items[slots[i]].stackSize + 1 <= items[slots[i]].getMaxStackSize())
				{
					if(doAdd) items[slots[i]].stackSize++;
					return true;
				}
			}
		}
		
		for(int i = 0; i < slots.length; i++)
		{
			if(items[slots[i]] == null)
			{
				if(doAdd) items[slots[i]] = is;
				return true;
			}
		}
		
		return false;
	}
}