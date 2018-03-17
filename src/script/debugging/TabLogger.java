package script.debugging;

import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.script.MethodProvider;

public class TabLogger {
    private final MethodProvider _ctx;
    private Tab prev;
    public boolean Stop;
    private long prevTime = System.currentTimeMillis();
    private long stopAt = System.currentTimeMillis() + 1000 * 60 * 1;

    public TabLogger(MethodProvider ctx) {
        _ctx = ctx;
    }

    public void execute() {
        while(System.currentTimeMillis() < stopAt) {
            try {
                Thread.sleep(5);
            } catch (Exception ignored) {}
            Tab current = _ctx.getTabs().getOpen();
            if (current == prev)
                continue;
            long currentTime = System.currentTimeMillis();
            _ctx.log(prev + ": " + (currentTime - prevTime));
            prev = current;
            prevTime = currentTime;
        }
    }
}
