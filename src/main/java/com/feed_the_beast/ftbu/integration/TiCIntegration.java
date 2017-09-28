package com.feed_the_beast.ftbu.integration;

import com.feed_the_beast.ftbl.api.EventHandler;
import com.feed_the_beast.ftbl.lib.Color4I;
import com.feed_the_beast.ftbl.lib.icon.IconAnimation;
import com.feed_the_beast.ftbl.lib.icon.ItemIcon;
import com.feed_the_beast.ftbl.lib.util.JsonUtils;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.feed_the_beast.ftbu.api.guide.ClientGuideEvent;
import com.feed_the_beast.ftbu.api.guide.GuideTitlePage;
import com.feed_the_beast.ftbu.api.guide.IGuidePage;
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
		GuideTitlePage guide = event.getModGuide("tconstruct");
		guide.page.setIcon(new ItemIcon("tconstruct:toolforge"));
		guide.page.println(new GuideHrLine(1, Color4I.NONE));
		guide.page.println(new GuideContentsLine(guide.page));

		GuidePage pageIntro = loadPage(event, "intro");

		if (pageIntro != null)
		{
			pageIntro.setTitle(new TextComponentString("Introduction")); //LANG
			pageIntro.setIcon(new ItemIcon("tconstruct:tooltables"));
			guide.page.addSub(pageIntro);
		}

		IGuidePage toolMaterials = guide.page.getSub("materials");
		toolMaterials.setTitle(new TextComponentString("Materials")); //LANG
		toolMaterials.setIcon(new ItemIcon(new ItemStack(Items.IRON_PICKAXE)));

		ImmutableList mats = ImmutableList.of(TinkerMaterials.wood, TinkerMaterials.cobalt, TinkerMaterials.ardite, TinkerMaterials.manyullyn);

		for (Material material : TinkerRegistry.getAllMaterials())
		{
			if (material.isHidden() || !material.hasItems())
			{
				continue;
			}

			IGuidePage page = toolMaterials.getSub(material.getIdentifier());
			page.setIcon(new ItemIcon(material.getRepresentativeItem()));
			page.setTitle(new TextComponentString(material.getLocalizedName()));

			for (IMaterialStats stats : material.getAllStats())
			{
				ITextComponent component = new TextComponentString(stats.getLocalizedName());
				component.getStyle().setUnderlined(true);
				page.println(component);

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
					page.println(new IconAnimationLine(parts, 8));
				}

				for (int i = 0; i < stats.getLocalizedInfo().size(); i++)
				{
					ITextComponent component1 = new TextComponentString(transformString(stats.getLocalizedInfo().get(i)));
					component1.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(transformString(stats.getLocalizedDesc().get(i)))));
					page.println(new GuideExtendedTextLine(component1));
				}

				page.println(null);
			}
		}

		IGuidePage modifiers = guide.page.getSub("modifiers");
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
					IGuidePage page = modifiers.getSub(modifier.getIdentifier());
					page.setTitle(new TextComponentString(modifier.getLocalizedName()));
					page.println(transformString(modifier.getLocalizedDesc()));
					IconAnimation displayItems = new IconAnimation(Collections.emptyList());

					if (o.has("text"))
					{
						page.println(null);
						for (JsonElement e : o.get("text").getAsJsonArray())
						{
							page.println(JsonUtils.deserializeTextComponent(e));
						}
					}

					if (o.has("effects"))
					{
						page.println(null);
						page.println("Effects:");
						for (JsonElement e : o.get("effects").getAsJsonArray())
						{
							page.println(JsonUtils.deserializeTextComponent(e));
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
							page.println(null);
							page.println(new IconAnimationLine(displayItems, 8));
						}
					}
				}
			}
			catch (Exception ex)
			{
			}
		}

		modifiers.sort(false);

		GuidePage pageSmeltry = loadPage(event, "smeltery");

		if (pageSmeltry != null)
		{
			pageSmeltry.setTitle(new TextComponentString("Smeltry")); //LANG
			pageSmeltry.setIcon(new ItemIcon("tconstruct:toolstation"));
			guide.page.addSub(pageSmeltry);
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
	private static GuidePage loadPage(ClientGuideEvent event, String id)
	{
		try
		{
			//FIXME
			IResource resource = event.getResourceManager().getResource(new ResourceLocation("tconstruct", "book/en_US/sections/" + id + ".json"));
			JsonElement json = JsonUtils.fromJson(new InputStreamReader(resource.getInputStream()));

			if (json.isJsonArray())
			{
				GuidePage page = new GuidePage(id);

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