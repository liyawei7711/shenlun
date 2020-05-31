package huaiye.com.vim.models.contacts.bean;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity(tableName = "tb_group_list", primaryKeys ={"strGroupID","strGroupDomainCode"})
public class GroupInfo implements Serializable, Parcelable {
    @NonNull
    @ColumnInfo
    public String strGroupDomainCode;
    @NonNull
    @ColumnInfo
    public String strGroupID;
    @ColumnInfo
    public String strGroupName;
    @ColumnInfo
    public String strHeadUrl;
    @ColumnInfo
    public int nMsgTop;
    @ColumnInfo
    public int nNoDisturb;

    public GroupInfo(){

    }

    protected GroupInfo(Parcel in) {
        strGroupDomainCode = in.readString();
        strGroupID = in.readString();
        strGroupName = in.readString();
        strHeadUrl = in.readString();
        nMsgTop = in.readInt();
        nNoDisturb = in.readInt();
    }

    public static final Creator<GroupInfo> CREATOR = new Creator<GroupInfo>() {
        @Override
        public GroupInfo createFromParcel(Parcel in) {
            return new GroupInfo(in);
        }

        @Override
        public GroupInfo[] newArray(int size) {
            return new GroupInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(strGroupDomainCode);
        parcel.writeString(strGroupID);
        parcel.writeString(strGroupName);
        parcel.writeString(strHeadUrl);
        parcel.writeInt(nMsgTop);
        parcel.writeInt(nNoDisturb);
    }
}