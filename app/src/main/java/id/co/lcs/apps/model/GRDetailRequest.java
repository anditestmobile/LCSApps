package id.co.lcs.apps.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by TED on 15-Jul-20
 */
public class GRDetailRequest implements Serializable {
    @SerializedName("GRNo")
    private String grNo;

    public String getGrNo() {
        return grNo;
    }

    public void setGrNo(String grNo) {
        this.grNo = grNo;
    }
}
