package com.feed_the_beast.ftbu.gui;

import com.feed_the_beast.ftbl.api.EnumTeamColor;
import com.feed_the_beast.ftbl.api.gui.IMouseButton;
import com.feed_the_beast.ftbl.lib.MouseButton;
import com.feed_the_beast.ftbl.lib.client.CachedVertexData;
import com.feed_the_beast.ftbl.lib.client.ClientUtils;
import com.feed_the_beast.ftbl.lib.gui.Button;
import com.feed_the_beast.ftbl.lib.gui.GuiBase;
import com.feed_the_beast.ftbl.lib.gui.GuiHelper;
import com.feed_the_beast.ftbl.lib.gui.GuiIcons;
import com.feed_the_beast.ftbl.lib.gui.GuiLang;
import com.feed_the_beast.ftbl.lib.gui.Panel;
import com.feed_the_beast.ftbl.lib.gui.misc.GuiChunkSelectorBase;
import com.feed_the_beast.ftbl.lib.gui.misc.GuiConfigs;
import com.feed_the_beast.ftbl.lib.gui.misc.ThreadReloadChunkSelector;
import com.feed_the_beast.ftbu.FTBUCommon;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.api.chunks.IChunkUpgrade;
import com.feed_the_beast.ftbu.api_impl.ChunkUpgrade;
import com.feed_the_beast.ftbu.client.FTBUClient;
import com.feed_the_beast.ftbu.net.MessageClaimedChunksModify;
import com.feed_the_beast.ftbu.net.MessageClaimedChunksRequest;
import com.feed_the_beast.ftbu.net.MessageClaimedChunksUpdate;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GuiClaimedChunks extends GuiChunkSelectorBase
{
	public static GuiClaimedChunks instance;
	private static final Map<UUID, ClaimedChunks.Team> TEAMS = new HashMap<>();
	private static final ClaimedChunks.Data[] chunkData = new ClaimedChunks.Data[GuiConfigs.CHUNK_SELECTOR_TILES_GUI * GuiConfigs.CHUNK_SELECTOR_TILES_GUI];
	private static int claimedChunks, loadedChunks, maxClaimedChunks, maxLoadedChunks;
	private static final ClaimedChunks.Data NULL_CHUNK_DATA = new ClaimedChunks.Data();

	private static final CachedVertexData AREA = new CachedVertexData(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

	static
	{
		NULL_CHUNK_DATA.owner = "";

		for (int i = 0; i < chunkData.length; i++)
		{
			chunkData[i] = new ClaimedChunks.Data();
		}
	}

	private static ClaimedChunks.Data getAt(int x, int y)
	{
		int i = x + y * GuiConfigs.CHUNK_SELECTOR_TILES_GUI;
		return i < 0 || i >= chunkData.length ? NULL_CHUNK_DATA : chunkData[i];
	}

	private static boolean hasBorder(ClaimedChunks.Data data, ClaimedChunks.Data with)
	{
		return (data.flags != with.flags || data.team != with.team) && !with.hasUpgrade(ChunkUpgrade.LOADED);
	}

	public static void setData(MessageClaimedChunksUpdate m)
	{
		claimedChunks = m.claimedChunks;
		loadedChunks = m.loadedChunks;
		maxClaimedChunks = m.maxClaimedChunks;
		maxLoadedChunks = m.maxLoadedChunks;
		System.arraycopy(m.chunkData, 0, chunkData, 0, chunkData.length);
		TEAMS.putAll(m.teams);

		if (FTBUClient.JM_INTEGRATION != null)
		{
			for (int z = 0; z < GuiConfigs.CHUNK_SELECTOR_TILES_GUI; z++)
			{
				for (int x = 0; x < GuiConfigs.CHUNK_SELECTOR_TILES_GUI; x++)
				{
					FTBUClient.JM_INTEGRATION.chunkChanged(new ChunkPos(m.startX + x, m.startZ + z), m.chunkData[x + z * GuiConfigs.CHUNK_SELECTOR_TILES_GUI]);
				}
			}
		}

		AREA.reset();
		EnumTeamColor prevCol = null;
		ClaimedChunks.Data data;

		for (int i = 0; i < chunkData.length; i++)
		{
			data = chunkData[i];

			if (!data.hasUpgrade(ChunkUpgrade.CLAIMED))
			{
				continue;
			}

			if (prevCol != data.team.color)
			{
				prevCol = data.team.color;
				AREA.color.set(data.team.color.getColor(), 150);
			}

			AREA.rect((i % GuiConfigs.CHUNK_SELECTOR_TILES_GUI) * 16, (i / GuiConfigs.CHUNK_SELECTOR_TILES_GUI) * 16, 16, 16);
		}

		boolean borderU, borderD, borderL, borderR;

		for (int i = 0; i < chunkData.length; i++)
		{
			data = chunkData[i];

			if (!data.hasUpgrade(ChunkUpgrade.CLAIMED))
			{
				continue;
			}

			int x = i % GuiConfigs.CHUNK_SELECTOR_TILES_GUI;
			int dx = x * 16;
			int y = i / GuiConfigs.CHUNK_SELECTOR_TILES_GUI;
			int dy = y * 16;

			borderU = y > 0 && hasBorder(data, getAt(x, y - 1));
			borderD = y < (GuiConfigs.CHUNK_SELECTOR_TILES_GUI - 1) && hasBorder(data, getAt(x, y + 1));
			borderL = x > 0 && hasBorder(data, getAt(x - 1, y));
			borderR = x < (GuiConfigs.CHUNK_SELECTOR_TILES_GUI - 1) && hasBorder(data, getAt(x + 1, y));

			if (data.hasUpgrade(ChunkUpgrade.LOADED))
			{
				AREA.color.set(255, 80, 80, 230);
			}
			else
			{
				AREA.color.set(80, 80, 80, 230);
			}

			if (borderU)
			{
				AREA.rect(dx, dy, 16, 1);
			}

			if (borderD)
			{
				AREA.rect(dx, dy + 15, 16, 1);
			}

			if (borderL)
			{
				AREA.rect(dx, dy, 1, 16);
			}

			if (borderR)
			{
				AREA.rect(dx + 15, dy, 1, 16);
			}
		}
	}

	private final String currentDimName;
	private final Button buttonRefresh, buttonClose, buttonUnclaimAll;

	public GuiClaimedChunks()
	{
		currentDimName = ClientUtils.MC.world.provider.getDimensionType().getName();

		buttonClose = new Button(0, 0, 16, 16, GuiLang.BUTTON_CLOSE.translate())
		{
			@Override
			public void onClicked(GuiBase gui, IMouseButton button)
			{
				GuiHelper.playClickSound();
				gui.closeGui();
			}
		};

		buttonClose.setIcon(GuiIcons.ACCEPT);

		buttonRefresh = new Button(0, 16, 16, 16, GuiLang.BUTTON_REFRESH.translate())
		{
			@Override
			public void onClicked(GuiBase gui, IMouseButton button)
			{
				new MessageClaimedChunksRequest(startX, startZ).sendToServer();
				ThreadReloadChunkSelector.reloadArea(ClientUtils.MC.world, startX, startZ);
			}
		};

		buttonRefresh.setIcon(GuiIcons.REFRESH);

		buttonUnclaimAll = new Button(0, 32, 16, 16)
		{
			@Override
			public void onClicked(GuiBase gui, IMouseButton button)
			{
				GuiHelper.playClickSound();
				String s = GuiScreen.isShiftKeyDown() ? FTBULang.BUTTON_CLAIMS_UNCLAIM_ALL_Q.translate() : FTBULang.BUTTON_CLAIMS_UNCLAIM_ALL_DIM_Q.translate(currentDimName);
				ClientUtils.MC.displayGuiScreen(new GuiYesNo((set, id) ->
				{
					if (set)
					{
						ClientUtils.execClientCommand("/ftb chunks unclaim_all " + (id == 1));
					}

					gui.openGui();
					gui.refreshWidgets();
				}, s, "", GuiScreen.isShiftKeyDown() ? 1 : 0));
			}

			@Override
			public void addMouseOverText(GuiBase gui, List<String> list)
			{
				list.add(GuiScreen.isShiftKeyDown() ? FTBULang.BUTTON_CLAIMS_UNCLAIM_ALL.translate() : FTBULang.BUTTON_CLAIMS_UNCLAIM_ALL_DIM.translate(currentDimName));
			}
		};

		buttonUnclaimAll.setIcon(GuiIcons.REMOVE);
	}

	@Override
	public void onInit()
	{
		buttonRefresh.onClicked(this, MouseButton.LEFT);
	}

	@Override
	public int getSelectionMode(IMouseButton button)
	{
		boolean claim = !GuiScreen.isShiftKeyDown();
		boolean flag = button.isLeft();

		if (flag)
		{
			return claim ? MessageClaimedChunksModify.CLAIM : MessageClaimedChunksModify.LOAD;
		}
		else
		{
			return claim ? MessageClaimedChunksModify.UNCLAIM : MessageClaimedChunksModify.UNLOAD;
		}
	}

	@Override
	public void onChunksSelected(Collection<ChunkPos> chunks)
	{
		new MessageClaimedChunksModify(startX, startZ, currentSelectionMode, chunks).sendToServer();
	}

	@Override
	public void drawArea(Tessellator tessellator, BufferBuilder buffer)
	{
		AREA.draw(tessellator, buffer);
	}

	@Override
	public void addCornerButtons(Panel panel)
	{
		panel.add(buttonClose);
		panel.add(buttonRefresh);
		panel.add(buttonUnclaimAll);
	}

	@Override
	public void addCornerText(List<String> list)
	{
		list.add(FTBULang.LABEL_CCHUNKS_COUNT.translate(claimedChunks, maxClaimedChunks));
		list.add(FTBULang.LABEL_LCHUNKS_COUNT.translate(loadedChunks, maxLoadedChunks));
	}

	@Override
	public void addButtonText(GuiChunkSelectorBase.MapButton button, List<String> list)
	{
		ClaimedChunks.Data data = chunkData[button.index];

		if (data.hasUpgrade(ChunkUpgrade.CLAIMED))
		{
			list.add(data.team.formattedName);
			list.add(TextFormatting.GREEN + ChunkUpgrade.CLAIMED.getLangKey().translate());

			if (data.team.isAlly)
			{
				list.add(data.owner);

				for (IChunkUpgrade upgrade : FTBUCommon.CHUNK_UPGRADES)
				{
					if (upgrade != null && data.hasUpgrade(upgrade))
					{
						list.add(TextFormatting.RED + upgrade.getLangKey().translate());
					}
				}
			}
		}
		else
		{
			list.add(TextFormatting.DARK_GREEN + ChunkUpgrade.WILDERNESS.getLangKey().translate());
		}

		if (GuiScreen.isCtrlKeyDown())
		{
			list.add(button.chunkPos.toString());
		}
	}
}