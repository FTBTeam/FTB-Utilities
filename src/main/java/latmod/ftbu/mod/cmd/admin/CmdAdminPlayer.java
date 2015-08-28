package latmod.ftbu.mod.cmd.admin;

import java.io.*;
import java.util.UUID;

import latmod.ftbu.core.*;
import latmod.ftbu.core.cmd.*;
import latmod.ftbu.core.inv.*;
import latmod.ftbu.core.util.*;
import latmod.ftbu.core.world.*;
import latmod.ftbu.mod.FTBUGuiHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.util.*;

import com.mojang.authlib.GameProfile;

public class CmdAdminPlayer extends SubCommand //TODO: Swap player and command
{
	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{
		CommandLM.checkArgs(args, 1);
		
		if(LatCoreMC.isDevEnv && args[0].equals("addfake"))
		{
			CommandLM.checkArgs(args, 3);
			
			UUID id = LatCoreMC.getUUIDFromString(args[1]);
			if(id == null) return CommandLM.error(new ChatComponentText("Invalid UUID!"));
			
			if(LMWorldServer.inst.getPlayer(id) != null || LMWorldServer.inst.getPlayer(args[2]) != null)
				return CommandLM.error(new ChatComponentText("Player already exists!"));
			
			LMPlayerServer p = new LMPlayerServer(LMWorldServer.inst, LMPlayerServer.nextPlayerID(), new GameProfile(id, args[2]));
			LMWorldServer.inst.players.add(p);
			p.updateLastSeen();
			
			return new ChatComponentText("Fake player " + args[2] + " added!");
		}
		
		IChatComponent mustBeOnline = CommandLM.error(new ChatComponentText("The player must be online!"));
		IChatComponent mustBeOffline = CommandLM.error(new ChatComponentText("The player must be offline!"));
		
		if(args[0].equals("@a"))
		{
			String[] s = LMWorldServer.inst.getAllPlayerNames(NameType.ON);
			
			for(int i = 0; i < s.length; i++)
			{
				String[] args1 = args.clone();
				args1[0] = s[i];
				onCommand(ics, args1);
			}
			
			return null;
		}
		
		CommandLM.checkArgs(args, 2);
		
		if(args[1].equals("delete"))
		{
			int playerID = CommandLM.parseInt(ics, args[0]);
			LMPlayer p = CommandLM.getLMPlayer(playerID);
			if(p.isOnline()) return mustBeOffline;
			LMWorldServer.inst.players.removeObj(playerID);
			return new ChatComponentText("Player removed!");//LANG
		}
		
		LMPlayerServer p = CommandLM.getLMPlayer(args[0]);
		
		if(args[1].equals("saveinv"))
		{
			if(!p.isOnline()) return mustBeOnline;
			
			try
			{
				EntityPlayerMP ep = p.getPlayer();
				NBTTagCompound tag = new NBTTagCompound();
				writeItemsToNBT(ep.inventory, tag, "Inventory");
				
				if(LatCoreMC.isModInstalled(OtherMods.BAUBLES))
				{
					IInventory inv = BaublesHelper.getBaubles(ep);
					if(inv != null) writeItemsToNBT(inv, tag, "Baubles");
				}
				
				String filename = ep.getCommandSenderName();
				if(args.length == 3) filename = "custom/" + args[2];
				LMNBTUtils.writeMap(new FileOutputStream(LMFileUtils.newFile(new File(LatCoreMC.latmodFolder, "playerinvs/" + filename + ".dat"))), tag);
			}
			catch(Exception e)
			{
				if(LatCoreMC.isDevEnv) e.printStackTrace();
				return CommandLM.error(new ChatComponentText("Failed to save inventory!"));
			}
			
			return new ChatComponentText("Inventory saved!");
		}
		else if(args[1].equals("loadinv"))
		{
			if(!p.isOnline()) return mustBeOnline;
			
			try
			{
				EntityPlayerMP ep = p.getPlayer();
				String filename = ep.getCommandSenderName();
				if(args.length == 3) filename = "custom/" + args[2];
				NBTTagCompound tag = LMNBTUtils.readMap(new FileInputStream(new File(LatCoreMC.latmodFolder, "playerinvs/" + filename + ".dat")));
				
				readItemsFromNBT(ep.inventory, tag, "Inventory");
				
				if(LatCoreMC.isModInstalled(OtherMods.BAUBLES))
				{
					IInventory inv = BaublesHelper.getBaubles(ep);
					if(inv != null) readItemsFromNBT(inv, tag, "Baubles");
				}
			}
			catch(Exception e)
			{
				if(LatCoreMC.isDevEnv) e.printStackTrace();
				return CommandLM.error(new ChatComponentText("Failed to load inventory!"));
			}
			
			return new ChatComponentText("Inventory loaded!");
		}
		else if(args[1].equals("notify"))
		{
			if(!p.isOnline()) return mustBeOnline;
			
			CommandLM.checkArgs(args, 3);
			
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
			
			return CommandLM.error(new ChatComponentText("Invalid notification: " + s));
		}
		else if(args[1].equals("displayitem"))
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
			
			return CommandLM.error(new ChatComponentText("Invalid item!"));
		}
		
		return null;
	}
	
	public String[] getTabStrings(ICommandSender ics, String args[], int i)
	{
		if(i == 1) return new String[] { "delete", "saveinv", "loadinv", "notify", "displayitem" };
		return null;
	}
	
	public NameType getUsername(String[] args, int i)
	{
		if(i == 0) return NameType.OFF;
		return NameType.NONE;
	}
	
	private static void writeItemsToNBT(IInventory inv, NBTTagCompound tag, String s)
	{
		NBTTagList list = new NBTTagList();
		
		for(int i = 0; i < inv.getSizeInventory(); i++)
		{
			ItemStack is = inv.getStackInSlot(i);
			
			if(is != null)
			{
				NBTTagCompound tag1 = new NBTTagCompound();
				tag1.setShort("S", (short)i);
				tag1.setString("ID", LMInvUtils.getRegName(is.getItem()));
		        tag1.setByte("C", (byte)is.stackSize);
		        tag1.setShort("D", (short)is.getItemDamage());
		        if (is.stackTagCompound != null) tag1.setTag("T", is.stackTagCompound);
				list.appendTag(tag1);
			}
			
		}
		
		if(list.tagCount() > 0) tag.setTag(s, list);
	}
	
	private static void readItemsFromNBT(IInventory inv, NBTTagCompound tag, String s)
	{
		for(int i = 0; i < inv.getSizeInventory(); i++)
			inv.setInventorySlotContents(i, null);
		
		if(tag.hasKey(s))
		{
			NBTTagList list = tag.getTagList(s, LMNBTUtils.MAP);
			
			for(int i = 0; i < list.tagCount(); i++)
			{
				NBTTagCompound tag1 = list.getCompoundTagAt(i);
				Item item = LMInvUtils.getItemFromRegName(tag1.getString("ID"));
		        
		        if(item != null)
		        {
		        	int slot = tag1.getShort("S");
		        	int size = tag1.getByte("C");
		        	int dmg = Math.max(0, tag1.getShort("D"));
		        	ItemStack is = new ItemStack(item, size, dmg);
		        	if(tag1.hasKey("T", 10)) is.setTagCompound(tag1.getCompoundTag("T"));
		        	inv.setInventorySlotContents(slot, is);
		        }
		        
				if(i >= inv.getSizeInventory()) break;
			}
		}
		
		inv.markDirty();
	}
}