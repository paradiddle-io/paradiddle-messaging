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
