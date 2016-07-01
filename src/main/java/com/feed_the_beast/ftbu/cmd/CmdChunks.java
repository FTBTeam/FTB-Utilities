package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.api.ForgePlayer;
import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbl.api.ForgeWorldMP;
import com.feed_the_beast.ftbl.api.cmd.CommandLM;
import com.feed_the_beast.ftbl.api.cmd.CommandSubBase;
import com.feed_the_beast.ftbl.api.notification.Notification;
import com.feed_the_beast.ftbl.api.permissions.Context;
import com.feed_the_beast.ftbl.api.permissions.PermissionAPI;
import com.feed_the_beast.ftbl.util.ChunkDimPos;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.net.MessageAreaUpdate;
import com.feed_the_beast.ftbu.world.ClaimedChunk;
import com.feed_the_beast.ftbu.world.FTBUWorldDataMP;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;

/**
 * Created by LatvianModder on 27.06.2016.
 */
public class CmdChunks extends CommandSubBase
{
    public class CmdClaim extends CommandLM
    {
        public CmdClaim()
        {
            super("claim");
        }

        @Override
        public int getRequiredPermissionLevel()
        {
            return 0;
        }

        @Override
        public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException
        {
            EntityPlayerMP ep = getCommandSenderAsPlayer(sender);
            ForgePlayerMP p = ForgePlayerMP.get(ep);

            ChunkDimPos pos;

            if(args.length >= 2)
            {
                pos = new ChunkDimPos(ep.dimension, parseInt(args[0]), parseInt(args[1]));
            }
            else
            {
                pos = p.getPos().toChunkPos();
            }

            if(FTBUWorldDataMP.claimChunk(p, pos))
            {
                new Notification("modify_chunk").addText(new TextComponentString("Chunk Claimed")).sendTo(ep); //TODO: Lang
                new MessageAreaUpdate(pos.chunkXPos, pos.chunkZPos, pos.dim, 1, 1).sendTo(ep);
            }
            else
            {
                Notification.error("modify_chunk", new TextComponentString("Can't modify this chunk!")).sendTo(ep);
            }
        }
    }

    public class CmdUnclaim extends CommandLM
    {
        public CmdUnclaim()
        {
            super("unclaim");
        }

        @Override
        public int getRequiredPermissionLevel()
        {
            return 0;
        }

        @Override
        public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException
        {
            EntityPlayerMP ep = getCommandSenderAsPlayer(sender);
            ForgePlayerMP p = ForgePlayerMP.get(ep);

            ChunkDimPos pos;

            if(args.length >= 2)
            {
                pos = new ChunkDimPos(ep.dimension, parseInt(args[0]), parseInt(args[1]));
            }
            else
            {
                pos = p.getPos().toChunkPos();
            }

            if(!p.equalsPlayer(FTBUWorldDataMP.chunks.getOwnerPlayer(pos)) && !PermissionAPI.hasPermission(ep.getGameProfile(), FTBUPermissions.CLAIMS_MODIFY_OTHER_CHUNKS, false, new Context(ep).setCustomObject(Context.CHUNK, pos)))
            {
                throw new CommandException("commands.generic.permission");
            }

            if(FTBUWorldDataMP.unclaimChunk(p, pos))
            {
                new Notification("modify_chunk").addText(new TextComponentString("Chunk Unclaimed")).sendTo(ep); //TODO: Lang
                new MessageAreaUpdate(pos.chunkXPos, pos.chunkZPos, pos.dim, 1, 1).sendTo(ep);
            }
            else
            {
                Notification.error("modify_chunk", new TextComponentString("Can't modify this chunk!")).sendTo(ep);
            }
        }
    }

    public class CmdLoad extends CommandLM
    {
        public CmdLoad()
        {
            super("load");
        }

        @Override
        public int getRequiredPermissionLevel()
        {
            return 0;
        }

        @Override
        public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException
        {
            EntityPlayerMP ep = getCommandSenderAsPlayer(sender);
            ForgePlayerMP p = ForgePlayerMP.get(ep);

            ChunkDimPos pos;

            if(args.length >= 2)
            {
                pos = new ChunkDimPos(ep.dimension, parseInt(args[0]), parseInt(args[1]));
            }
            else
            {
                pos = p.getPos().toChunkPos();
            }

            if(FTBUWorldDataMP.setLoaded(p, pos, true))
            {
                new Notification("modify_chunk").addText(new TextComponentString("Chunk Loaded")).sendTo(ep); //TODO: Lang
                new MessageAreaUpdate(pos.chunkXPos, pos.chunkZPos, pos.dim, 1, 1).sendTo(ep);
            }
            else
            {
                Notification.error("modify_chunk", new TextComponentString("Can't modify this chunk!")).sendTo(ep); //TODO: Lang
            }
        }
    }

