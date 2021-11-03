package id.co.lcs.apps.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by TED on 15-Jul-20
 */
public class GRRequest implements Serializable {
    @SerializedName("DocDueDate")
    private String grDate;
    @SerializedName("ToWhsCode")
    private String warehouse;

    public String getGrDate() {
        return grDate;
    }

    public void setGrDate(String grDate) {
        this.grDate = grDate;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }
}
