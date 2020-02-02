/**
 * MIT License
 * <p>
 * Copyright (c) 2019 Anatoly Gudkov
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE  LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package fir.needle.joint.logging;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SystemLogger implements Logger {
    private static final int ERROR_LEVEL = 0;
    private static final int INFO_LEVEL = 1;
    private static final int TRACE_LEVEL = 2;

    private final int level;

    SystemLogger(final int level) {
        this.level = level;
    }

    public static SystemLogger error() {
        return new SystemLogger(ERROR_LEVEL);
    }

    public static SystemLogger info() {
        return new SystemLogger(INFO_LEVEL);
    }

    public static SystemLogger trace() {
        return new SystemLogger(TRACE_LEVEL);
    }

    @Override
    public boolean isInfoEnabled() {
        return level >= INFO_LEVEL;
    }

    @Override
    public boolean isErrorEnabled() {
        return level >= ERROR_LEVEL;
    }

    @Override
    public boolean isTraceEnabled() {
        return level >= TRACE_LEVEL;
    }

    @Override
    public void info(final CharSequence message) {
        if (!isInfoEnabled()) {
            return;
        }

        System.out.println(message);
    }

    @Override
    public void trace(final CharSequence message) {
        if (!isTraceEnabled()) {
            return;
        }

        System.out.println(message);
    }

    @Override
    public void trace(final CharSequence message, final Throwable t) {
        System.out.println(message.toString() + ' ' + t + '\n' + getStackTrace(t));
    }

    @Override
    public void error(final CharSequence message) {
        if (!isErrorEnabled()) {
            return;
        }

        System.err.println(message);
    }

    @Override
    public void error(final CharSequence message, final Throwable t) {
        System.err.println(message.toString() + ' ' + t + '\n' + getStackTrace(t));
    }

    private String getStackTrace(final Throwable e) {
        return Stream.of(e.getStackTrace())
            .map(Object::toString)
            .collect(Collectors.joining("\n"));
    }
}
