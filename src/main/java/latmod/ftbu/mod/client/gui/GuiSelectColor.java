package latmod.ftbu.mod.client.gui;

import latmod.ftbu.core.gui.*;
import latmod.ftbu.core.util.FastList;
import latmod.ftbu.mod.FTBU;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class GuiSelectColor extends GuiLM
{
	public static final ResourceLocation tex = FTBU.mod.getLocation("textures/gui/colselector.png");
	public static final TextureCoords col_tex = new TextureCoords(tex, 143, 10, 28, 36);
	public static final TextureCoords slider_red_tex = new TextureCoords(tex, 143, 0, 6, 10);
	public static final TextureCoords slider_green_tex = new TextureCoords(tex, 149, 0, 6, 10);
	public static final TextureCoords slider_blue_tex = new TextureCoords(tex, 155, 0, 6, 10);
	
	public final GuiScreen parentGui;
	public final int initCol;
	
	public GuiSelectColor(GuiScreen g, int col)
	{
		super(new ContainerEmpty.ClientGui(), tex);
		parentGui = g;
		initCol = col;
		xSize = 143;
		ySize = 48;
	}
	
	public static interface ColorSelectorCallback
	{
		public void onColorSelected(int color);
	}
	
	public void addWidgets(FastList<WidgetLM> l)
	{
	}
	
	public void closeGui()
	{
		if(parentGui != null) mc.displayGuiScreen(parentGui);
		else mc.thePlayer.closeScreen();
	}
}