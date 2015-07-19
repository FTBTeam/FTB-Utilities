package latmod.ftbu.core.tile;
import latmod.ftbu.core.*;
import latmod.ftbu.core.block.BlockLM;
import latmod.ftbu.core.net.*;
import latmod.ftbu.core.util.LatCore;
import latmod.ftbu.mod.FTBU;
import net.minecraft.block.Block;
import net.minecraft.entity.player.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.*;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraftforge.common.util.ForgeDirection;

public class TileLM extends TileEntity implements IClientActionTile
{
	public static final String ACTION_BUTTON_PRESSED = "button";
	public static final String ACTION_OPEN_GUI = "openGUI";
	public static final String ACTION_CUSTOM_NAME = "customName";
	
	public static final int[] NO_SLOTS = new int[0];
	
	public String customName = "";
	private boolean isDirty = true;
	public boolean isLoaded = false;
	public long tick = 0L;
	public final LMSecurity security = new LMSecurity(null);
	public boolean redstonePowered = false;
	
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
	{
		NBTTagCompound tag = new NBTTagCompound();
		writeTileData(tag);
		writeTileClientData(tag);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
	}
	
	public final void onDataPacket(NetworkManager m, S35PacketUpdateTileEntity p)
	{ FTBU.proxy.readTileData(this, p); }
	
	public void readTileData(NBTTagCompound tag)
	{
		security.readFromNBT(tag, "Security");
		customName = tag.getString("CustomName");
		tick = tag.getLong("Tick");
		if(tick < 0L) tick = 0L;
	}
	
	public void writeTileData(NBTTagCompound tag)
	{
		security.writeToNBT(tag, "Security");
		if(customName == null) customName = "";
		if(!customName.isEmpty()) tag.setString("CustomName", customName);
		if(tick < 0L) tick = 0L;
		tag.setLong("Tick", tick);
	}
	
	public void readTileServerData(NBTTagCompound tag)
	{
	}
	
	public void writeTileServerData(NBTTagCompound tag)
	{
	}
	
	public void readTileClientData(NBTTagCompound tag)
	{
	}
	
	public void writeTileClientData(NBTTagCompound tag)
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
		blockType = getBlockType();
		
		if(blockType != null)
		{
			isLoaded = true;
			getMeta();
			onNeighborBlockChange(blockType);
		}
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
	{
		getBlockType();
		getBlockMetadata();
	}
	
	public void onPlacedBy(EntityPlayer ep, ItemStack is)
	{
		security.setOwner(ep);
		markDirty();
	}
	
	public void onBroken()
	{
		markDirty();
	}
	
	public final void printOwner(EntityPlayer ep)
	{ security.printOwner(ep); }
	
	public void setMeta(int m)
	{ blockMetadata = m; worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, m, 3); isDirty = true; }
	
	public void getMeta()
	{ blockMetadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord); }
	
	public BlockLM getBlockType()
	{
		Block b = super.getBlockType();
		if(b instanceof BlockLM) return (BlockLM)b;
		return null;
	}
	
	public boolean recolourBlock(ForgeDirection side, int col)
	{ return false; }
	
	/** Player can be null */
	public boolean isMinable(EntityPlayer ep)
	{ return ep == null || security.canInteract(ep); }
	
	public boolean isExplosionResistant()
	{ return !security.level.isPublic(); }
	
	public final void sendClientAction(String action, NBTTagCompound data)
	{ LMNetHelper.sendToServer(new MessageClientTileAction(this, action, data)); }
	
	public void clientPressButton(String button, int mouseButton, NBTTagCompound data)
	{
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("ID", button);
		tag.setByte("MB", (byte)mouseButton);
		if(data != null) tag.setTag("D", data);
		sendClientAction(ACTION_BUTTON_PRESSED, tag);
	}
	
	public void clientPressButton(String button, int mouseButton)
	{ clientPressButton(button, mouseButton, null); }
	
	public void clientOpenGui(NBTTagCompound data)
	{ sendClientAction(ACTION_OPEN_GUI, data); }
	
	public void clientCustomName(String name)
	{
		NBTTagCompound data = new NBTTagCompound();
		data.setString("Name", name);
		sendClientAction(ACTION_CUSTOM_NAME, data);
	}
	
	public void onClientAction(EntityPlayerMP ep, String action, NBTTagCompound data)
	{
		if(action.equals(ACTION_BUTTON_PRESSED))
		{
			handleButton(data.getString("ID"), data.getByte("MB"), data.getCompoundTag("D"), ep);
			markDirty();
		}
		else if(action.equals(ACTION_OPEN_GUI))
			LatCoreMC.openGui(ep, (IGuiTile)this, data);
		else if(action.equals(ACTION_CUSTOM_NAME))
		{
			String name = data.getString("Name");
			customName = (name.length() == 0) ? null : name;
			markDirty();
		}
	}
	
	public void handleButton(String button, int mouseButton, NBTTagCompound data, EntityPlayerMP ep)
	{
	}
	
	public final boolean isServer()
	{ return !worldObj.isRemote; }
	
	public void notifyNeighbors()
	{ worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, blockType); }
	
	public int getDimension()
	{ return worldObj == null ? 0 : worldObj.provider.dimensionId; }
	
	public final int hashCode()
	{ return LatCore.hashCode(xCoord, yCoord, zCoord, getDimension()); }
	
	public final boolean equals(Object o)
	{
		if(o == null) return false;
		if(o == this) return true;
		
		if(o.hashCode() == hashCode() && o instanceof TileLM)
		{
			TileLM t = (TileLM)o;
			return t.getDimension() == getDimension() && t.xCoord == xCoord && t.yCoord == yCoord && t.zCoord == zCoord;
		}

		return false;
	}
	
	public boolean equalsMOP(MovingObjectPosition mop)
	{ return mop != null && mop.blockX == xCoord && mop.blockY == yCoord && mop.blockZ == zCoord; }
	
	public void markDirty()
	{ isDirty = true; }
	
	public void onNeighborBlockChange(Block b)
	{ redstonePowered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord); }
	
	public LMSecurity getSecurity()
	{ return security; }
	
	public TileEntity getTile(int side)
	{ return worldObj.getTileEntity(xCoord + Facing.offsetsXForSide[side], yCoord + Facing.offsetsYForSide[side], zCoord + Facing.offsetsZForSide[side]); }
	
	public Block getBlock(int side)
	{ return worldObj.getBlock(xCoord + Facing.offsetsXForSide[side], yCoord + Facing.offsetsYForSide[side], zCoord + Facing.offsetsZForSide[side]); }
}