package latmod.ftbu.block;

import cpw.mods.fml.relauncher.*;
import ftb.lib.mod.FTBLibFinals;
import latmod.ftbu.item.ItemBlockLM;
import latmod.ftbu.tile.TileLM;
import latmod.ftbu.util.LMMod;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.*;

public abstract class BlockLM extends BlockContainer implements IBlockLM
{
	public final String blockName;
	public ArrayList<ItemStack> blocksAdded = new ArrayList<>();
	private Item blockItem = null;
	
	public BlockLM(String s, Material m)
	{
		super(m);
		blockName = s;
		setBlockName(getMod().getBlockName(s));
		setBlockTextureName(s);
		setHardness(1.8F);
		setResistance(3F);
		isBlockContainer = false;
	}
	
	public Class<? extends ItemBlockLM> getItemBlock()
	{ return ItemBlockLM.class; }
	
	public abstract LMMod getMod();
	
	@SideOnly(Side.CLIENT)
	public abstract CreativeTabs getCreativeTabToDisplayOn();

	public abstract TileLM createNewTileEntity(World w, int m);
	
	@SuppressWarnings("unchecked")
	public final <E> E register()
	{
		getMod().addBlock(this);
		return (E) this;
	}
	
	@SuppressWarnings("unchecked")
	public final <E> E registerDevOnly()
	{
		if(FTBLibFinals.DEV) getMod().addBlock(this);
		return (E) this;
	}
	
	public final String getItemID()
	{ return blockName; }
	
	public void onPostLoaded()
	{ blocksAdded.add(new ItemStack(this)); }
	
	public int damageDropped(int i)
	{ return i; }
	
	public boolean hasTileEntity(int m)
	{ return isBlockContainer; }
	
	public String getUnlocalizedName(int m)
	{ return getMod().getBlockName(blockName); }
	
	public void addAllDamages(int until)
	{
		for(int i = 0; i < until; i++)
			blocksAdded.add(new ItemStack(this, 1, i));
	}
	
	@SuppressWarnings("all")
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item j, CreativeTabs c, List l)
	{ l.addAll(blocksAdded); }

	public void onBlockPlacedBy(World w, int x, int y, int z, EntityLivingBase el, ItemStack is)
	{
		super.onBlockPlacedBy(w, x, y, z, el, is);
		
		if(isBlockContainer && el instanceof EntityPlayer)
		{
			TileLM tile = (TileLM) w.getTileEntity(x, y, z);
			if(tile != null) tile.onPlacedBy((EntityPlayer) el, is);
		}
	}
	
	public float getPlayerRelativeBlockHardness(EntityPlayer ep, World w, int x, int y, int z)
	{
		if(isBlockContainer)
		{
			TileLM tile = (TileLM) w.getTileEntity(x, y, z);
			if(tile != null && !tile.isMinable(ep)) return -1F;
		}
		
		return super.getPlayerRelativeBlockHardness(ep, w, x, y, z);
	}
	
	public float getBlockHardness(World w, int x, int y, int z)
	{
		if(isBlockContainer)
		{
			TileLM tile = (TileLM) w.getTileEntity(x, y, z);
			if(tile != null && !tile.isMinable(null)) return -1F;
		}
		
		return super.getBlockHardness(w, x, y, z);
	}
	
	public float getExplosionResistance(Entity e, World w, int x, int y, int z, double ex, double ey, double ez)
	{
		if(isBlockContainer)
		{
			TileLM tile = (TileLM) w.getTileEntity(x, y, z);
			if(tile != null && tile.isExplosionResistant()) return 1000000F;
		}
		
		return super.getExplosionResistance(e, w, x, y, z, ex, ey, ez);
	}
	
	public int getMobilityFlag()
	{ return isBlockContainer ? 2 : 0; }
	
	public void breakBlock(World w, int x, int y, int z, Block b, int m)
	{
		if(!w.isRemote && isBlockContainer)
		{
			TileLM tile = (TileLM) w.getTileEntity(x, y, z);
			if(tile != null) tile.onBroken();
		}
		super.breakBlock(w, x, y, z, b, m);
	}

	public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer ep, int s, float x1, float y1, float z1)
	{
		if(!isBlockContainer) return false;
		TileLM tile = (TileLM) w.getTileEntity(x, y, z);
		return (tile != null) ? tile.onRightClick(ep, ep.getHeldItem(), s, x1, y1, z1) : false;
	}
	
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir)
	{ blockIcon = ir.registerIcon(getMod().assets + getTextureName()); }

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int s, int m)
	{ return blockIcon; }

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s)
	{ return getIcon(s, iba.getBlockMetadata(x, y, z)); }

	public ForgeDirection[] getValidRotations(World worldObj, int x, int y, int z)
	{ return null; }
	
	public boolean rotateBlock(World w, int x, int y, int z, ForgeDirection side)
	{ return false; }

	public boolean onBlockEventReceived(World w, int x, int y, int z, int eventID, int param)
	{
		if(isBlockContainer)
		{
			TileLM t = (TileLM) w.getTileEntity(x, y, z);
			if(t != null) return t.receiveClientEvent(eventID, param);
		}
		
		return false;
	}

	public boolean recolourBlock(World w, int x, int y, int z, ForgeDirection side, int col)
	{
		if(isBlockContainer)
		{
			TileLM t = (TileLM) w.getTileEntity(x, y, z);
			if(t != null) return t.recolourBlock(side, col);
		}
		return false;
	}
	
	public void loadRecipes()
	{
	}
	
	@SideOnly(Side.CLIENT)
	public void addInfo(ItemStack is, EntityPlayer ep, List<String> l)
	{
	}
	
	public void onNeighborBlockChange(World w, int x, int y, int z, Block b)
	{
		if(isBlockContainer)
		{
			TileLM t = (TileLM) w.getTileEntity(x, y, z);
			if(t != null) t.onNeighborBlockChange(b);
		}
	}
	
	public final Item getItem()
	{
		if(blockItem == null) blockItem = Item.getItemFromBlock(this);
		return blockItem;
	}
	
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World w, int x, int y, int z)
	{
		setBlockBoundsBasedOnState(w, x, y, z);
		return super.getCollisionBoundingBoxFromPool(w, x, y, z);
	}
	
	@SideOnly(Side.CLIENT)
	public final IIcon getBlockIcon() { return blockIcon; }
	
	@Deprecated
	public final int onBlockPlaced(World w, int x, int y, int z, int s, float hitX, float hitY, float hitZ, int m)
	{ return m; }
	
	public int onBlockPlaced(World w, EntityPlayer ep, MovingObjectPosition mop, int m)
	{ return m; }
	
	@SuppressWarnings("unchecked")
	public final <T extends TileEntity> T getTile(Class<T> c, IBlockAccess iba, int x, int y, int z)
	{
		TileEntity te = iba.getTileEntity(x, y, z);
		if(te != null && c.isAssignableFrom(te.getClass())) return (T) te;
		return null;
	}
}