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

package me.tassu.easy;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.val;
import me.tassu.easy.api.binder.BindManager;
import me.tassu.easy.inject.module.BinderModule;
import me.tassu.easy.inject.module.BukkitModule;
import me.tassu.easy.inject.module.ConfigModule;
import me.tassu.easy.log.Log;
import me.tassu.easy.register.config.Config;
import me.tassu.easy.register.core.IRegistrable;
import me.tassu.easy.register.core.RequiredPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static me.tassu.cfg.ConfigUtil.run;

@SuppressWarnings("WeakerAccess")
public abstract class EasyPlugin extends JavaPlugin {

    private Injector injector;

    @Inject private Log log;
    @Inject private PluginManager pluginManager;

    protected void load() {}
    protected void init() {}
    protected void stop() {}

    @Getter
    private Set<IRegistrable> registrableSet = new HashSet<>();

    @Override
    public final void onLoad() {
        injector = Guice.createInjector(
                new BukkitModule(this),
                new ConfigModule(this),
                new BinderModule(getBinder())
        );

        injector.injectMembers(this);

        load();
    }

    @Override
    public final void onEnable() {
        init();

        log.info("Loaded {} registrable objects", registrableSet.size());
    }

    @Override
    public final void onDisable() {
        stop();

        registrableSet.forEach(IRegistrable::close);
    }

    protected BindManager getBinder() {
        return new BindManager();
    }

    protected void registerInstance(IRegistrable registrable) {
        injector.injectMembers(registrable);

        pluginManager.registerEvents(registrable, this);
        registrable.register();

        log.debug("Registering {}", registrable.getClass().getSimpleName());
        registrableSet.add(registrable);
    }

    protected void registerClass(Class<? extends IRegistrable> clazz) {
        if (!clazz.isAnnotationPresent(Singleton.class)) {
            log.error("Tried to register {} but it is not a singleton.", clazz.getName());
            return;
        }

        if (clazz.isAnnotationPresent(RequiredPlugin.class)) {
            for (val plugin : clazz.getAnnotation(RequiredPlugin.class).value()) {
                if (!pluginManager.isPluginEnabled(plugin)) return;
            }
        }

        registerInstance(injector.getInstance(clazz));
    }

    @SuppressWarnings("unchecked")
    protected void registerAll(@NonNull Object... objects) {
        for (Object object : objects) {
            if (object instanceof IRegistrable) {
                registerInstance((IRegistrable) object);
            } else if (object instanceof Class<?>) {
                Class<?> clazz = (Class<?>) object;

                if (IRegistrable.class.isAssignableFrom(clazz)) {
                    registerClass((Class<? extends IRegistrable>) clazz);
                } else {
                    log.error("Failed to register {} as it does not implement Registrable.", clazz.getSimpleName());
                    log.error("Stack trace: ", Thread.currentThread().getStackTrace());
                }
            } else {
                log.error("Failed to register {}: unknown object.", object.getClass().getSimpleName());
                log.error("Stack trace: ", Thread.currentThread().getStackTrace());
            }
        }
    }

    @Override
    public void reloadConfig() {
        registrableSet.stream()
                .filter(it -> it instanceof Config)
                .map(it -> (Config) it)
                .forEach(it -> run(it::load));
    }

    public <T> Optional<T> getModule(Class<T> clazz) {
        return registrableSet.stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .findFirst();
    }
}
