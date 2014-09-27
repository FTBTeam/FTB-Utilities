package latmod.core.mod.tile;
import java.util.Arrays;

import latmod.core.*;
import latmod.core.mod.*;
import latmod.core.mod.net.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.*;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileLM extends TileEntity implements ITileInterface, IInventory, IClientActionTile
{
	public static final String ACTION_BUTTON_PRESSED = "button";
	public static final String ACTION_OPEN_GUI = "openGUI";
	public static final String ACTION_CUSTOM_NAME = "customName";
	
	public static final int[] NO_SLOTS = new int[0];
	
	public String customName = null;
	public boolean dropItems = true;
	private boolean isDirty = true;
	public boolean isLoaded = false;
	public long tick = 0L;
	public final LMSecurity security = new LMSecurity(null);
	public boolean redstonePowered = false;
	
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
	{ readTileData(p.func_148857_g()); onUpdatePacket(); }
	
	public void readTileData(NBTTagCompound tag)
	{
		security.readFromNBT(tag, "Security");
		
		if(items != null)
		InvUtils.readItemsFromNBT(items, tag, "Items");
		
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
	
	public void onUpdatePacket()
	{
		if(rerenderBlock())
			worldObj.func_147479_m(xCoord, yCoord, zCoord);
	}
	
	public boolean rerenderBlock()
	{ return false; }
	
	public boolean onRightClick(EntityPlayer ep, ItemStack is, int side, float x, float y, float z)
	{
		return false;
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
		onNeighborBlockChange();
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
			
			if(isServer())
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
		security.owner = ep.getUniqueID();
		markDirty();
	}
	
	public void onBroken()
	{
		if(isServer() && dropItems && items != null && items.length > 0)
		{
			InvUtils.dropAllItems(worldObj, xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, items);
			Arrays.fill(items, null);
		}
		
		markDirty();
	}
	
	public final void printOwner(EntityPlayer ep)
	{ LatCoreMC.printChat(ep, LC.mod.translate("owner", LMPlayer.getPlayer(security.owner).username)); }
	
	public void dropItem(ItemStack is, double ox, double oy, double oz)
	{ EntityItem ei = new EntityItem(worldObj, xCoord + 0.5D + ox, yCoord + 0.5D + oy, zCoord + 0.5D + oz, is);
	ei.delayBeforeCanPickup = 20; worldObj.spawnEntityInWorld(ei); }
	
	public void setMeta(int m)
	{ worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, m, 3); isDirty = true; }
	
	public void getMeta()
	{ blockMetadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord); }
	
	public boolean recolourBlock(ForgeDirection side, int col)
	{ return false; }
	
	/** Player can be null */
	public boolean isMinable(EntityPlayer ep)
	{ return ep == null || security.canInteract(ep); }
	
	public boolean isExplosionResistant()
	{ return !security.level.isPublic(); }
	
	public final void sendClientAction(String action, NBTTagCompound data)
	{ LMNetHandler.INSTANCE.sendToServer(new MessageClientTileAction(this, action, data)); }
	
	public void clientPressButton(String button, int mouseButton)
	{
		NBTTagCompound data = new NBTTagCompound();
		data.setString("ID", button);
		data.setByte("MB", (byte)mouseButton);
		sendClientAction(ACTION_BUTTON_PRESSED, data);
	}
	
	public void clientOpenGui(int guiID)
	{
		NBTTagCompound data = new NBTTagCompound();
		data.setByte("ID", (byte)guiID);
		sendClientAction(ACTION_OPEN_GUI, data);
	}
	
	public void clientCustomName(String name)
	{
		NBTTagCompound data = new NBTTagCompound();
		data.setString("Name", name);
		sendClientAction(ACTION_CUSTOM_NAME, data);
	}
	
	public void onClientAction(EntityPlayer ep, String action, NBTTagCompound data)
	{
		if(action.equals(ACTION_BUTTON_PRESSED))
		{
			String button = data.getString("ID");
			int mouseButton = data.getByte("MB");
			handleButton(button, mouseButton, ep);
			markDirty();
		}
		else if(action.equals(ACTION_OPEN_GUI))
		{
			int guiID = data.getByte("ID");
			openGui(guiID, ep);
		}
		else if(action.equals(ACTION_CUSTOM_NAME))
		{
			String name = data.getString("Name");
			customName = (name.length() == 0) ? null : name;
			markDirty();
		}
	}
	
	public void handleButton(String button, int mouseButton, EntityPlayer ep)
	{
	}
	
	public void openGui(int guiID, EntityPlayer ep)
	{
	}
	
	public final boolean isServer()
	{ return !worldObj.isRemote; }
	
	public void notifyNeighbors()
	{ worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, blockType); }
	
	public boolean equals(Object o)
	{
		if(o instanceof TileLM)
		{
			TileLM t = (TileLM)o;
			return t.worldObj.provider.dimensionId == worldObj.provider.dimensionId &&
				t.xCoord == xCoord && t.yCoord == yCoord && t.zCoord == zCoord;
		}

		return false;
	}
	
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
	
	public void onNeighborBlockChange()
	{ redstonePowered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord); }
}