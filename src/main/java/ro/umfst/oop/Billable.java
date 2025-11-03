package ro.umfst.oop;

public interface Billable {

    double calculateLineTotal();

    double getBasePrice();

    int getQuantity();

    void setQuantity(int quantity);
}
