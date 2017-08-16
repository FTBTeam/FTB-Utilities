package com.feed_the_beast.ftbu.handlers;

import com.feed_the_beast.ftbl.api.EventHandler;
import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.IForgeTeam;
import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbl.api.events.player.ForgePlayerDeathEvent;
import com.feed_the_beast.ftbl.api.events.player.ForgePlayerLoggedInEvent;
import com.feed_the_beast.ftbl.api.events.player.ForgePlayerLoggedOutEvent;
import com.feed_the_beast.ftbl.api.events.player.ForgePlayerSettingsEvent;
import com.feed_the_beast.ftbl.lib.Notification;
import com.feed_the_beast.ftbl.lib.config.PropertyItemStack;
import com.feed_the_beast.ftbl.lib.config.PropertyTextComponent;
import com.feed_the_beast.ftbl.lib.math.BlockDimPos;
import com.feed_the_beast.ftbl.lib.math.BlockPosContainer;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbl.lib.util.InvUtils;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.feed_the_beast.ftbu.FTBUNotifications;
import com.feed_the_beast.ftbu.api.chunks.BlockInteractionType;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunkStorage;
import com.feed_the_beast.ftbu.api_impl.FTBUChunkManager;
import com.feed_the_beast.ftbu.config.FTBUConfigLogin;
import com.feed_the_beast.ftbu.config.FTBUConfigWorld;
import com.feed_the_beast.ftbu.world.FTBUPlayerData;
import com.feed_the_beast.ftbu.world.FTBUUniverseData;
import com.google.common.base.Objects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
@EventHandler
public class FTBUPlayerEventHandler
{
	@SubscribeEvent
	public static void onLoggedIn(ForgePlayerLoggedInEvent event)
	{
		if (event.getPlayer().isFake())
		{
			return;
		}

		EntityPlayerMP ep = event.getPlayer().getPlayer();

		if (event.isFirstLogin())
		{
			if (FTBUConfigLogin.ENABLE_STARTING_ITEMS.getBoolean())
			{
				for (IConfigValue value : FTBUConfigLogin.STARTING_ITEMS)
				{
					InvUtils.giveItem(ep, ((PropertyItemStack) value).getItem());
				}
			}
		}

		if (FTBUConfigLogin.ENABLE_MOTD.getBoolean())
		{
			for (IConfigValue value : FTBUConfigLogin.MOTD)
			{
				ITextComponent t = ((PropertyTextComponent) value).getText();

				if (t != null)
				{
					ep.sendMessage(t);
				}
			}
		}

		FTBUChunkManager.INSTANCE.checkAll();
	}

	@SubscribeEvent
	public static void onLoggedOut(ForgePlayerLoggedOutEvent event)
	{
		FTBUChunkManager.INSTANCE.checkAll();
		FTBUUniverseData.updateBadge(event.getPlayer().getId());
	}

	@SubscribeEvent
	public static void onDeath(ForgePlayerDeathEvent event)
	{
		FTBUPlayerData data = FTBUPlayerData.get(event.getPlayer());
		if (data != null)
		{
			data.lastDeath = new BlockDimPos(event.getPlayer().getPlayer());
		}
	}

	@SubscribeEvent
	public static void getSettings(ForgePlayerSettingsEvent event)
	{
		FTBUPlayerData data = FTBUPlayerData.get(event.getPlayer());
		if (data != null)
		{
			data.addConfig(event);
		}
	}
	
    /*
	@SubscribeEvent
    public void addInfo(ForgePlayerInfoEvent event)
    {
        if(owner.getRank().config.show_rank.getMode())
		{
		    Rank rank = getRank();
		    IChatComponent rankC = new ChatComponentText("[" + rank.ID + "]");
		    rankC.getChatStyle().setColor(rank.color.getMode());
		    info.add(rankC);
		}
    }
    */

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onChunkChanged(EntityEvent.EnteringChunk e)
	{
		if (e.getEntity().world.isRemote || !(e.getEntity() instanceof EntityPlayerMP))
		{
			return;
		}

		EntityPlayerMP ep = (EntityPlayerMP) e.getEntity();
		IForgePlayer player = FTBLibAPI.API.getUniverse().getPlayer(ep);

		if (player == null || !player.isOnline())
		{
			return;
		}

		FTBUPlayerData data = FTBUPlayerData.get(player);

		if (data != null)
		{
			data.lastSafePos = new BlockDimPos(ep);
		}

		updateChunkMessage(ep, new ChunkDimPos(e.getNewChunkX(), e.getNewChunkZ(), ep.dimension));
	}

