package ro.umfst.oop;

public class Customer extends BaseEntity implements Identifiable {

    private String name;
    private String email;
    private String loyaltyTier;

    public Customer(String customerId, String name, String email, String loyaltyTier) {
        super(customerId);
        this.name = name;
        this.email = email;
        this.loyaltyTier = (loyaltyTier != null) ? loyaltyTier : "STANDARD";
    }

    // --- Interface Methods ---

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getEmail() {
        return email;
    }

    // --- Abstract Method Implementation ---

    @Override
    public String businessKey() {
        return this.email;
    }

    public String getLoyaltyTier() {
        return loyaltyTier;
    }

    @Override
    public String toString() {
        return "Customer[id=" + id + ", name=" + name + ", email=" + email + ", tier=" + loyaltyTier + "]";
    }
}
