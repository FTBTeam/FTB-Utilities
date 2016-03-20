package ftb.utils.client.gui;

import ftb.lib.TextureCoords;
import ftb.lib.api.ForgeWorldSP;
import ftb.lib.api.client.FTBLibClient;
import ftb.lib.api.gui.GuiLM;
import ftb.lib.api.gui.widgets.*;
import ftb.utils.api.guide.*;
import ftb.utils.client.FTBUClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.util.*;

public class GuiGuide extends GuiLM
{
	public static final ResourceLocation tex = new ResourceLocation("ftbu", "textures/gui/guide.png");
	public static final TextureCoords tex_slider = new TextureCoords(tex, 0, 30, 12, 18, 64, 64);
	public static final TextureCoords tex_back = new TextureCoords(tex, 13, 30, 14, 11, 64, 64);
	public static final TextureCoords tex_close = new TextureCoords(tex, 13, 41, 14, 11, 64, 64);
	public static final TextureCoords tex_back_on = new TextureCoords(tex, 27, 30, 14, 11, 64, 64);
	public static final TextureCoords tex_close_on = new TextureCoords(tex, 27, 41, 14, 11, 64, 64);
	public static final TextureCoords tex_bullet = new TextureCoords(tex, 0, 49, 6, 6, 64, 64);
	
	public static final TextureCoords tex_bg_M = new TextureCoords(tex, 30, 0, 16, 16, 64, 64);
	public static final TextureCoords tex_bg_MU = new TextureCoords(tex, 14, 0, 1, 13, 64, 64);
	public static final TextureCoords tex_bg_MD = new TextureCoords(tex, 14, 16, 1, 13, 64, 64);
	public static final TextureCoords tex_bg_ML = new TextureCoords(tex, 0, 14, 13, 1, 64, 64);
	public static final TextureCoords tex_bg_MR = new TextureCoords(tex, 16, 14, 13, 1, 64, 64);
	
	public static final TextureCoords tex_bg_NN = new TextureCoords(tex, 0, 0, 13, 13, 64, 64);
	public static final TextureCoords tex_bg_PN = new TextureCoords(tex, 16, 0, 13, 13, 64, 64);
	public static final TextureCoords tex_bg_NP = new TextureCoords(tex, 0, 16, 13, 13, 64, 64);
	public static final TextureCoords tex_bg_PP = new TextureCoords(tex, 16, 16, 13, 13, 64, 64);
	
	public static final int textColor = 0xFF7B6534;
	public static final int textColorOver = 0xFF9D6A00;
	public int panelWidth;
	
	public final GuiGuide parentGui;
	public final GuidePage category;
	public final String categoryTitle;
	public GuidePage selectedCategory;
	
	public final SliderLM sliderCategories, sliderText;
	public final ButtonLM buttonBack;
	
	public final PanelLM panelCategories, panelText;
	
	public static GuiGuide clientGuideGui = null;
	
	public static GuiGuide openClientGui(boolean open)
	{
		if(clientGuideGui == null) clientGuideGui = new GuiGuide(null, GuideRepoList.clientGuideFile);
		if(open) FTBLibClient.openGui(clientGuideGui);
		return clientGuideGui;
	}
	
