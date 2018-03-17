package script.util;

import org.osbot.rs07.utility.ConditionalSleep;

import java.util.function.BooleanSupplier;

public class Sleep extends ConditionalSleep {

    private final BooleanSupplier condition;
    private boolean waitInitialInterval;

    public Sleep(final BooleanSupplier condition, final int timeout) {
        super(timeout);
        this.condition = condition;
    }

    public Sleep(final BooleanSupplier condition, final int timeout, final int interval) {
        super(timeout, interval);
        this.condition = condition;
    }

    public Sleep(final BooleanSupplier condition, final int timeout, final int interval, boolean forceWaitFirstInterval) {
        super(timeout, interval);
        this.condition = condition;
        this.waitInitialInterval = true;
    }

    @Override
    public final boolean condition() throws InterruptedException {
        if(waitInitialInterval)
            return waitInitialInterval = false;
        return condition.getAsBoolean();
    }

    public static boolean sleepUntil(final BooleanSupplier condition, final int timeout) {
        return new Sleep(condition, timeout).sleep();
    }

    public static boolean sleepUntil(final BooleanSupplier condition, final int timeout, final int interval) {
        return new Sleep(condition, timeout, interval).sleep();
    }

    public static boolean sleepUntil(final BooleanSupplier condition, final int timeout, final int interval, final boolean forceWaitFirstInterval) {
        return new Sleep(condition, timeout, interval, forceWaitFirstInterval).sleep();
    }

    public static void sleep(final int lengthMs)
    {
        try {
            Thread.sleep(lengthMs);
        } catch (InterruptedException ignored) { }
    }
}