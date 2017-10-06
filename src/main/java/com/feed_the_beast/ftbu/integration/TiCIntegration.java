package com.feed_the_beast.ftbu.integration;

import com.feed_the_beast.ftbl.api.EventHandler;
import com.feed_the_beast.ftbl.lib.icon.IconAnimation;
import com.feed_the_beast.ftbl.lib.icon.ItemIcon;
import com.feed_the_beast.ftbl.lib.util.JsonUtils;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.feed_the_beast.ftbl.lib.util.misc.Color4I;
import com.feed_the_beast.ftbu.api.guide.ClientGuideEvent;
import com.feed_the_beast.ftbu.api.guide.IGuidePage;
import com.feed_the_beast.ftbu.api.guide.IGuideTitlePage;
import com.feed_the_beast.ftbu.gui.guide.GuideContentsLine;
import com.feed_the_beast.ftbu.gui.guide.GuideExtendedTextLine;
import com.feed_the_beast.ftbu.gui.guide.GuideHrLine;
import com.feed_the_beast.ftbu.gui.guide.GuidePage;
import com.feed_the_beast.ftbu.gui.guide.IconAnimationLine;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.resources.IResource;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.IMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.tools.TinkerMaterials;

import javax.annotation.Nullable;
import java.io.InputStreamReader;
import java.util.Collections;

/**
 * @author LatvianModder
 */
@EventHandler(value = Side.CLIENT, requiredMods = "tconstruct")
public class TiCIntegration
{
	@SubscribeEvent
	public static void onGuideEvent(ClientGuideEvent event)
	{
		IGuideTitlePage page = event.getModGuide("tconstruct");
		page.setIcon(new ItemIcon("tconstruct:toolforge"));
		page.println(new GuideHrLine(1, Color4I.NONE));
		page.println(new GuideContentsLine(page));

		IGuidePage pageIntro = loadPage(event, "intro", page);

		if (pageIntro != null)
		{
			pageIntro.setTitle(new TextComponentString("Introduction")); //LANG
			pageIntro.setIcon(new ItemIcon("tconstruct:tooltables"));
			page.addSub(pageIntro);
		}

		IGuidePage toolMaterials = page.getSub("materials");
		toolMaterials.setTitle(new TextComponentString("Materials")); //LANG
		toolMaterials.setIcon(new ItemIcon(new ItemStack(Items.IRON_PICKAXE)));

		ImmutableList mats = ImmutableList.of(TinkerMaterials.wood, TinkerMaterials.cobalt, TinkerMaterials.ardite, TinkerMaterials.manyullyn);

		for (Material material : TinkerRegistry.getAllMaterials())
		{
			if (material.isHidden() || !material.hasItems())
			{
				continue;
			}

			IGuidePage page1 = toolMaterials.getSub(material.getIdentifier());
			page1.setIcon(new ItemIcon(material.getRepresentativeItem()));
			page1.setTitle(new TextComponentString(material.getLocalizedName()));

			for (IMaterialStats stats : material.getAllStats())
			{
				ITextComponent component = new TextComponentString(stats.getLocalizedName());
				component.getStyle().setUnderlined(true);
				page1.println(component);

				//List<ITrait> traits = material.getAllTraitsForStats(stats.getIdentifier());
				//allTraits.addAll(traits);
				IconAnimation parts = new IconAnimation(Collections.emptyList());

				for (IToolPart part : TinkerRegistry.getToolParts())
				{
					if (part.hasUseForStat(stats.getIdentifier()))
					{
						parts.list.add(new ItemIcon(part.getItemstackWithMaterial(material)));
					}
				}

				if (parts.list.size() > 0)
				{
					page1.println(new IconAnimationLine(parts, 8));
				}

				for (int i = 0; i < stats.getLocalizedInfo().size(); i++)
				{
					ITextComponent component1 = new TextComponentString(transformString(stats.getLocalizedInfo().get(i)));
					component1.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(transformString(stats.getLocalizedDesc().get(i)))));
					page1.println(new GuideExtendedTextLine(component1));
				}

				page1.println(null);
			}
		}

		IGuidePage modifiers = page.getSub("modifiers");
		modifiers.setTitle(new TextComponentString("Modifiers")); //LANG
		modifiers.setIcon(new ItemIcon(new ItemStack(Items.REDSTONE)));

		for (IModifier modifier : TinkerRegistry.getAllModifiers())
		{
			if (modifier.isHidden() || !modifier.hasItemsToApplyWith())
			{
				continue;
			}

			try
			{
				IResource resource = event.getResourceManager().getResource(new ResourceLocation("tconstruct", "book/en_US/modifiers/" + modifier.getIdentifier() + ".json"));
				JsonElement json = JsonUtils.fromJson(new InputStreamReader(resource.getInputStream()));

				if (json.isJsonObject())
				{
					JsonObject o = json.getAsJsonObject();
					IGuidePage page1 = modifiers.getSub(modifier.getIdentifier());
					page1.setTitle(new TextComponentString(modifier.getLocalizedName()));
					page1.println(transformString(modifier.getLocalizedDesc()));
					IconAnimation displayItems = new IconAnimation(Collections.emptyList());

					if (o.has("text"))
					{
						page1.println(null);
						for (JsonElement e : o.get("text").getAsJsonArray())
						{
							page1.println(JsonUtils.deserializeTextComponent(e));
						}
					}

					if (o.has("effects"))
					{
						page1.println(null);
						page1.println("Effects:");
						for (JsonElement e : o.get("effects").getAsJsonArray())
						{
							page1.println(JsonUtils.deserializeTextComponent(e));
						}
					}

					if (o.has("demoTool"))
					{
						for (JsonElement e : o.get("demoTool").getAsJsonArray())
						{
							Item item = Item.getByNameOrId(e.getAsString());

							if (item instanceof ToolCore)
							{
								displayItems.list.add(new ItemIcon(((ToolCore) item).buildItemForRendering(mats.subList(0, ((ToolCore) item).getRequiredComponents().size()))));
							}
						}

						if (!displayItems.list.isEmpty())
						{
							page1.println(null);
							page1.println(new IconAnimationLine(displayItems, 8));
						}
					}
				}
			}
			catch (Exception ex)
			{
			}
		}

		modifiers.sort(false);

		IGuidePage pageSmeltry = loadPage(event, "smeltery", page);

		if (pageSmeltry != null)
		{
			pageSmeltry.setTitle(new TextComponentString("Smeltry")); //LANG
			pageSmeltry.setIcon(new ItemIcon("tconstruct:toolstation"));
			page.addSub(pageSmeltry);
		}

        /*
		GuidePage searedFurnace = pagePage.getSub("seared_furnace");
        searedFurnace.println("Seared Furnace");

        GuidePage tinkerTank = pagePage.getSub("tinker_tank");
        tinkerTank.println("Tinker Tank");
        */
	}

	private static String transformString(String s)
	{
		return StringUtils.trimAllWhitespace(s.replace("\\n", "\n"));
	}

	@Nullable
	private static IGuidePage loadPage(ClientGuideEvent event, String id, IGuidePage p)
	{
		try
		{
			//FIXME
			IResource resource = event.getResourceManager().getResource(new ResourceLocation("tconstruct", "book/en_US/sections/" + id + ".json"));
			JsonElement json = JsonUtils.fromJson(new InputStreamReader(resource.getInputStream()));

			if (json.isJsonArray())
			{
				GuidePage page = new GuidePage(id, p);

				for (JsonElement e : json.getAsJsonArray())
				{
				}

				return page;
			}
		}
		catch (Exception ex)
		{
		}

		return null;
	}
}