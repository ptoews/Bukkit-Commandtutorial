package me.doopy.commandTutorial;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import me.doopy.commandTutorial.CommandTutorial;

public class PlayerListener implements Listener {
	
	private CommandTutorial plugin;
    	private String[] msg;
    	private String[] cmd;
    	private String msg_start, msg_continue_tut;

	
	public PlayerListener(CommandTutorial tutorial) {
		plugin = tutorial;
		String root = "tutorials.";
		msg = new String[100];
		cmd = new String[100];
		for(int i = 0; i < 100; i++) {
			msg[i] = plugin.getConfig().getString(root + i + ".message", null);
			cmd[i] = plugin.getConfig().getString(root + i + ".command", null);
		}
		msg_start = plugin.getConfig().getString("messages.start");
		msg_continue_tut = plugin.getConfig().getString("messages.continue-tut");
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)  {
		Player player = event.getPlayer();
		if(player.hasPermission("tut.ignore") || !player.hasPermission("tut.use")) return;
		File user = new File("plugins//CommandTutorial//user" + File.separator + player.getName() + ".yml");
		if (!user.exists()) {
			//System.out.println("[TuT]" + player.getName() + ".yml existiert nicht. Erstelle sie...");
			UserManager.setStepForUser(player, 0);
			player.sendMessage(ChatColor.BLUE + "[TuT] " + ChatColor.AQUA + msg_start);
		} else if(UserManager.readStepOfUser(player) < 100 && UserManager.readStepOfUser(player) >= 0){
			player.sendMessage(ChatColor.BLUE + "[TuT] " + ChatColor.AQUA + msg_continue_tut);
		}
	}
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)  {
		plugin.getLogger().info("command preprocess");
		String command = event.getMessage().toString();
		Player player = event.getPlayer();
		int step = UserManager.readStepOfUser(player);;
		if (step < 0) {
			UserManager.setStepForUser(player, 0);
			step = 0;
		}
		if(step >= 99) return;
		plugin.getLogger().info("preprocess before if, step = " + step + " & cmd[step] = " + cmd[step] + " & command = " + command);
    		if (cmd[step] != null && command.startsWith(cmd[step])) {
    			plugin.getLogger().info("preprocess in if");
    			UserManager.setStepForUser(player, step + 1);
    		}
	}
}
