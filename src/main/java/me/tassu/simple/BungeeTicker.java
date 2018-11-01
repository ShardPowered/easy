/*
 * MIT License
 *
 * Copyright (c) 2018 Tassu <hello@tassu.me>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.tassu.simple;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import me.tassu.easy.EasyPlugin;
import me.tassu.easy.register.Worker;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.List;
import java.util.Map;

@Singleton
public class BungeeTicker extends Worker implements PluginMessageListener {

    @Inject private Server server;
    @Inject private EasyPlugin plugin;

    @Getter @Setter
    private List<String> servers = Lists.newArrayList("ALL");

    @Getter
    private Map<String, Integer> data = Maps.newHashMap();

    @Override
    public void register() {
        this.setDelay(100)
                .setPeriod(40)
                .setSync(true)
                .setRunning(true);

        server.getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        server.getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", this);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }

        val in = ByteStreams.newDataInput(message);
        if (!in.readUTF().equals("PlayerCount")) {
            return;
        }

        data.put(in.readUTF(), in.readInt());
    }

    @Override
    public void run() {
        val player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
        if (player == null) return;

        for (val server : servers) {
            val out = ByteStreams.newDataOutput();
            out.writeUTF("PlayerCount");
            out.writeUTF(server);

            player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
        }
    }
}
