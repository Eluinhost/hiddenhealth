/*
 * Entry.java
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * This file is part of HiddenHealth.
 *
 * HiddenHealth is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * HiddenHealth is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with HiddenHealth.  If not, see <http ://www.gnu.org/licenses/>.
 */

package gg.uhc.hiddenhealth;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Entry extends JavaPlugin {

    protected HiddenHealth plugin;

    public void onEnable() {
        if (null == plugin) plugin = new HiddenHealth(this);

        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);
        saveConfig();

        plugin.setIsHidingHealth(config.getBoolean("hide health"));
        plugin.setIsHidingHunger(config.getBoolean("hide hunger"));
        plugin.setHealthValue((float) config.getDouble("show health value"));
        plugin.setHungerValue(config.getInt("show hunger value"));
        plugin.setSaturationValue((float) config.getDouble("show saturation value"));
    }

}
