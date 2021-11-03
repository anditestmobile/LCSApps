package id.co.lcs.apps.model;

import java.io.Serializable;

/**
 * Created by TED on 15-Jul-20
 */
public class StockTakeResponse implements Serializable {
    private int StatusCode;
    private String StatusMessage;
    private WSDocMessage ResponseData;
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

    public WSDocMessage getResponseData() {
        return ResponseData;
    }

    public void setResponseData(WSDocMessage responseData) {
        ResponseData = responseData;
    }

    public String getSalesPeriod() {
        return SalesPeriod;
    }

    public void setSalesPeriod(String salesPeriod) {
        SalesPeriod = salesPeriod;
    }
}
