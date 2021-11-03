package id.co.lcs.apps.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by TED on 29-Sep-20
 */
public class LoginResponse implements Serializable {
    private int StatusCode;
    private String StatusMessage;
    private List<User> ResponseData;

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

    public List<User> getResponseData() {
        return ResponseData;
    }

    public void setResponseData(List<User> responseData) {
        ResponseData = responseData;
    }
}
