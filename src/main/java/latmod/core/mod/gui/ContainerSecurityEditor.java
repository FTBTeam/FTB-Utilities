package latmod.core.mod.gui;

import latmod.core.mod.LC;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class ContainerSecurityEditor extends ContainerLM
{
	public ContainerSecurityEditor(EntityPlayer ep, IInventory i)
	{
		super(ep, i);
	}
	
	public ResourceLocation getTexture()
	{ return LC.mod.getLocation("/textures/gui/security.png"); }
}