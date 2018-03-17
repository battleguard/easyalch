package script.view;

import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.input.mouse.InventorySlotDestination;
import org.osbot.rs07.script.Script;
import script.services.PriceLoader;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class EasyAlchView extends JFrame {
    private static final int height = 32;

    public EasyAlchView(List<Item> items, Script ctx) {
        super("EasyAlch");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(getOwner());

        JPanel bottumPane = new JPanel();

        bottumPane.setLayout(new GridLayout(1, 1));
        bottumPane.setPreferredSize(new Dimension(240, height));
        final JButton startButton = new JButton("Start");
        bottumPane.add(startButton);

        JPanel bottumPane2 = new JPanel(new GridLayout(1, 1));
        bottumPane2.setPreferredSize(new Dimension(240, height));
        bottumPane2.add(new JLabel("Please check all items you wish to alch", JLabel.CENTER));
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        JPanel leftPane = new JPanel(new GridLayout(items.size(), 1));
        leftPane.setPreferredSize(new Dimension(60, height * (items.size())));

        items.stream().map( item -> InventorySlotDestination.getSlot(ctx.getInventory().getSlot(item)))
                .map(rec -> ctx.getBot().getCanvas().getGameBuffer().getSubimage(rec.x, rec.y, rec.width, rec.height))
                .map(ImageIcon::new).map(JLabel::new).forEach(leftPane::add);

        JPanel rightPane = new JPanel(new GridLayout(items.size(), 1));
        rightPane.setPreferredSize(new Dimension(140, height * items.size()));

        List<JCheckBox> checkBoxes = items.stream().map(Item::getName).map(JCheckBox::new).collect(Collectors.toList());
        checkBoxes.forEach(rightPane::add);


        JPanel profitPane = new JPanel(new GridLayout(items.size(), 1));
        profitPane.setPreferredSize(new Dimension(40, height * items.size()));

        ctx.execute(new PriceLoader(ctx, priceMap -> {
            final int natureRunePrice = priceMap.get(561).itemPrice;
            ctx.log("Using nature rune ge price of " + natureRunePrice);
            for(Item item : items)
            {
                PriceLoader.ItemPriceConfig data = priceMap.getOrDefault(item.getUnnotedId(), null);
                if(data == null)
                {
                    ctx.log("Was unable to find item price for item: " + item.getName());
                    continue;
                }

                final int profit = data.highAlchPrice - data.itemPrice - natureRunePrice;
                JLabel label = new JLabel(profit + "gp");
                label.setForeground(profit > 0 ? Color.GREEN : Color.RED);
                profitPane.add(label);
                ctx.log("Added profit label for item: " + item.getName() + " Profit: " + profit );
            }
        }));

        JPanel centerPane = new JPanel();
        centerPane.setLayout(new BoxLayout(centerPane, BoxLayout.X_AXIS));
        centerPane.add(leftPane);
        centerPane.add(rightPane);
        centerPane.add(profitPane);

        getContentPane().add(centerPane);
        getContentPane().add(bottumPane2);
        getContentPane().add(bottumPane);
        pack();
        setVisible(true);

        startButton.addActionListener(e -> {
            for (int i = checkBoxes.size() - 1; i >= 0; i--) {
                boolean selected = checkBoxes.get(i).isSelected();
                ctx.log(items.get(i).getName() + " Selected= " + selected);
                if (!selected)
                    items.remove(i);
            }
            dispose();
        });
    }
}
