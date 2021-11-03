package id.co.lcs.apps.model;

import java.io.Serializable;
import java.util.List;

public class CommitedResponse implements Serializable {
    private int StatusCode;
    private String StatusMessage;
    private List<Commited> ResponseData;

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

    public List<Commited> getResponseData() {
        return ResponseData;
    }

    public void setResponseData(List<Commited> responseData) {
        ResponseData = responseData;
    }
}
