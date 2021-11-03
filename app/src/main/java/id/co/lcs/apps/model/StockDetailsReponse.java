package id.co.lcs.apps.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by TED on 15-Jul-20
 */
public class StockDetailsReponse implements Serializable {
    private int StatusCode;
    private String StatusMessage;
    private ArrayList<WMStock> ResponseData;

    public int getStatusCode() {
        return StatusCode;
    }

    public void setStatusCode(int statusCode) {
        StatusCode = statusCode;
    }

    public String getStatusMessage() {
        return StatusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        StatusMessage = statusMessage;
    }

    public ArrayList<WMStock> getResponseData() {
        return ResponseData;
    }

    public void setResponseData(ArrayList<WMStock> responseData) {
        ResponseData = responseData;
    }
}
