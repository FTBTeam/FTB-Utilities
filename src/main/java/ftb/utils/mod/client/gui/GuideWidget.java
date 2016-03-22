package ftb.utils.mod.client.gui;

import ftb.lib.TextureCoords;
import ftb.lib.api.client.*;
import ftb.lib.api.gui.GuiLM;
import ftb.lib.api.gui.widgets.ButtonLM;
import ftb.lib.mod.client.gui.GuiViewImage;
import ftb.utils.api.guide.*;
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
			GuideImage image = textLine.getImage();
			texture = image == null ? null : image.createDisplay();
			
			if(texture == null)
			{
				IChatComponent c = textLine.getText();
				
				if(c != null)
				{
					text = gui.getFontRenderer().listFormattedStringToWidth(c.getFormattedText(), g.panelText.width);
				}
			}
		}
		
		if(texture != null)
		{
			double w = Math.min(gui.panelText.width, texture.width);
			double h = texture.getHeight(w);
			texture = new TextureCoords(texture.texture, 0, 0, w, h, w, h);
			
			width = texture.widthI();
			height = texture.heightI() + 1;
		}
		else if(text != null)
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
		if(textLine != null)
		{
			if(textLine.getClickAction() != null)
			{
				textLine.getClickAction().onClicked();
			}
			else if(texture != null)
			{
				gui.mc.displayGuiScreen(new GuiViewImage(gui, textLine.getImage().createAcual()));
			}
		}
	}
	
	public void renderWidget()
	{
		int ax = getAX();
		int ay = getAY();
		
		if(ay < -height || ay > gui.mainPanel.height) return;
		boolean mouseOver = mouseOver();
		
		if(texture != null)
		{
			GlStateManager.color(1F, 1F, 1F, 1F);
			FTBLibClient.setTexture(texture.texture);
			GuiLM.render(texture, ax, ay, gui.getZLevel(), texture.width, texture.height);
		}
		else if(text != null && !text.isEmpty())
		{
			int color = textLine.getClickAction() != null ? 0xFF0000FF : GuideClientSettings.text_color.getAsInt();
			
			for(int i = 0; i < text.size(); i++)
			{
				gui.getFontRenderer().drawString(text.get(i), ax, ay + i * 10, color);
			}
		}
		
		if(mouseOver)
		{
			GlStateManager.color(0F, 0F, 0F, 0.101F);
			GuiLM.drawBlankRect(ax, ay, gui.getZLevel(), width, height);
		}
	}
}
