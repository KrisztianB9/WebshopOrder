package ro.umfst.oop;

public abstract class OrderItem extends BaseEntity implements Billable {

    protected String name;
    protected int quantity;
    protected double basePrice;
    protected double lineDiscount;  // Total discount amount from JSON
    protected double taxAmount;

    protected double totalLineValue;

    public OrderItem(String sku, String name, int quantity, double basePrice, double lineDiscount) {
        super(sku);
        this.name = name;
        this.quantity = quantity;
        this.basePrice = basePrice;
        this.lineDiscount = lineDiscount;
        this.taxAmount = 0.0;
        this.totalLineValue = 0.0;
    }

    @Override
    public String businessKey() {
        return this.id;
    }

    public String getSku() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public double getBasePrice() {
        return basePrice;
    }

    public double getLineDiscount() {
        return lineDiscount;
    }

    public double getTotalLineValue() {
        return totalLineValue;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public double getTaxAmount() {
        return this.taxAmount;
    }

    public void setTotalLineValue(double totalLineValue) {
        this.totalLineValue = totalLineValue;
    }
}
