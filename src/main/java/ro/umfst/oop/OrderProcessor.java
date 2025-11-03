package ro.umfst.oop;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OrderProcessor {

    public void processOrders(List<Order> orders) {
        if (orders == null) return;

        for (Order order : orders) {
            processOrder(order, true);
        }
    }

    public void processOrder(Order order, boolean applyExtraDiscounts) {
        if (order == null) return;

        double runningGrandTotal = 0.0;
        double runningTotalTax = 0.0;
        double runningTotalDiscount = 0.0;

        String customerType = order.getCustomer().getLoyaltyTier();
        double orderTaxRate = order.getOrderTaxRate();

        // 1. Iterate and apply logic
        for (OrderItem item : order.getItems()) {

            double subTotal = item.getBasePrice() * item.getQuantity();

            // 2. Apply GOLD discount *before* calculating tax
            if (applyExtraDiscounts && ("GOLD".equals(customerType) || "PLATINUM".equals(customerType))) {
                if (item instanceof PhysicalItem) {
                    PhysicalItem pItem = (PhysicalItem) item;
                    // Apply 5% discount
                    double goldDiscount = subTotal * 0.05;
                    pItem.applyGoldDiscount(goldDiscount);
                }
            }

            // 3. Calculate tax
            // Tax is applied *after* all discounts
            double preTaxTotal = subTotal - item.getLineDiscount();
            if (item instanceof PhysicalItem) {
                preTaxTotal -= ((PhysicalItem) item).getGoldCustomerDiscount();
            }
            // Ensure pre-tax total isn't negative (due to discounts)
            preTaxTotal = Math.max(0.0, preTaxTotal);

            double taxAmount = preTaxTotal * orderTaxRate;
            item.setTaxAmount(taxAmount);

            // 4. Calculate line total (Polymorphism)
            double lineTotal = item.calculateLineTotal();
            runningGrandTotal += lineTotal;

            // 5. Aggregate totals
            runningTotalTax += taxAmount;
            runningTotalDiscount += item.getLineDiscount();
            if(item instanceof PhysicalItem) {
                runningTotalDiscount += ((PhysicalItem)item).getGoldCustomerDiscount();
            }
        }

        // 6. Add shipping price
        runningGrandTotal += order.getShippingPrice();

        // 7. Set final totals on the order
        order.setGrandTotal(runningGrandTotal);
        order.setTotalTax(runningTotalTax);
        order.setTotalDiscount(runningTotalDiscount);

        // 8. Sort the items
        Collections.sort(order.getItems(), new Comparator<OrderItem>() {
            public int compare(OrderItem o1, OrderItem o2) {
                return Double.compare(o2.getTotalLineValue(), o1.getTotalLineValue());
            }
        });
    }
}
