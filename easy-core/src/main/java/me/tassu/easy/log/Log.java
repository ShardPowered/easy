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

package me.tassu.easy.log;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import org.bukkit.plugin.PluginDescriptionFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

@Singleton
public class Log {

    private Logger logger;

    @Getter
    private static Log instance;

    @Inject
    public Log(PluginDescriptionFile file) {
        instance = this;
        logger = LoggerFactory.getLogger(file.getPrefix() == null ? file.getName() : file.getPrefix());
    }

    public void info(String string, Object... objects) {
        logger.info(string, objects);
    }

    public void error(String string, Object... objects) {
        logger.error(string, objects);
    }

    public void debug(String string, Object... objects) {
        logger.info("§9[DEBUG]§r " + string, objects);
    }

    public void error(String string, Throwable throwable) {
        logger.error(string, throwable);
    }

    public void error(String string, StackTraceElement[] trace) {
        error(string);

        for (StackTraceElement element : trace) {
            error(element.toString());
        }
    }
}
