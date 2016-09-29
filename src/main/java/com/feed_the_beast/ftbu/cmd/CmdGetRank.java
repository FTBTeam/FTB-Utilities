package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.rankconfig.RankConfigAPI;
import com.feed_the_beast.ftbl.lib.cmd.CommandLM;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api.IRank;
import com.feed_the_beast.ftbu.api_impl.FTBUtilitiesAPI_Impl;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

/**
 * Created by LatvianModder on 21.02.2016.
 */
public class CmdGetRank extends CommandLM
{
    public CmdGetRank()
    {
        super("getrank");
    }

    @Override
    public boolean isUsernameIndex(String[] args, int i)
    {
        return i == 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
    {
        checkArgs(args, 1, "<player>");
        IForgePlayer p = getForgePlayer(args[0]);
        IRank r = FTBUtilitiesAPI_Impl.INSTANCE.getRank(p.getProfile());
        ITextComponent c = new TextComponentString(r.getName());
        c.getStyle().setColor((TextFormatting) RankConfigAPI.getRankConfig(p.getProfile(), FTBUPermissions.DISPLAY_COLOR).getValue());
        ics.addChatMessage(c);
    }
}
