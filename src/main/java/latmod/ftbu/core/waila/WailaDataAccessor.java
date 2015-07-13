package latmod.ftbu.core.waila;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class WailaDataAccessor
{
	public EntityPlayer player;
	public World world;
	public MovingObjectPosition position;
	public TileEntity tile;
	public Block block;
	public int meta;
	public int side;
}