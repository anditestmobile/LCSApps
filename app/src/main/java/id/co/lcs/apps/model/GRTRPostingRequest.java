package id.co.lcs.apps.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by TED on 15-Jul-20
 */

public class GRTRPostingRequest implements Serializable {
    @SerializedName("FromWhsCode")
    private String fromWH;
    @SerializedName("ToWhsCode")
    private String toWH;
    @SerializedName("DocNum")
    private String docNum;
    @SerializedName("DocDate")
    private String docDate;
    @SerializedName("DocDueDate")
    private String docDueDate;
    @SerializedName("ITItem")
    private List<GRTRPostingDetail> grTRDetails;

    public String getFromWH() {
        return fromWH;
    }

    public void setFromWH(String fromWH) {
        this.fromWH = fromWH;
    }

    public String getToWH() {
        return toWH;
    }

    public void setToWH(String toWH) {
        this.toWH = toWH;
    }

    public String getDocNum() {
        return docNum;
    }

    public void setDocNum(String docNum) {
        this.docNum = docNum;
    }

    public String getDocDate() {
        return docDate;
    }

    public void setDocDate(String docDate) {
        this.docDate = docDate;
    }

    public String getDocDueDate() {
        return docDueDate;
    }

    public void setDocDueDate(String docDueDate) {
        this.docDueDate = docDueDate;
    }

    public List<GRTRPostingDetail> getGrTRDetails() {
        return grTRDetails;
    }

    public void setGrTRDetails(List<GRTRPostingDetail> grTRDetails) {
        this.grTRDetails = grTRDetails;
    }
}
