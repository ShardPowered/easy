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

import com.google.common.reflect.TypeToken;
import com.google.inject.AbstractModule;
import lombok.AllArgsConstructor;
import lombok.val;
import me.tassu.cfg.ConfigFactory;
import me.tassu.cfg.impl.HoconConfigFactory;
import me.tassu.easy.EasyPlugin;
import me.tassu.easy.config.VectorSerializer;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.bukkit.util.Vector;

@AllArgsConstructor
public class ConfigModule extends AbstractModule {

    private EasyPlugin plugin;

    @Override
    protected void configure() {
        val serializers = HoconConfigurationLoader.builder().getDefaultOptions().getSerializers();
        serializers.registerType(TypeToken.of(Vector.class), new VectorSerializer());

        val factory = new HoconConfigFactory(plugin.getDataFolder().toPath(), serializers);
        bind(ConfigFactory.class).toInstance(factory);
    }
}
