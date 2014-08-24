package latmod.core.mod.tile;
import net.minecraft.tileentity.TileEntity;

public interface ITileInterface
{
	public TileEntity getTile();
	public boolean isServer();
}