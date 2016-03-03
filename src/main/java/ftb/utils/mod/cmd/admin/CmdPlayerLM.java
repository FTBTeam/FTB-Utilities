package ftb.utils.mod.cmd.admin;

import com.mojang.authlib.GameProfile;
import ftb.lib.FTBLib;
import ftb.lib.api.cmd.*;
import ftb.lib.api.players.*;
import latmod.lib.json.UUIDTypeAdapterLM;
import net.minecraft.command.*;
import net.minecraft.util.*;

import java.util.UUID;

public class CmdPlayerLM extends CommandSubLM
{
	public CmdPlayerLM()
	{
		super("player_lm", CommandLevel.OP);
		
		if(FTBLib.DEV_ENV) add(new CmdAddFake("add_fake"));
		add(new CmdDelete("delete"));
	}
	
	public static class CmdAddFake extends CommandLM
	{
		public CmdAddFake(String s)
		{ super(s, CommandLevel.OP); }
		
		public String getCommandUsage(ICommandSender ics)
		{ return '/' + commandName + " <player>"; }
		
		public Boolean getUsername(String[] args, int i)
		{ return (i == 0) ? Boolean.FALSE : null; }
		
		public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
		{
			checkArgs(args, 2);
			
			UUID id = UUIDTypeAdapterLM.getUUID(args[0]);
			if(id == null) return error(new ChatComponentText("Invalid UUID!"));
			
			if(LMWorldMP.inst.getPlayer(id) != null || LMWorldMP.inst.getPlayer(args[1]) != null)
				return error(new ChatComponentText("Player already exists!"));
			
			LMPlayerMP p = new LMPlayerMP(new GameProfile(id, args[1]));
			LMWorldMP.inst.playerMap.put(p.getProfile().getId(), p);
			p.refreshStats();
			
			return new ChatComponentText("Fake player " + args[1] + " added!");
		}
	}
	
	public static class CmdDelete extends CommandLM
	{
		public CmdDelete(String s)
		{ super(s, CommandLevel.OP); }
		
		public String getCommandUsage(ICommandSender ics)
		{ return '/' + commandName + " <player>"; }
		
		public Boolean getUsername(String[] args, int i)
		{ return (i == 0) ? Boolean.FALSE : null; }
		
		public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
		{
			checkArgs(args, 1);
			LMPlayerMP p = LMPlayerMP.get(args[0]);
			if(p.isOnline()) return error(new ChatComponentText("The player must be offline!"));
			LMWorldMP.inst.playerMap.remove(p.getProfile().getId());
			return new ChatComponentText("Player removed!");
		}
	}
}