	public GuiGuide(GuiGuide g, GuidePage c)
	{
		super(null, null);
		parentGui = g;
		category = c;
		categoryTitle = category.getTitleComponent().getFormattedText();
		selectedCategory = category;
		
		sliderCategories = new SliderLM(this, 0, 0, tex_slider.widthI(), 0, tex_slider.heightI())
		{
			public boolean canMouseScroll()
			{ return gui.mouse().x < panelWidth; }
			
			public boolean isEnabled()
			{ return true; }
		};
		
		sliderCategories.isVertical = true;
		
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
				
				if(selectedCategory == category || category.getFormattedText().isEmpty())
				{
					if(parentGui == null) mc.thePlayer.closeScreen();
					else
					{
						parentGui.selectedCategory = parentGui.category;
						parentGui.sliderText.value = 0F;
						FTBLibClient.openGui(parentGui);
					}
				}
				else
				{
					selectedCategory = category;
					sliderText.value = 0F;
					initLMGui();
				}
			}
		};
		
		panelCategories = new PanelLM(this, 0, 0, 0, 0)
		{
			public void addWidgets()
			{
				height = 0;
				
				List<GuidePage> categoryList = new ArrayList<>();
				categoryList.addAll(category.childPages.values());
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
				fontRendererObj.setUnicodeFlag(FTBUClient.guide_unicode.get());
				
				for(GuideTextLine line : selectedCategory.text)
				{
					GuideWidget l = new GuideWidget(GuiGuide.this, line);
					height += l.height;
					add(l);
				}
				
				fontRendererObj.setUnicodeFlag(uni);
			}
		};
	}
	
	public void addWidgets()
	{
		mainPanel.add(sliderCategories);
		mainPanel.add(sliderText);
		mainPanel.add(buttonBack);
		mainPanel.add(panelCategories);
		mainPanel.add(panelText);
	}
	
	public void initLMGui()
	{
		mainPanel.width = width;
		mainPanel.height = height;
		panelWidth = mainPanel.width / 7 * 2;
		
		panelCategories.posX = 10;
		panelCategories.posY = 46;
		panelCategories.width = panelWidth - 20;
		panelCategories.height = mainPanel.height - 56;
		
		panelText.posX = panelWidth + 10;
		panelText.posY = 10;
		panelText.width = width - panelWidth - 20;
		panelText.height = mainPanel.height - 20;
		
		sliderCategories.posX = panelWidth - sliderCategories.width - 10;
		sliderCategories.posY = 46;
		sliderCategories.height = mainPanel.height - 56;
		
		sliderText.posY = 10;
		sliderText.height = mainPanel.height - 20;
		sliderText.posX = mainPanel.width - sliderText.width - 10;
		
		buttonBack.posX = 12;
		buttonBack.posY = 12;
		
		//
		
		if(category.getParentTop() == GuideRepoList.clientGuideFile)
		{
			GuideRepoList.reloadFromFolder(ForgeWorldSP.inst.getMode());
			clientGuideGui = this;
		}
	}
	
	public void drawTexturedModalRectD(double x, double y, double u, double v, double w, double h)
	{ drawTexturedModalRectD(x, y, zLevel, u, v, w, h, 64, 64); }
	
	public void drawBackground()
	{
		if(sliderCategories.update())
		{
			if(sliderCategories.value == 0F || panelCategories.height - (mainPanel.height - 56F) <= 0F)
			{
				panelCategories.posY = 46;
				sliderCategories.value = 0F;
			}
			else
			{
				panelCategories.posY = (int) (46F - (sliderCategories.value * (panelCategories.height - (mainPanel.height - 56F))));
			}
		}
		
		if(sliderText.update())
		{
			if(sliderText.value == 0F || panelText.height - (mainPanel.height - 20F) <= 0F)
			{
				panelText.posY = 10;
				sliderText.value = 0F;
			}
			else
			{
				panelText.posY = (int) (10F - (sliderText.value * (panelText.height - (mainPanel.height - 20F))));
			}
		}
		
		super.drawBackground();
		
		FTBLibClient.setTexture(texture);
		
		GlStateManager.color(1F, 1F, 1F, 1F);
		
		renderFilling(panelWidth, 0, mainPanel.width - panelWidth, mainPanel.height);
		renderFilling(0, 36, panelWidth, mainPanel.height - 32);
		
		boolean uni = fontRendererObj.getUnicodeFlag();
		fontRendererObj.setUnicodeFlag(FTBUClient.guide_unicode.get());
		
		panelText.renderWidget();
		
		fontRendererObj.setUnicodeFlag(uni);
		
		panelCategories.renderWidget();
		
		GlStateManager.color(1F, 1F, 1F, 1F);
		
		renderBorders(panelWidth, 0, mainPanel.width - panelWidth, mainPanel.height);
		renderBorders(0, 36, panelWidth, mainPanel.height - 36);
		renderFilling(0, 0, panelWidth, 36);
		renderBorders(0, 0, panelWidth, 36);
		
		//		GlStateManager.color(0.3F, 1F, 1F, 1F);
		//		panelCategories.render(tex_bg_M);
		//		panelText.render(tex_bg_M);
		//		GlStateManager.color(1F, 1F, 1F, 1F);
		
		sliderCategories.renderSlider(tex_slider);
		sliderText.renderSlider(tex_slider);
		buttonBack.render((parentGui == null) ? (buttonBack.mouseOver() ? tex_close_on : tex_close) : (buttonBack.mouseOver() ? tex_back_on : tex_back));
		fontRendererObj.drawString(categoryTitle, buttonBack.getAX() + buttonBack.width + 5, mainPanel.posY + 14, textColor);
	}
	
	public void drawDefaultBackground()
	{
	}
	
	private void renderBorders(int px, int py, int w, int h)
	{
		render(tex_bg_NN, px, py, zLevel, 13, 13);
		render(tex_bg_NP, px, py + h - 13, zLevel, 13, 13);
		render(tex_bg_PN, px + w - 13, py, zLevel, 13, 13);
		render(tex_bg_PP, px + w - 13, py + h - 13, zLevel, 13, 13);
		
		render(tex_bg_MU, px + 13, py, zLevel, w - 24, 13);
		render(tex_bg_MR, px + w - 13, py + 13, zLevel, 13, h - 25);
		render(tex_bg_MD, px + 13, py + h - 13, zLevel, w - 24, 13);
		render(tex_bg_ML, px, py + 13, zLevel, 13, h - 25);
	}
	
	private void renderFilling(int px, int py, int w, int h)
	{
		render(tex_bg_M, px + 4, py + 4, zLevel, w - 8, h - 8);
	}
}