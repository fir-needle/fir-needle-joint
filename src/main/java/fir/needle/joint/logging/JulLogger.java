/**
 * MIT License
 * <p>
 * Copyright (c) 2019 Nikita Vasilev
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

import java.util.logging.Level;

public class JulLogger implements Logger {
    private final java.util.logging.Logger logger;

    public JulLogger(final java.util.logging.Logger logger) {
        this.logger = logger;
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isLoggable(Level.INFO);
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isLoggable(Level.FINEST);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isLoggable(Level.SEVERE);
    }

    @Override
    public void info(final CharSequence message) {
        logger.info(message.toString());
    }

    @Override
    public void trace(final CharSequence message) {
        logger.finest(message.toString());
    }

    @Override
    public void trace(final CharSequence message, final Throwable t) {
        logger.log(Level.FINEST, message.toString() + ' ' + t.getMessage(), t);
    }

    @Override
    public void error(final CharSequence message) {
        logger.severe(message.toString());
    }

    @Override
    public void error(final CharSequence message, final Throwable t) {
        logger.log(Level.SEVERE, message.toString() + ' ' + t, t);
    }
}