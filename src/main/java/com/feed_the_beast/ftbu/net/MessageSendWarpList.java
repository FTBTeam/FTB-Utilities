package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.lib.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.lib.net.MessageToClient;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.gui.GuiWarps;
import com.feed_the_beast.ftbu.world.FTBUPlayerData;
import com.feed_the_beast.ftbu.world.FTBUUniverseData;
import io.netty.buffer.ByteBuf;
import net.minecraft.command.ICommand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.ArrayList;
import java.util.List;

public class MessageSendWarpList extends MessageToClient<MessageSendWarpList>
{
    public static class WarpItem implements Comparable<WarpItem>
    {
        public static final byte TYPE_SPECIAL_IN = 0;
        public static final byte TYPE_SPECIAL_OUT = 1;
        public static final byte TYPE_WARP = 2;
        public static final byte TYPE_HOME = 3;

        public static final WarpItem CANCEL = new WarpItem("Cancel", "", TYPE_SPECIAL_IN);

        public final String name;
        public final String cmd;
        public final byte type;

        private WarpItem(String n, String c, byte t)
        {
            name = n;
            cmd = c;
            type = t;
        }

        public boolean isSpecial()
        {
            return type == TYPE_SPECIAL_IN || type == TYPE_SPECIAL_OUT;
        }

        public boolean innerCircle()
        {
            return type == TYPE_SPECIAL_IN || type == TYPE_HOME;
        }

        @Override
        public int compareTo(WarpItem o)
        {
            return name.compareToIgnoreCase(o.name);
        }
    }

    private List<WarpItem> warps;

    public MessageSendWarpList()
    {
    }

    private static String command(EntityPlayerMP player, String name, String backup)
    {
        ICommand command = player.mcServer.getCommandManager().getCommands().get(name);
        return "/" + ((command != null && command.checkPermission(player.mcServer, player)) ? name : backup);
    }

    MessageSendWarpList(EntityPlayerMP player)
    {
        warps = new ArrayList<>();

        warps.add(new WarpItem("Spawn", command(player, "spawn", "ftb spawn"), WarpItem.TYPE_SPECIAL_OUT));
        warps.add(new WarpItem("Back", command(player, "back", "ftb back"), WarpItem.TYPE_SPECIAL_IN));

        FTBUUniverseData universeData = FTBUUniverseData.get();

        if(universeData != null)
        {
            String cmd = command(player, "warp", "ftb warp") + " ";

            for(String s : universeData.listWarps())
            {
                warps.add(new WarpItem(s, cmd + s, WarpItem.TYPE_WARP));
            }
        }

        FTBUPlayerData playerData = FTBUPlayerData.get(FTBLibIntegration.API.getUniverse().getPlayer(player));

        if(playerData != null)
        {
            String cmd = command(player, "home", "ftb home") + " ";

            for(String s : playerData.listHomes())
            {
                warps.add(new WarpItem(s, cmd + s, WarpItem.TYPE_HOME));
            }
        }
    }

    @Override
    public LMNetworkWrapper getWrapper()
    {
        return FTBUNetHandler.NET;
    }

    @Override
    public void fromBytes(ByteBuf io)
    {
        int s = io.readUnsignedShort();
        warps = new ArrayList<>(s);

        while(--s >= 0)
        {
            String n = ByteBufUtils.readUTF8String(io);
            String c = ByteBufUtils.readUTF8String(io);
            warps.add(new WarpItem(n, c, io.readByte()));
        }
    }

    @Override
    public void toBytes(ByteBuf io)
    {
        io.writeShort(warps.size());

        for(WarpItem w : warps)
        {
            ByteBufUtils.writeUTF8String(io, w.name);
            ByteBufUtils.writeUTF8String(io, w.cmd);
            io.writeByte(w.type);
        }
    }

    @Override
    public void onMessage(MessageSendWarpList m, EntityPlayer player)
    {
        if(GuiWarps.INSTANCE != null)
        {
            GuiWarps.INSTANCE.setData(m.warps);
        }
    }
}