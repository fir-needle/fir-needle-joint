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
package fir.needle.joint.colleclions;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class ConcurrentObjectPool<T> implements Pool<T> {

    private final Pool<T> objectPool;
    private final ReentrantLock lock = new ReentrantLock();

    public ConcurrentObjectPool(final Pool<T> objectPool) {
        this.objectPool = objectPool;
    }

    public ConcurrentObjectPool(final Supplier<T> supplier) {
        objectPool = new ObjectPool<>(supplier);
    }

    @Override
    public T borrow() {
        lock.lock();
        try {
            return objectPool.borrow();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void release(final T item) {
        lock.lock();
        try {
            objectPool.release(item);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void release(final List<T> items) {
        lock.lock();
        try {
            objectPool.release(items);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int getAllocatedSize() {
        lock.lock();
        try {
            return objectPool.getAllocatedSize();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int getUsedSize() {
        lock.lock();
        try {
            return objectPool.getUsedSize();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        lock.lock();
        try {
            return objectPool.toString();
        } finally {
            lock.unlock();
        }
    }
}