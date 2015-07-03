package latmod.ftbu.mod.client.minimap;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.init.Blocks;

public class BlockColors
{
	public static MapColor getBlockColor(Block b, int m)
	{
		if(b == Blocks.sandstone) return MapColor.sandColor;
		else if(b == Blocks.fire) return MapColor.redColor;
		else return b.getMapColor(m);
	}
}