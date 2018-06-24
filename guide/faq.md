# How to allow player to use an OP command?
Add command permission node, like `comands.tp: true` under `[player]` (or any other) rank in `local/ranks.txt`.

# How to change max homes for players?
Same as allowing command, except use `ftbutilities.homes.max: 30` node. Change 30 to any number >= 0.

# How to change max claimable/loadable chunks for players?
Same as allowing command, except use `ftbutilities.claims.max_chunks : 30` and/or `ftbutilities.chunkloader.max_chunks: 30` nodes. Change 30 to any number >= 0.
