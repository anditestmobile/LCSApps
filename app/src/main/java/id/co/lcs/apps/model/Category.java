package id.co.lcs.apps.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by TED on 15-Jul-20
 */
public class Category implements Serializable {
    @SerializedName("Category")
    private String category;
    @SerializedName("Index")
    private int index;
    private ArrayList<Product> productArrayList = new ArrayList<>();

    public Category() {
    }

    public Category(String category, ArrayList<Product> productArrayList) {
        this.category = category;
        this.productArrayList = productArrayList;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public ArrayList<Product> getProductArrayList() {
        return productArrayList;
    }

    public void setProductArrayList(ArrayList<Product> productArrayList) {
        this.productArrayList = productArrayList;
    }
}
