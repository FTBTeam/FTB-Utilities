package latmod.core.base;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;

public abstract class ContainerLM extends Container
{
	public EntityPlayer player;
	public TileLM tile;
	
	public ContainerLM(EntityPlayer ep, TileLM t)
	{
		player = ep;
		tile = t;
	}
	
	public ItemStack transferStackInSlot(EntityPlayer ep, int i)
	{
		return null;
	}
	
	public void addPlayerSlots(int offsetY)
	{
		for(int y = 0; y < 3; y++) for(int x = 0; x < 9; x++)
		addSlotToContainer(new Slot(player.inventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18 + offsetY));
		
		for(int x = 0; x < 9; x++)
		addSlotToContainer(new Slot(player.inventory, x, 8 + x * 18, 142 + offsetY));
	}
	
	public boolean canInteractWith(EntityPlayer ep)
	{ return true; }
	
	public abstract ResourceLocation getTexture();
}