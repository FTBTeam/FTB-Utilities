package ftb.utils.client.gui;

import ftb.lib.TextureCoords;
import ftb.lib.api.client.FTBLibClient;
import ftb.lib.api.gui.GuiLM;
import ftb.lib.api.gui.widgets.ButtonLM;
import ftb.utils.api.guide.GuideTextLine;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.IChatComponent;

import java.util.List;

/**
 * Created by LatvianModder on 04.03.2016.
 */
public class GuideWidget extends ButtonLM
{
	public final GuiGuide gui;
	public final GuideTextLine textLine;
	public TextureCoords texture;
	public List<String> text;
	
	public GuideWidget(GuiGuide g, GuideTextLine l)
	{
		super(g, 0, g.panelText.height, 0, 0);
		gui = g;
		textLine = l;
		
		if(textLine != null)
		{
			texture = textLine.getTexture(g.category.getImageFolder());
			
			IChatComponent c = textLine.getText().createCopy();
			
			if(c != null)
			{
				if(l.getClickAction() != null) c.getChatStyle().setUnderlined(true);
				text = gui.getFontRenderer().listFormattedStringToWidth(c.getFormattedText(), g.panelText.width);
			}
		}
		
		if(texture != null)
		{
			width = texture.widthI();
			height = texture.heightI();
		}
		else
		{
			if(text != null)
			{
				if(text.size() > 1) width = g.panelText.width;
				else width = g.getFontRenderer().getStringWidth(text.get(0));
				height = 10 * text.size();
			}
			else
			{
				width = 0;
				height = 11;
			}
			
			/*
			for(String s1 : fontRendererObj.listFormattedStringToWidth(s.trim(), panelText.width))
			{
			}
			*/
			//if(!l.text.isEmpty()) l.text = l.text.replace('\ufffd', '\u00a7');
		}
	}
	
	public void addMouseOverText(List<String> l)
	{
		if(textLine != null)
		{
			List<IChatComponent> h = textLine.getHover();
			
			if(h != null)
			{
				for(IChatComponent c : h)
				{
					l.add(c.getFormattedText());
				}
			}
		}
	}
	
	public void onButtonPressed(int b)
	{
		if(textLine != null && textLine.getClickAction() != null)
		{
			textLine.getClickAction().onClicked();
		}
	}
	
	public void renderWidget()
	{
		int ax = getAX();
		int ay = getAY();
		
		if(ay < -height || ay > gui.mainPanel.height) return;
		boolean mouseOver = mouseOver();
		
		if(texture != null && texture.isValid())
		{
			GlStateManager.color(1F, 1F, 1F, 1F);
			FTBLibClient.setTexture(texture.texture);
			double w = Math.min(gui.panelText.width, texture.width);
			GuiLM.render(texture, ax, ay, gui.getZLevel(), w, texture.getHeight(w) + 1);
			//GuiLM.drawTexturedRectD(ax, ay, gui.getZLevel(), w, line.texture.getHeight(w), 0D, line.texture.minU, 1D, line.texture.maxU);
		}
		else if(text != null && !text.isEmpty())
		{
			for(int i = 0; i < text.size(); i++)
			{
				gui.getFontRenderer().drawString(text.get(i), ax, ay + i * 10, GuiGuide.textColor);
			}
		}
		
		if(mouseOver)
		{
			GlStateManager.color(0F, 0F, 0F, 0.101F);
			GuiLM.drawBlankRect(ax, ay, gui.getZLevel(), width, height);
		}
	}
}
