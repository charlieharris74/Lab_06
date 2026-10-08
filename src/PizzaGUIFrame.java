import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Lets the user choose one pizza and view its itemized receipt. */
public class PizzaGUIFrame extends JFrame
{
    private final String[] sizes = {"Small", "Medium", "Large", "Super"};
    private final int[] basePrices = {800, 1200, 1600, 2000};
    private final String[] toppingNames = {
            "Pepperoni", "Sausage", "Mushrooms", "Onions", "Green Peppers", "Extra Cheese"
    };
    private final ButtonGroup crustGroup = new ButtonGroup();
    private final JComboBox<String> sizeBox = new JComboBox<>(sizes);
    private final JCheckBox[] toppingBoxes = new JCheckBox[toppingNames.length];
    private final JTextArea receiptArea = new JTextArea(14, 45);

    /** Creates the selection panels, receipt display, and command buttons. */
    public PizzaGUIFrame()
    {
        super("Pizza Order Form");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(new EmptyBorder(14, 14, 14, 14));
        setContentPane(mainPanel);

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        JLabel title = new JLabel("Build Your Pizza", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        topPanel.add(title, BorderLayout.NORTH);

        JPanel choicesPanel = new JPanel(new GridLayout(1, 3, 12, 0));
        choicesPanel.add(createCrustPanel());
        choicesPanel.add(createSizePanel());
        choicesPanel.add(createToppingsPanel());
        topPanel.add(choicesPanel, BorderLayout.CENTER);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(createReceiptPanel(), BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent event)
            {
                quitForm();
            }
        });
        pack();
        setMinimumSize(new Dimension(780, 640));
        setSize(780, 640);
        setLocationRelativeTo(null);
    }

    private JPanel createCrustPanel()
    {
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Crust"));
        for (String crust : new String[]{"Thin", "Regular", "Deep-dish"})
        {
            JRadioButton button = new JRadioButton(crust);
            button.setActionCommand(crust);
            crustGroup.add(button);
            panel.add(button);
        }
        return panel;
    }

    private JPanel createSizePanel()
    {
        JPanel panel = new JPanel(new BorderLayout(5, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Size"));
        sizeBox.setSelectedIndex(-1);
        JPanel selectionPanel = new JPanel(new BorderLayout(5, 5));
        selectionPanel.add(new JLabel("Choose a size:"), BorderLayout.NORTH);
        selectionPanel.add(sizeBox, BorderLayout.CENTER);
        panel.add(selectionPanel, BorderLayout.NORTH);
        panel.add(new JLabel("<html>Small: $8.00<br>Medium: $12.00<br>Large: $16.00<br>Super: $20.00</html>"),
                BorderLayout.CENTER);
        return panel;
    }

    private JPanel createToppingsPanel()
    {
        JPanel panel = new JPanel(new GridLayout(toppingNames.length + 1, 1, 4, 2));
        panel.setBorder(BorderFactory.createTitledBorder("Toppings"));
        panel.add(new JLabel("$1.00 each - choose at least one"));
        for (int i = 0; i < toppingNames.length; i++)
        {
            toppingBoxes[i] = new JCheckBox(toppingNames[i]);
            panel.add(toppingBoxes[i]);
        }
        return panel;
    }

    private JPanel createReceiptPanel()
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Order Receipt"));
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        receiptArea.setMargin(new Insets(10, 10, 10, 10));
        panel.add(new JScrollPane(receiptArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createButtonPanel()
    {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        JButton orderButton = new JButton("Order");
        JButton clearButton = new JButton("Clear");
        JButton quitButton = new JButton("Quit");
        orderButton.addActionListener(event -> placeOrder());
        clearButton.addActionListener(event -> clearForm());
        quitButton.addActionListener(event -> quitForm());
        panel.add(orderButton);
        panel.add(clearButton);
        panel.add(quitButton);
        return panel;
    }

    /** Checks the required selections before displaying the order. */
    private void placeOrder()
    {
        if (crustGroup.getSelection() == null)
        {
            showMissingChoice("Please choose a crust.");
            return;
        }
        int sizeIndex = sizeBox.getSelectedIndex();
        if (sizeIndex == -1)
        {
            showMissingChoice("Please choose a pizza size.");
            return;
        }
        List<String> selectedToppings = new ArrayList<>();
        for (JCheckBox topping : toppingBoxes)
            if (topping.isSelected())
                selectedToppings.add(topping.getText());
        if (selectedToppings.isEmpty())
        {
            showMissingChoice("Please choose at least one topping.");
            return;
        }
        String crust = crustGroup.getSelection().getActionCommand();
        receiptArea.setText(buildReceipt(crust, sizes[sizeIndex], basePrices[sizeIndex], selectedToppings));
        receiptArea.setCaretPosition(0);
    }

    private void showMissingChoice(String message)
    {
        JOptionPane.showMessageDialog(this, message, "Incomplete order", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Formats one pizza as a receipt. Prices are stored in cents to avoid
     * rounding errors in the order's currency calculations.
     */
    static String buildReceipt(String crust, String size, int basePrice, List<String> toppings)
    {
        int subtotal = basePrice + toppings.size() * 100;
        int tax = (int) Math.round(subtotal * 0.07);
        int total = subtotal + tax;
        StringBuilder receipt = new StringBuilder();
        receipt.append("============================================\n");
        receipt.append("              PIZZA ORDER\n");
        receipt.append("============================================\n");
        receipt.append(String.format(Locale.US, "%-33s %10s%n", "Type of Crust & Size", "Price"));
        receipt.append(String.format(Locale.US, "%-33s %10s%n", crust + " / " + size, money(basePrice)));
        receipt.append("\n");
        receipt.append(String.format(Locale.US, "%-33s %10s%n", "Ingredient", "Price"));
        for (String topping : toppings)
            receipt.append(String.format(Locale.US, "%-33s %10s%n", topping, "$1.00"));
        receipt.append("--------------------------------------------\n");
        receipt.append(String.format(Locale.US, "%-33s %10s%n", "Sub-total:", money(subtotal)));
        receipt.append(String.format(Locale.US, "%-33s %10s%n", "Tax (7%):", money(tax)));
        receipt.append("--------------------------------------------\n");
        receipt.append(String.format(Locale.US, "%-33s %10s%n", "Total:", money(total)));
        receipt.append("============================================\n");
        return receipt.toString();
    }

    private static String money(int cents)
    {
        return String.format(Locale.US, "$%.2f", cents / 100.0);
    }

    /** Resets every selection and removes the previous receipt. */
    private void clearForm()
    {
        crustGroup.clearSelection();
        sizeBox.setSelectedIndex(-1);
        for (JCheckBox topping : toppingBoxes)
            topping.setSelected(false);
        receiptArea.setText("");
    }

    private void quitForm()
    {
        int answer = JOptionPane.showConfirmDialog(this, "Are you sure you want to quit?",
                "Quit", JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION)
            dispose();
    }
}
