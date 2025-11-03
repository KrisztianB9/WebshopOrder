package ro.umfst.oop;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ReportGenerator {

    private NumberFormat currencyFormat;

    public ReportGenerator() {
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.GERMANY);
    }

    public void writeReport(List<Order> orders, String filePath) throws IOException {
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;

        if (orders == null || orders.isEmpty()) {
            System.out.println("No orders to report.");
            return;
        }

        try {
            fileWriter = new FileWriter(filePath);
            bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write("========================================\n");
            bufferedWriter.write("        WEB SHOP ORDER REPORT (Batch)     \n");
            bufferedWriter.write("========================================\n");
            bufferedWriter.newLine();

            for (Order order : orders) {
                // 1. Order Header
                bufferedWriter.write("--------------------------------------------------------------------------------\n");
                bufferedWriter.write("Order ID: " + order.getId() + " (" + order.getStatus() + ")\n");
                bufferedWriter.write("Currency: " + order.getCurrency() + "\n");
                bufferedWriter.newLine();

                // 2. Customer Info
                Customer c = order.getCustomer();
                bufferedWriter.write("Customer:\n");
                bufferedWriter.write("  Name: " + c.getName() + "\n");
                bufferedWriter.write("  Email: " + c.getEmail() + "\n");
                bufferedWriter.write("  Tier: " + c.getLoyaltyTier() + "\n");
                bufferedWriter.newLine();

                // 3. Items (Sorted)
                bufferedWriter.write("Order Items (Sorted by Total Value):\n");
                bufferedWriter.write(String.format("%-20s | %-35s | %-3s | %-10s | %-12s\n",
                        "SKU", "Name", "Qty", "Details", "Line Total"));
                bufferedWriter.write("................................................................................\n");

                for (OrderItem item : order.getItems()) {
                    String details = "";
                    if (item instanceof PhysicalItem) {
                        details = "PHYSICAL";
                    } else if (item instanceof DigitalItem) {
                        details = "DIGITAL";
                    } else if (item instanceof ServiceItem) {
                        details = "SERVICE";
                    }

                    bufferedWriter.write(String.format("%-20s | %-35s | %-3d | %-10s | %-12s\n",
                            item.getSku(),
                            item.getName(),
                            item.getQuantity(),
                            details,
                            currencyFormat.format(item.getTotalLineValue())
                    ));
                }
                bufferedWriter.newLine();

                // 4. Totals
                bufferedWriter.write("Order Summary:\n");
                // Calculate item subtotal
                double itemSubtotal = order.getGrandTotal() - order.getShippingPrice();
                bufferedWriter.write("  Items Subtotal:   " + currencyFormat.format(itemSubtotal) + "\n");
                bufferedWriter.write("  Shipping:         " + currencyFormat.format(order.getShippingPrice()) + "\n");
                bufferedWriter.write("  Total Tax:        " + currencyFormat.format(order.getTotalTax()) + "\n");
                bufferedWriter.write("  Total Discounts:  " + currencyFormat.format(order.getTotalDiscount()) + "\n");
                bufferedWriter.write("  GRAND TOTAL:      " + currencyFormat.format(order.getGrandTotal()) + "\n");
                bufferedWriter.newLine();

                // 5. Shipping
                double totalWeight = 0.0;
                for(OrderItem item : order.getItems()) {
                    if (item instanceof Shippable) {
                        Shippable s = (Shippable) item;
                        totalWeight += s.getWeight() * item.getQuantity();
                    }
                }
                bufferedWriter.write("Shipping Information:\n");
                bufferedWriter.write("  Total Weight:     " + String.format("%.2f", totalWeight) + " kg\n");
                bufferedWriter.write("  Shipping Status:  " + (totalWeight > 0 ? "REQUIRES SHIPPING" : "DIGITAL/SERVICE ORDER") + "\n");
                bufferedWriter.newLine();
            }

            bufferedWriter.write("========================================\n");
            bufferedWriter.write("            END OF BATCH REPORT         \n");
            bufferedWriter.write("========================================\n");

        } catch (IOException e) {
            System.err.println("Could not write report: " + e.getMessage());
            throw e; // Re-throw
        } finally {
            try {
                if (bufferedWriter != null) bufferedWriter.close();
                if (fileWriter != null) fileWriter.close();
            } catch (IOException e) {
                System.err.println("Failed to close report writer: " + e.getMessage());
            }
        }
    }
}