    public class CmdUnload extends CommandLM
    {
        public CmdUnload()
        {
            super("unload");
        }

        @Override
        public int getRequiredPermissionLevel()
        {
            return 0;
        }

        @Override
        public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException
        {
            EntityPlayerMP ep = getCommandSenderAsPlayer(sender);

            ForgePlayerMP p = ForgePlayerMP.get(ep);
            ChunkDimPos pos;

            if(args.length >= 2)
            {
                pos = new ChunkDimPos(ep.dimension, parseInt(args[0]), parseInt(args[1]));
            }
            else
            {
                pos = p.getPos().toChunkPos();
            }

            if(FTBUWorldDataMP.setLoaded(p, pos, false))
            {
                new Notification("modify_chunk").addText(new TextComponentString("Chunk Unloaded")).sendTo(ep); //TODO: Lang
                new MessageAreaUpdate(pos.chunkXPos, pos.chunkZPos, pos.dim, 1, 1).sendTo(ep);
            }
            else
            {
                Notification.error("modify_chunk", new TextComponentString("Can't modify this chunk!")).sendTo(ep); //TODO: Lang
            }
        }
    }

    public class CmdUnclaimAll extends CommandLM
    {
        public CmdUnclaimAll()
        {
            super("unclaim_all");
        }

        @Override
        public int getRequiredPermissionLevel()
        {
            return 0;
        }

        @Override
        public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException
        {
            EntityPlayerMP ep = getCommandSenderAsPlayer(sender);

            checkArgs(args, 1, "<all_dimensions> [player]");

            ForgePlayerMP p;

            if(args.length >= 2)
            {
                if(!PermissionAPI.hasPermission(ep.getGameProfile(), FTBUPermissions.CLAIMS_MODIFY_OTHER_CHUNKS, false, new Context(ep)))
                {
                    throw new CommandException("commands.generic.permission");
                }

                p = ForgePlayerMP.get(args[1]);
            }
            else
            {
                p = ForgePlayerMP.get(ep);
            }

            FTBUWorldDataMP.unclaimAllChunks(p, parseBoolean(args[0]) ? null : ep.dimension);
            new Notification("unclaimed_all").addText(new TextComponentString("Unclaimed all chunks")).sendTo(ep); //TODO: Lang
        }
    }

    public class CmdUnloadAll extends CommandLM
    {
        public CmdUnloadAll()
        {
            super("admin_unload_all");
        }

        @Override
        public boolean isUsernameIndex(String[] args, int i)
        {
            return i == 0;
        }

        @Override
        public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender ics, @Nonnull String[] args) throws CommandException
        {
            checkArgs(args, 1, "<player>");

            if(args[0].equals("@a"))
            {
                for(ClaimedChunk c : FTBUWorldDataMP.chunks.getAllChunks())
                {
                    c.loaded = false;
                }
                for(ForgePlayer p : ForgeWorldMP.inst.getOnlinePlayers())
                {
                    p.toMP().sendUpdate();
                }
                ics.addChatMessage(new TextComponentString("Unloaded all chunks")); //TODO: Lang
                return;
            }

            ForgePlayerMP p = ForgePlayerMP.get(args[0]);
            for(ClaimedChunk c : FTBUWorldDataMP.chunks.getChunks(p.getProfile().getId()))
            {
                c.loaded = false;
            }
            if(p.isOnline())
            {
                p.sendUpdate();
            }
            ics.addChatMessage(new TextComponentString("Unloaded all " + p.getProfile().getName() + "'s chunks")); //TODO: Lang
        }
    }

    public class CmdAdminUnclaimAll extends CommandLM
    {
        public CmdAdminUnclaimAll()
        {
            super("admin_unclaim_all");
        }

        @Nonnull
        @Override
        public String getCommandUsage(@Nonnull ICommandSender ics)
        {
            return '/' + commandName + " <player | @a>";
        }

        @Override
        public boolean isUsernameIndex(String[] args, int i)
        {
            return i == 0;
        }

        @Override
        public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender ics, @Nonnull String[] args) throws CommandException
        {
            checkArgs(args, 1, "<player>");
            ForgePlayerMP p = ForgePlayerMP.get(args[0]);
            FTBUWorldDataMP.unclaimAllChunks(p, null);
            ics.addChatMessage(new TextComponentString("Unclaimed all " + p.getProfile().getName() + "'s chunks")); //TODO: Lang
        }
    }

    public CmdChunks()
    {
        super("chunks");
        add(new CmdClaim());
        add(new CmdUnclaim());
        add(new CmdLoad());
        add(new CmdUnload());

        add(new CmdUnclaimAll());
        add(new CmdUnloadAll());
        add(new CmdAdminUnclaimAll());
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }
}
