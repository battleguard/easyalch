package script.nodes;

import org.osbot.rs07.script.MethodProvider;
import script.util.Rand;
import script.util.Timer;

public class MoveCameraNode extends Node {
    private final MethodProvider _ctx;
    private final Timer timer = new Timer(Rand.nextInt(3, 8) * 1000 * 60);

    public MoveCameraNode(MethodProvider ctx) {
        _ctx = ctx;
    }

    @Override
    public boolean activate() {
        return !timer.isRunning();
    }

    @Override
    public void execute() {
        _ctx.log("Moving Camera");
        _ctx.getCamera().movePitch(Rand.nextInt(10, 90));
        _ctx.getCamera().moveYaw(Rand.nextInt(340, 380) % 360);
        timer.setEndIn(Rand.nextInt(3, 8) * 1000 * 60);
    }
}
