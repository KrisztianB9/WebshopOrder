package ro.umfst.oop;

public class ServiceItem extends OrderItem {

    private String provider;
    private int termsMonths;

    public ServiceItem(String sku, String name, int quantity, double basePrice, double lineDiscount, String provider, int termsMonths) {
        super(sku, name, quantity, basePrice, lineDiscount);
        this.provider = provider;
        this.termsMonths = termsMonths;
    }

    public String getProvider() {
        return provider;
    }

    public int getTermsMonths() {
        return termsMonths;
    }

    @Override
    public double calculateLineTotal() {
        double subTotal = this.basePrice * this.quantity;
        this.totalLineValue = (subTotal + this.taxAmount) - this.lineDiscount;
        return this.totalLineValue;
    }

    @Override
    public String toString() {
        return "ServiceItem[sku=" + getSku() + ", name=" + name + ", qty=" + quantity + "]";
    }
}
