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

package gg.uhc.hiddenhealth;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class HealthUpdatePacketAdapter extends PacketAdapter {
    protected static final WrappedDataWatcher.Serializer FLOAT_SERIALIZER = WrappedDataWatcher.Registry.get(Float.class);
    protected static final WrappedDataWatcher.WrappedDataWatcherObject HEALTH_METADATA = new WrappedDataWatcher.WrappedDataWatcherObject(6, FLOAT_SERIALIZER);

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

            StructureModifier<List<WrappedWatchableObject>> metadata = event.getPacket().getWatchableCollectionModifier();

            metadata.write(0, Lists.transform(metadata.read(0), MODIFY_HEALTH));
        }
    }

    protected final Function<WrappedWatchableObject, WrappedWatchableObject> MODIFY_HEALTH = new Function<WrappedWatchableObject, WrappedWatchableObject>() {
        public WrappedWatchableObject apply(WrappedWatchableObject input) {
            // if this metadata is health and not dead (to avoid buggy client behaviour)
            if (input.getIndex() != 6 || (Float) input.getValue() <= 0) return input;

            return new WrappedWatchableObject(HEALTH_METADATA, hiddenHealth.getHealthValue());
        }
    };
}
