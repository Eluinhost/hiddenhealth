Hidden Health
=============

Bukkit plugin that hides players health and/or hunger information from them

Requires ProtocolLib to be installed to run correctly.

## Configuration

`hide health` If true, hides health values, false acts normal  
`hide hunger` If true, hides hunger/saturation values, false acts normal  
`show health value` float, the amount of health to show the player  
`show hunger value` int, the amount of hunger to show the player  
`show saturation value` float, the amount of saturation to send the player  

## Commands

No commands.

## Known Problems

After configuration changes and a server reload player displayed values will stay the same as
the old configuration until either their health and/or food updates.