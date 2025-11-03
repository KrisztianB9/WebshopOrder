package ro.umfst.oop;

public class DigitalItem extends OrderItem {

    private String licenseKey;

    public DigitalItem(String sku, String name, int quantity, double basePrice, double lineDiscount, String licenseKey) {
        super(sku, name, quantity, basePrice, lineDiscount);
        this.licenseKey = licenseKey;
    }

    public String getLicenseKey() {
        return licenseKey;
    }

    @Override
    public double calculateLineTotal() {
        double subTotal = this.basePrice * this.quantity;
        this.totalLineValue = (subTotal + this.taxAmount) - this.lineDiscount; //no gold discount
        return this.totalLineValue;
    }

    @Override
    public String toString() {
        return "DigitalItem[sku=" + getSku() + ", name=" + name + ", qty=" + quantity + "]";
    }
}
