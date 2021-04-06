package de.peacepunkt.tda2plugin;

import de.peacepunkt.tda2plugin.game.Broadcaster;
import de.peacepunkt.tda2plugin.game.command.EditCommands;
import de.peacepunkt.tda2plugin.game.Handlers.EditorHandler;
import de.peacepunkt.tda2plugin.game.command.ChatCommands;
import de.peacepunkt.tda2plugin.kits.ExtraCommands;
import de.peacepunkt.tda2plugin.kits.KitHandler;
import de.peacepunkt.tda2plugin.minipvp.MiniPvPHandler;
import de.peacepunkt.tda2plugin.pubsub.LivePublisher;
import de.peacepunkt.tda2plugin.structures.Cannon.CannonCommands;
import de.peacepunkt.tda2plugin.structures.Gate.GateCommands;
import de.peacepunkt.tda2plugin.persistence.*;
import de.peacepunkt.tda2plugin.stats.command.StatsCommands;
import de.peacepunkt.tda2plugin.structures.TopThing.TopThingCommands;
import de.peacepunkt.tda2plugin.vote.BasicVoteListener;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

import org.bukkit.generator.ChunkGenerator;

import de.peacepunkt.tda2plugin.game.RoundHandlerNew;
import de.peacepunkt.tda2plugin.game.command.RoundCommands;
import de.peacepunkt.tda2plugin.game.command.SpawnWorldGenerator;
import de.peacepunkt.tda2plugin.team.TeamCommands;
import de.peacepunkt.tda2plugin.structures.catapult.CatapultCommands;
import de.peacepunkt.tda2plugin.structures.flag.FlagCommands;


public class Main extends JavaPlugin implements Listener {
	public static int numOfAssists = 3;
	public static long flagCaptureSpeed = 100L;
	public static int maxAssistTime = 35; //seconds
	public static int numVoteMaps = 3;
	public static double roundLength = 30; //minutes
	public static String sqlUser;
	public static String sqlPassword;
	private static boolean unixSocketEnabled = false;
	public static int numberOfBirds;
	public static String discordLink = "https://discord.gg/rb7TG3n";
	public static String path;
	public static String arenasPath;
	RoundHandlerNew pr;
	MiniPvPHandler miniPvPHandler;

	public static String warning = null;
	public BasicVoteListener voteListener;

	@Override
	public void onEnable() {
		path = getDataFolder() + "/WorldInfos/";
		arenasPath = path + "Arenas/";

		MainHolder.getInstance().init(this);

		loadProperties();
		try {
			HibernateUtil.getSessionFactory();
		} catch (Exception e) {
			System.out.println("Can't establish connection to database");
			warning = ChatColor.RED+""+ChatColor.BOLD+"Warning: Currently stats won't be saved!";
		}
		//not necessary but then all kit classes have been found for later usage
		KitHandler.getInstance();

		makePluginfolders();
		//loadPermissions();
		this.miniPvPHandler = new MiniPvPHandler(this);
		getServer().getPluginManager().registerEvents(this.miniPvPHandler, this);
		new EditCommands();
		new RoundCommands(this);
        getServer().getPluginManager().registerEvents(new FlagCommands(this), this);
		new CatapultCommands(this);
		new TeamCommands(this);
		new StatsCommands(this);
		new TopThingCommands(this);
		new ExtraCommands(this);
		new BubiCommands(this);

		voteListener = new BasicVoteListener(this);
		getServer().getPluginManager().registerEvents(voteListener, this);
		getServer().getPluginManager().registerEvents(new ChatCommands(this), this);

		getServer().getPluginManager().registerEvents(new GateCommands(this), this);
		getServer().getPluginManager().registerEvents(new CannonCommands(this), this);
		getServer().getPluginManager().registerEvents(this, this);
		if(Main.unixSocketEnabled) {
			//getServer().getPluginManager().registerEvents(new PlayerPublisher(this), this);
			//getServer().getPluginManager().registerEvents(new MvpRoundPublisher(this), this);
			getServer().getPluginManager().registerEvents(new LivePublisher(this), this);
		}
		//getServer().getPluginManager().registerEvents(StatsService.getInstance(this), this);

		pr = new RoundHandlerNew(this);
		pr.start();
		String message = ChatColor.GREEN +"Want Sharpness or an additional heart? Do /vote and check out /recruitmentstats for more infos on how to get those perks >:D";
		new Broadcaster(this, message, 29*60*20, 60 * 20);
		new Broadcaster(this, ChatColor.GREEN + "Join our discord to find out when bigger rounds start: " + Main.discordLink, 20*60*20, 5*60*20);
		getServer().getPluginManager().registerEvents(pr, this);
	}


