package latmod.ftbu.net;

import ftb.lib.*;
import ftb.lib.api.config.ConfigRegistry;
import ftb.lib.mod.FTBLibFinals;
import ftb.lib.mod.net.MessageEditConfig;
import latmod.ftbu.api.guide.ServerGuideFile;
import latmod.ftbu.badges.ServerBadges;
import latmod.ftbu.util.*;
import latmod.ftbu.world.*;
import latmod.lib.config.*;

public enum ClientAction
{
	NULL
			{
				public boolean onAction(int extra, LMPlayerServer owner)
				{ return false; }
			},
	
	ADD_FRIEND
			{
				public boolean onAction(int extra, LMPlayerServer owner)
				{
					if(extra > 0)
					{
						LMPlayerServer p = owner.world.getPlayer(extra);
						if(p == null || p.equalsPlayer(owner)) return false;
						
						if(!owner.friends.contains(p.playerID))
						{
							owner.friends.add(p.playerID);
							owner.sendUpdate();
							p.sendUpdate();
							p.checkNewFriends();
						}
					}
					else
					{
						for(LMPlayerServer p : owner.world.playerMap.values())
						{
							if(!p.equalsPlayer(owner) && p.isFriendRaw(owner) && !owner.isFriendRaw(p))
							{
								owner.friends.add(p.playerID);
								owner.sendUpdate();
								p.sendUpdate();
								p.checkNewFriends();
							}
						}
					}
					
					return true;
				}
			},
	
	REM_FRIEND
			{
				public boolean onAction(int extra, LMPlayerServer owner)
				{
					LMPlayerServer p = owner.world.getPlayer(extra);
					if(p == null || p.equalsPlayer(owner)) return false;
					
					if(owner.friends.contains(p.playerID))
					{
						owner.friends.removeValue(p.playerID);
						owner.sendUpdate();
						p.sendUpdate();
						p.checkNewFriends();
					}
					
					return true;
				}
			},
	
	DENY_FRIEND
			{
				public boolean onAction(int extra, LMPlayerServer owner)
				{
					LMPlayerServer p = owner.world.getPlayer(extra);
					if(p == null || p.equalsPlayer(owner)) return false;
					
					if(p.friends.contains(owner.playerID))
					{
						p.friends.removeValue(owner.playerID);
						owner.sendUpdate();
						p.sendUpdate();
					}
					
					return true;
				}
			},
	
	REQUEST_PLAYER_INFO
			{
				public boolean onAction(int extra, LMPlayerServer owner)
				{
					new MessageLMPlayerInfo(owner, extra).sendTo(owner.getPlayer());
					return false;
				}
			},
	
	REQUEST_SERVER_INFO
			{
				public boolean onAction(int extra, LMPlayerServer owner)
				{
					LatCoreMC.displayGuide(owner.getPlayer(), new ServerGuideFile(owner));
					return false;
				}
			},
	
	REQUEST_SELF_UPDATE
			{
				public boolean onAction(int extra, LMPlayerServer owner)
				{
					new MessageLMPlayerUpdate(owner, true).sendTo(owner.getPlayer());
					return false;
				}
			},
	
	REQUEST_BADGE
			{
				public boolean onAction(int extra, LMPlayerServer owner)
				{
					new MessageSendBadge(extra, ServerBadges.getServerBadge(owner.world.getPlayer(extra)).ID).sendTo(owner.getPlayer());
					return false;
				}
			},
	
	BUTTON_RENDER_BADGE
			{
				public boolean onAction(int extra, LMPlayerServer owner)
				{
					owner.renderBadge = extra == 1;
					return true;
				}
			},
	
	BUTTON_CHAT_LINKS
			{
				public boolean onAction(int extra, LMPlayerServer owner)
				{
					owner.getSettings().chatLinks = extra == 1;
					return true;
				}
			},
	
	BUTTON_CLAIMED_CHUNKS_SETTINGS
			{
				public boolean onAction(int extra, final LMPlayerServer owner)
				{
					final PersonalSettings settings = LMWorldClient.inst.clientPlayer.getSettings();
					final ConfigGroup group = new ConfigGroup("claims_config_" + owner.getName())
					{
						public void onLoadedFromGroup(ConfigGroup g)
						{
							owner.sendUpdate();
							if(FTBLibFinals.DEV) FTBLib.dev_logger.info("claimed chunks settings loaded from " + g);
						}
					};
					
					group.add(new ConfigEntryBool("explosions", settings.explosions)
					{
						public boolean get()
						{ return settings.explosions; }
						
						public void set(boolean v)
						{ settings.explosions = v; }
					}, false);
					
					group.add(new ConfigEntryEnum<LMSecurityLevel>("security_level", LMSecurityLevel.class, LMSecurityLevel.VALUES_3, settings.blocks, false)
					{
						public LMSecurityLevel get()
						{ return settings.blocks; }
						
						public void set(Object v)
						{
							settings.blocks = (LMSecurityLevel) v;
							owner.sendUpdate();
						}
					}, false);
					
					group.add(new ConfigEntryBool("fake_players", settings.fakePlayers)
					{
						public boolean get()
						{ return settings.fakePlayers; }
						
						public void set(boolean v)
						{
							settings.fakePlayers = v;
							owner.sendUpdate();
						}
					}, false);
					
					ConfigRegistry.Provider provider = new ConfigRegistry.Provider()
					{
						public String getID()
						{ return group.ID; }
						
						public ConfigGroup getGroup()
						{ return group; }
					};
					
					ConfigRegistry.tempMap.put(provider.getID(), provider);
					new MessageEditConfig(LMAccessToken.generate(owner.getPlayer()), true, provider).sendTo(owner.getPlayer());
					return false;
				}
			},

	/*
	MISC
	{
		public boolean onAction(int extra, LMPlayerServer owner)
		{
			return false;
		}
	},
	*/

	/*
	MISC
	{
		public boolean onAction(int extra, LMPlayerServer owner)
		{
			return false;
		}
	},
	*/

	/*
	MISC
	{
		public boolean onAction(int extra, LMPlayerServer owner)
		{
			return false;
		}
	},
	*/;
	public static final ClientAction[] VALUES = values();
	public final byte ID;
	
	ClientAction()
	{ ID = (byte) ordinal(); }
	
	public abstract boolean onAction(int extra, LMPlayerServer owner);
	
	public void send(int extra)
	{ new MessageClientAction(this, extra).sendToServer(); }
}
