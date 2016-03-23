package ftb.utils.mod.client.gui.guide;

import ftb.lib.TextureCoords;
import ftb.lib.api.client.*;
import ftb.lib.api.gui.GuiLM;
import ftb.lib.api.gui.widgets.*;
import ftb.utils.api.guide.*;
import ftb.utils.api.guide.lines.GuideTextLine;
import latmod.lib.LMColor;
import net.minecraft.util.ResourceLocation;

import java.util.*;

public class GuiGuide extends GuiLM
{
	public static final ResourceLocation tex = new ResourceLocation("ftbu", "textures/gui/guide.png");
	public static final TextureCoords tex_slider = new TextureCoords(tex, 0, 30, 12, 18, 64, 64);
	public static final TextureCoords tex_back = new TextureCoords(tex, 13, 30, 14, 11, 64, 64);
	public static final TextureCoords tex_close = new TextureCoords(tex, 13, 41, 14, 11, 64, 64);
	public static final TextureCoords tex_bullet = new TextureCoords(tex, 0, 49, 6, 6, 64, 64);
	
	public static final TextureCoords tex_bg_MU = new TextureCoords(tex, 14, 0, 1, 13, 64, 64);
	public static final TextureCoords tex_bg_MD = new TextureCoords(tex, 14, 16, 1, 13, 64, 64);
	public static final TextureCoords tex_bg_ML = new TextureCoords(tex, 0, 14, 13, 1, 64, 64);
	public static final TextureCoords tex_bg_MR = new TextureCoords(tex, 16, 14, 13, 1, 64, 64);
	
	public static final TextureCoords tex_bg_NN = new TextureCoords(tex, 0, 0, 13, 13, 64, 64);
	public static final TextureCoords tex_bg_PN = new TextureCoords(tex, 16, 0, 13, 13, 64, 64);
	public static final TextureCoords tex_bg_NP = new TextureCoords(tex, 0, 16, 13, 13, 64, 64);
	public static final TextureCoords tex_bg_PP = new TextureCoords(tex, 16, 16, 13, 13, 64, 64);
	
	public int panelWidth;
	
	public final GuiGuide parentGui;
	public final GuidePage page;
	public final String pageTitle;
	public GuidePage selectedPage;
	
	public final SliderLM sliderPages, sliderText;
	public final ButtonLM buttonBack;
	
	public final PanelLM panelPages, panelText;
	public int colorText, colorBackground;
	
	public static GuiGuide clientGuideGui = null;
	
	public static GuiGuide openClientGui(boolean open)
	{
		if(clientGuideGui == null) clientGuideGui = new GuiGuide(null, ClientGuideFile.instance);
		if(open) FTBLibClient.openGui(clientGuideGui);
		return clientGuideGui;
	}
	
	public GuiGuide(GuiGuide g, GuidePage c)
	{
		super(null, null);
		parentGui = g;
		page = c;
		pageTitle = page.getTitleComponent().getFormattedText();
		selectedPage = page;
		
		sliderPages = new SliderLM(this, 0, 0, tex_slider.widthI(), 0, tex_slider.heightI())
		{
			public boolean canMouseScroll()
			{ return gui.mouse().x < panelWidth; }
			
			public boolean isEnabled()
			{ return true; }
		};
		
		sliderPages.isVertical = true;
		
		sliderText = new SliderLM(this, 0, 0, tex_slider.widthI(), 0, tex_slider.heightI())
		{
			public boolean canMouseScroll()
			{ return gui.mouse().x > panelWidth; }
			
			public boolean isEnabled()
			{ return true; }
		};
		
		sliderText.isVertical = true;
		
		buttonBack = new ButtonLM(this, 0, 0, tex_back.widthI(), tex_back.heightI())
		{
			public void onButtonPressed(int b)
			{
				FTBLibClient.playClickSound();
				
				if(selectedPage == page || page.getFormattedText().isEmpty())
				{
					if(parentGui == null) mc.thePlayer.closeScreen();
					else
					{
						parentGui.selectedPage = parentGui.page;
						parentGui.sliderText.value = 0F;
						parentGui.panelText.posY = 10;
						FTBLibClient.openGui(parentGui);
					}
				}
				else
				{
					selectedPage = page;
					sliderText.value = 0F;
					panelText.posY = 10;
					initLMGui();
					refreshWidgets();
				}
			}
		};
		
		panelPages = new PanelLM(this, 0, 0, 0, 0)
		{
			public void addWidgets()
			{
				height = 0;
				
				List<GuidePage> categoryList = new ArrayList<>();
				categoryList.addAll(page.childPages.values());
				Collections.sort(categoryList);
				
				for(GuidePage c : categoryList)
				{
					GuideButtonCategory b = new GuideButtonCategory(GuiGuide.this, c);
					add(b);
					height += b.height;
				}
			}
		};
		
		panelText = new PanelLM(this, 0, 0, 0, 0)
		{
			public void addWidgets()
			{
				height = 0;
				
				boolean uni = fontRendererObj.getUnicodeFlag();
				fontRendererObj.setUnicodeFlag(GuideClientSettings.unicode.get());
				
				for(GuideTextLine line : selectedPage.text)
				{
					ButtonGuideTextLine l = line == null ? new ButtonGuideTextLine(GuiGuide.this, null) : line.createWidget(GuiGuide.this);
					
					if(l != null && l.height > 0)
					{
						height += l.height;
						add(l);
					}
				}
				
				fontRendererObj.setUnicodeFlag(uni);
				sliderText.scrollStep = 30F / (float) height;
			}
		};
	}
	
