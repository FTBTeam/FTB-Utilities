package latmod.ftbu.mod.cmd.admin;

import java.io.*;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import latmod.ftbu.cmd.*;
import latmod.ftbu.inv.*;
import latmod.ftbu.mod.*;
import latmod.ftbu.notification.Notification;
import latmod.ftbu.util.*;
import latmod.ftbu.world.*;
import latmod.lib.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;

public class CmdAdminPlayer extends CommandLM
{
	public CmdAdminPlayer(String s)
	{ super(s, CommandLevel.OP); }
	
	public String[] getTabStrings(ICommandSender ics, String args[], int i)
	{
		if(i == 0) return new String[] { "delete", "saveinv", "loadinv", "notify", "displayitem" };
		return null;
	}
	
	public NameType getUsername(String[] args, int i)
	{
		if(i == 1) return NameType.OFF;
		return NameType.NONE;
	}
	
	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{
		checkArgs(args, 1);
		
		if(FTBUFinals.DEV && args[0].equals("addfake"))
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
			String[] s = LMWorldServer.inst.getAllPlayerNames(NameType.ON);
			
			for(int i = 0; i < s.length; i++)
			{
				String[] args1 = args.clone();
				args1[1] = s[i];
				onCommand(ics, args1);
			}
			
			return null;
		}
		
		checkArgs(args, 2);
		
		if(args[0].equals("delete"))
		{
			int playerID = parseInt(ics, args[1]);
			LMPlayer p = getLMPlayer(playerID);
			if(p.isOnline()) return mustBeOffline;
			LMWorldServer.inst.players.removeObj(playerID);
			return new ChatComponentText("Player removed!");//LANG
		}
		
		LMPlayerServer p = getLMPlayer(args[1]);
		
		if(args[0].equals("saveinv"))
		{
			if(!p.isOnline()) return mustBeOnline;
			
			try
			{
				EntityPlayerMP ep = p.getPlayer();
				NBTTagCompound tag = new NBTTagCompound();
				StringIDInvLoader.writeInvToNBT(ep.inventory, tag, "Inventory");
				
				if(LatCoreMC.isModInstalled(OtherMods.BAUBLES))
					StringIDInvLoader.writeInvToNBT(BaublesHelper.getBaubles(ep), tag, "Baubles");
				
				String filename = ep.getCommandSenderName();
				if(args.length == 3) filename = "custom/" + args[2];
				LMNBTUtils.writeMap(new FileOutputStream(LMFileUtils.newFile(new File(LatCoreMC.localFolder, "playerinvs/" + filename + ".dat"))), tag);
			}
			catch(Exception e)
			{
				if(FTBUFinals.DEV) e.printStackTrace();
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
				NBTTagCompound tag = LMNBTUtils.readMap(new FileInputStream(new File(LatCoreMC.localFolder, "playerinvs/" + filename + ".dat")));
				
				StringIDInvLoader.readInvFromNBT(ep.inventory, tag, "Inventory");
				
				if(LatCoreMC.isModInstalled(OtherMods.BAUBLES))
					StringIDInvLoader.readInvFromNBT(BaublesHelper.getBaubles(ep), tag, "Baubles");
			}
			catch(Exception e)
			{
				if(FTBUFinals.DEV) e.printStackTrace();
				return error(new ChatComponentText("Failed to load inventory!"));
			}
			
			return new ChatComponentText("Inventory loaded!");
		}
		else if(args[0].equals("notify"))
		{
			if(!p.isOnline()) return mustBeOnline;
			
			checkArgs(args, 3);
			
			String s = LMStringUtils.unsplitSpaceUntilEnd(2, args);
			
			try
			{
				Notification n = Notification.fromJson(s);
				
				if(n != null)
				{
					LatCoreMC.notifyPlayer(p.getPlayer(), n);
					return null;
				}
			}
			catch(Exception e)
			{ e.printStackTrace(); }
			
			return error(new ChatComponentText("Invalid notification: " + s));
		}
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
		}
		
		return null;
	}
}