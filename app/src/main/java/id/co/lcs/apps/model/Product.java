package id.co.lcs.apps.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by TED on 15-Jul-20
 */

public class Product implements Serializable {
    @SerializedName("ItemCode")
    private String productCode;
    @SerializedName("ItemName")
    private String productName;
    @SerializedName("UomName")
    private String uomName;
    @SerializedName("Category")
    private String productCategory;
//    @SerializedName("ImgURL")
    @SerializedName("LocalImgURL")
    private String imageUrl;
    @SerializedName("InStock")
    private String inStock;
    @SerializedName("Price")
    private double price;
    @SerializedName("TotalIndex")
    private int totaIndex;
    @SerializedName("RowNumber")
    private int rowNumber;
    @SerializedName("SalesAmt")
    private double salesAmt;
    private int qty;

    private boolean wishList;
    private boolean statusCheckout;
    private ArrayList<ProductDetail> productDetailArrayList;
    private ArrayList<ProductDetail> productDetailArrayListMore;

    public Product() {
    }

    public Product(String productCode, String productName, String uomName, String productCategory, String imageUrl, String inStock, double price, int totaIndex, int rowNumber, double salesAmt, int qty, boolean wishList, boolean statusCheckout, ArrayList<ProductDetail> productDetailArrayList, ArrayList<ProductDetail> productDetailArrayListMore) {
        this.productCode = productCode;
        this.productName = productName;
        this.uomName = uomName;
        this.productCategory = productCategory;
        this.imageUrl = imageUrl;
        this.inStock = inStock;
        this.price = price;
        this.totaIndex = totaIndex;
        this.rowNumber = rowNumber;
        this.salesAmt = salesAmt;
        this.qty = qty;
        this.wishList = wishList;
        this.statusCheckout = statusCheckout;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ArrayList<ProductDetail> getProductDetailArrayListMore() {
        return productDetailArrayListMore;
    }

    public void setProductDetailArrayListMore(ArrayList<ProductDetail> productDetailArrayListMore) {
        this.productDetailArrayListMore = productDetailArrayListMore;
    }

    public ArrayList<ProductDetail> getProductDetailArrayList() {
        return productDetailArrayList;
    }

    public void setProductDetailArrayList(ArrayList<ProductDetail> productDetailArrayList) {
        this.productDetailArrayList = productDetailArrayList;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public boolean isWishList() {
        return wishList;
    }

    public void setWishList(boolean wishList) {
        this.wishList = wishList;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isStatusCheckout() {
        return statusCheckout;
    }

    public void setStatusCheckout(boolean statusCheckout) {
        this.statusCheckout = statusCheckout;
    }

    public int getTotaIndex() {
        return totaIndex;
    }

    public void setTotaIndex(int totaIndex) {
        this.totaIndex = totaIndex;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getInStock() {
        return inStock;
    }

    public void setInStock(String inStock) {
        this.inStock = inStock;
    }

    public double getSalesAmt() {
        return salesAmt;
    }

    public void setSalesAmt(double salesAmt) {
        this.salesAmt = salesAmt;
    }

    public String getUomName() {
        return uomName;
    }

    public void setUomName(String uomName) {
        this.uomName = uomName;
    }
}
