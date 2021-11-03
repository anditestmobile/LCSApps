package id.co.lcs.apps.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CategoryResponse implements Serializable {
    private int StatusCode;
    private String StatusMessage;
    private List<Category> ResponseData;

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

    public List<Category> getResponseData() {
        return ResponseData;
    }

    public void setResponseData(List<Category> responseData) {
        ResponseData = responseData;
    }
}
