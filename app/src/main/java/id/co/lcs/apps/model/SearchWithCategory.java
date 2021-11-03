package id.co.lcs.apps.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by TED on 15-Jul-20
 */
public class SearchWithCategory implements Serializable {
    @SerializedName("SearchStr")
    private String search;
    @SerializedName("Category")
    private String category;

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