	public void addWidgets()
	{
		mainPanel.add(sliderPages);
		mainPanel.add(sliderText);
		mainPanel.add(buttonBack);
		mainPanel.add(panelPages);
		mainPanel.add(panelText);
	}
	
	public void initLMGui()
	{
		mainPanel.width = width;
		mainPanel.height = height;
		panelWidth = mainPanel.width / 7 * 2;
		
		panelPages.posX = 10;
		panelPages.posY = 46;
		panelPages.width = panelWidth - 20;
		panelPages.height = mainPanel.height - 56;
		
		panelText.posX = panelWidth + 10;
		panelText.posY = 10;
		panelText.width = width - panelWidth - 20 - sliderText.width;
		panelText.height = mainPanel.height - 20;
		
		sliderPages.posX = panelWidth - sliderPages.width - 10;
		sliderPages.posY = 46;
		sliderPages.height = mainPanel.height - 56;
		
		sliderText.posY = 10;
		sliderText.height = mainPanel.height - 20;
		sliderText.posX = panelText.getAX() + panelText.width + 1;
		
		buttonBack.posX = 12;
		buttonBack.posY = 12;
		
		LMColor c = page.getTextColor();
		if(c == null) c = GuideClientSettings.text_color.value;
		colorText = 0xFF000000 | c.color();
		
		c = page.getBackgroundColor();
		if(c == null) c = GuideClientSettings.bg_color.value;
		colorBackground = 0xFF000000 | c.color();
		
		//
		
		if(page.getParentTop() == ClientGuideFile.instance)
		{
			clientGuideGui = this;
		}
	}
	
	public void drawTexturedModalRectD(double x, double y, double u, double v, double w, double h)
	{ drawTexturedModalRectD(x, y, zLevel, u, v, w, h, 64, 64); }
	
	public void drawBackground()
	{
		sliderPages.update();
		
		if(sliderPages.value == 0F || panelPages.height - (mainPanel.height - 56F) <= 0F)
		{
			panelPages.posY = 46;
			sliderPages.value = 0F;
		}
		else
		{
			panelPages.posY = (int) (46F - (sliderPages.value * (panelPages.height - (mainPanel.height - 56F))));
		}
		
		sliderText.update();
		
		if(sliderText.value == 0F || panelText.height - (mainPanel.height - 20F) <= 0F)
		{
			panelText.posY = 10;
			sliderText.value = 0F;
		}
		else
		{
			panelText.posY = (int) (10F - (sliderText.value * (panelText.height - (mainPanel.height - 20F))));
		}
		
		super.drawBackground();
		
		FTBLibClient.setTexture(texture);
		
		GlStateManager.color(1F, 1F, 1F, 1F);
		
		renderFilling(panelWidth, 0, mainPanel.width - panelWidth, mainPanel.height, GuideClientSettings.transparency.getAsInt());
		renderFilling(0, 36, panelWidth, mainPanel.height - 32, 255);
		
		boolean uni = fontRendererObj.getUnicodeFlag();
		fontRendererObj.setUnicodeFlag(GuideClientSettings.unicode.get());
		
		panelText.renderWidget();
		
		fontRendererObj.setUnicodeFlag(uni);
		
		panelPages.renderWidget();
		
		GlStateManager.color(1F, 1F, 1F, 1F);
		
		renderBorders(panelWidth, 0, mainPanel.width - panelWidth, mainPanel.height);
		renderBorders(0, 36, panelWidth, mainPanel.height - 36);
		renderFilling(0, 0, panelWidth, 36, 255);
		renderBorders(0, 0, panelWidth, 36);
		
		sliderPages.renderSlider(tex_slider);
		sliderText.renderSlider(tex_slider);
		FTBLibClient.setGLColor(colorText, 255);
		buttonBack.render((parentGui == null) ? tex_close : tex_back);
		
		GlStateManager.color(1F, 1F, 1F, 1F);
		fontRendererObj.drawString(pageTitle, buttonBack.getAX() + buttonBack.width + 5, mainPanel.posY + 14, colorText);
	}
	
	public void drawDefaultBackground()
	{
	}
	
	private void renderBorders(int px, int py, int w, int h)
	{
		GlStateManager.color(1F, 1F, 1F, 1F);
		render(tex_bg_NN, px, py, zLevel, 13, 13);
		render(tex_bg_NP, px, py + h - 13, zLevel, 13, 13);
		render(tex_bg_PN, px + w - 13, py, zLevel, 13, 13);
		render(tex_bg_PP, px + w - 13, py + h - 13, zLevel, 13, 13);
		
		render(tex_bg_MU, px + 13, py, zLevel, w - 24, 13);
		render(tex_bg_MR, px + w - 13, py + 13, zLevel, 13, h - 25);
		render(tex_bg_MD, px + 13, py + h - 13, zLevel, w - 24, 13);
		render(tex_bg_ML, px, py + 13, zLevel, 13, h - 25);
	}
	
	private void renderFilling(int px, int py, int w, int h, int a)
	{
		FTBLibClient.setGLColor(colorBackground, a);
		drawBlankRect(px + 4, py + 4, zLevel, w - 8, h - 8);
	}
}