	private void loadProperties() {
		this.getConfig().options().copyDefaults(true);
		this.getConfig().addDefault("numOfAssists", 1);
		this.getConfig().addDefault("flagCaptureSpeed", 60);
		this.getConfig().addDefault("maxAssistTime", 25);
		this.getConfig().addDefault("roundLength", 30.0);
		this.getConfig().addDefault("unixSocket", false);
		this.getConfig().addDefault("birds", 150);
		this.getConfig().addDefault("numVoteMaps", 3);
		this.getConfig().addDefault("sqlPassword", "todo");
		this.getConfig().addDefault("sqlUser", "todo");
		this.getConfig().addDefault("discord", "https://discord.gg/rb7TG3n");
		this.saveConfig();

		Main.numOfAssists = this.getConfig().getInt("numOfAssists");
		Main.flagCaptureSpeed = this.getConfig().getLong("flagCaptureSpeed");
		Main.maxAssistTime = this.getConfig().getInt("maxAssistTime");
		Main.roundLength = this.getConfig().getDouble("roundLength");
		Main.unixSocketEnabled = this.getConfig().getBoolean("unixSocket");
		Main.numberOfBirds = this.getConfig().getInt("birds");
		Main.discordLink = this.getConfig().getString("discord");
		Main.numVoteMaps = this.getConfig().getInt("numVoteMaps");
		Main.sqlPassword = this.getConfig().getString("sqlPassword");
		Main.sqlUser = this.getConfig().getString("sqlUser");
		if(!Main.unixSocketEnabled) {
			System.out.println("Unix Sockets disabled. Data will not be published in /out!");
		}
	}
	private void dummyData() {
		PlayerStatsDaoImpl i = new PlayerStatsDaoImpl();
		i.add(new PlayerStats("uuid1", "sero"));
		PlayerStats full = new PlayerStats("uuid2", "dieannakonda");
		full.setRepairs(10);
		full.setKills(200);
		full.setDeaths(44);
		full.setCaptures(3010);
		full.setAssists(1111);
		MVP fullMVP = new MVP("uuid2", 102, 33, 108, 0, 0, 0);
		i.add(full);
		new MVPDaoImpl().add(fullMVP);
		fullMVP = new MVP("uuid2", 103, 33, 118, 0, 0, 111);
		new MVPDaoImpl().add(fullMVP);

	}

	public void skip(String mapName) {
		if(mapName == null || mapName.equals("")) {
			pr.skip();
		} else {
			pr.skipTo(mapName);
		}
	}
	@Override
	public void onDisable() {
		//savePermissions();
		getLogger().info("hello world disabled");
		EditorHandler.getInstance().removeAllBuilders();

	}

	public List<Player> getPlayersNotToTp() {
		List<Player> ret =  new ArrayList<>();
		ret.addAll(this.miniPvPHandler.getCurrentPlayers());
		ret.addAll(EditorHandler.getInstance().getAllPlayersInEditor());
		return ret;
	}

	private void makePluginfolders() {
		File f = new File(getDataFolder() + "/WorldInfos/");
		f.mkdirs();
	}
	
	/**
	 * Makes void Worlds when creating a new World
	 */
	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
	    return new SpawnWorldGenerator();
	}
	
	public RoundHandlerNew getRoundHandler() {
		return pr;
	}
	public MiniPvPHandler getMiniPvPHandler() {
		return this.miniPvPHandler;
	}


	@EventHandler
	public void onPlayerGameModeChangeEvent(PlayerGameModeChangeEvent event) {
		if(EditorHandler.getInstance().isPlayerInEditMode(event.getPlayer()) && !event.getPlayer().isOp()) {
			if (!(event.getNewGameMode().equals(GameMode.CREATIVE) || event.getNewGameMode().equals(GameMode.SPECTATOR))) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.GRAY + "You can only be in creative or spectator mode in the editor");
			}
		}
	}

	public boolean isPlayerInMiniArena(Player player) {
		return miniPvPHandler.contains(player);
	}
}


