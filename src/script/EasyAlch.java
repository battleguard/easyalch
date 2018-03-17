package script;

import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.listener.MessageListener;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import script.nodes.AlchNode;
import script.nodes.MoveCameraNode;
import script.nodes.Node;
import script.nodes.SetupAlchNode;
import script.services.ScriptPainter;
import script.util.Sleep;
import script.util.Timer;
import script.view.EasyAlchView;
import script.view.ErrorMessageView;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@ScriptManifest(name = "EasyAlch", author = "Battleguard", version = 4.1, info = "Magic: Alchs all the items you select.  No messing with ids.  Automatically sets up alch spot for you!", logo = "")
public class EasyAlch extends Script implements MessageListener {

    public final Rectangle HighAlchSpellLocation = new Rectangle(703, 316, 24, 24);
    public Rectangle AlchLocation;
    public Rectangle AlchItemLocation;
    public Point AlchClickPoint;
    private ScriptPainter _painter;

    public Timer lastAlch = new Timer(5000);
    public final ArrayList<Item> goodItems = new ArrayList<>();
    private EasyAlchView gui;

    private final Node[] jobs = new Node[]{
            new MoveCameraNode(this),
            new SetupAlchNode(this),
            new AlchNode(this, lastAlch)
    };

    @Override
    public void onStart() {
        getTabs().open(Tab.INVENTORY);
        Sleep.sleep(1000); // give time for inventory graphics to show up on startup for showing item images on gui
        List<Item> items = getInventory().filter(i -> i.getAmount() > 10 && !i.getName().contains(" rune") && !i.getName().equals("Coins"));
        if (items.isEmpty()) {
            ErrorMessageView.display(items.get(0), new Exception("No items in inventory to alch"));
            stop(false);
            return;
        }

        gui = new EasyAlchView(items, this);
        while (gui.isVisible())
            Sleep.sleep(1000);
        goodItems.addAll(items);
        goodItems.stream().forEach(i -> log("Alching item: " + i.getName()));
        if (goodItems.isEmpty()) {
            log("You have selected zero items to alch stopping script.");
            stop(false);
            return;
        }
        _painter = new ScriptPainter(this, this);
    }

    @Override
    public void onExit() { // 2700, 300
        if (gui != null) {
            gui.dispose();
        }
        if (_painter != null) {
            getBot().getPainters().remove(_painter);
        }
    }

    @Override
    public int onLoop() {
        try {
            for (Node curJob : jobs) {
                if (curJob.activate()) {
                    curJob.execute();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log("showing error message");
            ErrorMessageView.display(goodItems.get(0), e);
        }
        return 0;
    }

    @Override
    public void onMessage(Message e) {
        if (e.getType() == Message.MessageType.GAME && e.getMessage().equals("You do not have enough Nature Runes to cast this spell.")) {
            log("You are out of nature runes");
            stop(true);
        }
    }
}
