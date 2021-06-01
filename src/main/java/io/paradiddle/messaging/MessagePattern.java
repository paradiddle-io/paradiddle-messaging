/*
 * paradiddle-messaging A library that builds on OpenJDK Project Loom to provide Erlang-style message passing between processes.
 * Copyright (C) 2021 Michael Juliano
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package io.paradiddle.messaging;

import java.util.function.Consumer;

public interface MessagePattern {
    boolean matches(Object message);
    void apply(Object message);

    class Generic<T> implements MessagePattern {
        private final Class<T> messageType;
        private final Consumer<T> action;

        public Generic(final Class<T> messageType, final Consumer<T> action) {
            this.messageType = messageType;
            this.action = action;
        }

        @Override
        public boolean matches(final Object message) {
            return this.messageType.isInstance(message);
        }

        @Override
        public void apply(final Object message) {
            final T converted = this.messageType.cast(message);
            this.action.accept(converted);
        }
    }

}
