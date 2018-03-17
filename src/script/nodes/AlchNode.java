package script.nodes;

import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.script.MethodProvider;
import script.util.Rand;
import script.util.Sleep;
import script.util.Timer;

import java.util.Random;

public class AlchNode extends Node {

    private final MethodProvider _ctx;
    private final Timer _lastLachTimer;

    public AlchNode(MethodProvider ctx, Timer lastAlchTimer) {
        _ctx = ctx;
        _lastLachTimer = lastAlchTimer;
    }

    @Override
    public boolean activate() {
        return _ctx.getTabs().getOpen() == Tab.MAGIC;
    }

    @Override
    public void execute() { // 100ms to perform an automated click
        _ctx.getMouse().click(false);
        if(!Sleep.sleepUntil(() -> _ctx.getTabs().getOpen() == Tab.INVENTORY, 1000 ))
            return;
        Sleep.sleep(Rand.nextInt(260, 300));
        _ctx.getMouse().click(false);
        _lastLachTimer.reset();
        if(Sleep.sleepUntil(() -> _ctx.getTabs().getOpen() == Tab.MAGIC, 4000))
        {
            Sleep.sleep(Rand.nextInt(255, 300)); // 200 + 100 = 100 TIME TO CLICK ON THE MAGIC BUTTON WHEN IT REAPPEARS
        }
    }
}
