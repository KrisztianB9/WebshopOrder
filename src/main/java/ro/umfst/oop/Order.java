package ro.umfst.oop;
import java.util.ArrayList;
import java.util.List;

public class Order extends BaseEntity {

    private String status;
    private String currency;
    private Customer customer;
    private List<OrderItem> items;

    private double shippingPrice;
    private double orderTaxRate;

    private double grandTotal;
    private double totalTax;
    private double totalDiscount;

    public Order(String orderId, String status, String currency, Customer customer, double shippingPrice, double orderTaxRate) {
        super(orderId);
        this.status = status;
        this.currency = currency;
        this.customer = customer;
        this.shippingPrice = shippingPrice;
        this.orderTaxRate = orderTaxRate;
        this.items = new ArrayList<OrderItem>();

        this.grandTotal = 0;
        this.totalTax = 0;
        this.totalDiscount = 0;
    }

    @Override
    public String businessKey() {
        return this.id;
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public double getShippingPrice() {
        return shippingPrice;
    }

    public double getOrderTaxRate() {
        return orderTaxRate;
    }

    public String getStatus() {
        return status;
    }

    public String getCurrency() {
        return currency;
    }

    public double getGrandTotal() { return grandTotal; }
    public void setGrandTotal(double grandTotal) { this.grandTotal = grandTotal; }

    public double getTotalTax() { return totalTax; }
    public void setTotalTax(double totalTax) { this.totalTax = totalTax; }

    public double getTotalDiscount() { return totalDiscount; }
    public void setTotalDiscount(double totalDiscount) { this.totalDiscount = totalDiscount; }
}
