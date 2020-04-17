package huaiye.com.vim.models.contacts.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class ContactsGroupUserListBean implements Serializable {
    public int nResultCode;
    public String strResultDescribe;
    public String strGroupDomainCode;
    public String strGroupID;
    public String strGroupName;
    public String strAnnouncement;
    public String strCreateTime;
    public String strCreaterDomainCode;
    public String strCreaterID;
    public int nBeinviteMode;
    public int nInviteMode;
    public int nTeamMemberLimit;
    public String strHeadUrl;

    public ArrayList<LstGroupUser> lstGroupUser;

    public static class LstGroupUser implements Serializable, Parcelable {
        public String strUserDomainCode;
        public String strUserID;
        public String strUserName;
        public String strHeadUrl;

        public LstGroupUser() {
        }

        protected LstGroupUser(Parcel in) {
            strUserDomainCode = in.readString();
            strUserID = in.readString();
            strUserName = in.readString();
            strHeadUrl = in.readString();
        }

        public static final Creator<LstGroupUser> CREATOR = new Creator<LstGroupUser>() {
            @Override
            public LstGroupUser createFromParcel(Parcel in) {
                return new LstGroupUser(in);
            }

            @Override
            public LstGroupUser[] newArray(int size) {
                return new LstGroupUser[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(strUserDomainCode);
            parcel.writeString(strUserID);
            parcel.writeString(strUserName);
            parcel.writeString(strHeadUrl);
        }
    }
}
