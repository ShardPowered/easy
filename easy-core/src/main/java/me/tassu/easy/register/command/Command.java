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

package me.tassu.easy.register.command;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import me.tassu.easy.api.message.IMessageProvider;
import me.tassu.easy.register.command.error.CommandException;
import me.tassu.easy.register.command.error.MissingPermissionException;
import me.tassu.easy.register.core.IRegistrable;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.List;

public abstract class Command extends org.bukkit.command.Command implements IRegistrable {

    @Inject private IMessageProvider messenger;

    @Inject private SimpleCommandMap map;
    @Inject private PluginDescriptionFile descriptionFile;

    private String permission;

    public Command(String name) {
        super(name);

        if (getClass().isAnnotationPresent(Aliases.class)) {
            this.setAliases(Lists.newArrayList(getClass().getAnnotation(Aliases.class).value()));
        }

        if (getClass().isAnnotationPresent(Permission.class)) {
            this.permission = getClass().getAnnotation(Permission.class).value();
        }
    }

    @Override
    public void register() {
        map.register(descriptionFile.getName(), this);
    }

    @Override
    public final boolean execute(CommandSender sender, String commandLabel, String[] args) {
        try {
            if (permission != null && !sender.hasPermission(permission)) {
                throw new MissingPermissionException(permission);
            }

            check(sender, commandLabel, Lists.newArrayList(args)); // for abstract commands

            run(sender, commandLabel, Lists.newArrayList(args));
        } catch (MissingPermissionException ex) {
            messenger.sendMissingPermissionMessage(sender, ex.getPermission());
        } catch (CommandException ex) {
            throw new RuntimeException(ex);
        }

        return true;
    }

    @SuppressWarnings({"WeakerAccess", "RedundantThrows", "unused"})
    protected void check(CommandSender sender, String label, List<String> args) throws CommandException {}
    protected abstract void run(CommandSender sender, String label, List<String> args) throws CommandException;

}
