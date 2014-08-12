package latmod.core.mod.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;

public interface ILinkable extends ITileInterface
{
	public boolean onLinked(EntityPlayer ep, MovingObjectPosition tilePos, MovingObjectPosition linkPos);
}