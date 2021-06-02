/*
 * paradiddle-otp A library that builds on OpenJDK Project Loom to bring Erlang idioms to Java.
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

package io.paradiddle.otp;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

public interface Mailbox {
    void receive(Object message);
    void retrieve(MessagePattern... patterns) throws InterruptedException;

    class Generic implements Mailbox {
        private final BlockingDeque<Object> messageQueue;

        public Generic() {
            this.messageQueue = new LinkedBlockingDeque<>();
        }

        @Override
        public void receive(final Object message) {
            this.messageQueue.add(message);
        }

        @Override
        public void retrieve(final MessagePattern... patterns) throws InterruptedException {
            final List<MessagePattern> patternList = List.of(patterns);
            final Deque<Object> saveQueue = new LinkedList<>();
            final AtomicBoolean notMatched = new AtomicBoolean();
            do {
                final Object message = this.messageQueue.take();
                patternList.stream()
                    .filter(pattern -> pattern.matches(message))
                    .findFirst()
                    .ifPresentOrElse(
                        pattern -> {
                            pattern.apply(message);
                            notMatched.set(false);
                        },
                        () -> {
                            saveQueue.addFirst(message);
                            notMatched.set(true);
                        }
                    );
            } while(notMatched.get());
            saveQueue.forEach(this.messageQueue::addFirst);
        }
    }
}
