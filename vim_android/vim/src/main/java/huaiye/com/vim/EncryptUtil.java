package huaiye.com.vim;

import android.text.TextUtils;

import com.huaiye.cmf.sdp.SdpMessageCmProcessIMReq;
import com.huaiye.cmf.sdp.SdpMessageCmProcessIMRsp;
import com.huaiye.cmf.sdp.SdpMessageCmStartSessionRsp;
import com.huaiye.cmf.sdp.SdpMessageCmStopSessionRsp;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.sdkabi._api.ApiEncrypt;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdkabi._params.encrypt.ParamsEncryptStartSession;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.msgs.FileLocalNameBean;

public class EncryptUtil {

    /**
     * 新视频加密
     *
     * @param isCaller
     * @param otherId
     * @param otherDomain
     * @param nMeetID
     * @param strMeetDomainCode
     * @param callback
     */
    public static void startEncrypt(boolean isCaller, final String otherId, String otherDomain,
                                    String nMeetID, String strMeetDomainCode,
                                    final SdkCallback<SdpMessageCmStartSessionRsp> callback) {
        int type = TextUtils.isEmpty(nMeetID) ? ParamsEncryptStartSession.SCM_CALL_TYPE_PEER_VIDEO : ParamsEncryptStartSession.SCM_CALL_TYPE_GROUP_VIDEO;
        ParamsEncryptStartSession paramsEncryptStartSession = SdkParamsCenter.Encrypt.EncryptStartSession()
                .setCallType(type)
                .setPeerID(otherId)
                .setPeerDomainCode(otherDomain)
                .setIsCaller(isCaller)
                .setGroupID(nMeetID)
                .setGroupDomainCode(strMeetDomainCode);

        HYClient.getModule(ApiEncrypt.class)
                .encryptStartSession(paramsEncryptStartSession
                        , new SdkCallback<SdpMessageCmStartSessionRsp>() {
                            @Override
                            public void onSuccess(SdpMessageCmStartSessionRsp resp) {
                                if (callback != null) {
                                    callback.onSuccess(resp);
                                }
                            }

                            @Override
                            public void onError(ErrorInfo error) {
                                if (callback != null) {
                                    callback.onError(error);
                                }
                            }
                        });
    }

    /**
     * 音视频解密
     *
     * @param callID
     */
    public static void endEncrypt(int callID) {
        HYClient.getModule(ApiEncrypt.class)
                .encryptStopSession(SdkParamsCenter.Encrypt.EncryptStopSession()
                        .setCallID(callID), new SdkCallback<SdpMessageCmStopSessionRsp>() {
                    @Override
                    public void onSuccess(SdpMessageCmStopSessionRsp resp) {
                    }

                    @Override
                    public void onError(ErrorInfo error) {
                    }
                });
    }

