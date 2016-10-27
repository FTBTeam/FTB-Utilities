package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.api.info.IGuiInfoPage;
import com.feed_the_beast.ftbl.lib.cmd.CommandLM;
import com.feed_the_beast.ftbl.lib.info.InfoPage;
import com.feed_the_beast.ftbu.FTBLibIntegration;
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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by LatvianModder on 28.04.2016.
 */
public class CmdServerInfo extends CommandLM
{
    @Override
    public String getCommandName()
    {
        return "server_info";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        EntityPlayerMP ep = getCommandSenderAsPlayer(sender);

        InfoPage serverInfo = new InfoPage("server_info"); //TODO: Lang

        IGuiInfoPage page = serverInfo.getSub("loaded_chunks"); // TODO: Lang

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

            IGuiInfoPage dim = page.getSub(w.provider.getDimensionType().getName());

            for(Map.Entry<String, Collection<ChunkPos>> e1 : chunksMap.entrySet())
            {
                IGuiInfoPage mod = dim.getSub(e1.getKey() + " [" + e1.getValue().size() + "]");
                for(ChunkPos c : e1.getValue())
                {
                    mod.println(c.chunkXPos + ", " + c.chunkZPos + " [ " + c.getCenterXPos() + ", " + c.getCenterZPosition() + " ]");
                }
            }
        }

        IGuiInfoPage list = serverInfo.getSub("entities"); //LANG

        for(String s : EntityList.NAME_TO_CLASS.keySet())
        {
            list.println("[" + EntityList.getIDFromString(s) + "] " + s);
        }

        list = serverInfo.getSub("enchantments"); //LANG

        for(Enchantment e : Enchantment.REGISTRY)
        {
            list.println("[" + e.getRegistryName() + "] " + e.getTranslatedName(1));
        }

        FTBLibIntegration.API.displayInfoGui(ep, serverInfo);
    }
}