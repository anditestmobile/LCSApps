package id.co.lcs.apps.model;

import java.util.ArrayList;

/**
 * Created by TED on 15-Jul-20
 */
public class UserLogin {
    private String UserID;
    private String Pwd;

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getPwd() {
        return Pwd;
    }

    public void setPwd(String pwd) {
        Pwd = pwd;
    }
}
