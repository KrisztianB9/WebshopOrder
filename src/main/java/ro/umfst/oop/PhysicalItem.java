package ro.umfst.oop;

public class PhysicalItem extends OrderItem implements Shippable {

    private double weight;
    private double goldCustomerDiscount;

    public PhysicalItem(String sku, String name, int quantity, double basePrice, double lineDiscount, double weight) {
        super(sku, name, quantity, basePrice, lineDiscount);
        this.weight = weight;
        this.goldCustomerDiscount = 0.0;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    public void applyGoldDiscount(double discountAmount) {
        this.goldCustomerDiscount = discountAmount;
    }

    public double getGoldCustomerDiscount() {
        return goldCustomerDiscount;
    }

    @Override
    public double calculateLineTotal() {
        double subTotal = this.basePrice * this.quantity;
        this.totalLineValue = (subTotal + this.taxAmount) - this.lineDiscount - this.goldCustomerDiscount;
        return this.totalLineValue;
    }

    @Override
    public String toString() {
        return "PhysicalItem[sku=" + getSku() + ", name=" + name + ", qty=" + quantity + "]";
    }
}
