package id.co.lcs.apps.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by TED on 15-Jul-20
 */
public class SerialAndBatchDetails implements Serializable {
    @SerializedName("BatchDetails")
    private List<BatchInternal> batchDetails;
    @SerializedName("SerialDetails")
    private List<SerialNumberInternal> serialDetails;

    public List<BatchInternal> getBatchDetails() {
        return batchDetails;
    }

    public void setBatchDetails(List<BatchInternal> batchDetails) {
        this.batchDetails = batchDetails;
    }

    public List<SerialNumberInternal> getSerialDetails() {
        return serialDetails;
    }

    public void setSerialDetails(List<SerialNumberInternal> serialDetails) {
        this.serialDetails = serialDetails;
    }
}
