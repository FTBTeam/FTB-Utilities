```
[player is default_player_rank] // Make this the default rank for OPs
ftbutilities.claims.block.edit.gravestone.gravestone: true // Allow all players to break gravestones
command.ftbutilties.home: false // Deny players from using homes
ftbutilities.claims.max_chunks: 30 // Set max claimable chunks to 30

[vip extends player] // Include all permissions and configs from player rank
ftbutilities.claims.max_chunks: 80 // Set max claimable chunks to 80, overrides player
ftbutilities.chunkloader.max_chunks: 40 // Set max loadable chunks to 80
ftbutilities.chat.prefix.right.text: "VIP" // Change prefix's right part text to VIP
ftbutilities.chat.prefix.right.color: "aqua" // Change prefix's right part color to aqua
ftbutilities.chat.name_format: "<&l{name}&r>" // Change name to bold letters

[admin extends vip is default_op_rank] // Include all permissions and configs from vip rank and make this the default rank for OPs
*: true // Give admins all permissions, but this isn't always a good idea. If you want to allow them to use all commands, use command: true
ftbutilities.chat.name_format: "<&2{name}&r>" // Name color will be dark green
```

