package de.peacepunkt.tda2plugin.team;

import org.bukkit.ChatColor;
import org.bukkit.Material;


public class DefaultTeemThemes {

	public Material TeamMaterial;
	public Material SecondTeamMaterial;
	public ChatColor colorCode;
	
	public static DefaultTeemThemes getDefaultTeemTheme(int id) {
		switch(id) {
		case 0:
			return DefaultTeemThemes.redTeam();
		case 1:
			return DefaultTeemThemes.blueTeam();
		case 2:
			return DefaultTeemThemes.greenTeam();
		case 3:
			return DefaultTeemThemes.yellowTeam();
		case 4:
			return DefaultTeemThemes.aquaTeam();
		case 5:
			return DefaultTeemThemes.orangeTeam();
		case 6:
			return DefaultTeemThemes.blackTeam();
		case 7:
			return DefaultTeemThemes.darkGreenTeam();
		case 8:
			return DefaultTeemThemes.whiteTeam();
		case 9:
			return DefaultTeemThemes.limeTeam();
		case 10:
			return DefaultTeemThemes.purpleTeam();
		default:
			return DefaultTeemThemes.neutralTeam();
		}
	}


	private static DefaultTeemThemes purpleTeam() {
		DefaultTeemThemes ret = new DefaultTeemThemes();
		ret.TeamMaterial = Material.PURPLE_WOOL;
		ret.SecondTeamMaterial = Material.MAGENTA_WOOL;
		ret.colorCode = ChatColor.LIGHT_PURPLE;
		return ret;
	}
	private static DefaultTeemThemes redTeam() {
		DefaultTeemThemes ret = new DefaultTeemThemes();
		ret.TeamMaterial = Material.RED_WOOL;
		ret.SecondTeamMaterial = Material.PINK_WOOL;
		ret.colorCode = ChatColor.RED;
		return ret;
	}
	private static DefaultTeemThemes blueTeam() {
		DefaultTeemThemes ret = new DefaultTeemThemes();
		ret.TeamMaterial = Material.BLUE_WOOL;
		ret.SecondTeamMaterial = Material.LIGHT_BLUE_WOOL;
		ret.colorCode = ChatColor.BLUE;
		return ret;
	}
	private static DefaultTeemThemes greenTeam() {
		DefaultTeemThemes ret = new DefaultTeemThemes();
		ret.TeamMaterial = Material.LIME_WOOL;
		ret.SecondTeamMaterial = Material.GREEN_WOOL;
		ret.colorCode = ChatColor.GREEN;
		return ret;
	}
	private static DefaultTeemThemes yellowTeam() {
		DefaultTeemThemes ret = new DefaultTeemThemes();
		ret.TeamMaterial = Material.YELLOW_WOOL;
		ret.SecondTeamMaterial = Material.WHITE_WOOL;
		ret.colorCode = ChatColor.YELLOW;
		return ret;
	}
	private static DefaultTeemThemes aquaTeam() {
		DefaultTeemThemes ret = new DefaultTeemThemes();
		ret.TeamMaterial = Material.CYAN_WOOL;
		ret.SecondTeamMaterial = Material.BLUE_WOOL;
		ret.colorCode = ChatColor.AQUA;
		return ret;
	}

	private static DefaultTeemThemes orangeTeam() {
		DefaultTeemThemes ret = new DefaultTeemThemes();
		ret.TeamMaterial = Material.ORANGE_WOOL;
		ret.SecondTeamMaterial = Material.YELLOW_WOOL;
		ret.colorCode = ChatColor.YELLOW;
		return ret;
	}
	private static DefaultTeemThemes blackTeam() {
		DefaultTeemThemes ret = new DefaultTeemThemes();
		ret.TeamMaterial = Material.BLACK_WOOL;
		ret.SecondTeamMaterial = Material.GRAY_WOOL;
		ret.colorCode = ChatColor.DARK_GRAY;
		return ret;
	}
	private static DefaultTeemThemes darkGreenTeam() {
		DefaultTeemThemes ret = new DefaultTeemThemes();
		ret.TeamMaterial = Material.GREEN_WOOL;
		ret.SecondTeamMaterial = Material.LIME_WOOL;
		ret.colorCode = ChatColor.DARK_GREEN;
		return ret;
	}
	private static DefaultTeemThemes neutralTeam() {
		DefaultTeemThemes ret = new DefaultTeemThemes();
		ret.TeamMaterial = Material.LIGHT_GRAY_WOOL;
		ret.SecondTeamMaterial = Material.GRAY_WOOL;
		ret.colorCode = ChatColor.GRAY;
		return ret;
	}
	private static DefaultTeemThemes whiteTeam() {
		DefaultTeemThemes ret = new DefaultTeemThemes();
		ret.TeamMaterial = Material.WHITE_WOOL;
		ret.SecondTeamMaterial = Material.GREEN_WOOL;
		ret.colorCode = ChatColor.WHITE;
		return ret;
	}
	private static DefaultTeemThemes limeTeam() {
		DefaultTeemThemes ret = new DefaultTeemThemes();
		ret.TeamMaterial = Material.LIME_WOOL;
		ret.SecondTeamMaterial = Material.GREEN_WOOL;
		ret.colorCode = ChatColor.GREEN;
		return ret;
	}
}
