package ro.umfst.oop;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DataParser {

    /**
     * Parses the JSON file at the given path.
     * @param filePath Path to data.json
     * @return A List of populated Order objects.
     * @throws IOException if the file cannot be read.
     */
    public List<Order> parseOrders(String filePath) throws IOException {
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        List<Order> orderList = new ArrayList<Order>();

        try {
            fileReader = new FileReader(filePath);
            bufferedReader = new BufferedReader(fileReader);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            String jsonData = sb.toString();

            // --- Begin JSON Parsing ---
            JSONObject root = new JSONObject(jsonData);
            JSONArray ordersJson = root.getJSONArray("orders");

            // --- Iterate over each order ---
            for (int i = 0; i < ordersJson.length(); i++) {
                String currentOrderId = "N/A";
                try {
                    JSONObject orderJson = ordersJson.getJSONObject(i);
                    currentOrderId = orderJson.optString("orderId", "UNKNOWN-" + i);

                    // 1. Parse Order Details
                    String status = orderJson.optString("status", "UNKNOWN");

                    // Handle currency being a number or string
                    Object currencyObj = orderJson.opt("currency");
                    String currency = "EUR"; // Default
                    if (currencyObj instanceof String) {
                        currency = (String) currencyObj;
                    } else if (currencyObj instanceof Number) {
                        currency = currencyObj.toString();
                    }

                    double shippingPrice = orderJson.getJSONObject("shipping").optDouble("price", 0.0);
                    if (shippingPrice < 0) {
                        throw new DomainValidationException("Invalid shipping price: " + shippingPrice);
                    }

                    // Get first tax rate
                    JSONArray taxesJson = orderJson.optJSONArray("taxes");
                    double orderTaxRate = 0.0;
                    if (taxesJson != null && taxesJson.length() > 0) {
                        orderTaxRate = taxesJson.getJSONObject(0).optDouble("rate", 0.0);
                        if (orderTaxRate < 0) {
                            throw new DomainValidationException("Invalid tax rate: " + orderTaxRate);
                        }
                    }

                    // 2. Parse Customer
                    Customer customer = parseCustomer(orderJson.getJSONObject("customer"));

                    // 3. Create Order
                    Order order = new Order(currentOrderId, status, currency, customer, shippingPrice, orderTaxRate);

                    // 4. Parse Items
                    JSONArray itemsJson = orderJson.getJSONArray("items");
                    for (int j = 0; j < itemsJson.length(); j++) {
                        order.addItem(parseItem(itemsJson.getJSONObject(j)));
                    }

                    // If all parsing succeeded, add the order
                    orderList.add(order);

                } catch (DomainValidationException e) {
                    System.err.println("SKIPPING ORDER " + currentOrderId + ". Validation Failed: " + e.getMessage());
                } catch (JSONException e) {
                    System.err.println("SKIPPING ORDER " + currentOrderId + ". Invalid JSON structure: " + e.getMessage());
                }
            } // End of orders loop

            return orderList;

        } catch (FileNotFoundException e) {
            throw new IOException("Data file not found: " + filePath, e);
        } catch (IOException e) {
            throw new IOException("Error reading data file: " + filePath, e);
        } catch (JSONException e) {
            throw new JSONException("Invalid root JSON format: " + e.getMessage());
        } finally {
            // Clean up resources
            try {
                if (bufferedReader != null) bufferedReader.close();
                if (fileReader != null) fileReader.close();
            } catch (IOException e) {
                System.err.println("Failed to close file reader: " + e.getMessage());
            }
        }
    }

    /**
     * Helper method to parse a Customer JSONObject.
     */
    private Customer parseCustomer(JSONObject custJson) throws DomainValidationException {
        String custId = custJson.optString("customerId", "CUST-UNKNOWN");

        JSONObject nameJson = custJson.optJSONObject("name");
        String name = "N/A";
        if (nameJson != null) {
            name = nameJson.optString("first", "") + " " + nameJson.optString("last", "");
            name = name.trim();
        }

        String email = custJson.optString("email");
        if (email == null || email.isEmpty() || email.equals("not-an-email")) {
            // Business key is critical
            throw new DomainValidationException("Customer 'email' is missing or invalid.");
        }

        String loyalty = custJson.optString("loyaltyTier", "STANDARD");
        return new Customer(custId, name, email, loyalty);
    }

    /**
     * Helper method to parse an Item JSONObject.
     */
    private OrderItem parseItem(JSONObject itemJson) throws DomainValidationException, JSONException {
        String sku = itemJson.optString("sku");
        if (sku == null || sku.isEmpty()) {
            throw new DomainValidationException("Item 'sku' is missing or empty.");
        }

        String name = itemJson.optString("name", "Unnamed Item");

        int qty = itemJson.optInt("quantity", 1);
        if (qty <= 0) {
            throw new DomainValidationException("Invalid quantity for SKU " + sku + ": " + qty);
        }

        // Handle price being a string ("free") or number
        Object priceObj = itemJson.opt("price");
        double price = 0.0;
        if (priceObj instanceof Number) {
            price = ((Number) priceObj).doubleValue();
        } else if (priceObj instanceof String) {
            throw new DomainValidationException("Invalid price format (String) for SKU " + sku);
        }
        if (price < 0) {
            throw new DomainValidationException("Invalid price for SKU " + sku + ": " + price);
        }

        // Sum discounts
        double lineDiscount = 0.0;
        JSONArray discountsJson = itemJson.optJSONArray("discounts");
        if (discountsJson != null) {
            for (int k = 0; k < discountsJson.length(); k++) {
                lineDiscount += discountsJson.getJSONObject(k).optDouble("amount", 0.0);
            }
        }

        String type = itemJson.getString("type");

        // Polymorphic creation
        if ("PHYSICAL".equals(type)) {
            double weight = 0.0;
            JSONObject weightJson = itemJson.optJSONObject("weight");
            if(weightJson != null) {
                weight = weightJson.optDouble("value", 0.0);
            }
            return new PhysicalItem(sku, name, qty, price, lineDiscount, weight);

        } else if ("DIGITAL".equals(type)) {
            String licenseKey = "N/A";
            JSONObject licenseJson = itemJson.optJSONObject("license");
            if(licenseJson != null) {
                licenseKey = licenseJson.optString("key", "N/A");
            }
            return new DigitalItem(sku, name, qty, price, lineDiscount, licenseKey);

        } else if ("SERVICE".equals(type)) {
            String provider = "N/A";
            int terms = 0;
            JSONObject serviceJson = itemJson.optJSONObject("service");
            if (serviceJson != null) {
                provider = serviceJson.optString("provider", "N/A");
                terms = serviceJson.optInt("termsMonths", 0);
            }
            return new ServiceItem(sku, name, qty, price, lineDiscount, provider, terms);

        } else {
            throw new DomainValidationException("Unknown item 'type': " + type);
        }
    }
}
