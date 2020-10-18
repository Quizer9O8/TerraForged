/*
 * MIT License
 *
 * Copyright (c) 2020 TerraForged
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

package com.terraforged.mod.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.terraforged.mod.Log;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConfigRef implements Supplier<CommentedFileConfig> {

    private final Object lock = new Object();
    private final Supplier<CommentedFileConfig> factory;

    private CommentedFileConfig ref;

    public ConfigRef(Supplier<CommentedFileConfig> factory) {
        this.factory = factory;
    }

    @Override
    public CommentedFileConfig get() {
        synchronized (lock) {
            if (ref != null) {
                Log.info("Loading config: {}", ref.getFile().getName());
                ref.load();
                return ref;
            }
            return ref = factory.get();
        }
    }

    public <T> void getValue(String name, T def, Consumer<T> consumer) {
        synchronized (lock) {
            CommentedFileConfig current = ref;
            if (current != null) {
                T value = current.getOrElse(name, def);
                consumer.accept(value);
            }
        }
    }
}
