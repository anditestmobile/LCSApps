package id.co.lcs.apps.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by TED on 15-Jul-20
 */
public class Commited implements Serializable {
    @SerializedName("DocType")
    private String docType;
    @SerializedName("DocNum")
    private String docNum;
    @SerializedName("CommitedQty")
    private String commitedQty;

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getDocNum() {
        return docNum;
    }

    public void setDocNum(String docNum) {
        this.docNum = docNum;
    }

    public String getCommitedQty() {
        return commitedQty;
    }

    public void setCommitedQty(String commitedQty) {
        this.commitedQty = commitedQty;
    }
}
