package huaiye.com.vim.dao.msgs;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EncyptJsonDao implements Serializable {


    /**
     * ProductName : HY_VIM
     * License : RT_KMC
     * Package : {"FileName":"00000000000000000015_2019111115028.dat","FileSize":222,"CreateDate":"2019111115028","IDA":"00000000000000000015","Version":"1001","SHA-256":"b68a0627fe719e041f1750eefbb83dc5c6ff5ad94be93d8b703bc5d5d47d0365"}
     */

    private String ProductName;
    private String License;
    private PackageBean Package;

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String ProductName) {
        this.ProductName = ProductName;
    }

    public String getLicense() {
        return License;
    }

    public void setLicense(String License) {
        this.License = License;
    }

    public PackageBean getPackage() {
        return Package;
    }

    public void setPackage(PackageBean Package) {
        this.Package = Package;
    }

    public static class PackageBean implements Serializable{
        /**
         * FileName : 00000000000000000015_2019111115028.dat
         * FileSize : 222
         * CreateDate : 2019111115028
         * IDA : 00000000000000000015
         * Version : 1001
         * SHA-256 : b68a0627fe719e041f1750eefbb83dc5c6ff5ad94be93d8b703bc5d5d47d0365
         */

        private String FileName;
        private int FileSize;
        private String CreateDate;
        private String IDA;
        private String Version;
        @SerializedName("SHA-256")
        private String SHA256;

        public String getFileName() {
            return FileName;
        }

        public void setFileName(String FileName) {
            this.FileName = FileName;
        }

        public int getFileSize() {
            return FileSize;
        }

        public void setFileSize(int FileSize) {
            this.FileSize = FileSize;
        }

        public String getCreateDate() {
            return CreateDate;
        }

        public void setCreateDate(String CreateDate) {
            this.CreateDate = CreateDate;
        }

        public String getIDA() {
            return IDA;
        }

        public void setIDA(String IDA) {
            this.IDA = IDA;
        }

        public String getVersion() {
            return Version;
        }

        public void setVersion(String Version) {
            this.Version = Version;
        }

        public String getSHA256() {
            return SHA256;
        }

        public void setSHA256(String SHA256) {
            this.SHA256 = SHA256;
        }
    }
}
