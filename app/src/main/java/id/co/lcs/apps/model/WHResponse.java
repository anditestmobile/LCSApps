package id.co.lcs.apps.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by TED on 29-Sep-20
 */
public class WHResponse implements Serializable {
    private int StatusCode;
    private String StatusMessage;
    private List<Warehouse> ResponseData;

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

    public List<Warehouse> getResponseData() {
        return ResponseData;
    }

    public void setResponseData(List<Warehouse> responseData) {
        ResponseData = responseData;
    }
}