	public static void updateChunkMessage(EntityPlayerMP player, ChunkDimPos pos)
	{
		IForgePlayer newTeamOwner = ClaimedChunkStorage.INSTANCE.getChunkOwner(pos);

		FTBUPlayerData data = FTBUPlayerData.get(FTBLibAPI.API.getUniverse().getPlayer(player));

		if (data == null)
		{
			return;
		}

		if (!Objects.equal(data.lastChunkOwner, newTeamOwner))
		{
			data.lastChunkOwner = newTeamOwner;

			if (newTeamOwner != null)
			{
				IForgeTeam team = newTeamOwner.getTeam();

				if (team != null)
				{
					Notification notification = Notification.of(FTBUNotifications.WILDERNESS.getId());
					notification.addLine(StringUtils.color(new TextComponentString(team.getTitle()), team.getColor().getTextFormatting()));

					if (!team.getDesc().isEmpty())
					{
						notification.addLine(StringUtils.italic(new TextComponentString(team.getDesc()), true));
					}

					notification.send(player);
				}
			}
			else
			{
				FTBUNotifications.WILDERNESS.send(player);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onPlayerAttacked(LivingAttackEvent e)
	{
		if (e.getEntity().world.isRemote)
		{
			return;
		}

		if (e.getEntity().dimension != 0 || !(e.getEntity() instanceof EntityPlayerMP) || e.getEntity() instanceof FakePlayer)
		{
			return;
		}

		Entity entity = e.getSource().getTrueSource();

		if (entity != null && (entity instanceof EntityPlayerMP || entity instanceof IMob))
		{
			if (entity instanceof FakePlayer)
			{
				return;
			}
			/*else if(entity instanceof EntityPlayerMP && PermissionAPI.hasPermission(((EntityPlayerMP) entity).getGameProfile(), FTBLibPermissions.INTERACT_SECURE, false, new Context(entity)))
			{
                return;
            }*/

			if ((FTBUConfigWorld.SAFE_SPAWN.getBoolean() && FTBUUniverseData.isInSpawnD(e.getEntity().dimension, e.getEntity().posX, e.getEntity().posZ)))
			{
				e.setCanceled(true);
			}
			/*else
			{
				ClaimedChunk c = Claims.getMode(dim, cx, cz);
				if(c != null && c.claims.settings.isSafe()) e.setCanceled(true);
			}*/
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
	{
		if (event.getEntityPlayer() instanceof EntityPlayerMP && !ClaimedChunkStorage.INSTANCE.canPlayerInteract((EntityPlayerMP) event.getEntityPlayer(), event.getHand(), new BlockPosContainer(event), BlockInteractionType.INTERACT))
		{
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onRightClickItem(PlayerInteractEvent.RightClickItem event)
	{
		if (event.getEntityPlayer() instanceof EntityPlayerMP && !ClaimedChunkStorage.INSTANCE.canPlayerInteract((EntityPlayerMP) event.getEntityPlayer(), event.getHand(), new BlockPosContainer(event), BlockInteractionType.ITEM))
		{
			event.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onBlockBreak(BlockEvent.BreakEvent event)
	{
		if (event.getPlayer() instanceof EntityPlayerMP && !ClaimedChunkStorage.INSTANCE.canPlayerInteract((EntityPlayerMP) event.getPlayer(), EnumHand.MAIN_HAND, new BlockPosContainer(event.getWorld(), event.getPos(), event.getState()), BlockInteractionType.EDIT))
		{
			event.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onBlockPlace(BlockEvent.PlaceEvent event)
	{
		if (event.getPlayer() instanceof EntityPlayerMP && !ClaimedChunkStorage.INSTANCE.canPlayerInteract((EntityPlayerMP) event.getPlayer(), EnumHand.MAIN_HAND, new BlockPosContainer(event.getWorld(), event.getPos(), event.getPlacedBlock()), BlockInteractionType.EDIT))
		{
			event.setCanceled(true);
		}
	}

	@Optional.Method(modid = "chiselsandbits")
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onChiselEvent(mod.chiselsandbits.api.EventBlockBitModification event)
	{
		if (event.getPlayer() instanceof EntityPlayerMP && !ClaimedChunkStorage.INSTANCE.canPlayerInteract((EntityPlayerMP) event.getPlayer(), event.getHand(), new BlockPosContainer(event.getWorld(), event.getPos(), event.getWorld().getBlockState(event.getPos())), event.isPlacing() ? BlockInteractionType.CNB_PLACE : BlockInteractionType.CNB_BREAK))
		{
			event.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onBlockLeftClick(PlayerInteractEvent.LeftClickBlock event)
	{
		if (event.getEntityPlayer() instanceof EntityPlayerMP && !ClaimedChunkStorage.INSTANCE.canPlayerInteract((EntityPlayerMP) event.getEntityPlayer(), event.getHand(), new BlockPosContainer(event), BlockInteractionType.EDIT))
		{
			event.setCanceled(true);
		}
	}

    /*
	@SubscribeEvent(priority = EventPriority.HIGH)
    public static void onItemPickup(EntityItemPickupEvent event)
    {
    }

    @Optional.Method(modid = "iChunUtil") //TODO: Change to lowercase whenever iChun does
    @SubscribeEvent
    public static void onBlockPickupEventEvent(me.ichun.mods.ichunutil.api.event.BlockPickupEvent event)
    {
    }
    */
}