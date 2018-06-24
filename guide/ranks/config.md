# Enabling Ranks
They are enabled by default, but if they aren't, set `Enabled` & `Override Chat` to `true` under `FTBUtilities > Ranks` or `config/ftbutilities.cfg`.
You can find all permissions and configurations assigned to them in the `all_permissions.html` file.
While //Comments are used in examples, they won't work in actual files, and will be removed by mod.

# Permissions
To add permission to rank simply write the node below rank definition and add value after `: `

## Command Permissions
Command permissions are special and generated every time server loads. For example `/heal` permission would be `command.ftbutilities.heal`, because it was added by FTBUtilities mod and command name is heal. Tree commands are supported, e.g., `/ranks get LatvianModder` would be `command.ftbutilities.ranks.get`. Vanilla commands dont have mod name, so `/gamemode` would be `command.gamemode`

## Example:
```
[some_rank]
ftbutilities.claims.block.edit.minecraft.sand: true
ftbutilities.claims.block.edit.minecraft.stone: false
ftbutilities.homes.max: 10
command.ftbutilities.heal: true
```

Will allow the rank to break sand in claims and use `/heal`, and deny the rank to break stone in a claims.

# Inheritance
You can include all permissions from parent rank by adding ` extends parent_rank` inside `[rank]`. You can only extend one rank

# Default Rank Assignment
Default ranks aren't required, but it's still recommended to use them. Add ` is default_player_rank` or ` is default_op_rank` inside `[rank]`.
They basically tell what rank to use when player doesn't have any rank assigned.
You can have multiple tags, but those two are the only functioning ones, currently.

## Example
```
[player is default_player_rank] // This rank will be used for players that don't have a rank assigned
command.heal: true

[admin extends player is default_op_rank] // This rank will be used for OPs that don't have a rank assigned
command: true // Allows to use all commands
```


# Chat Formatting
To change player name formatting from default `<Player>` to something more interesting, use `ftbutilities.chat.name_format` permission
All permissions that you can use to modify chat message text:

* `ftbutilities.chat.text.color`
* `ftbutilities.chat.text.bold`
* `ftbutilities.chat.text.italic`
* `ftbutilities.chat.text.underlined`
* `ftbutilities.chat.text.strikethrough`
* `ftbutilities.chat.text.obfuscated`


## Example
```
[admin]
ftbutilities.chat.name_format: "<&2Admin &l{name}&r>"
ftbutilities.chat.text.italic: true
```

Text Admin be added before player name with a space and both that text and name will be dark green, but the player name will be bold
The result will be `<Admin LatvianModder> Hello!`, where:

* `Admin` and `LatvianModder` will be dark green;
* `LatvianModder` will be bold;
* `Hello!` will be italic.

