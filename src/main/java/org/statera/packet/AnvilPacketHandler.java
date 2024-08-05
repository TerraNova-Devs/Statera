package org.statera.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import org.statera.Statera;

public class AnvilPacketHandler {

    private final ProtocolManager protocolManager;
    private final Statera plugin;

    public AnvilPacketHandler(Statera plugin) {
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        registerPacketListener(plugin);
        this.plugin = plugin;
    }

    private void registerPacketListener(Statera plugin) {
        PacketListener packetListener = new PacketAdapter(plugin, PacketType.Play.Server.WINDOW_DATA) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (event.getPacketType() == PacketType.Play.Server.WINDOW_DATA) {
                    int property = event.getPacket().getIntegers().read(1);
                    int value = event.getPacket().getIntegers().read(2);

                    // Property 0 is the repair cost; we can manipulate it
                    if (property == 0 && value >= 40) {
                        event.getPacket().getIntegers().write(2, 39); // Set to just below "too expensive"
                    }
                }
            }
        };

        protocolManager.addPacketListener(packetListener);
    }

    public void unregister() {
        protocolManager.removePacketListeners(plugin);
    }
}
