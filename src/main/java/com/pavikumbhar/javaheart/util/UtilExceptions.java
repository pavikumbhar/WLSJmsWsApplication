package com.pavikumbhar.javaheart.util;

import java.util.function.Supplier;

public class UtilExceptions {
    
    @FunctionalInterface
    public interface CheckedSupplier<X> {
        
        X get() throws Throwable;
    }
    
    public static <X> Supplier<X> undeclareCheckedException(final CheckedSupplier<X> supplier) {
        return () -> {
            try {
                return supplier.get();
            } catch (final Throwable checkedException) {
                return rethrowUnchecked(checkedException);
            }
        };
    }
    
    public static <R> R rethrowUnchecked(final Throwable checkedException) {
        return UtilExceptions.<R, RuntimeException> thrownInsteadOf(checkedException);
    }
    
    @SuppressWarnings("unused")
    public static <T extends Throwable> void declareToThrow(final Class<T> clazz) throws T {
        // do nothing  
    }
    
    @SuppressWarnings("unchecked")
    private static <R, T extends Throwable> R thrownInsteadOf(final Throwable t) throws T {
        throw (T) t;
    }
    
}