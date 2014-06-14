package latmod.core.base;
import java.util.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.*;
import net.minecraft.util.IIcon;
import net.minecraft.world.*;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class BlockLM extends BlockContainer
{
	public final String blockName;
	public ArrayList<ItemStack> blocksAdded = new ArrayList<ItemStack>();
	public final LMMod mod;
	
	public BlockLM(LMMod bm, String s, Material m)
	{
		super(m);
		mod = bm;
		blockName = s;
		setBlockName(mod.getBlockName(s));
		setHardness(1.8F);
		setResistance(3F);
		isBlockContainer = false;
	}

	public void onPostLoaded()
	{ blocksAdded.add(new ItemStack(this)); }

	public Block setHardness(float f)
	{ return super.setHardness(f); }

	@SideOnly(Side.CLIENT)
	public abstract CreativeTabs getCreativeTabToDisplayOn();

	public int damageDropped(int i)
	{ return i; }

	public boolean hasTileEntity(int m)
	{ return isBlockContainer; }
	
	public String getUnlocalizedName(int m)
	{ return mod.getBlockName(blockName); }
	
	public void addAllDamages(int until)
	{
		for(int i = 0; i < until; i++)
		blocksAdded.add(new ItemStack(this, 1, i));
	}
	
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item j, CreativeTabs c, List l)
	{ l.addAll(blocksAdded); }

	public void onBlockPlacedBy(World w, int x, int y, int z, EntityLivingBase el, ItemStack is)
	{
		if(isBlockContainer && el instanceof EntityPlayer)
		{
			TileLM tile = (TileLM) w.getTileEntity(x, y, z);
			if(tile != null) tile.onPlacedBy((EntityPlayer)el, is);
		}
	}

	public void onPostBlockPlaced(World w, int x, int y, int z, int s)
	{
		if(isBlockContainer)
		{
			//TileLME tile = (TileLME) w.getTileEntity(x, y, z);
			//if(tile != null) tile.onPostPlaced(s);
		}
	}
	
	public float getPlayerRelativeBlockHardness(EntityPlayer ep, World w, int x, int y, int z)
	{
		TileLM tile = (TileLM) w.getTileEntity(x, y, z);
		if(tile != null && tile.isIndestructible(ep)) return -1F;
		return super.getPlayerRelativeBlockHardness(ep, w, x, y, z);
	}
	
	public float getExplosionResistance(Entity e, World w, int x, int y, int z, double ex, double ey, double ez)
	{
		TileLM tile = (TileLM) w.getTileEntity(x, y, z);
		if(tile != null && tile.isIndestructible(null)) return 1000000F;
		return super.getExplosionResistance(e, w, x, y, z, ex, ey, ez);
	}

	public int getMobilityFlag()
	{ return 2; }

	public ArrayList<ItemStack> getDrops(World w, int x, int y, int z, int m, int f)
	{
		/*if(!isBlockContainer || !dropSpecialBlock()) return super.getDrops(w, x, y, z, m, f);
		TileLME tile = (TileLME) w.getTileEntity(x, y, z);
		if(tile != null)
		{
			ArrayList<ItemStack> al = new ArrayList<ItemStack>();
			tile.addDropItems(al, this, w.getBlockMetadata(x, y, z));
			return al;
		}
		*/

		return super.getDrops(w, x, y, z, m, f);
	}
	
	public boolean dropSpecialBlock()
	{ return false; }

	public void breakBlock(World w, int x, int y, int z, Block b, int m)
	{
		if(!w.isRemote && isBlockContainer)
		{ TileLM tile = (TileLM) w.getTileEntity(x, y, z);
		if(tile != null) tile.onBroken(); }
		super.breakBlock(w, x, y, z, b, m);
	}

	public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer ep, int s, float x1, float y1, float z1)
	{
		if(!isBlockContainer) return false;
		TileLM tile = (TileLM) w.getTileEntity(x, y, z);
		return (tile != null) ? tile.onRightClick(ep, ep.getCurrentEquippedItem(), s, x1, y1, z1) : false;
	}

	public final ItemStack create(Object... args)
	{ return new ItemStack(this); }
	
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir)
	{ blockIcon = ir.registerIcon(mod.assets + blockName); }

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int s, int m)
	{ return blockIcon; }

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s)
	{ return getIcon(s, iba.getBlockMetadata(x, y, z)); }

	public boolean rotateBlock(World w, int x, int y, int z, ForgeDirection side)
	{
		return false;
	}

	public ForgeDirection[] getValidRotations(World worldObj, int x, int y, int z)
	{ return ForgeDirection.VALID_DIRECTIONS; }

	public boolean onBlockEventReceived(World w, int x, int y, int z, int eventID, int param)
	{
		TileLM t = (TileLM) w.getTileEntity(x, y, z);
		if(t != null) return t.receiveClientEvent(eventID, param);
		return false;
	}

	public boolean recolourBlock(World w, int x, int y, int z, ForgeDirection side, int col)
	{
		TileLM t = (TileLM) w.getTileEntity(x, y, z);
		if(t != null) return t.colorBlock(side, col);
		return false;
	}
	
	public void loadRecipes()
	{
	}
}