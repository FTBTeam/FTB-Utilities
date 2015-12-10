package latmod.ftbu.mod.cmd.admin;

import java.io.*;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import ftb.lib.*;
import ftb.lib.cmd.*;
import ftb.lib.item.StringIDInvLoader;
import ftb.lib.mod.FTBLibFinals;
import latmod.ftbu.world.*;
import latmod.lib.*;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;

public class CmdPlayerLM extends CommandLM
{
	public CmdPlayerLM()
	{ super("player_lm", CommandLevel.OP); }
	
	public String[] getTabStrings(ICommandSender ics, String args[], int i) throws CommandException
	{
		if(i == 0) return new String[] { "delete", "saveinv", "loadinv", /*"displayitem"*/ };
		return super.getTabStrings(ics, args, i);
	}
	
	public Boolean getUsername(String[] args, int i)
	{ return (i == 1) ? Boolean.FALSE : null; }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
	{
		checkArgs(args, 1);
		
		if(FTBLibFinals.DEV && args[0].equals("addfake"))
		{
			checkArgs(args, 3);
			
			UUID id = LMStringUtils.fromString(args[1]);
			if(id == null) return error(new ChatComponentText("Invalid UUID!"));
			
			if(LMWorldServer.inst.getPlayer(id) != null || LMWorldServer.inst.getPlayer(args[2]) != null)
				return error(new ChatComponentText("Player already exists!"));
			
			LMPlayerServer p = new LMPlayerServer(LMWorldServer.inst, LMPlayerServer.nextPlayerID(), new GameProfile(id, args[2]));
			LMWorldServer.inst.players.add(p);
			p.refreshStats();
			
			return new ChatComponentText("Fake player " + args[2] + " added!");
		}
		
		IChatComponent mustBeOnline = error(new ChatComponentText("The player must be online!"));
		IChatComponent mustBeOffline = error(new ChatComponentText("The player must be offline!"));
		
		if(args[1].equals("@a"))
		{
			FastList<String> s = LMWorldServer.inst.getAllPlayerNames(Boolean.TRUE);
			
			for(int i = 0; i < s.size(); i++)
			{
				String[] args1 = args.clone();
				args1[1] = s.get(i);
				onCommand(ics, args1);
			}
			
			return null;
		}
		
		checkArgs(args, 2);
		LMPlayerServer p = LMPlayerServer.get(args[1]);
		
		if(args[0].equals("delete"))
		{
			int playerID = parseInt(ics, args[1]);
			if(p.isOnline()) return mustBeOffline;
			LMWorldServer.inst.players.remove((Object)Integer.valueOf(playerID));
			return new ChatComponentText("Player removed!");
		}
		
		if(args[0].equals("saveinv"))
		{
			if(!p.isOnline()) return mustBeOnline;
			
			try
			{
				EntityPlayerMP ep = p.getPlayer();
				NBTTagCompound tag = new NBTTagCompound();
				StringIDInvLoader.writeInvToNBT(ep.inventory, tag, "Inventory");
				
				if(FTBLib.isModInstalled(OtherMods.BAUBLES))
					StringIDInvLoader.writeInvToNBT(BaublesHelper.getBaubles(ep), tag, "Baubles");
				
				String filename = ep.getCommandSenderName();
				if(args.length == 3) filename = "custom/" + args[2];
				LMNBTUtils.writeMap(new FileOutputStream(LMFileUtils.newFile(new File(FTBLib.folderLocal, "ftbu/playerinvs/" + filename + ".dat"))), tag);
			}
			catch(Exception e)
			{
				if(FTBLibFinals.DEV) e.printStackTrace();
				return error(new ChatComponentText("Failed to save inventory!"));
			}
			
			return new ChatComponentText("Inventory saved!");
		}
		else if(args[0].equals("loadinv"))
		{
			if(!p.isOnline()) return mustBeOnline;
			
			try
			{
				EntityPlayerMP ep = p.getPlayer();
				String filename = ep.getCommandSenderName();
				if(args.length == 3) filename = "custom/" + args[2];
				NBTTagCompound tag = LMNBTUtils.readMap(new FileInputStream(new File(FTBLib.folderLocal, "ftbu/playerinvs/" + filename + ".dat")));
				
				StringIDInvLoader.readInvFromNBT(ep.inventory, tag, "Inventory");
				
				if(FTBLib.isModInstalled(OtherMods.BAUBLES))
					StringIDInvLoader.readInvFromNBT(BaublesHelper.getBaubles(ep), tag, "Baubles");
			}
			catch(Exception e)
			{
				if(FTBLibFinals.DEV) e.printStackTrace();
				return error(new ChatComponentText("Failed to load inventory!"));
			}
			
			return new ChatComponentText("Inventory loaded!");
		}
		/* TODO: Reimplement me
		else if(args[0].equals("displayitem"))
		{
			if(!p.isOnline()) return mustBeOnline;
			
			ItemStack is = p.getPlayer().inventory.getCurrentItem();
			
			if(p.getPlayer().inventory.getCurrentItem() != null)
			{
				ItemDisplay itemDisplay = new ItemDisplay(is, is.getDisplayName(), is.hasDisplayName() ? FastList.asList(is.getItem().getItemStackDisplayName(is)) : null, 8F);
				NBTTagCompound data = new NBTTagCompound();
				itemDisplay.writeToNBT(data);
				FTBUGuiHandler.instance.openGui(p.getPlayer(), FTBUGuiHandler.DISPLAY_ITEM, data);
				return null;
			}
			
			return error(new ChatComponentText("Invalid item!"));
		}*/
		
		return null;
	}
}