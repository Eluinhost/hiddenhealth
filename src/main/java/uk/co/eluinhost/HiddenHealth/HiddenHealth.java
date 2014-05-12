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

package uk.co.eluinhost.hiddenhealth;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class HiddenHealth extends JavaPlugin {

    //the packet identifier for 0x06 Update Health, also contains hunger + saturation
    private static final PacketType UPDATE_HEALTH_PACKET = PacketType.Play.Server.UPDATE_HEALTH;
    //the packet identifier for 0x1C entity metadata, client will update it's visual health
    //when any metadata sent to itself if sent with the health flag on it
    private static final PacketType ENTITY_METADATA = PacketType.Play.Server.ENTITY_METADATA;

    //set up permission constants
    private static final String BYPASS_HEALTH_PERMISSION = "HiddenHealth.bypass.health";
    private static final String BYPASS_HUNGER_PERMISSION = "HiddenHealth.bypass.hunger";

    //health value to set as always showing
    private static final float HEALTH_VALUE = 20F;

    //hunger value to always show
    private static final int HUNGER_VALUE = 20;

    //saturation to always show
    private static final float SATURATION_VALUE = 5;

    //flags for hiding
    private static boolean hideHealth;
    private static boolean hideHunger;

    /**
     * When the plugin loads
     */
    @Override
    public void onEnable(){

        //set up the default config options
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);
        saveConfig();

        //get config values
        hideHealth = config.getBoolean("hideHealth");
        hideHunger = config.getBoolean("hideHunger");

        //the protocal manager
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

        //set up a listener to change outgoing packets
        PacketAdapter updateHealthPacketAdapter = new PacketAdapter(this, ListenerPriority.HIGHEST, UPDATE_HEALTH_PACKET) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if(event.getPacketType() == UPDATE_HEALTH_PACKET){
                    Player player = event.getPlayer();
                    //check if they don't have the bypass permission
                    //check if they're not dead, stops VERY buggy behaviour on death
                    if(hideHealth && !player.hasPermission(BYPASS_HEALTH_PERMISSION) && event.getPacket().getFloat().read(0)>0)
                        event.getPacket().getFloat().write(0, HEALTH_VALUE);
                    if(hideHunger && !player.hasPermission(BYPASS_HUNGER_PERMISSION)){
                        event.getPacket().getIntegers().write(0,HUNGER_VALUE);
                        event.getPacket().getFloat().write(1, SATURATION_VALUE);
                    }
                }
            }
        };

        PacketAdapter metadataPacketAdapter = new PacketAdapter(this, ListenerPriority.HIGHEST, ENTITY_METADATA) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if(event.getPacketType() == ENTITY_METADATA){
                    //only process if it's metadata for the player it's being sent to
                    if(event.getPlayer().getEntityId() == event.getPacket().getIntegers().read(0)){
                        //get the data watcher for the entity metadata
                        WrappedDataWatcher dataWatcher = new WrappedDataWatcher(event.getPacket().getWatchableCollectionModifier().read(0));
                        //get the health flag from index 6 of the metadata
                        Float health = dataWatcher.getFloat(6);
                        //if this metadata contained a health flag (!= null) and not dead (to avoid buggy client behaviour)
                        if(hideHealth && !event.getPlayer().hasPermission("HiddenHealth.bypass.health") && health != null && health > 0){
                            //set the health flag to the value
                            dataWatcher.setObject(6,HEALTH_VALUE);
                        }
                    }
                }
            }
        };

        //add the packet listeners
        protocolManager.addPacketListener(updateHealthPacketAdapter);
        protocolManager.addPacketListener(metadataPacketAdapter);
    }
}
