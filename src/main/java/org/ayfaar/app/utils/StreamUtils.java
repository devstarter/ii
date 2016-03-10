package org.ayfaar.app.utils;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.stream.Collector;

public class StreamUtils {
    public static <E> Collector<E, ?, Optional<E>> single() {
        return Collector.of(
                AtomicReference::new, new BiConsumer<AtomicReference<E>, E>() {
                    @Override
                    public void accept(AtomicReference<E> ref, E e) {
                        if (!ref.compareAndSet(null, e)) {
                            throw new IllegalArgumentException("Multiple values");
                        }
                    }
                },
                (ref1, ref2) -> {
                    if (ref1.get() == null) {
                        return ref2;
                    } else if (ref2.get() != null) {
                        throw new IllegalArgumentException("Multiple values");
                    } else {
                        return ref1;
                    }
                },
                ref -> Optional.ofNullable(ref.get()),
                Collector.Characteristics.UNORDERED);
    }
}
