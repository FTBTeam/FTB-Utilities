package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.api.cmd.CommandLM;
import com.feed_the_beast.ftbl.api.cmd.CommandLevel;
import com.feed_the_beast.ftbl.util.BlockDimPos;
import com.feed_the_beast.ftbl.util.LMDimUtils;
import com.feed_the_beast.ftbu.FTBULang;
import com.feed_the_beast.ftbu.world.FTBUWorldDataMP;
import latmod.lib.LMStringUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import java.util.Collection;
import java.util.List;

public class CmdWarp extends CommandLM
{
    public CmdWarp()
    { super("warp", CommandLevel.ALL); }
    
    @Override
    public String getCommandUsage(ICommandSender ics)
    { return '/' + commandName + " <ID>"; }
    
    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender ics, String[] args, BlockPos pos)
    {
        if(args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, FTBUWorldDataMP.get().warps.list());
        }
        
        return super.getTabCompletionOptions(server, ics, args, pos);
    }
    
    @Override
    public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
    {
        checkArgs(args, 1);
        if(args[0].equals("list"))
        {
            Collection<String> list = FTBUWorldDataMP.get().warps.list();
            ics.addChatMessage(new TextComponentString(list.isEmpty() ? "-" : LMStringUtils.strip(list)));
            return;
        }
        
        EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
        BlockDimPos p = FTBUWorldDataMP.get().warps.get(args[0]);
        if(p == null) { throw new CommandException("ftbu.cmd.warp_not_set", args[0]); }
        LMDimUtils.teleportPlayer(ep, p);
        FTBULang.warp_tp.printChat(ics, args[0]);
    }
}