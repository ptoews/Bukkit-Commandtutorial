package me.doopy.commandTutorial;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class UserManager {
	private static String userDir = "plugins//CommandTutorial//user";
	
   	public static void setStepForUser(Player player, int newstep) {
        File userFile = new File(userDir + File.separator + player.getName() + ".yml" );
        FileConfiguration usercfg = YamlConfiguration.loadConfiguration(userFile);
    	try {
    		if (!userFile.exists() ) {
    		    userFile.createNewFile();
    			usercfg = YamlConfiguration.loadConfiguration(userFile);
    		}
			usercfg.set("step", newstep);
            usercfg.save(userFile);
		} catch (IOException e) {
			System.out.println("[TuT] The user yml file couldnt be edited!");
			e.printStackTrace();
		}
    }
    
    public static int readStepOfUser(Player player) {
        File userFile = new File(userDir + File.separator + player.getName() + ".yml" );
        if(!userFile.exists()) {
        	return -1;
        }
    	FileConfiguration usercfg = YamlConfiguration.loadConfiguration(userFile);
    	
    	int value = usercfg.getInt("step", 99);
    	return value;
    }
    

}