    /**
     * setM_strData: 设置加密数据
     * setDoEncrypt：设置是加密还是解密，加密为true,解密为false
     * setCallType： 设置加密类型，群组为true,点对点为false
     * setGroupID: 群组聊天时需要设置，设置群组id,
     * setGroupDomainCode：群组聊天时需要设置，设置群组域编码
     * setPeerID： 点对点时需要设置，为对方的id
     * setPeerDomainCode： 点对点时需要设置，为对方的域编码
     * setUsers： 群组聊天和点对点都需要设置，点对点设置为对端，群组设置为除自己以外所有成员
     */
    public static void encryptTxt(String strText, boolean setDoEncrypt, boolean setCallType,
                                  String setGroupID, String setGroupDomainCode,
                                  String peerid, String peerdomain,
                                  ArrayList<SdpMessageCmProcessIMReq.UserInfo> users, final SdkCallback<SdpMessageCmProcessIMRsp> callback) {
        HYClient.getModule(ApiEncrypt.class).encryptTextMsg(
                SdkParamsCenter.Encrypt.EncryptTextMsg()
                        .setM_strData(strText)
                        .setDoEncrypt(setDoEncrypt)
                        .setCallType(setCallType)
                        .setUsers(users)
                        .setGroupID(setGroupID)
                        .setGroupDomainCode(setGroupDomainCode)
                        .setPeerDomainCode(peerdomain)
                        .setPeerID(peerid),
                new SdkCallback<SdpMessageCmProcessIMRsp>() {
                    @Override
                    public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                        if (callback != null) {
                            callback.onSuccess(resp);
                        }
                    }

                    @Override
                    public void onError(ErrorInfo error) {
                        if (callback != null) {
                            callback.onError(error);
                        }
                    }
                }
        );
    }

    /**
     * 文本信息本地加密
     *
     * @param strMsg       设置加密数据
     * @param setDoEncrypt 设置是加密还是解密，加密为true,解密为false
     *                     m_lstData: 返回列表中会有一个用户为自己的对应加解密数据
     */
    public static void localEncryptText(String strMsg, boolean setDoEncrypt, SdkCallback<SdpMessageCmProcessIMRsp> callback) {
        HYClient.getModule(ApiEncrypt.class).localEncryptTextMsg(
                SdkParamsCenter.Encrypt.LocalEncryptTextMsg()
                        .setDoEncrypt(setDoEncrypt)
                        .setM_strData(strMsg)
                , new SdkCallback<SdpMessageCmProcessIMRsp>() {
                    @Override
                    public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                        if (callback != null) {
                            callback.onSuccess(resp);
                        }
                    }

                    @Override
                    public void onError(ErrorInfo error) {
                        if (callback != null) {
                            callback.onError(error);
                        }
                    }
                }
        );
    }

    /**
     * setSrcFile: 设置要加密的文件路径
     * setDstFile：设置要加密后的文件路径
     * setDoEncrypt：设置是加密还是解密，加密为true,解密为false
     * *  setCallType： 设置加密类型，群组为true,点对点为false
     * *  setGroupID: 群组聊天时需要设置，设置群组id,
     * *  setGroupDomainCode：群组聊天时需要设置，设置群组域编码
     * * setPeerID： 点对点时需要设置，为对方的id
     * *  setPeerDomainCode： 点对点时需要设置，为对方的域编码
     * * setUsers： 群组聊天和点对点都需要设置，点对点设置为对端，群组设置为除自己以外所有成员
     */
    public static void encryptFile(String setSrcFile, String setDstFile,
                                   boolean setDoEncrypt, boolean setCallType,
                                   String setGroupID, String setGroupDomainCode,
                                   String peerid, String peerdomain,
                                   ArrayList<SdpMessageCmProcessIMReq.UserInfo> users, final SdkCallback<SdpMessageCmProcessIMRsp> callback) {
        HYClient.getModule(ApiEncrypt.class).encryptFile(
                SdkParamsCenter.Encrypt.EncryptFile()
                        .setSrcFile(setSrcFile)
                        .setDstFile(setDstFile)
                        .setDoEncrypt(setDoEncrypt)
                        .setCallType(setCallType)
                        .setGroupID(setGroupID)
                        .setGroupDomainCode(setGroupDomainCode)
                        .setPeerDomainCode(peerdomain)
                        .setPeerID(peerid)
                        .setUsers(users),
                new SdkCallback<SdpMessageCmProcessIMRsp>() {
                    @Override
                    public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                        if (callback != null) {
                            callback.onSuccess(resp);
                        }
                    }

                    @Override
                    public void onError(ErrorInfo error) {
                        if (callback != null) {
                            callback.onError(error);
                        }
                    }
                }
        );
    }

    /**
     * 文件本地加密
     *
     * @param srcFile      设置要加密的文件路径
     * @param dstFile      设置要加密后的文件路径
     * @param setDoEncrypt 设置是加密还是解密，加密为true,解密为false
     *                     m_strData: 加密后或解密后文件路径
     */
    public static void localEncryptFile(String srcFile, String dstFile,
                                        boolean setDoEncrypt, SdkCallback<SdpMessageCmProcessIMRsp> callback) {
        HYClient.getModule(ApiEncrypt.class).localEncryptFile(
                SdkParamsCenter.Encrypt.LocalEncryptFile()
                        .setDoEncrypt(setDoEncrypt)
                        .setSrcFile(srcFile)
                        .setDstFile(dstFile)
                , new SdkCallback<SdpMessageCmProcessIMRsp>() {
                    @Override
                    public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                        VIMApp.getInstance().addLinShiFile(resp.m_strData);
                        if (callback != null) {
                            callback.onSuccess(resp);
                        }
                    }

                    @Override
                    public void onError(ErrorInfo error) {
                        if (callback != null) {
                            callback.onError(error);
                        }
                    }
                }
        );
    }

    /**
     * 传输加密文本转换为本地加密文本
     *
     * @param setM_strData       数据
     * @param setCallType        设置加密类型，群组为true,点对点为false
     * m_lstData: m_nResultCode为0，列表中只有一条本地加密密文数据
     */
    public static void converEncryptText(String setM_strData, boolean setCallType,
                                         String setGroupID, String setGroupDomainCode,
                                         String setPeerID, String setPeerDomainCode, SdkCallback<SdpMessageCmProcessIMRsp> callback) {
        HYClient.getModule(ApiEncrypt.class).convertEncryptTextMsg(
                SdkParamsCenter.Encrypt.ConvertEncryptTextMsg()
                        .setM_strData(setM_strData)
                        .setCallType(setCallType)
                        .setGroupDomainCode(setGroupDomainCode)
                        .setGroupID(setGroupID)
                        .setPeerID(setPeerID)
                        .setPeerDomainCode(setPeerDomainCode)
                , new SdkCallback<SdpMessageCmProcessIMRsp>() {
                    @Override
                    public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                        if (callback != null) {
                            callback.onSuccess(resp);
                        }
                    }

                    @Override
                    public void onError(ErrorInfo error) {
                        if (callback != null) {
                            callback.onError(error);
                        }
                    }
                }
        );
    }

    /**
     * 传输加密文件转换为本地加密文件
     * @param srcFile 设置要加密的文件路径
     * @param dstFile 设置要加密后的文件路径
     * @param setCallType  设置加密类型，群组为true,点对点为false
     * @param setGroupID  群组聊天时需要设置，设置群组id,
     * @param setGroupDomainCode 群组聊天时需要设置，设置群组域编码
     * @param setPeerID 点对点/群组时需要设置，为对方的id
     * @param setPeerDomainCode 点对点/群组时需要设置，为对方的域编码
     * m_strData: 转换后的本地加密后或解密后文件
     */
    public static void converEncryptFile(String srcFile, String dstFile, boolean setCallType,
                                         String setGroupID, String setGroupDomainCode,
                                         String setPeerID, String setPeerDomainCode, SdkCallback<SdpMessageCmProcessIMRsp> callback) {
        HYClient.getModule(ApiEncrypt.class).convertEncryptFile(
                SdkParamsCenter.Encrypt.ConvertEncryptFile()
                        .setSrcFile(srcFile)
                        .setDstFile(dstFile)
                        .setCallType(setCallType)
                        .setGroupID(setGroupID)
                        .setGroupDomainCode(setGroupDomainCode)
                        .setPeerID(setPeerID)
                        .setPeerDomainCode(setPeerDomainCode)
                , new SdkCallback<SdpMessageCmProcessIMRsp>() {
                    @Override
                    public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                        if (callback != null) {
                            callback.onSuccess(resp);
                        }
                    }

                    @Override
                    public void onError(ErrorInfo error) {
                        if (callback != null) {
                            callback.onError(error);
                        }
                    }
                }
        );
    }

    /**
     * 获取新的文件名称
     *
     * @param file
     * @return
     */
    public static String getNewFile(String file) {
        FileLocalNameBean bean = AppDatas.MsgDB().getFileLocalListDao().getFileLocalInfo(file);
        if (bean == null) {
            String fortFile = file.substring(0, file.lastIndexOf("/"));
            String realName = file.substring(file.lastIndexOf("/"));
            String endString = "";
            try {
                endString = realName.substring(realName.lastIndexOf("."));
            } catch (Exception e) {

            }
            String newName = fortFile + "/" + System.currentTimeMillis() + endString;

            AppDatas.MsgDB().getFileLocalListDao().insert(new FileLocalNameBean(file, newName));

            return newName;
        } else {
            return bean.localFile;
        }

    }

    /**
     * 使用指定哈希算法计算摘要信息
     *
     * @param content   内容
     * @param algorithm 哈希算法
     * @return 内容摘要
     */
    public static String getMD5Digest(String content, String algorithm) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            messageDigest.update(content.getBytes("utf-8"));
            return bytesToHexString(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将字节数组转换成16进制字符串
     *
     * @param bytes 即将转换的数据
     * @return 16进制字符串
     */
    private static String bytesToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer(bytes.length);
        String temp = null;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(0xFF & bytes[i]);
            if (temp.length() < 2) {
                sb.append(0);
            }
            sb.append(temp);
        }
        return sb.toString();
    }

}
