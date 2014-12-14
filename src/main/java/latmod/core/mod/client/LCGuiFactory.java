package latmod.core.mod.client;

import java.util.Set;

import latmod.core.mod.LC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import cpw.mods.fml.client.IModGuiFactory;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class LCGuiFactory implements IModGuiFactory
{
	public void initialize(Minecraft mc)
	{
	}
	
	public Class<? extends GuiScreen> mainConfigGuiClass()
	{ return ModGuiConfig.class; }
	
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
	{ return null; }
	
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement e)
	{ return null; }
	
	public static class ModGuiConfig extends GuiConfig
	{
		@SuppressWarnings("all")
		public ModGuiConfig(GuiScreen s)
		{ super(s, new ConfigElement(LC.mod.config.getCategory("client")).getChildElements(), LC.mod.modID, false, false, LC.mod.config.getAbridgedPath()); }
	}
}