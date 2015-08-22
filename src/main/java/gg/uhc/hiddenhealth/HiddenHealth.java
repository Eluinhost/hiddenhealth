/*
 * HiddenHealth.java
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

import com.comphenix.protocol.ProtocolLibrary;
import org.bukkit.plugin.Plugin;

public class HiddenHealth {

    // set up permission constants
    public static final String BYPASS_HEALTH_PERMISSION = "HiddenHealth.bypass.health";
    public static final String BYPASS_HUNGER_PERMISSION = "HiddenHealth.bypass.hunger";

    // health value to set as always showing
    protected float showHealthValue;

    // hunger value to always show
    protected int showHungerValue;

    // saturation to always show
    protected float showSaturationValue;

    // flags for hiding
    protected boolean hideHealth;
    protected boolean hideHunger;

    public HiddenHealth(Plugin plugin) {
        // setup listeners
        ProtocolLibrary.getProtocolManager().addPacketListener(new HealthUpdatePacketAdapter(plugin, this));
    }

    public boolean isHidingHealth() {
        return hideHealth;
    }

    public void setIsHidingHealth(boolean hide) {
        hideHealth = hide;
    }

    public boolean isHidingHunger() {
        return hideHunger;
    }

    public void setIsHidingHunger(boolean hide) {
        hideHunger = hide;
    }

    public float getSaturationValue() {
        return showSaturationValue;
    }

    public int getHungerValue() {
        return showHungerValue;
    }

    public void setSaturationValue(float saturation) {
        showSaturationValue = saturation;
    }

    public void setHungerValue(int hunger) {
        showHungerValue = hunger;
    }

    public void setHealthValue(float health) {
        showHealthValue = health;
    }

    public float getHealthValue() {
        return showHealthValue;
    }
}
