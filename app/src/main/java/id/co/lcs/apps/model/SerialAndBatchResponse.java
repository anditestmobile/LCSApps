package id.co.lcs.apps.model;

import java.io.Serializable;
import java.util.ArrayList;

public class SerialAndBatchResponse implements Serializable {
    private int StatusCode;
    private String StatusMessage;
    private ArrayList<SerialAndBatchDetails> ResponseData;

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

    public ArrayList<SerialAndBatchDetails> getResponseData() {
        return ResponseData;
    }

    public void setResponseData(ArrayList<SerialAndBatchDetails> responseData) {
        ResponseData = responseData;
    }
}
