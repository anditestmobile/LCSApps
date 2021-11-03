package id.co.lcs.apps.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by TED on 15-Jul-20
 */
public class StockTRReponse implements Serializable {
    private int StatusCode;
    private String StatusMessage;
    private ArrayList<TRStock> ResponseData;

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

    public ArrayList<TRStock> getResponseData() {
        return ResponseData;
    }

    public void setResponseData(ArrayList<TRStock> responseData) {
        ResponseData = responseData;
    }
}
