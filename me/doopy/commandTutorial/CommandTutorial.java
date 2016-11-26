package me.doopy.commandTutorial;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.doopy.commandTutorial.PlayerListener;

public class CommandTutorial extends JavaPlugin {
	private PlayerListener playerListener;
	
	private boolean skip;
	private String msg_completed;
	private String msg_skip, msg_skipped, loot, msg_alreadydone;
	private int loots_count;
    	private String[] msg;
    	private String[] cmd;
    	private String[][] loots;
	
	@Override
	public void onDisable() {
		System.out.println("[TuT] Tutorial "+ getDescription().getVersion() +" disabled!");
		
	}

	@Override
	public void onEnable() {
		msg = new String[100];
		cmd = new String[100];
		
		System.out.println("[TuT] Tutorial "+ getDescription().getVersion() +" enabled!");
		
		//Check if file hierarchy is intact
		File ordner = new File("plugins//CommandTutorial//user");
		if (!ordner.exists()) {
			ordner.mkdirs();
			System.out.println("[TuT] Tutorial folder couldnt be found. Creating it...");
		}
		File config = new File("plugins//CommandTutorial//config.yml");
		if(!config.exists()) {
			this.saveDefaultConfig();
			this.reloadConfig();
		}
		
		//Initializing Listener (Constructing the Listener has to be AFTER saving default config)
		playerListener = new PlayerListener(this);
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(playerListener, this);
		
		//Readout of tutorial data
        for (int i = 0; i<=99; i++) {
        	msg[i] = this.getConfig().getString("tutorials." + i + ".message", null);
            	cmd[i] = this.getConfig().getString("tutorials." + i + ".command", null);
        }
        
        	
        //Readout of messages
        msg_completed = this.getConfig().getString("messages.tutorial-completed");
        msg_skip = this.getConfig().getString("messages.check-skip");
        msg_skipped = this.getConfig().getString("messages.skipped");
        msg_alreadydone = this.getConfig().getString("messages.alreadydone");

        //Readout of loot and load all data in an array
        loot = this.getConfig().getString("general.loot");
        String[] loots1 = new String[loot.replaceAll("[^,]","").length()];
        loots1 = loot.split(",");
        loots_count = loots1.length;
        loots = new String[loots_count][2];
        for(int i = 0; i <= (loots1.length -1); i++) {
        	loots[i] = loots1[i].split(" ");
        }
        	
        }
	
	public void giveloot(Player player) {
		UserManager.setStepForUser(player, 100);
		for(int i=0;i<loots_count;i++) {
			String itemname = loots[i][0].trim();
			int count = Integer.parseInt(loots[i][1].trim());
			player.getInventory().addItem(new ItemStack(Material.getMaterial(itemname), count));
		}
		player.sendMessage(ChatColor.BLUE +"[TuT] "+ ChatColor.AQUA + msg_completed);
	}


	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (cmd.getName().equalsIgnoreCase("tut")) {
				if (args != null && args.length > 0 && args[0] != null) {
				if (args[0].compareToIgnoreCase("skip") == 0) {
					if(!this.getConfig().getBoolean("general.skippable")) return true;
					skip = true;
					player.sendMessage(ChatColor.BLUE + "[TuT] " + ChatColor.AQUA + msg_skip);
					return true;
				} else if (args[0].compareToIgnoreCase("confirm") == 0 && skip == true) {
					player.sendMessage(ChatColor.BLUE + "[TuT] " + ChatColor.AQUA + msg_skipped);
					giveloot(player);
					return true;
				} else if (args[0].compareToIgnoreCase("next") == 0) {
					if(player.hasPermission("tut.ignore") || !player.hasPermission("tut.use")) return true;
					int step = UserManager.readStepOfUser(player);
					int last = 0;
					for (int i = 0; i<=99; i++) {
						if (msg[i] != null && msg[i] != "") {
							last = i;
						} 
					}
					if (step <= last) {
						player.sendMessage(ChatColor.BLUE + "[TuT] " + ChatColor.AQUA + msg[step]);
					} else if(step == (last + 1)) {
						giveloot(player);
					} else if(step == 100) {
						player.sendMessage(ChatColor.BLUE + "[TuT] " + ChatColor.AQUA + msg_alreadydone);
					}
					return true;
				} else if (args[0].compareToIgnoreCase("info") == 0) {
					player.sendMessage(ChatColor.AQUA + "..........." + ChatColor.BLUE + "[CommandTutorial]" + ChatColor.AQUA + "...........");
					player.sendMessage(ChatColor.BLUE + "Version: " + ChatColor.AQUA + getDescription().getVersion());
					player.sendMessage(ChatColor.BLUE + "Developer: " + ChatColor.AQUA + "Doopy");
					player.sendMessage(ChatColor.AQUA + ".............................................");
					return true;
				} else if (args[0].compareToIgnoreCase("reload") == 0 && player.isOp() == true) {
					if(!player.hasPermission("tut.reload")) return true;
					player.sendMessage(ChatColor.BLUE + "[TuT] " + ChatColor.AQUA + "TuT is reloading now...");
					onDisable();
					onEnable();
					player.sendMessage(ChatColor.BLUE + "[TuT] " + ChatColor.AQUA + "Reload complete!");
				}
				} else {
					return true;
			    } 
			}
		}
		return true;
	}
}
