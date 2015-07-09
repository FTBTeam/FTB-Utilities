package latmod.ftbu.mod.client;

import java.util.Set;

import latmod.ftbu.core.util.FastList;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.client.gui.GuiClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import cpw.mods.fml.client.IModGuiFactory;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class FTBUGuiFactory implements IModGuiFactory
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
		{ super(s, new FastList<>(), FTBU.mod.modID, false, false, ""); }
		
		public void initGui()
		{ mc.displayGuiScreen(new GuiClientConfig(parentScreen)); }
		
		public void onGuiClosed()
		{ }
	}
}