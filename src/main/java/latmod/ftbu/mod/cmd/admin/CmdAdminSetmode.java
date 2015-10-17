package latmod.ftbu.mod.cmd.admin;

import java.io.File;
import java.util.*;

import latmod.ftbu.cmd.*;
import latmod.ftbu.util.*;
import latmod.ftbu.world.LMWorldServer;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.*;

public class CmdAdminSetmode extends CommandLM
{
	public CmdAdminSetmode(String s)
	{ super(s, CommandLevel.OP); }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{
		if(args.length == 0)
		{
			String[] listS = new File(LatCoreMC.modpackFolder, "gamemodes").list();
			
			if(listS == null || listS.length == 0)
				return new ChatComponentText("Packs found: 0");
			
			Arrays.sort(listS);
			ArrayList<String> list = new ArrayList<String>();
			for(int i = 0; i < listS.length; i++)
				list.add(listS[i]);
			
			list.remove("null");
			list.remove("common");
			
			ics.addChatMessage(new ChatComponentText("Packs found: " + list.size()));
			for(int i = 0; i < list.size(); i++)
				ics.addChatMessage(new ChatComponentText("/" + list.get(i)));
			return null;
		}
		
		if(args[0].equalsIgnoreCase("common") || args[0].equalsIgnoreCase("null"))
			throw new IllegalArgumentException("Mode ID can't be " + args[0]);
		
		if(!new File(LatCoreMC.modpackFolder, "gamemodes/" + args[0]).exists())
			return new ChatComponentText("Invalid gamemode: " + args[0]);
		
		LMWorldServer.inst.jsonSettings.gamemode = args[0];
		LMWorldServer.inst.update();
		
		LatCoreMC.printChat(BroadcastSender.inst, "Gamemode changed to: " + args[0]);
		CmdAdminReload.reload(ics);
		return null;
	}
}