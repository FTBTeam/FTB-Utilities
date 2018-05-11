package com.feed_the_beast.ftbutilities.handlers;

import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.math.BlockDimPos;
import com.feed_the_beast.ftblib.lib.math.BlockPosContainer;
import com.feed_the_beast.ftblib.lib.math.ChunkDimPos;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftblib.lib.util.text_components.Notification;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.FTBUtilitiesNotifications;
import com.feed_the_beast.ftbutilities.data.BlockInteractionType;
import com.feed_the_beast.ftbutilities.data.ClaimedChunk;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesPlayerData;
import com.google.common.base.Objects;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.server.command.TextComponentHelper;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBUtilities.MOD_ID)
public class FTBUtilitiesPlayerEventHandler
{
	@SubscribeEvent
	public static void onDeath(LivingDeathEvent event)
	{
		if (event.getEntity() instanceof EntityPlayerMP)
		{
			FTBUtilitiesPlayerData data = FTBUtilitiesPlayerData.get(Universe.get().getPlayer(event.getEntity()));
			data.setLastDeath(new BlockDimPos(event.getEntity()));
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onChunkChanged(EntityEvent.EnteringChunk event)
	{
		if (event.getEntity().world.isRemote || !(event.getEntity() instanceof EntityPlayerMP) || !Universe.loaded())
		{
			return;
		}

		EntityPlayerMP player = (EntityPlayerMP) event.getEntity();
		ForgePlayer p = Universe.get().getPlayer(player.getGameProfile());

		if (p == null || p.isFake())
		{
			return;
		}

		FTBUtilitiesPlayerData.get(p).setLastSafePos(new BlockDimPos(player));
		updateChunkMessage(player, new ChunkDimPos(event.getNewChunkX(), event.getNewChunkZ(), player.dimension));
	}

	public static void updateChunkMessage(EntityPlayerMP player, ChunkDimPos pos)
	{
		if (ClaimedChunks.instance == null)
		{
			return;
		}

		ClaimedChunk chunk = ClaimedChunks.instance.getChunk(pos);
		ForgeTeam team = chunk == null ? null : chunk.getTeam();

		FTBUtilitiesPlayerData data = FTBUtilitiesPlayerData.get(Universe.get().getPlayer(player));

		if (!Objects.equal(data.lastChunkTeam, team))
		{
			data.lastChunkTeam = team;

			if (team != null)
			{
				Notification notification = Notification.of(FTBUtilitiesNotifications.CHUNK_CHANGED, team.getTitle());

				if (!team.getDesc().isEmpty())
				{
					notification.addLine(StringUtils.italic(new TextComponentString(team.getDesc()), true));
				}

				notification.send(player.mcServer, player);
			}
			else
			{
				Notification.of(FTBUtilitiesNotifications.CHUNK_CHANGED, StringUtils.color(TextComponentHelper.createComponentTranslation(player, "ftbutilities.lang.chunks.wilderness"), TextFormatting.DARK_GREEN)).send(player.mcServer, player);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onEntityAttacked(AttackEntityEvent event)
	{
		if (ClaimedChunks.instance != null && event.getEntityPlayer() instanceof EntityPlayerMP)
		{
			if (!ClaimedChunks.instance.canPlayerAttackEntity((EntityPlayerMP) event.getEntityPlayer(), event.getTarget()))
			{
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
	{
		if (ClaimedChunks.instance != null && event.getEntityPlayer() instanceof EntityPlayerMP)
		{
			if (!ClaimedChunks.instance.canPlayerInteract((EntityPlayerMP) event.getEntityPlayer(), event.getHand(), new BlockPosContainer(event), BlockInteractionType.INTERACT))
			{
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onRightClickItem(PlayerInteractEvent.RightClickItem event)
	{
		if (ClaimedChunks.instance != null && event.getEntityPlayer() instanceof EntityPlayerMP)
		{
			if (!ClaimedChunks.instance.canPlayerInteract((EntityPlayerMP) event.getEntityPlayer(), event.getHand(), new BlockPosContainer(event), BlockInteractionType.ITEM))
			{
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onBlockBreak(BlockEvent.BreakEvent event)
	{
		if (ClaimedChunks.instance != null && event.getPlayer() instanceof EntityPlayerMP)
		{
			if (!ClaimedChunks.instance.canPlayerInteract((EntityPlayerMP) event.getPlayer(), EnumHand.MAIN_HAND, new BlockPosContainer(event.getWorld(), event.getPos(), event.getState()), BlockInteractionType.EDIT))
			{
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onBlockPlace(BlockEvent.PlaceEvent event)
	{
		if (ClaimedChunks.instance != null && event.getPlayer() instanceof EntityPlayerMP)
		{
			if (!ClaimedChunks.instance.canPlayerInteract((EntityPlayerMP) event.getPlayer(), EnumHand.MAIN_HAND, new BlockPosContainer(event.getWorld(), event.getPos(), event.getPlacedBlock()), BlockInteractionType.EDIT))
			{
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onBlockLeftClick(PlayerInteractEvent.LeftClickBlock event)
	{
		if (ClaimedChunks.instance != null && event.getEntityPlayer() instanceof EntityPlayerMP)
		{
			if (!ClaimedChunks.instance.canPlayerInteract((EntityPlayerMP) event.getEntityPlayer(), event.getHand(), new BlockPosContainer(event), BlockInteractionType.EDIT))
			{
				event.setCanceled(true);
			}
		}
	}

    /*
	@SubscribeEvent(priority = EventPriority.HIGH)
    public static void onItemPickup(EntityItemPickupEvent event)
    {
    }
    */
}