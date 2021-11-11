package id.co.lcs.apps.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by TED on 15-Jul-20
 */
public class QuotationResponse implements Serializable {
    private int StatusCode;
    private String StatusMessage;
    private List<SalesOrderRequest> ResponseData;
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

    public List<SalesOrderRequest> getResponseData() {
        return ResponseData;
    }

    public void setResponseData(List<SalesOrderRequest> responseData) {
        ResponseData = responseData;
    }

    public String getSalesPeriod() {
        return SalesPeriod;
    }

    public void setSalesPeriod(String salesPeriod) {
        SalesPeriod = salesPeriod;
    }
}
