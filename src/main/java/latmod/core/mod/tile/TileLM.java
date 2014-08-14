package latmod.core.mod.tile;
import latmod.core.*;
import latmod.core.mod.net.*;
import latmod.core.security.LMSecurity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.*;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileLM extends TileEntity implements ITileInterface, IInventory
{
	public static final String ACTION_BUTTON_PRESSED = "button";
	public static final String ACTION_OPEN_GUI = "openGUI";
	
	public static final int[] NO_SLOTS = new int[0];
	
	public String customName = null;
	public boolean dropItems = true;
	private boolean isDirty = true;
	public boolean isLoaded = false;
	public long tick = 0L;
	public LMSecurity security = null;
	
	public ItemStack items[] = null;
	
	@Override
	public final TileEntity getTile()
	{ return this; }
	
	public final void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		readTileData(tag);
		readTileServerData(tag);
	}
	
	public final void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		writeTileData(tag);
		writeTileServerData(tag);
	}
	
	public final Packet getDescriptionPacket()
	{ NBTTagCompound tag = new NBTTagCompound(); writeTileData(tag);
	return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag); }
	
	public final void onDataPacket(NetworkManager m, S35PacketUpdateTileEntity p)
	{ readTileData(p.func_148857_g()); }
	
	public void readTileData(NBTTagCompound tag)
	{
		if(tag.hasKey("Security"))
		security.readFromNBT(tag, "Security");
		
		if(items != null)
		items = InvUtils.readItemsFromNBT(items.length, tag, "Items");
		
		customName = null;
		if(tag.hasKey("CustomName")) customName = tag.getString("CustomName");
		tick = tag.getLong("Tick");
		if(tick < 0L) tick = 0L;
	}
	
	public void readTileServerData(NBTTagCompound tag)
	{
	}

	public void writeTileData(NBTTagCompound tag)
	{
		security.writeToNBT(tag, "Security");
		
		if(items != null)
		InvUtils.writeItemsToNBT(items, tag, "Items");
		
		if(customName != null) tag.setString("CustomName", customName);
		if(tick < 0L) tick = 0L;
		tag.setLong("Tick", tick);
	}
	
	public void writeTileServerData(NBTTagCompound tag)
	{
	}
	
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
	
	public final void updateEntity()
	{
		if(!isLoaded) onLoaded();
		
		onUpdate();
		
		if(isDirty)
		{
			isDirty = false;
			sendDirtyUpdate();
		}
		tick++;
	}
	
	public void onUpdate() { }
	
	public void sendDirtyUpdate()
	{
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	public void onPlaced()
	{ blockType = worldObj.getBlock(xCoord, yCoord, zCoord); }
	
	public void onPlacedBy(EntityPlayer ep, ItemStack is)
	{
		security = new LMSecurity(ep.getUniqueID());
		markDirty();
	}
	
	public void onBroken()
	{
		if(dropItems) InvUtils.dropAllItems(worldObj, xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, items);
		markDirty();
	}
	
	public boolean onRightClick(EntityPlayer ep, ItemStack is, int side, float x, float y, float z)
	{
		return false;
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
	
	public boolean recolourBlock(ForgeDirection side, int col)
	{ return false; }
	
	/** Player can be null */
	public boolean isMinable(EntityPlayer ep)
	{ return ep == null || security.canInteract(ep); }
	
	public boolean isExplosionResistant()
	{ return !security.level.isPublic(); }
	
	public final void sendClientAction(String action, NBTTagCompound data)
	{ LMNetHandler.INSTANCE.sendToServer(new MessageClientTileAction(this, action, data)); }
	
	// Inventory stuff //
	
	@Override
	public void markDirty()
	{ isDirty = true; }
	
	@Override
	public String getInventoryName()
	{ return hasCustomInventoryName() ? customName : "Inventory"; }

	@Override
	public boolean hasCustomInventoryName()
	{ return customName != null; }

	@Override
	public void openInventory() { }

	@Override
	public void closeInventory() { }
	
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
	{ return items != null && security.canInteract(ep); }

	@Override
	public boolean isItemValidForSlot(int i, ItemStack is)
	{ return items != null; }
}