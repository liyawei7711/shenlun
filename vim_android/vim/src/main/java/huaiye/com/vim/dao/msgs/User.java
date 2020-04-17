package huaiye.com.vim.dao.msgs;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

@Entity(tableName = "tb_friend_list", primaryKeys ={"strUserID","strDomainCode"})
public class User implements Parcelable {

    @NonNull
    @ColumnInfo
    public String strUserID;
    /**
     * 登录名
     */
    @ColumnInfo
    public String strLoginName;
    /**
     * 用户名
     */
    @ColumnInfo
    public String strUserName;
    /**
     * 用户名拼音
     */
    @ColumnInfo
    public String strUserNamePinYin;
    /**
     * 用户角色编号
     */
    @ColumnInfo
    public int nRoleID;
    /**
     * 角色类型 1:超级管理员 2：自定义
     */
    @ColumnInfo
    public int nRoleType;
    /**
     * 性别（0：未知 1：男 2：女）
     */
    @ColumnInfo
    public int nSex;
    /**
     * 手机号码
     */
    @ColumnInfo
    public String strMobilePhone;
    /**
     * 优先级，越小越高
     */
    @ColumnInfo
    public int nPriority;
    /**
     * 备注
     */
    @ColumnInfo
    public String strRemark;
    @ColumnInfo
    public String strLastLoginTime;
    /**
     * 用户状态 -1:未登录 0:离线 1：空闲，2采集中，3：对讲中 4：会议中
     */
    @ColumnInfo
    public int nStatus;
    /**
     * 最新经度
     */
    @ColumnInfo
    public double dLongitude;
    /**
     * 最新纬度
     */
    @ColumnInfo
    public double dLatitude;
    /**
     * 最新高度
     */
    @ColumnInfo
    public double dHeight;
    /**
     * 当前速度
     */
    @ColumnInfo
    public double dSpeed;

    /**
     * 业务域code，与请求中的一致，请求中为空则为本域code
     */
    @NonNull
    @ColumnInfo
    public String strDomainCode;


    @Ignore
    public String strUserDomainCode;


    @ColumnInfo
    public String strCollectTime;

    @ColumnInfo
    public String strHeadUrl;

    /**
     * (可选)用户登录sie的token
     */
    @ColumnInfo
    public String strUserTokenID;
    /**
     * 频道域（注以下四个字段，nState=3有效）
     */
    @ColumnInfo
    public String strTrunkChannelDomainCode;
    /**
     * 频道ID
     */
    @ColumnInfo
    public int nTrunkChannelID;
    /**
     * 频道名
     */
    @ColumnInfo
    public String strTrunkChannelName;
    /**
     * 是否正在发言
     * 0：否 状态为对讲中
     * 1：是 状态为发言
     */
    @ColumnInfo
    public int nSpeaking;
    /**
     * 登陆终端类型 1：android 2：ios 3：PC 4：web
     */
    @ColumnInfo
    public int nDevType;
    /**
     * 用户所属部门(0表示没有部门)
     */
    @ColumnInfo
    public int nDepID;
    /**
     * 部门名称
     */
    @ColumnInfo
    public String strDepName;
    @ColumnInfo
    public String strRoleName;
    /**
     * 终端设备也当成人处理
     */
    @ColumnInfo
    public int deviceType;

    @Ignore
    public int nJoinStatus;
    @Ignore
    public boolean canDel;


    public User() {
    }

    public User(Parcel source) {
        strUserID = source.readString();
        strLoginName = source.readString();
        strUserName = source.readString();
        strUserNamePinYin = source.readString();
        nRoleID = source.readInt();
        nRoleType = source.readInt();
        nSex = source.readInt();
        strMobilePhone = source.readString();
        nPriority = source.readInt();
        strRemark = source.readString();
        strLastLoginTime = source.readString();
        nStatus = source.readInt();
        dLongitude = source.readDouble();
        dLatitude = source.readDouble();
        dHeight = source.readDouble();
        dSpeed = source.readDouble();
        strDomainCode = source.readString();
        strUserTokenID = source.readString();
        strTrunkChannelDomainCode = source.readString();
        nTrunkChannelID = source.readInt();
        strTrunkChannelName = source.readString();
        nSpeaking = source.readInt();
        nDevType = source.readInt();
        nDepID = source.readInt();
        strDepName = source.readString();
        strRoleName = source.readString();
        deviceType = source.readInt();
        strCollectTime = source.readString();
        strHeadUrl = source.readString();
        nJoinStatus = source.readInt();
        strUserDomainCode = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(strUserID);
        dest.writeString(strLoginName);
        dest.writeString(strUserName);
        dest.writeString(strUserNamePinYin);
        dest.writeInt(nRoleID);
        dest.writeInt(nRoleType);
        dest.writeInt(nSex);
        dest.writeString(strMobilePhone);
        dest.writeInt(nPriority);
        dest.writeString(strRemark);
        dest.writeString(strLastLoginTime);
        dest.writeInt(nStatus);
        dest.writeDouble(dLongitude);
        dest.writeDouble(dLatitude);
        dest.writeDouble(dHeight);
        dest.writeDouble(dSpeed);
        dest.writeString(strDomainCode);
        dest.writeString(strUserTokenID);
        dest.writeString(strTrunkChannelDomainCode);
        dest.writeInt(nTrunkChannelID);
        dest.writeString(strTrunkChannelName);
        dest.writeInt(nSpeaking);
        dest.writeInt(nDevType);
        dest.writeInt(nDepID);
        dest.writeString(strDepName);
        dest.writeString(strRoleName);
        dest.writeInt(deviceType);
        dest.writeString(strCollectTime);
        dest.writeString(strHeadUrl);
        dest.writeInt(nJoinStatus);
        dest.writeString(strUserDomainCode);

    }

    public static final Parcelable.Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };


}