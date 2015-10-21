package latmod.ftbu.mod.client.gui;

import java.util.List;

import org.lwjgl.opengl.GL11;

import ftb.lib.client.FTBLibClient;
import latmod.ftbu.api.guide.GuideCategory;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.util.gui.*;
import latmod.lib.FastList;
import net.minecraft.util.ResourceLocation;

public class GuiGuide extends GuiLM
{
	public static final ResourceLocation tex = FTBU.mod.getLocation("textures/gui/guide.png");
	public static final TextureCoords tex_slider = new TextureCoords(tex, 0, 240, 12, 18, 512, 512);
	public static final TextureCoords tex_back = new TextureCoords(tex, 0, 260, 15, 11, 512, 512);
	public static final TextureCoords tex_close = new TextureCoords(tex, 0, 271, tex_back.width, tex_back.height, 512, 512);
	
	public static final int textColor = 0xFF7B6534;
	public static final int textColorOver = 0xFF9D6A00;
	public static int maxCategoryButtons = 15;
	public static int maxTextLines = 20;
	
	public final GuiGuide parentGui;
	public final GuideCategory category;
	
	public final SliderLM sliderCategories, sliderText;
	public final ButtonLM buttonBack;
	
	public final WidgetLM categoriesPanel, textPanel;
	public final FastList<String> allTextLines;
	public final FastList<ButtonCategory> categoryButtons; // Max 16
	public final FastList<String> textLines; // Max 20
	
	public GuiGuide(GuiGuide g, GuideCategory c)
	{
		super(null, tex);
		parentGui = g;
		category = c;
		
		hideNEI = true;
		xSize = 328;
		ySize = 240;
		
		sliderCategories = new SliderLM(this, 11, 14, tex_slider.width, 210, tex_slider.height)
		{
			public boolean canMouseScroll()
			{ return gui.mouseX < guiLeft + xSize / 2; }
			
			public boolean isEnabled()
			{ return category.subcategories.size() > maxCategoryButtons; }
		};
		
		sliderCategories.isVertical = true;
		
		sliderText = new SliderLM(this, 304, 14, tex_slider.width, 210, tex_slider.height)
		{
			public boolean canMouseScroll()
			{ return gui.mouseX > guiLeft + xSize / 2; }
			
			public boolean isEnabled()
			{ return allTextLines.size() > maxTextLines; }
		};
		
		sliderText.isVertical = true;
		
		buttonBack = new ButtonLM(this, 35, 12, tex_back.width, tex_back.height)
		{
			public void onButtonPressed(int b)
			{
				playClickSound();
				if(parentGui == null)
					mc.thePlayer.closeScreen();
				else mc.displayGuiScreen(parentGui);
			}
		};
		
		categoriesPanel = new WidgetLM(this, 33, 29, 128, 200);
		textPanel = new WidgetLM(this, 167, 10, 128, 219);
		
		allTextLines = new FastList<String>();
		categoryButtons = new FastList<ButtonCategory>();
		textLines = new FastList<String>();
	}
	
	public void addWidgets()
	{
		mainPanel.add(sliderCategories);
		mainPanel.add(sliderText);
		mainPanel.add(buttonBack);
		mainPanel.add(textPanel);
		
		categoryButtons.clear();
		int catl = category.subcategories.size();
		int off = 0;
		
		if(catl > maxCategoryButtons)
		{
			float f = sliderCategories.value * (catl - 1 - maxCategoryButtons);
			off = (int)f;
			sliderCategories.scrollStep = 1F / (catl - 1 - maxCategoryButtons);
		}
		
		for(int i = 0; i < maxCategoryButtons; i++)
		{
			if(i + off < catl)
				categoryButtons.add(new ButtonCategory(GuiGuide.this, categoriesPanel.posX, categoriesPanel.posY + i * 13, categoriesPanel.width, 13, category.subcategories.get(i + off)));
		}
		
		mainPanel.addAll(categoryButtons);
	}
	
	@SuppressWarnings("unchecked")
	public void initLMGui()
	{
		allTextLines.clear();
		
		String s = category.getText();
		if(s != null && s.length() > 0)
		{
			boolean uni = fontRendererObj.getUnicodeFlag();
			fontRendererObj.setUnicodeFlag(true);
			List<String> list = fontRendererObj.listFormattedStringToWidth(s.trim(), textPanel.width);
			
			for(int i = 0; i < list.size(); i++)
				allTextLines.add(list.get(i));
			
			fontRendererObj.setUnicodeFlag(uni);
		}
		
		refreshText();
	}
	
	public void refreshText()
	{
		textLines.clear();
		
		int lines = allTextLines.size();
		int off = 0;
		
		if(lines > maxTextLines)
		{
			float f = sliderText.value * (lines - 1 - maxTextLines);
			off = (int)f;
			sliderText.scrollStep = 1F / (lines - 1 - maxTextLines);
		}
		
		for(int i = 0; i < maxTextLines; i++)
		{
			if(i + off < lines)
				textLines.add(allTextLines.get(i + off));
		}
	}
	
	public void drawTexturedModalRectD(double x, double y, double u, double v, double w, double h)
	{ drawTexturedModalRectD(x, y, zLevel, u, v, w, h, 512, 512); }
	
	public void drawBackground()
	{
		if(sliderCategories.isEnabled() && sliderCategories.update())
			refreshWidgets();
		
		if(sliderText.isEnabled() && sliderText.update())
			refreshText();
		
		super.drawBackground();
		
		if(sliderCategories.isEnabled()) sliderCategories.renderSlider(tex_slider);
		if(sliderText.isEnabled()) sliderText.renderSlider(tex_slider);
		
		FTBLibClient.setGLColor(buttonBack.mouseOver() ? textColorOver : textColor, 255);
		buttonBack.render((parentGui == null) ? tex_close : tex_back);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		
		getFontRenderer();
		
		fontRendererObj.drawString(category.getTitle(), getPosX(53), getPosY(14), textColor);
		
		if(!textLines.isEmpty())
		{
			boolean uni = fontRendererObj.getUnicodeFlag();
			fontRendererObj.setUnicodeFlag(true);
			for(int i = 0; i < textLines.size(); i++)
				fontRendererObj.drawString(textLines.get(i), textPanel.getAX(), textPanel.getAY() + i * 11, textColor);
			fontRendererObj.setUnicodeFlag(uni);
		}
		
		if(!categoryButtons.isEmpty())
		{
			for(int i = 0; i < categoryButtons.size(); i++)
				categoryButtons.get(i).renderWidget();
		}
	}
	
	public class ButtonCategory extends ButtonLM
	{
		public final GuideCategory cat;
		
		public ButtonCategory(GuiLM g, int x, int y, int w, int h, GuideCategory c)
		{
			super(g, x, y, w, h);
			cat = c;
		}
		
		public void onButtonPressed(int b)
		{
			gui.playClickSound();
			gui.mc.displayGuiScreen(new GuiGuide(GuiGuide.this, cat));
		}
		
		public boolean isEnabled()
		{ return true; }
		
		public void renderWidget()
		{
			if(!isEnabled()) return;
			int ax = getAX();
			int ay = getAY();
			gui.getFontRenderer().drawString(cat.getTitle(), ax + 1, ay + 1, mouseOver(ax, ay) ? textColorOver : textColor);
		}
	}
}