package latmod.ftbu.core.item;

import latmod.ftbu.core.client.LatCoreMCClient;
import latmod.ftbu.core.util.*;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.*;

public abstract class ItemMaterialsLM extends ItemLM
{
	public final FastMap<Integer, MaterialItem> materials;
	public String folder = "";
	
	public ItemMaterialsLM(String s)
	{
		super(s);
		materials = new FastMap<Integer, MaterialItem>();
		setHasSubtypes(true);
		setMaxDamage(0);
	}
	
	public ItemStack add(MaterialItem m)
	{
		materials.put(m.damage, m);
		
		if(m.getRenderPasses() > 1)
			requiresMultipleRenderPasses = true;
		
		return m.getStack();
	}
	
	public String getUnlocalizedName(ItemStack is)
	{
		MaterialItem m = materials.get(is.getItemDamage());
		if(m != null) return m.getUnlocalizedName();
		return "unknown";
	}
	
	public void onPostLoaded()
	{
		for(MaterialItem m : materials.values)
		{
			itemsAdded.add(m.getStack());
			m.onPostLoaded();
		}
	}
	
	public void loadRecipes()
	{
		for(MaterialItem m : materials.values)
			m.loadRecipes();
	}
	
	public int getRenderPasses(int i)
	{
		if(!requiresMultipleRenderPasses) return 1;
		MaterialItem m = materials.get(i);
		if(m != null) return m.getRenderPasses();
		return 1;
	}
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister ir)
	{
		itemIcon = LatCoreMCClient.unknownItemIcon;
		for(MaterialItem m : materials.values)
			m.registerIcons(ir);
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamageForRenderPass(int i, int r)
	{
		MaterialItem m = materials.get(i);
		if(m != null) return m.getIcon(r);
		return itemIcon;
	}
	
	@SideOnly(Side.CLIENT)
	public void addInfo(ItemStack is, EntityPlayer ep, FastList<String> l)
	{
		MaterialItem m = materials.get(is.getItemDamage());
		if(m != null) m.addInfo(ep, l);
	}
}
