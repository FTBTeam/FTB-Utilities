package ftb.utils.cmd;

import ftb.lib.BlockDimPos;
import ftb.lib.LMDimUtils;
import ftb.lib.api.ForgePlayerMP;
import ftb.lib.api.ForgeWorldMP;
import ftb.lib.api.cmd.CommandLM;
import ftb.lib.api.cmd.CommandLevel;
import ftb.lib.api.permissions.ForgePermissionRegistry;
import ftb.utils.FTBULang;
import ftb.utils.FTBUPermissions;
import ftb.utils.world.FTBUPlayerDataMP;
import latmod.lib.LMStringUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import java.util.Collection;
import java.util.List;

public class CmdHome extends CommandLM
{
	public CmdHome()
	{ super("home", CommandLevel.ALL); }
	
	@Override
	public String getCommandUsage(ICommandSender ics)
	{ return '/' + commandName + " <ID>"; }
	
	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender ics, String[] args, BlockPos pos)
	{
		if(args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, FTBUPlayerDataMP.get(ForgeWorldMP.inst.getPlayer(ics)).homes.list());
		}
		
		return super.getTabCompletionOptions(server, ics, args, pos);
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		FTBUPlayerDataMP d = FTBUPlayerDataMP.get(ForgePlayerMP.get(ep));
		checkArgs(args, 1);
		
		if(args[0].equals("list"))
		{
			Collection<String> list = d.homes.list();
			ics.addChatMessage(new TextComponentString(list.size() + " / " + FTBUPermissions.homes_max.get(ep.getGameProfile()).getAsShort() + ": "));
			if(!list.isEmpty()) ics.addChatMessage(new TextComponentString(LMStringUtils.strip(list)));
			return;
		}
		
		BlockDimPos pos = d.homes.get(args[0]);
		
		if(pos == null) throw new CommandException("ftbu.cmd.home_not_set", args[0]);
		
		if(ep.dimension != pos.dim.getId() && !ForgePermissionRegistry.hasPermission(FTBUPermissions.homes_cross_dim, ep.getGameProfile()))
			throw new CommandException("ftbu.cmd.home_cross_dim");
		
		LMDimUtils.teleportPlayer(ep, pos);
		FTBULang.warp_tp.printChat(ics, args[0]);
	}
}