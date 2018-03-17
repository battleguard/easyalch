package script.nodes;

import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.ui.EquipmentSlot;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.input.mouse.InventorySlotDestination;
import org.osbot.rs07.utility.Condition;
import script.*;
import script.util.Rand;
import script.util.Sleep;

public class SetupAlchNode extends Node {

    private EasyAlch easyAlch;
    private static final int _alchSlot = 15;

    public SetupAlchNode(EasyAlch easyAlch) {
        this.easyAlch = easyAlch;
    }

    @Override
    public boolean activate() {
        return !easyAlch.lastAlch.isRunning();
    }

    @Override
    public void execute() {
        easyAlch.log("resetting alch");
        if (getOverlap()) {
            easyAlch.log("Overlap found moving mouse to alch point: " + easyAlch.AlchClickPoint);
            easyAlch.getTabs().open(Tab.MAGIC);
            easyAlch.getMouse().move(easyAlch.AlchClickPoint.x, easyAlch.AlchClickPoint.y);
            easyAlch.lastAlch.reset();
        }
    }

    private boolean getOverlap() {
        easyAlch.getTabs().open(Tab.INVENTORY);
        Sleep.sleep(500);
        Item currentItem = easyAlch.goodItems.get(0);

        Item inventoryItem = easyAlch.inventory.getItem(currentItem.getId());
        if (removeIfEquipped(currentItem))
            inventoryItem = easyAlch.inventory.getItem(currentItem.getId());
        if (inventoryItem == null) {
            easyAlch.log("Finished Item: " + currentItem.getName());
            easyAlch.goodItems.remove(0);
            if(easyAlch.goodItems.isEmpty()) {
                easyAlch.stop(false);
                return false;
            }
            currentItem = easyAlch.goodItems.get(0);
            easyAlch.log("Starting Item: " + currentItem.getName());
        }

        int curSlot = easyAlch.inventory.getSlot(inventoryItem);
        easyAlch.AlchItemLocation = InventorySlotDestination.getSlot(easyAlch.inventory.getSlot(curSlot));
        if (curSlot != _alchSlot) {
            boolean moveItem = easyAlch.mouse.continualClick(new InventorySlotDestination(easyAlch.getBot(), curSlot), new Condition() {
                @Override
                public boolean evaluate() {
                    return easyAlch.mouse.move(new InventorySlotDestination(easyAlch.getBot(), _alchSlot), true);
                }
            });
            if (!moveItem || easyAlch.inventory.getSlot(inventoryItem) != _alchSlot)
                return false;
        }
        easyAlch.AlchItemLocation = InventorySlotDestination.getSlot(easyAlch.inventory.getSlot(inventoryItem));
        easyAlch.AlchLocation = easyAlch.HighAlchSpellLocation.intersection(easyAlch.AlchItemLocation);
        if (easyAlch.AlchLocation.isEmpty())
            return false;
        easyAlch.AlchClickPoint = Rand.nextPoint(easyAlch.AlchLocation);
        return true;
    }

    private boolean removeIfEquipped(final Item item) {
        return easyAlch.getEquipment().isWearingItem(EquipmentSlot.ARROWS, item) && easyAlch.getEquipment().unequip(EquipmentSlot.ARROWS);
    }
}
