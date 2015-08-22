/*
 * HealthUpdatePacketAdapter.java
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
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class HealthUpdatePacketAdapter extends PacketAdapter {

    // the packet identifier for 0x06 Update Health, also contains hunger + saturation
    protected static final PacketType UPDATE_HEALTH_PACKET = PacketType.Play.Server.UPDATE_HEALTH;

    // the packet identifier for 0x1C entity metadata, client will update it's visual health
    // when any metadata sent to itself if sent with the health flag on it
    protected static final PacketType ENTITY_METADATA = PacketType.Play.Server.ENTITY_METADATA;

    protected final HiddenHealth hiddenHealth;

    public HealthUpdatePacketAdapter(Plugin plugin, HiddenHealth hiddenHealth) {
        super(plugin, ListenerPriority.HIGHEST, UPDATE_HEALTH_PACKET, ENTITY_METADATA);
        this.hiddenHealth = hiddenHealth;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if(event.getPacketType() == UPDATE_HEALTH_PACKET){
            Player player = event.getPlayer();

            // check if they don't have the bypass permission
            // check if they're not dead, stops VERY buggy behaviour on death
            if (hiddenHealth.isHidingHealth() && !player.hasPermission(HiddenHealth.BYPASS_HEALTH_PERMISSION) && event.getPacket().getFloat().read(0)>0) {
                event.getPacket().getFloat().write(0, hiddenHealth.getHealthValue());
            }

            if(hiddenHealth.isHidingHunger() && !player.hasPermission(HiddenHealth.BYPASS_HUNGER_PERMISSION)){
                event.getPacket().getIntegers().write(0, hiddenHealth.getHungerValue());
                event.getPacket().getFloat().write(1, hiddenHealth.getSaturationValue());
            }

            return;
        }

        if(event.getPacketType() == ENTITY_METADATA){
            // only run if we're hiding health and they dont have bypass permissions
            if (!hiddenHealth.isHidingHealth() || event.getPlayer().hasPermission(HiddenHealth.BYPASS_HEALTH_PERMISSION)) return;

            // only process if it's metadata for the player it's being sent to
            if(event.getPlayer().getEntityId() != event.getPacket().getIntegers().read(0))  return;

            // get the data watcher for the entity metadata
            WrappedDataWatcher dataWatcher = new WrappedDataWatcher(event.getPacket().getWatchableCollectionModifier().read(0));

            // get the health flag from index 6 of the metadata
            Float health = dataWatcher.getFloat(6);

            // if this metadata contained a health flag (!= null) and not dead (to avoid buggy client behaviour)
            if(health != null && health > 0){
                // set the health flag to the value
                dataWatcher.setObject(6, hiddenHealth.getHealthValue());
            }
        }
    }
}
