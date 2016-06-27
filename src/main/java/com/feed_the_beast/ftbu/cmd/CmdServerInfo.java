package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.api.cmd.CommandLM;
import com.feed_the_beast.ftbl.api.info.InfoPage;
import com.feed_the_beast.ftbu.badges.Badge;
import com.feed_the_beast.ftbu.client.FTBUActions;
import com.feed_the_beast.ftbu.world.FTBUWorldDataMP;
import com.google.common.collect.ImmutableSetMultimap;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by LatvianModder on 28.04.2016.
 */
public class CmdServerInfo extends CommandLM
{
    public CmdServerInfo()
    {
        super("server_info");
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException
    {
        EntityPlayerMP ep = getCommandSenderAsPlayer(sender);

        InfoPage serverInfo = new InfoPage("server_info").setTitle(FTBUActions.SERVER_INFO.displayName);

        InfoPage page = serverInfo.getSub("loaded_chunks"); // TODO: Lang

        for(WorldServer w : DimensionManager.getWorlds())
        {
            ImmutableSetMultimap<ChunkPos, ForgeChunkManager.Ticket> map = ForgeChunkManager.getPersistentChunksFor(w);

            Map<String, Collection<ChunkPos>> chunksMap = new HashMap<>();

            for(ForgeChunkManager.Ticket t : map.values())
            {
                Collection<ChunkPos> list = chunksMap.get(t.getModId());
                if(list == null)
                {
                    chunksMap.put(t.getModId(), list = new HashSet<>());
                }
                for(ChunkPos c : t.getChunkList())
                {
                    if(!list.contains(c))
                    {
                        list.add(c);
                    }
                }
            }

            InfoPage dim = page.getSub(w.provider.getDimensionType().getName());

            for(Map.Entry<String, Collection<ChunkPos>> e1 : chunksMap.entrySet())
            {
                InfoPage mod = dim.getSub(e1.getKey() + " [" + e1.getValue().size() + "]");
                for(ChunkPos c : e1.getValue())
                {
                    mod.printlnText(c.chunkXPos + ", " + c.chunkZPos + " [ " + c.getCenterXPos() + ", " + c.getCenterZPosition() + " ]");
                }
            }
        }

        InfoPage list = serverInfo.getSub("entities"); //LANG

        for(String s : EntityList.NAME_TO_CLASS.keySet())
        {
            list.printlnText("[" + EntityList.getIDFromString(s) + "] " + s);
        }

        list = serverInfo.getSub("enchantments"); //LANG

        for(Enchantment e : Enchantment.REGISTRY)
        {
            list.printlnText("[" + e.getRegistryName() + "] " + e.getTranslatedName(1));
        }

        list = serverInfo.getSub("loaded_badges"); //LANG

        for(Badge b : FTBUWorldDataMP.localBadges.badgeMap.values())
        {
            list.printlnText(b.getID() + ": " + b.imageURL);
        }

        serverInfo.displayGuide(ep);
    }
}