package ro.umfst.oop;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import javax.swing.SwingUtilities;

public class Main {
    private static final String DATA_FILE = "data.json";
    private static final String REPORT_FILE = "report.txt";
    static void main() {
        final DataParser parser = new DataParser();
        final OrderProcessor processor = new OrderProcessor();
        final ReportGenerator reportGen = new ReportGenerator();
        final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.GERMANY);

        try {
            System.out.println("Parsing " + DATA_FILE + "...");
            final List<Order> orders = parser.parseOrders(DATA_FILE);
            System.out.println("Parsing complete. " + orders.size() + " valid orders loaded.");

            // 2. Process
            System.out.println("Processing " + orders.size() + " orders...");
            processor.processOrders(orders);
            System.out.println("Processing complete.");

            // 3. Report
            System.out.println("Writing report to " + REPORT_FILE + "...");
            reportGen.writeReport(orders, REPORT_FILE);
            System.out.println("Report complete.");

            // 4. Console Summary
            System.out.println("\n--- CONSOLE SUMMARY ---");
            for (Order order : orders) {
                System.out.println("  Order: " + order.getId() + " | Customer: " + order.getCustomer().getName());
                System.out.println("  Items: " + order.getItems().size() + " | Grand Total: " + currencyFormat.format(order.getGrandTotal()));
                System.out.println("  ---------------------");
            }
            System.out.println("\n");

            // 5. Launch GUI
            // Edit the first item of the first valid order
            if (!orders.isEmpty() && !orders.get(0).getItems().isEmpty()) {

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        try {
                            // Get first order and its data.json
                            final Order firstOrder = orders.get(0);
                            OrderItem itemToEdit = firstOrder.getItems().get(0);

                            // Pass customer and tax rate to editor
                            ItemEditor editor = new ItemEditor(
                                    itemToEdit,
                                    firstOrder.getCustomer(),
                                    firstOrder.getOrderTaxRate()
                            );
                            editor.setVisible(true);
                        } catch (Exception e) {
                            System.err.println("Failed to launch GUI: " + e.getMessage());
                        }
                    }
                });
            } else {
                System.out.println("No valid orders with items found, skipping GUI.");
            }

        } catch (Exception e) {
            // Catch-all for IO or other critical errors
            System.err.println("\n---CRITICAL ERROR---");
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
