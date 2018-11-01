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

package me.tassu.easy.util;

import lombok.val;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class TimeUtil {

    private TimeUtil() {}

    private static final DecimalFormat formatter = new DecimalFormat("00");

    public static String format(double input, int scale) {
        if (input < 60) {
            return new BigDecimal(input)
                    .setScale(scale, RoundingMode.HALF_UP)
                    .toPlainString() + "s";
        }

        val integer = (int) input;

        if (input < 3600) {
            val minutes = integer / 60;
            val seconds = integer % 60;
            return formatter.format(minutes) + "m " + formatter.format(seconds) + "s";
        }

        val hours = integer / 3600;
        val minutes = (integer % 3600) / 60;
        val seconds = integer % 60;

        return formatter.format(hours) + "h " + formatter.format(minutes) + "m " + formatter.format(seconds) + "s";
    }

    public static String format(double input) {
        return format(input, 1);
    }


}
