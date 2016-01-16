package latmod.ftbu.mod.client.gui.friends;

import ftb.lib.gui.GuiLM;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;

import java.util.*;

public class PanelText extends PanelFriendsGui
{
	public final List<String> text;
	
	public PanelText(GuiFriends g)
	{
		super(g);
		text = new ArrayList<>();
	}
	
	public FontRenderer getFont()
	{ return gui.getFontRenderer(); }
	
	public void addWidgets()
	{
		if(text.isEmpty()) return;
		FontRenderer fr = getFont();
		height = 4 + text.size() * (fr.FONT_HEIGHT + 3);
		width = 0;
		for(int i = 0; i < text.size(); i++)
			width = Math.max(width, fr.getStringWidth(text.get(i)) + 5);
	}
	
	public void renderWidget()
	{
		if(text.isEmpty()) return;
		FontRenderer fr = getFont();
		
		int ax = getAX();
		int ay = getAY();
		
		float z = gui.getZLevel();
		GlStateManager.color(0.4F, 0.4F, 0.4F, 0.2F);
		GuiLM.drawBlankRect(ax, ay, z, width, height);
		GlStateManager.color(1F, 1F, 1F, 1F);
		
		GlStateManager.translate(0F, 0F, z);
		for(int i = 0; i < text.size(); i++)
			fr.drawString(text.get(i), ax + 4, ay + 4 + i * (fr.FONT_HEIGHT + 3), 0xFFFFFFFF);
		GlStateManager.translate(0F, 0F, -z);
	}
}