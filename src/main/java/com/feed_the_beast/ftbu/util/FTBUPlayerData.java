package com.feed_the_beast.ftbu.util;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.IForgeTeam;
import com.feed_the_beast.ftbl.api.player.ForgePlayerConfigEvent;
import com.feed_the_beast.ftbl.lib.config.ConfigBoolean;
import com.feed_the_beast.ftbl.lib.io.Bits;
import com.feed_the_beast.ftbl.lib.math.BlockDimPos;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.api.chunks.ChunkUpgrade;
import com.feed_the_beast.ftbu.api_impl.ChunkUpgrades;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunk;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunks;
import com.feed_the_beast.ftbu.handlers.FTBLibIntegration;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * @author LatvianModder
 */
public class FTBUPlayerData implements INBTSerializable<NBTTagCompound>
{
	public final ConfigBoolean renderBadge = new ConfigBoolean(true);
	public final ConfigBoolean chatLinks = new ConfigBoolean(true);
	public final ConfigBoolean disableGlobalBadge = new ConfigBoolean(false);

	public final IForgePlayer player;
	public BlockDimPos lastDeath, lastSafePos;
	public IForgeTeam lastChunkTeam;
	public final BlockDimPosStorage homes;
	public boolean fly;

	public FTBUPlayerData(IForgePlayer p)
	{
		player = p;
		homes = new BlockDimPosStorage();
	}

	public static FTBUPlayerData get(IForgePlayer player)
	{
		return player.getData().get(FTBLibIntegration.FTBU_DATA);
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();

		nbt.setBoolean("RenderBadge", renderBadge.getBoolean());
		nbt.setBoolean("ChatLinks", chatLinks.getBoolean());
		nbt.setBoolean("DisableGlobalBadges", disableGlobalBadge.getBoolean());
		nbt.setTag("Homes", homes.serializeNBT());
		nbt.setBoolean("AllowFlying", fly);

		if (lastDeath != null)
		{
			nbt.setIntArray("LastDeath", lastDeath.toIntArray());
		}

		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		if (nbt == null)
		{
			return;
		}

		renderBadge.setBoolean(nbt.getBoolean("RenderBadge"));
		chatLinks.setBoolean(nbt.getBoolean("ChatLinks"));
		disableGlobalBadge.setBoolean(nbt.getBoolean("DisableGlobalBadges"));
		homes.deserializeNBT(nbt.getCompoundTag("Homes"));
		fly = nbt.getBoolean("AllowFlying");

		lastDeath = null;
		if (nbt.hasKey("LastDeath"))
		{
			int[] ai = nbt.getIntArray("LastDeath");
			lastDeath = (ai.length == 4) ? new BlockDimPos(ai) : null;
		}

		FTBUTeamData team = player.getTeam() == null ? null : FTBUTeamData.get(player.getTeam());

		if (team != null)
		{
			NBTTagList list = nbt.getTagList("ClaimedChunks", Constants.NBT.TAG_INT_ARRAY);

			for (int i = 0; i < list.tagCount(); i++)
			{
				int[] ai = list.getIntArrayAt(i);

				if (ai.length >= 3)
				{
					ClaimedChunk chunk = new ClaimedChunk(new ChunkDimPos(ai[1], ai[2], ai[0]), team);

					if (ai.length >= 4)
					{
						if (ai[3] == -1)
						{
							for (int j = 4; j < ai.length; j++)
							{
								ChunkUpgrade upgrade = FTBUUniverseData.getUpgradeFromId(ai[j]);

								if (upgrade != null)
								{
									chunk.setHasUpgrade(upgrade, true);
								}
							}
						}
						else
						{
							if (Bits.getFlag(ai[3], 1))
							{
								chunk.setHasUpgrade(ChunkUpgrades.LOADED, true);
							}
						}
					}

					ClaimedChunks.INSTANCE.addChunk(chunk);
				}
			}
		}
	}

	public void addConfig(ForgePlayerConfigEvent event)
	{
		String group = FTBUFinals.MOD_ID;
		event.getConfig().setGroupName(group, new TextComponentString(FTBUFinals.MOD_NAME));
		event.getConfig().add(group, "render_badge", renderBadge);
		event.getConfig().add(group, "chat_links", chatLinks);
		event.getConfig().add(group, "disable_global_badge", disableGlobalBadge);
	}
}