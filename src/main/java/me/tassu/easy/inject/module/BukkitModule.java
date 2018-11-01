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

package me.tassu.easy.inject.module;

import com.google.inject.AbstractModule;
import lombok.AllArgsConstructor;
import me.tassu.easy.EasyPlugin;
import me.tassu.easy.inject.ann.Console;
import me.tassu.easy.inject.ann.PluginFolder;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

import java.nio.file.Path;

@AllArgsConstructor
public class BukkitModule extends AbstractModule {

    private final EasyPlugin plugin;

    @Override
    protected void configure() {
        bindObjectAsSingleton(plugin);
        bind(EasyPlugin.class).toInstance(plugin);

        bind(PluginDescriptionFile.class).toInstance(plugin.getDescription());

        bind(Server.class).toInstance(Bukkit.getServer());
        bind(BukkitScheduler.class).toInstance(Bukkit.getScheduler());
        bind(PluginManager.class).toInstance(Bukkit.getPluginManager());

        bind(SimpleCommandMap.class)
                .toInstance((SimpleCommandMap) Bukkit.getCommandMap());

        bind(CommandSender.class)
                .annotatedWith(Console.class)
                .toInstance(Bukkit.getConsoleSender());

        bind(Path.class)
                .annotatedWith(PluginFolder.class)
                .toInstance(plugin.getDataFolder().toPath());
    }

    // Suppress warnings to convince the Java compiler to allow the cast.
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void bindObjectAsSingleton(Object object) {
        bind((Class) object.getClass()).toInstance(object);
    }

}
