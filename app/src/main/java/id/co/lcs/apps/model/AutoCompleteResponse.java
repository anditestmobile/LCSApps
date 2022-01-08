package id.co.lcs.apps.model;

import java.io.Serializable;
import java.util.List;

public class AutoCompleteResponse implements Serializable {
    private int StatusCode;
    private String StatusMessage;
    private List<AutoComplete> ResponseData;

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

    public List<AutoComplete> getResponseData() {
        return ResponseData;
    }

    public void setResponseData(List<AutoComplete> responseData) {
        ResponseData = responseData;
    }
}
