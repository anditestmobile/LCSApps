package id.co.lcs.apps.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by TED on 15-Jul-20
 */
public class WSDocMessage implements Serializable {
    @SerializedName("DocEntry")
    private String docNum;
    @SerializedName("Error")
    private String errorMessage;

    public String getDocNum() {
        return docNum;
    }

    public void setDocNum(String docNum) {
        this.docNum = docNum;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
