ARCHIVED. No longer being worked on as of 24/09/2023.

# The Emerald Lasso
A reimagining of the Golden Lasso from Extra Utils 2 and other similar items which allow storing entities in an time as a standalone mod from scratch.

## Why did I make this mod?
Now I know that there are many other great mods that add similar items and have much more involved design for catching entities, such as supplementaries or Apotheosis. So why did I make this?
Well, the answer is really two fold. The first is just that I love programming and developing things, and I wanted to have some fun making my own mod for a change. The second is that I have been playing a lot
of modded MC again, and various different modpacks. I couldn't always rely on there being a mod that does this, or that I wanted all of the features of those mods. So, I thought
why not just make my own? That lets me have full control over what it can and can't do. Now, taking those things. I thought why not share it with all the lovely people
who play. Now of course, there are probably much more polished mods out there that achieve the same goal and I ask that you take this for what it is. I won't be upset if you pick something much
cooler than what I have made! :)

## Current Features/Config Options

### For Latest Version 1.1.0

By default the lasso can pick up all entities, you can change this in the config. For more details see below.

Note: AS ALWAYS WITH MINECRAFT MODS PLEASE BACK UP YOUR WORLD BEFORE UPDATING THIS. IT IS SO EASY TO BREAK THINGS SO IF YOU HAVE A WORLD THAT YOU HAVE PLAYED FOR A LONG TIME I WOULD SUGGEST HOLDING OFF FOR NOW.

Also if you're wondering where [insert new minecraft game version here] is. I am probably in the process of updating, so just wait a little while.

You'll find a common config file when you add this mod that contains the following options:

1. num_entities_allowed - Lets you control how many entities the lasso can store. You can toggle this to between 1 and 5. 1 is the default
2. has_durability - Lets you control whether the lasso should have a durability or not. This is set to true by default. Durability is 250.
3. allow_all_entities - This config option is used to determine whether you want to use the entity whitelist or just let all entities (not including boss entities of course) be stored in the lasso. I.E ignore the whitelist. This is set to true by default, if you want to use the whitelist set this to false.
4. entity_health_system - Lets you decide whether an entity must be dropped to a certain health before being able to be stored or not. This is set to false by default, so if you want to use this system set it to true.
5. min_entity_health - Used for the above entity health system. This is the minimum health you must lower an entity to before it can be stored. You must set 6. entity_health_system to true or this will be completely ignored. Range is between 1.5 and 500.
7. vanilla_entity_whitelist - Lets you control what entities the lasso is able to pick up. Note this must have at least one value. There are some added by default to guide you with how to add entities. To add to this, you should follow the pattern of using the resource location. Which is usually id:name. For example if you want to add the wandering trader, you would add "minecraft:wandering_trader" to the list. Note that you must set allow_all_entities to false or else both whitelists will be completely ignored.
8. mod_entity_whitelist - The same as above, but for entities added by mods. You can actually put these in either but I made this so that you can keep them separate. You should follow the same pattern as above. If you're unsure about a mods id. The quickest way to find it is to use the /give command in game. If you search for an item in the mod, it will display what the id is. I.E /give EmpressValla itemname.

### For Version 1.0.3 and Below

You'll find a common config file when you add this mod that contains the following options:

1. num_entities_allowed - Lets you control how many entities the lasso can store. You can toggle this to between 1 and 5. 1 is the default
2. has_durability - Lets you control whether the lasso should have a durability or not. This is set to true by default. Durability is 250.
3. vanilla_entity_whitelist - Lets you control what entities the lasso is able to pick up. Note this must have at least one value. By default most of the animals and regular villagers are allowed. To add to this, you should follow the pattern of using the resource location. Which is usually id:name. For example if you want to add the wandering trader, you would add "minecraft:wandering_trader" to the list.
4. mod_entity_whitelist - The same as above, but for entities added by mods. You can actually put these in either but I made this so that you can keep them separate. You should follow the same pattern as above. If you're unsure about a mods id. The quickest way to find it is to use the /give command in game. If you search for an item in the mod, it will display what the id is. I.E /give EmpressValla itemname

## Offering Help/Suggestions & Reporting Issues
I am always open to help or suggestions. Although I am not new to writing in Java, I am new to making mods so there are likely some mistakes or potential bugs
that I have not caught. If possible, please use the issue tab here for reporting a bug or something not working correctly. It's much easier for me to filter through
than comments. Please try and be as detailed as possible, if it's an issue with picking up an entity let me know what mod it is from and what the entity is. If it's a crash
let me know the conditions that caused the crash. That way it's much easier for me to try and reproduce it in testing. You can use the issue tab for suggesting
new features as well, just tag them with something like feature so I can filter them.

**BronyPie is a good friend of mine and made a more lasso like texture for the mod so his paypal is linked below in the socials as well if you'd like to help out.**

## Pull Requests/Contributing
I am totally fine with people submitting a pull request on here for an improvement or otherwise. Just like for offering help if you are going to submit a PR
please be as detailed as possible so I know what's going on. It would be even better if you shoot me a friend request on discord, so that I can chat more 
readily about. Of course give me time to look through things. I'm not a wizard..yet anyway.

## Socials
- Discord: EmpressValla#2365
- Email: hyperconix@pm.me
- Paypal: [My Paypal](https://www.paypal.com/paypalme/IWuvStarlight)
- BronyPie's Paypal: [Here](https://www.paypal.com/paypalme/BronyPie)

## License
I will be keeping this repo and source code MIT. So you're free to use it however you wish. At the moment I'm still using a texture which I made myself so you're free
to use that as well. If that changes, you may need to add an additional license for the texture but I will update the readme if that is the case.
