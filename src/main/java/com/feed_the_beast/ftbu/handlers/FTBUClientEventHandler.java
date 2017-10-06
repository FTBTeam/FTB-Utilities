package com.feed_the_beast.ftbu.handlers;

import com.feed_the_beast.ftbl.api.EventHandler;
import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.ISidebarButton;
import com.feed_the_beast.ftbl.api.ISidebarButtonGroup;
import com.feed_the_beast.ftbl.lib.client.ClientUtils;
import com.feed_the_beast.ftbl.lib.icon.Icon;
import com.feed_the_beast.ftbl.lib.internal.FTBLibFinals;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.feed_the_beast.ftbu.api.guide.ClientGuideEvent;
import com.feed_the_beast.ftbu.api.guide.GuideType;
import com.feed_the_beast.ftbu.api.guide.IGuidePage;
import com.feed_the_beast.ftbu.api.guide.RegisterGuideLineProvidersEvent;
import com.feed_the_beast.ftbu.client.FTBUClient;
import com.feed_the_beast.ftbu.gui.GuiWarps;
import com.feed_the_beast.ftbu.gui.guide.GuideContentsLine;
import com.feed_the_beast.ftbu.gui.guide.GuideExtendedTextLine;
import com.feed_the_beast.ftbu.gui.guide.GuideHrLine;
import com.feed_the_beast.ftbu.gui.guide.GuideImageLine;
import com.feed_the_beast.ftbu.gui.guide.GuideListLine;
import com.feed_the_beast.ftbu.gui.guide.GuideSwitchLine;
import com.feed_the_beast.ftbu.gui.guide.GuideTextLineString;
import com.feed_the_beast.ftbu.gui.guide.GuideTitlePage;
import com.feed_the_beast.ftbu.gui.guide.Guides;
import com.feed_the_beast.ftbu.gui.guide.IconAnimationLine;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author LatvianModder
 */
@EventHandler(Side.CLIENT)
public class FTBUClientEventHandler
{
	@SubscribeEvent
	public static void registerGuideLineProviders(RegisterGuideLineProvidersEvent event)
	{
		event.register("img", (page, json) -> new GuideImageLine(json));
		event.register("image", (page, json) -> new GuideImageLine(json));
		event.register("text_component", (page, json) -> new GuideExtendedTextLine(json));
		event.register("text", (page, json) -> new GuideTextLineString(json));
		event.register("list", GuideListLine::new);
		event.register("hr", (page, json) -> new GuideHrLine(json));
		event.register("animation", (page, json) -> new IconAnimationLine(json));
		event.register("switch", GuideSwitchLine::new);
		event.register("contents", (page, json) -> new GuideContentsLine(page));
	}

	/*
	@SubscribeEvent
    public static void onTooltip(ItemTooltipEvent e)
    {
        if(FTBUConfigGeneral.isItemBanned(item, e.itemStack.getItemDamage()))
        {
            e.toolTip.add(EnumChatFormatting.RED + "Banned item");
        }
    }
    */

	@SubscribeEvent
	public static void onKeyEvent(InputEvent.KeyInputEvent event)
	{
		if (FTBUClient.KEY_GUIDE.isPressed())
		{
			ClientUtils.MC.addScheduledTask(Guides.OPEN_GUI);
		}

		if (FTBUClient.KEY_WARP.isPressed())
		{
			GuiWarps.INSTANCE = new GuiWarps();
			GuiWarps.INSTANCE.openGui();
			ClientUtils.execClientCommand("/ftb warp gui");
		}
	}

	@SubscribeEvent
	public static void onGuideEvent(ClientGuideEvent event)
	{
		GuideTitlePage page = new GuideTitlePage("sidebar_buttons", GuideType.OTHER);
		page.getAuthors().add("LatvianModder");
		page.setIcon(Icon.getIcon(FTBLibFinals.MOD_ID + ":textures/gui/teams.png"));
		page.setTitle(new TextComponentTranslation("sidebar_button"));

		for (ISidebarButtonGroup group : FTBLibAPI.API.getSidebarButtonGroups())
		{
			for (ISidebarButton button : group.getButtons())
			{
				if (button.isVisible() && StringUtils.canTranslate("sidebar_button." + button.getName() + ".tooltip"))
				{
					IGuidePage page1 = page.getSub(button.getName());
					page1.setIcon(button.getIcon());
					page1.setTitle(new TextComponentTranslation("sidebar_button." + button.getName()));
					page1.println(new TextComponentTranslation("sidebar_button." + button.getName() + ".tooltip"));
				}
			}
		}

		event.add(page);
	}
}