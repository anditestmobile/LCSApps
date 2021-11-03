package id.co.lcs.apps.model;

import java.io.Serializable;

public class GRPostingResponse implements Serializable {
    private int StatusCode;
    private String StatusMessage;
    private WSMessage ResponseData;

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

    public WSMessage getResponseData() {
        return ResponseData;
    }

    public void setResponseData(WSMessage responseData) {
        ResponseData = responseData;
    }
}
