package com.feed_the_beast.ftbu.gui;

import com.feed_the_beast.ftbl.api.EnumTeamColor;
import com.feed_the_beast.ftbl.lib.MouseButton;
import com.feed_the_beast.ftbl.lib.client.CachedVertexData;
import com.feed_the_beast.ftbl.lib.client.ClientUtils;
import com.feed_the_beast.ftbl.lib.gui.Button;
import com.feed_the_beast.ftbl.lib.gui.GuiBase;
import com.feed_the_beast.ftbl.lib.gui.GuiHelper;
import com.feed_the_beast.ftbl.lib.gui.GuiIcons;
import com.feed_the_beast.ftbl.lib.gui.GuiLang;
import com.feed_the_beast.ftbl.lib.gui.Panel;
import com.feed_the_beast.ftbl.lib.gui.SimpleButton;
import com.feed_the_beast.ftbl.lib.gui.misc.ChunkSelectorMap;
import com.feed_the_beast.ftbl.lib.gui.misc.GuiChunkSelectorBase;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.api.chunks.ChunkUpgrade;
import com.feed_the_beast.ftbu.api_impl.ChunkUpgrades;
import com.feed_the_beast.ftbu.client.FTBUClient;
import com.feed_the_beast.ftbu.net.MessageClaimedChunksModify;
import com.feed_the_beast.ftbu.net.MessageClaimedChunksRequest;
import com.feed_the_beast.ftbu.net.MessageClaimedChunksUpdate;
import com.feed_the_beast.ftbu.util.FTBUUniverseData;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GuiClaimedChunks extends GuiChunkSelectorBase
{
	public static GuiClaimedChunks instance;
	private static final ClientClaimedChunks.ChunkData[] chunkData = new ClientClaimedChunks.ChunkData[ChunkSelectorMap.TILES_GUI * ChunkSelectorMap.TILES_GUI];
	private static int claimedChunks, loadedChunks, maxClaimedChunks, maxLoadedChunks;
	private static final ClientClaimedChunks.ChunkData NULL_CHUNK_DATA = new ClientClaimedChunks.ChunkData(new ClientClaimedChunks.Team(UUID.randomUUID()));

	private static final CachedVertexData AREA = new CachedVertexData(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

	@Nullable
	private static ClientClaimedChunks.ChunkData getAt(int x, int y)
	{
		int i = x + y * ChunkSelectorMap.TILES_GUI;
		return i < 0 || i >= chunkData.length ? null : chunkData[i];
	}

	private static boolean hasBorder(ClientClaimedChunks.ChunkData data, @Nullable ClientClaimedChunks.ChunkData with)
	{
		if (with == null)
		{
			with = NULL_CHUNK_DATA;
		}

		return (!data.upgrades.equals(with.upgrades) || data.team != with.team) && !with.hasUpgrade(ChunkUpgrades.LOADED);
	}

	public static void setData(MessageClaimedChunksUpdate m)
	{
		claimedChunks = m.claimedChunks;
		loadedChunks = m.loadedChunks;
		maxClaimedChunks = m.maxClaimedChunks;
		maxLoadedChunks = m.maxLoadedChunks;
		Arrays.fill(chunkData, null);

		for (ClientClaimedChunks.Team team : m.teams.values())
		{
			for (Map.Entry<Integer, ClientClaimedChunks.ChunkData> entry : team.chunks.entrySet())
			{
				int x = entry.getKey() % ChunkSelectorMap.TILES_GUI;
				int z = entry.getKey() / ChunkSelectorMap.TILES_GUI;
				chunkData[x + z * ChunkSelectorMap.TILES_GUI] = entry.getValue();
			}
		}

		if (FTBUClient.JM_INTEGRATION != null)
		{
			for (int z = 0; z < ChunkSelectorMap.TILES_GUI; z++)
			{
				for (int x = 0; x < ChunkSelectorMap.TILES_GUI; x++)
				{
					FTBUClient.JM_INTEGRATION.chunkChanged(new ChunkPos(m.startX + x, m.startZ + z), chunkData[x + z * ChunkSelectorMap.TILES_GUI]);
				}
			}
		}

		AREA.reset();
		EnumTeamColor prevCol = null;
		ClientClaimedChunks.ChunkData data;

		for (int i = 0; i < chunkData.length; i++)
		{
			data = chunkData[i];

			if (data == null)
			{
				continue;
			}

			if (prevCol != data.team.color)
			{
				prevCol = data.team.color;
				AREA.color.set(data.team.color.getColor(), 150);
			}

			AREA.rect((i % ChunkSelectorMap.TILES_GUI) * 16, (i / ChunkSelectorMap.TILES_GUI) * 16, 16, 16);
		}

		boolean borderU, borderD, borderL, borderR;

		for (int i = 0; i < chunkData.length; i++)
		{
			data = chunkData[i];

			if (data == null)
			{
				continue;
			}

			int x = i % ChunkSelectorMap.TILES_GUI;
			int dx = x * 16;
			int y = i / ChunkSelectorMap.TILES_GUI;
			int dy = y * 16;

			borderU = y > 0 && hasBorder(data, getAt(x, y - 1));
			borderD = y < (ChunkSelectorMap.TILES_GUI - 1) && hasBorder(data, getAt(x, y + 1));
			borderL = x > 0 && hasBorder(data, getAt(x - 1, y));
			borderR = x < (ChunkSelectorMap.TILES_GUI - 1) && hasBorder(data, getAt(x + 1, y));

			if (data.hasUpgrade(ChunkUpgrades.LOADED))
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
		buttonClose = new SimpleButton(GuiLang.CLOSE.translate(), GuiIcons.ACCEPT, (gui, button) -> gui.closeGui());

		buttonRefresh = new SimpleButton(0, 16, GuiLang.REFRESH.translate(), GuiIcons.REFRESH, (gui, button) ->
		{
			new MessageClaimedChunksRequest(startX, startZ).sendToServer();
			ChunkSelectorMap.getMap().resetMap(startX, startZ);
		});

		buttonUnclaimAll = new Button(0, 32, 16, 16)
		{
			@Override
			public void onClicked(GuiBase gui, MouseButton button)
			{
				GuiHelper.playClickSound();
				String s = GuiScreen.isShiftKeyDown() ? FTBULang.CHUNKS_UNCLAIM_ALL_Q.translate() : FTBULang.CHUNKS_UNCLAIM_ALL_DIM_Q.translate(currentDimName);
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
				list.add(GuiScreen.isShiftKeyDown() ? FTBULang.CHUNKS_UNCLAIM_ALL.translate() : FTBULang.CHUNKS_UNCLAIM_ALL_DIM.translate(currentDimName));
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
	public int getSelectionMode(MouseButton button)
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
	public void addCornerText(List<String> list, Corner corner)
	{
		if (corner == Corner.BOTTOM_RIGHT)
		{
			list.add(FTBULang.CHUNKS_CLAIMED_COUNT.translate(claimedChunks, maxClaimedChunks));
			list.add(FTBULang.CHUNKS_LOADED_COUNT.translate(loadedChunks, maxLoadedChunks));
		}
		else if (corner == Corner.BOTTOM_LEFT)
		{
			list.add(StringUtils.translate(""));
			list.add(StringUtils.translate(""));
			list.add(StringUtils.translate(""));
			list.add(StringUtils.translate(""));
		}
	}

	@Override
	public void addButtonText(GuiChunkSelectorBase.MapButton button, List<String> list)
	{
		ClientClaimedChunks.ChunkData data = chunkData[button.index];

		if (data != null)
		{
			list.add(data.team.formattedName);
			list.add(TextFormatting.GREEN + ChunkUpgrades.CLAIMED.getLangKey().translate());

			if (data.team.isAlly)
			{
				for (ChunkUpgrade upgrade : FTBUUniverseData.CHUNK_UPGRADES.values())
				{
					if (!upgrade.isInternal() && data.hasUpgrade(upgrade))
					{
						list.add(TextFormatting.RED + upgrade.getLangKey().translate());
					}
				}
			}
		}
		else
		{
			list.add(TextFormatting.DARK_GREEN + ChunkUpgrades.WILDERNESS.getLangKey().translate());
		}

		if (GuiScreen.isCtrlKeyDown())
		{
			list.add(button.chunkPos.toString());
		}
	}
}