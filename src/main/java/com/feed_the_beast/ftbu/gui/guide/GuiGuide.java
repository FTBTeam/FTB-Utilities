package com.feed_the_beast.ftbu.gui.guide;

import com.feed_the_beast.ftbl.lib.client.ClientUtils;
import com.feed_the_beast.ftbl.lib.gui.Button;
import com.feed_the_beast.ftbl.lib.gui.GuiBase;
import com.feed_the_beast.ftbl.lib.gui.GuiHelper;
import com.feed_the_beast.ftbl.lib.gui.GuiLang;
import com.feed_the_beast.ftbl.lib.gui.Panel;
import com.feed_the_beast.ftbl.lib.gui.PanelScrollBar;
import com.feed_the_beast.ftbl.lib.gui.Theme;
import com.feed_the_beast.ftbl.lib.gui.Widget;
import com.feed_the_beast.ftbl.lib.gui.WidgetLayout;
import com.feed_the_beast.ftbl.lib.icon.Color4I;
import com.feed_the_beast.ftbl.lib.icon.ColoredIcon;
import com.feed_the_beast.ftbl.lib.icon.Icon;
import com.feed_the_beast.ftbl.lib.icon.PartIcon;
import com.feed_the_beast.ftbl.lib.util.misc.MouseButton;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.api.guide.IGuideGui;
import com.feed_the_beast.ftbu.api.guide.IGuidePage;
import com.feed_the_beast.ftbu.api.guide.IGuideTextLine;
import com.feed_the_beast.ftbu.api.guide.SpecialGuideButton;
import com.feed_the_beast.ftbu.client.GuideConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GuiGuide extends GuiBase implements IGuideGui
{
	private static final Icon TEX_BACK = Icon.getIcon(FTBUFinals.MOD_ID + ":textures/gui/guide/button_back.png").withUVfromCoords(0, 0, 14, 11, 16, 16);
	private static final Icon TEX_CLOSE = Icon.getIcon(FTBUFinals.MOD_ID + ":textures/gui/guide/button_close.png").withUVfromCoords(0, 0, 14, 11, 16, 16);

	private static final Icon FILLING = new Icon()
	{
		@Override
		public void draw(int x, int y, int w, int h, Color4I col)
		{
			GuideConfig.colors.getBackground().draw(x + 4, y + 4, w - 8, h - 8);
		}
	};

	private static final Theme GUIDE_THEME = new Theme()
	{
		private final Icon GUI = new PartIcon(Icon.getIcon(FTBUFinals.MOD_ID + ":textures/gui/guide/frame.png"), 32, 12, 1);
		private final Icon TEXTURE_SCROLL = Icon.getIcon(FTBUFinals.MOD_ID + ":textures/gui/guide/scroll.png");
		private final Icon TEX_SCROLL_V = TEXTURE_SCROLL.withUVfromCoords(0, 14, 12, 18, 32, 32);
		private final Icon TEX_SCROLL_H = TEXTURE_SCROLL.withUVfromCoords(14, 0, 18, 12, 32, 32);

		@Override
		public Color4I getContentColor(boolean dark)
		{
			return GuideConfig.colors.getText();
		}

		@Override
		public Icon getGui(boolean mouseOver)
		{
			return GUI;
		}

		@Override
		public Icon getWidget(boolean mouseOver)
		{
			return Icon.EMPTY;
		}

		@Override
		public Icon getSlot(boolean mouseOver)
		{
			return Icon.EMPTY;
		}

		@Override
		public Icon getScrollBarBackground()
		{
			return Icon.EMPTY;
		}

		@Override
		public Icon getScrollBar(boolean grabbed, boolean vertical)
		{
			return vertical ? TEX_SCROLL_V : TEX_SCROLL_H;
		}
	};

	private static class ButtonSpecial extends Button
	{
		private final SpecialGuideButton specialInfoButton;

		public ButtonSpecial(GuiBase gui, SpecialGuideButton b)
		{
			super(gui, 0, 0, 16, 16);
			specialInfoButton = b;
			setTitle(specialInfoButton.title.getFormattedText());
		}

		@Override
		public void onClicked(MouseButton button)
		{
			if (GuiHelper.onClickEvent(specialInfoButton.clickEvent))
			{
				GuiHelper.playClickSound();
			}
		}

		@Override
		public void renderWidget()
		{
			specialInfoButton.icon.draw(this);
		}
	}

	public final IGuidePage pageTree;
	public final Panel panelPages, panelText, panelTitle;
	public final PanelScrollBar sliderPages, sliderTextV;
	private final Button buttonBack;
	public int panelWidth;
	private final List<ButtonSpecial> specialButtons;

	private IGuidePage selectedPage;

	public GuiGuide(IGuidePage tree)
	{
		super(0, 0);
		selectedPage = pageTree = tree;

		buttonBack = new Button(this, 12, 12, 14, 11)
		{
			@Override
			public void onClicked(MouseButton button)
			{
				GuiHelper.playClickSound();
				sliderPages.setValue(0D);
				sliderTextV.setValue(0D);
				setSelectedPage(selectedPage.getParent());
			}

			@Override
			public String getTitle()
			{
				return (selectedPage.getParent() == null) ? GuiLang.CLOSE.translate() : GuiLang.BACK.translate();
			}
		};

		buttonBack.setIcon(new ColoredIcon(TEX_CLOSE, GuideConfig.colors.getText(), 0));

		panelPages = new Panel(this, 0, 0, 0, 0)
		{
			@Override
			public void addWidgets()
			{
				for (IGuidePage c : selectedPage.getChildren().values())
				{
					add(c.createWidget(GuiGuide.this));
				}

				panelTitle.refreshWidgets();
			}

			@Override
			public void updateWidgetPositions()
			{
				if (!widgets.isEmpty())
				{
					sliderPages.setElementSize(align(WidgetLayout.VERTICAL));
				}
			}
		};

		panelPages.addFlags(Panel.DEFAULTS);

		panelText = new Panel(this, 0, 0, 0, 0)
		{
			private final WidgetLayout LAYOUT = new WidgetLayout.Vertical(2, 0, 4);

			@Override
			public void addWidgets()
			{
				for (Widget w : panelPages.widgets)
				{
					if (w instanceof ButtonGuidePage)
					{
						((ButtonGuidePage) w).updateTitle();
					}
				}

				gui.getFontUnicode().push();
				gui.getFontUnicode().set(true);

				for (IGuideTextLine line : selectedPage.getText())
				{
					add(line == null ? new Widget(gui, 0, 0, panelText.width, gui.getFontHeight() + 1) : line.createWidget(GuiGuide.this, panelText));
				}

				gui.getFontUnicode().pop();
			}

			@Override
			public void updateWidgetPositions()
			{
				if (!widgets.isEmpty())
				{
					int s = align(LAYOUT);
					sliderTextV.setElementSize(s);
					sliderTextV.setSrollStepFromOneElementSize((s - 6) / widgets.size());
				}
			}
		};

		panelText.addFlags(Panel.DEFAULTS | Panel.UNICODE);

		panelTitle = new Panel(this, 0, 0, 0, 0)
		{
			@Override
			public void addWidgets()
			{
				add(buttonBack);
				buttonBack.setIcon(new ColoredIcon((selectedPage.getParent() == null) ? TEX_CLOSE : TEX_BACK, gui.getTheme().getContentColor(false), 0));

				specialButtons.clear();

				for (SpecialGuideButton button : selectedPage.getSpecialButtons())
				{
					specialButtons.add(new ButtonSpecial(gui, button));
				}

				addAll(specialButtons);
			}
		};

		sliderPages = new PanelScrollBar(this, 0, 0, 12, 0, 18, panelPages);
		sliderTextV = new PanelScrollBar(this, 0, 0, 12, 0, 18, panelText);
		specialButtons = new ArrayList<>();
	}

	@Override
	public IGuidePage getSelectedPage()
	{
		return selectedPage;
	}

	@Override
	public void setSelectedPage(@Nullable IGuidePage p)
	{
		sliderTextV.setValue(0D);

		if (selectedPage != p)
		{
			if (p == null)
			{
				ClientUtils.MC.player.closeScreen();
				return;
			}
			else
			{
				selectedPage = p;

				if (p.getChildren().isEmpty())
				{
					panelText.refreshWidgets();
				}
				else
				{
					sliderPages.setValue(0D);
					refreshWidgets();
				}
			}
		}

		panelTitle.refreshWidgets();
	}

	@Override
	public void addWidgets()
	{
		add(sliderTextV);
		add(panelPages);
		add(panelText);
		add(panelTitle);
		add(sliderPages);

		panelPages.setWidth(panelWidth - (sliderPages.isEnabled() ? 32 : 17));

		for (int i = 0; i < specialButtons.size(); i++)
		{
			ButtonSpecial b = specialButtons.get(i);
			b.posX = panelWidth - 24 - 20 * i;
			b.posY = 10;
		}

		if (!Guides.pageToOpen.isEmpty())
		{
			setSelectedPage(pageTree.getSub(Guides.pageToOpen));
			Guides.pageToOpen = "";
		}
	}

	@Override
	public void onInit()
	{
		posX = GuideConfig.border.width;
		posY = GuideConfig.border.height;
		setWidth(getScreen().getScaledWidth() - posX * 2);
		setHeight(getScreen().getScaledHeight() - posY * 2);

		panelWidth = (int) (width * 0.3D);

		panelTitle.width = panelWidth;
		panelTitle.height = 46;

		panelPages.posX = 10;
		panelPages.posY = 43;
		panelPages.setWidth(panelWidth - 17);
		panelPages.setHeight(height - 49);

		panelText.posX = panelWidth + 10;
		panelText.posY = 6;
		panelText.setWidth(width - panelWidth - 23 - sliderTextV.width);
		panelText.setHeight(height - 12);

		sliderPages.posX = panelWidth - sliderPages.width - 10;
		sliderPages.posY = 46;
		sliderPages.setHeight(height - 56);

		sliderTextV.posY = 10;
		sliderTextV.setHeight(height - 20);
		sliderTextV.posX = width - 10 - sliderTextV.width;
	}

	@Override
	public void drawBackground()
	{
		FILLING.draw(posX + panelWidth, posY, width - panelWidth, height);
		FILLING.draw(posX, posY + 36, panelWidth, height - 36);
		FILLING.draw(posX, posY, panelWidth, 36);

		GuiHelper.pushScissor(getScreen(), posX, posY, panelWidth, 36);
		drawString(selectedPage.getDisplayName().getFormattedText(), buttonBack.getAX() + buttonBack.width + 5, posY + 14);
		GuiHelper.popScissor();
		GlStateManager.color(1F, 1F, 1F, 1F);
	}

	@Override
	public void drawForeground()
	{
		GlStateManager.color(1F, 1F, 1F, 1F);
		getTheme().getGui(false).draw(posX + panelWidth, posY + 1, width - panelWidth - 1, height - 2);
		getTheme().getGui(false).draw(posX + 1, posY + 36, panelWidth - 2, height - 37);
		getTheme().getGui(false).draw(posX + 1, posY + 1, panelWidth - 2, 34);
		super.drawForeground();
	}

	@Override
	public Theme getTheme()
	{
		return GUIDE_THEME;
	}

	@Override
	public boolean drawDefaultBackground()
	{
		return false;
	}

	@Override
	public boolean changePage(String value)
	{
		IGuidePage page = pageTree.getSubRaw(value);

		if (page != null)
		{
			if (page.getParent() != null)
			{
				setSelectedPage(page.getParent());
			}

			setSelectedPage(page);
		}

		return false;
	}

	@Override
	@Nullable
	public GuiScreen getPrevScreen()
	{
		return null;
	}

	@Override
	public Icon getIcon()
	{
		return Icon.EMPTY;
	}
}