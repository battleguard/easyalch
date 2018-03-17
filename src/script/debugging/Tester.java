package script.debugging;

import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

@ScriptManifest(name = "Alch Timer", author = "Battleguard", version = 4.0, info = "Magic: Alchs all the items you select.  No messing with ids.  Automatically sets up alch spot for you!", logo = "")
public class Tester extends Script {

    @Override
    public void onStart() {
    }

    @Override
    public int onLoop() {
        return 500;
    }
}
