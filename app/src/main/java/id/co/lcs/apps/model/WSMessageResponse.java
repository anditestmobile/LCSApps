package id.co.lcs.apps.model;

import java.io.Serializable;

/**
 * Created by TED on 15-Jul-20
 */
public class WSMessageResponse implements Serializable {
    private int StatusCode;
    private String StatusMessage;
    private WSMessage ResponseData;
    private String SalesPeriod;

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

    public String getSalesPeriod() {
        return SalesPeriod;
    }

    public void setSalesPeriod(String salesPeriod) {
        SalesPeriod = salesPeriod;
    }
}
