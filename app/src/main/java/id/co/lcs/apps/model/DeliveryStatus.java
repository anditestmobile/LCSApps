package id.co.lcs.apps.model;

/**
 * Created by TED on 19-Jul-20
 */
public class DeliveryStatus {

    private String date;
    private String doNumber;
    private String status;

    public DeliveryStatus(String date, String doNumber, String status) {
        this.date = date;
        this.doNumber = doNumber;
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDoNumber() {
        return doNumber;
    }

    public void setDoNumber(String doNumber) {
        this.doNumber = doNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
