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

package me.tassu.easy.register;

import com.google.inject.Inject;
import me.tassu.easy.EasyPlugin;
import me.tassu.easy.log.Log;
import me.tassu.easy.register.core.IRegistrable;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

@SuppressWarnings("WeakerAccess")
public abstract class Worker implements IRegistrable, Runnable {

    @Inject private Log log;
    @Inject private EasyPlugin plugin;

    private long delay;
    private long period;
    private boolean sync;

    private BukkitTask task;

    /**
     * @return whether the task is running.
     */
    public boolean isRunning() {
        return task != null && !task.isCancelled();
    }

    /**
     * Runs or cancels the class' task.
     *
     * @param run whether we should run the task or not.
     */
    @SuppressWarnings("SameParameterValue")
    public void setRunning(boolean run) {
        // The plugin must be enabled in order for us to run tasks.
        if (!plugin.isEnabled()) {
            log.error("Attempted to alter task {} while it's parent is disabled.", getClass().getSimpleName());
            return;
        }

        if (run) {
            // Check that we have a valid wait period.
            if (period <= 0) {
                return;
            }

            // Check that the task isn't already running.
            if (isRunning()) {
                log.error("Attempted to run task while it is already active. Active Id: {}.", task.getTaskId());
                return;
            }

            if (sync) {
                task = Bukkit.getScheduler().runTaskTimer(plugin, this, delay, period);
            } else {
                task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, delay, period);
            }
        } else if (task != null) {
            task.cancel();
        }
    }

    public Worker setDelay(long delay) {
        this.delay = delay;
        return this;
    }

    public Worker setPeriod(long period) {
        this.period = period;
        return this;
    }

    public Worker setSync(boolean sync) {
        this.sync = sync;
        return this;
    }
}
