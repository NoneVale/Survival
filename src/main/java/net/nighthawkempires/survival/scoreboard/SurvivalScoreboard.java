package net.nighthawkempires.survival.scoreboard;

import net.nighthawkempires.core.CorePlugin;
import net.nighthawkempires.core.lang.Messages;
import net.nighthawkempires.core.scoreboard.NEScoreboard;
import net.nighthawkempires.core.settings.ConfigModel;
import net.nighthawkempires.survival.SurvivalPlugin;
import net.nighthawkempires.survival.user.UserModel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import static org.bukkit.ChatColor.*;

public class SurvivalScoreboard extends NEScoreboard {

    private int taskId;

    public int getPriority() {
        return 5;
    }

    public String getName() {
        return "survival";
    }

    public int getTaskId() {
        return this.taskId;
    }

    public Scoreboard getFor(Player player) {
        UserModel userModel = SurvivalPlugin.getUserRegistry().getUser(player.getUniqueId());
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("test", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(CorePlugin.getMessages().getMessage(Messages.SCOREBOARD_HEADER).replaceAll("%SERVER%",
                CorePlugin.getMessages().getServerTag(getConfig().getServerType())));
        Team top = scoreboard.registerNewTeam("top");
        top.addEntry(GRAY + " ➛  " + BLUE + "" + BOLD);
        top.setPrefix("");
        top.setSuffix("");
        Team middle = scoreboard.registerNewTeam("middle");
        middle.addEntry(GRAY + " ➛  " + GREEN + "" + BOLD);
        middle.setPrefix("");
        middle.setSuffix("");
        Team bottom = scoreboard.registerNewTeam("bottom");
        bottom.addEntry(GRAY + " ➛  " + GOLD + "" + BOLD);
        bottom.setPrefix("");
        bottom.setSuffix("");

        objective.getScore(DARK_GRAY + "" + STRIKETHROUGH + "" + BOLD + "--------------")
                .setScore(10);
        objective.getScore(GRAY + "" + BOLD + " Kills" + GRAY + ": ").setScore(9);
        objective.getScore(GRAY + " ➛  " + BLUE + "" + BOLD).setScore(8);
        top.setSuffix(GREEN + "" + BOLD + userModel.getKills());
        objective.getScore(DARK_PURPLE + " ").setScore(7);
        objective.getScore(GRAY + "" + BOLD + " Deaths" + GRAY + ": ")
                .setScore(6);
        objective.getScore(GRAY + " ➛  " + GREEN + "" + BOLD).setScore(5);
        middle.setSuffix(RED + "" + BOLD + userModel.getDeaths());
        objective.getScore(YELLOW + "  ").setScore(4);
        objective.getScore(GRAY + "" + BOLD + " K/D Ratio" + GRAY + ": ").setScore(3);
        objective.getScore(GRAY + " ➛  " + GOLD + "" + BOLD).setScore(2);
        bottom.setSuffix(GOLD + "" + BOLD + (userModel.getRatio() == -1 ? 0 : userModel.getRatio()));
        objective.getScore(DARK_GRAY + "" + STRIKETHROUGH + "" + BOLD + "--------------")
                .setScore(1);

        this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(CorePlugin.getPlugin(), () -> {
            top.setSuffix(GREEN + "" + BOLD + userModel.getKills());
            middle.setSuffix(RED + "" + BOLD + userModel.getDeaths());
            bottom.setSuffix(GOLD + "" + BOLD + (userModel.getRatio() == -1 ? "0" : userModel.getRatio()));
        }, 0 , 5);
        Bukkit.getScheduler().scheduleSyncDelayedTask(CorePlugin.getPlugin(), () -> {
            Bukkit.getScheduler().cancelTask(getTaskId());
        }, 295);
        return scoreboard;
    }

    private ConfigModel getConfig() {
        return CorePlugin.getConfigg();
    }
}