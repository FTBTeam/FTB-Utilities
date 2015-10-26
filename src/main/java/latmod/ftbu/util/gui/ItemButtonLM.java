package latmod.ftbu.util.gui;
import cpw.mods.fml.relauncher.*;
import ftb.lib.client.TextureCoords;
import net.minecraft.item.ItemStack;

@SideOnly(Side.CLIENT)
public abstract class ItemButtonLM extends ButtonLM
{
	public ItemStack item;
	
	public ItemButtonLM(GuiLM g, int x, int y, int w, int h, ItemStack is)
	{ super(g, x, y, w, h); item = is; }
	
	public ItemButtonLM(GuiLM g, int x, int y, int w, int h)
	{ this(g, x, y, w, h, null); }
	
	public void setItem(ItemStack is)
	{ item = is; }
	
	public void setBackground(TextureCoords t)
	{ background = t; }
	
	public void renderWidget()
	{ if(item != null) gui.drawItem(item, getAX(), getAY()); }
}