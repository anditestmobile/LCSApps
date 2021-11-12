package id.co.lcs.apps.model;

import java.io.Serializable;
import java.util.List;

public class CustomerRequest implements Serializable {
    private String Customer;

    public String getCustomer() {
        return Customer;
    }

    public void setCustomer(String customer) {
        Customer = customer;
    }
}
