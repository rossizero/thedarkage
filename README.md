# The battle for conwy castle - A Bukkit Plugin

This remake was initiated by player sero97. The idea of the minigame came from player rockslide that hosted the first server for many years.

## Requirements
This plugin works for minecraft version 1.16.5. 
To use a Paper Spigot Server is recommended (https://papermc.io/)
Spigot or Bukkit should also work though.

### Plugins
To put all of those plugins into the "plugin" folder of your server should do the trick.
Following plugins are required:
* WorldEdit https://dev.bukkit.org/projects/worldedit
* NuVotifier https://www.spigotmc.org/resources/nuvotifier.13449/
* OldCombatMechanics https://dev.bukkit.org/projects/oldcombatmechanics

Following plugins are recommended:
* AsyncWorldEdit https://www.spigotmc.org/resources/asyncworldedit.327/

## Setup
Build your TDA2Plugins.jar (For Intellij read this: https://www.jetbrains.com/help/idea/working-with-artifacts.html) and put it into the plugins folder of your server. Restart the server (never reload since that will break it). Now a TDA2Plugins folder should exist containing a config.yml file. This is a sample config file:
```
sqlPassword: <your password>
sqlUser: <your sql user>
numVoteMaps: 3
discord: <your own permanent discord server invite>
birds: 50
unixSocket: false
maxAssistTime: 35
roundLength: 30
flagCaptureRange: 8.0
flagCaptureSpeed: 60
numOfAssists: 7
cataStrength: 2.5
```
* **sqlUser** and sqlPassword: are the credentials for your mysql database. It needs the right to create/update/drop tables.
* **numVoteMaps**: Numver of maps players can vote for at the end of a roundLength
* **discord**: every 20 mins that link will be posted into the chat
* **birds**: Would tell the number of birds in the flock for the chaos kit. Temporary diabled because of lags though (it's real shitty code)
* **unixSocket**: We startet to build a website displaying live data of the game. Ignore this field and let it be false (if it exists)
* **maxAssistTime**: After what period of time a players assist is invalid (in seconds)
* **roundLength**: how long a map should last. 
* **flagCaptureRange**: this number can be overridden by individual flags
* **flagCaptureSpeed**: ticks between a flag update 
* **numOfAssists**: maximum number of players getting an assist of a killed player 
* **cataStrength**: default value, can be set for individual maps too.

Create a new map by typing `/makenewarena world_name <number of teams>`. There should be 1 + `<number of teams>` new worlds popping up now. One world with the name world_name (the arena) and `<number of teams>` many worlds with the name world_nameSpawnX. Those are the spawn rooms for your new arena. Never ever rename them. You can do /edit enter world_name then to go there. In the TDA2Plugins folder at your plugins there is now a WorldInfos folder that contains a folder for the created arena. Do not edit anything inside the `new` folder unless you know what you're doing. You can edit the settings for the arena though. This is the sample settings file for the Conwy arena:
```
timeOfDay: '1000'
storm: 'false'
thunder: 'false'
defaultKit: Swordsman
displayName: Conwy Castle
staged: true
cataStrength: 3.3
```
You need at least one staged map with at least one flag per team on it. You can add flags and other structures with ingame commands. Look at the plugin.yml for more infos.

## More stuff
Here is a link to some finished maps (including the settings files for those maps):  https://www.mediafire.com/folder/db3ztajpxn6lg/thedarkage3
Just make a new arena (for example `/makenewarena conwy 2`) with **exactly** the same name as the world folder of the map you want to import and replace the folders with the downloaded ones. Then place the data files into the WorldInfos folder of the TDA2Plugins folder and restart the server.
