# Terminology

# Ranks
Instead of assigning permissions to every user individually, we have groups of permissions, which can then be assigned to a user as a whole.
For example, in my `admin` rank, I might add permission to ignore claims, and then assign users to the admin rank. This means that they will get all of the permissions from `admin`, plus any they have themselves.

# Inheritance
Users and groups are able to inherit permissions from each other. For example, by default, all users inherit permissions from the `player` rank. You can setup your own groups an inheritances for your server, and make your own unique system.
For example, I might have 3 groups, `player`, `admin` and `vip`. I want `vip` to inherit permissions from `player`, and `admin` to inherit permissions from from the `vip` rank.

# Permission
On your server, there will be a number of features, commands, and functionality which is added to the game. Most of these actions have a permission associated with them, so you can control which users have access to each feature or command.
A permission is just a string, and is separated into parts using periods. For example, `ftbutilities.back.infinite` is the permission to use the `/back` command infinitely. Obviously we don’t want all users to have access to this, so we only give it to some users.
The string that represents a certain permission is also sometimes called a "permission node" or just "node" for short.
