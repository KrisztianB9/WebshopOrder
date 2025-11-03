package ro.umfst.oop;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class ItemEditor extends JFrame {

    private OrderItem item;
    private Customer customer;
    private double orderTaxRate;

    private JTextField qtyField;
    private JLabel lineTotalLabel;
    private JLabel discountLabel;
    private JLabel feedbackLabel;

    private NumberFormat currencyFormat;

    public ItemEditor(OrderItem item, Customer customer, double orderTaxRate) {
        this.item = item;
        this.customer = customer;
        this.orderTaxRate = orderTaxRate;
        // Use German locale for EUR
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.GERMANY);

        setTitle("Edit Item: " + item.getName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 275);
        setLocationRelativeTo(null); // Center screen

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Item Info Panel ---
        JPanel infoPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        infoPanel.add(new JLabel("Item:"));
        infoPanel.add(new JLabel("<html>" + item.getName() + "</html>")); // Wrap text
        infoPanel.add(new JLabel("SKU:"));
        infoPanel.add(new JLabel(item.getSku()));
        infoPanel.add(new JLabel("Base Price:"));
        infoPanel.add(new JLabel(currencyFormat.format(item.getBasePrice())));
        infoPanel.add(new JLabel("Base Discount:"));
        infoPanel.add(new JLabel(currencyFormat.format(item.getLineDiscount())));
        infoPanel.add(new JLabel("Quantity:"));

        qtyField = new JTextField(String.valueOf(item.getQuantity()), 5);
        infoPanel.add(qtyField);

        mainPanel.add(infoPanel, BorderLayout.NORTH);

        // --- Results Panel ---
        JPanel resultsPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        discountLabel = new JLabel("Loyalty Discount: " + currencyFormat.format(0.0));
        lineTotalLabel = new JLabel("Line Total (w/ Tax): " + currencyFormat.format(item.getTotalLineValue()));
        feedbackLabel = new JLabel(" "); // For errors

        resultsPanel.add(discountLabel);
        resultsPanel.add(lineTotalLabel);
        resultsPanel.add(feedbackLabel);
        mainPanel.add(resultsPanel, BorderLayout.CENTER);

        // --- Button Panel ---
        qtyField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { recompute(); }
            public void removeUpdate(DocumentEvent e) { recompute(); }
            public void changedUpdate(DocumentEvent e) { recompute(); }
        });

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ItemEditor.this.dispose();
            }
        });
        mainPanel.add(closeButton, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        recompute(); // Initial calculation
    }

    /**
     * Recomputes the line total based on the GUI inputs.
     */
    private void recompute() {
        int newQty;
        try {
            newQty = Integer.parseInt(qtyField.getText());
            if (newQty < 0) throw new NumberFormatException();
            feedbackLabel.setText(" "); // Clear error
        } catch (NumberFormatException e) {
            feedbackLabel.setText("Error: Quantity must be a positive number.");
            return;
        }

        // --- Re-run the business logic for this item ---
        double subTotal = item.getBasePrice() * newQty;
        double loyaltyDiscount = 0.0;

        // 1. Reset/Apply GOLD discount
        if (item instanceof PhysicalItem) {
            String tier = customer.getLoyaltyTier();
            PhysicalItem pItem = (PhysicalItem) item;

            if ("GOLD".equals(tier) || "PLATINUM".equals(tier)) {
                loyaltyDiscount = subTotal * 0.05;
                pItem.applyGoldDiscount(loyaltyDiscount);
            } else {
                pItem.applyGoldDiscount(0.0);
            }
        }

        // 2. Calculate tax
        double preTaxTotal = subTotal - item.getLineDiscount() - loyaltyDiscount;
        preTaxTotal = Math.max(0.0, preTaxTotal); // Don't tax negative
        double taxAmount = preTaxTotal * this.orderTaxRate;
        item.setTaxAmount(taxAmount);

        // 3. Set new quantity and recalculate
        item.setQuantity(newQty);
        double newLineTotal = item.calculateLineTotal();

        // 4. Update labels
        discountLabel.setText("Loyalty Discount: " + currencyFormat.format(loyaltyDiscount));
        lineTotalLabel.setText("Line Total (w/ Tax): " + currencyFormat.format(newLineTotal));
    }
}
