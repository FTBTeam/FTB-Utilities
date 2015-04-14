package latmod.core.mod.cmd;

import java.io.*;

import latmod.core.*;
import latmod.core.cmd.CommandLevel;
import latmod.core.event.ReloadEvent;
import latmod.core.net.*;
import latmod.core.util.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import baubles.api.BaublesApi;
import cpw.mods.fml.relauncher.Side;

public class CmdLatCoreAdmin extends CommandBaseLC
{
	public static CommandLevel commandLevel = CommandLevel.OP;
	
	public CmdLatCoreAdmin()
	{ super("latcoreadmin", commandLevel); }
	
	public void printHelp(ICommandSender ics)
	{
	}
	
	public String[] getSubcommands(ICommandSender ics)
	{ return new String[] { "block", "gamerule", "player", "reload" }; }
	
	public String[] getTabStrings(ICommandSender ics, String args[], int i)
	{
		if(i == 0) return getSubcommands(ics);
		
		if(i == 2 && isArg(args, 0, "player"))
			return new String[] { "delete", "saveinv", "loadinv", "notify" };
		
		if(i == 1 && isArg(args, 0, "gamerule"))
			return LMGamerules.rules.keys.toArray(new String[0]);
		
		if(i == 1 && isArg(args, 0, "reload"))
			return new String[] { "all", "self", "none" };
		
		return super.getTabStrings(ics, args, i);
	}
	
	public NameType getUsername(String[] args, int i)
	{
		if(i == 1 && isArg(args, 0, "player"))
			return NameType.OFF;
		return NameType.NONE;
	}
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		if(args == null || args.length == 0)
			return "Subcommands: " + LatCore.strip(getTabStrings(ics, args, 0));
		
		if(args[0].equals("player"))
		{
			checkArgs(args, 2);
			
			String mustBeOnline = "The player must be online!";
			String mustBeOffline = "The player must be offline!";
			
			if(args[1].equals("@a"))
			{
				String[] s = LMPlayer.getAllNames(NameType.ON);
				
				for(int i = 0; i < s.length; i++)
				{
					String[] args1 = args.clone();
					args1[1] = s[i];
					onCommand(ics, args1);
				}
				
				return null;
			}
			
			LMPlayer p = getLMPlayer(args[1]);
			
			if(args[2].equals("delete"))
			{
				if(p.isOnline()) return mustBeOffline;
				LMPlayer.map.remove(p.playerID);
				return FINE + "Player removed!";
			}
			else if(args[2].equals("saveinv"))
			{
				if(!p.isOnline()) return mustBeOnline;
				
				try
				{
					EntityPlayerMP ep = p.getPlayerMP();
					NBTTagCompound tag = new NBTTagCompound();
					writeItemsToNBT(ep.inventory, tag, "Inventory");
					
					if(LatCoreMC.isModInstalled("Baubles"))
					{
						IInventory inv = BaublesApi.getBaubles(ep);
						if(inv != null) writeItemsToNBT(inv, tag, "Baubles");
					}
					
					NBTHelper.writeMap(new FileOutputStream(LatCore.newFile(new File(LatCoreMC.latmodFolder, "playerinvs/" + ep.getCommandSenderName() + ".dat"))), tag);
				}
				catch(Exception e)
				{
					if(LatCoreMC.isDevEnv) e.printStackTrace();
					return "Failed to save inventory!";
				}
				
				return FINE + "Inventory saved!";
			}
			else if(args[2].equals("loadinv"))
			{
				if(!p.isOnline()) return mustBeOnline;
				
				try
				{
					EntityPlayerMP ep = p.getPlayerMP();
					NBTTagCompound tag = NBTHelper.readMap(new FileInputStream(new File(LatCoreMC.latmodFolder, "playerinvs/" + ep.getCommandSenderName() + ".dat")));
					
					readItemsFromNBT(ep.inventory, tag, "Inventory");
					
					if(LatCoreMC.isModInstalled("Baubles"))
					{
						IInventory inv = BaublesApi.getBaubles(ep);
						if(inv != null) readItemsFromNBT(inv, tag, "Baubles");
					}
				}
				catch(Exception e)
				{
					if(LatCoreMC.isDevEnv) e.printStackTrace();
					return "Failed to load inventory!";
				}
				
				return FINE + "Inventory loaded!";
			}
			else if(args[2].equals("notify"))
			{
				if(!p.isOnline()) return "The player must be online!";
				checkArgsStrong(args, 5);
				
				ItemStack is = InvUtils.parseItem(args[3]);
				if(is == null || is.getItem() == null) return "Item '" + args[3] + "' not found!";
				
				LatCoreMC.notifyPlayer(p.getPlayerMP(), new Notification(args[4].replace("\\_", "<$US>").replace('_', ' ').replace("<$US>", "_"), "", is));
				return null;
			}
		}
		else if(args[0].equals("block"))
		{
			EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
			
			try
			{
				MovingObjectPosition mop = MathHelperLM.rayTrace(ep);
				String b = InvUtils.getRegName(ep.worldObj.getBlock(mop.blockX, mop.blockY, mop.blockZ));
				if(b == null) return "Unknown block!";
				
				int meta = ep.worldObj.getBlockMetadata(mop.blockX, mop.blockY, mop.blockZ);
				TileEntity te = ep.worldObj.getTileEntity(mop.blockX, mop.blockY, mop.blockZ);
				
				LatCoreMC.printChat(ep, b + (meta > 0 ? (";" +  meta) : "") + " @ " + LatCore.stripInt(mop.blockX, mop.blockY, mop.blockZ));
				if(te != null) LatCoreMC.printChat(ep, "Tile: " + LatCore.classpath(te.getClass()));
				
				return null;
			}
			catch(Exception e)
			{ return "Unknown block!"; }
		}
		else if(args[0].equals("gamerule"))
		{
			if(args.length >= 2)
			{
				if(args.length >= 3)
				{
					LMGamerules.set(args[1], args[2]);
					return FINE + "LMGamerule '" + args[1] + "' set to " + args[2];
				}
				else
				{
					LMGamerules.Rule r = LMGamerules.get(args[1]);
					if(r == null) return FINE + "LMGamerule '" + args[1] + "' does not exist";
					return FINE + "LMGamrule '" + args[1] + "' is '" + r.value + "'";
				}
			}
		}
		else if(args[0].equals("reload"))
		{
			new ReloadEvent(Side.SERVER, ics).post();
			MessageLM.NET.sendToAll(new MessageReload());
			return FINE + "LatvianModders's mods reloaded (Server)";
		}
		
		return onCommand(ics, null);
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
				tag1.setString("ID", InvUtils.getRegName(is.getItem()));
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
			NBTTagList list = tag.getTagList(s, NBTHelper.MAP);
			
			for(int i = 0; i < list.tagCount(); i++)
			{
				NBTTagCompound tag1 = list.getCompoundTagAt(i);
				Item item = InvUtils.getItemFromRegName(tag1.getString("ID"));
		        
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