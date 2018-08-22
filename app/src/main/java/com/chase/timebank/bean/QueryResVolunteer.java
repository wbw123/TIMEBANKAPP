package com.chase.timebank.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by chase on 2018/5/14.
 */

public class QueryResVolunteer implements Serializable{

    /**
     * rows : [{"resGuid":"18044332-3BE9-4EA8-AEF2-A2B8B455CB6D","resReqGuid":"49542BD3-CA44-43F2-AAB7-0480087FA4B4","resUserGuid":"王泽东1","resAcceptTime":1523712945067,"resAcceptAddress":"","resServiceStartTime":null,"resServiceEndTime":null,"resTypeGuidProcessStatus":"撤销","resEvaluate":null,"resEarnCurrency":null,"resProcessUserGuid":null}]
     * total : 1
     */

    private int total;
    private ArrayList<VolunteerBean> rows;

    @Override
    public String toString() {
        return "QueryResVolunteer{" +
                "total=" + total +
                ", rows=" + rows +
                '}';
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public ArrayList<VolunteerBean> getRows() {
        return rows;
    }

    public void setRows(ArrayList<VolunteerBean> rows) {
        this.rows = rows;
    }

    public static class VolunteerBean implements Serializable {
        /**
         * resGuid : 18044332-3BE9-4EA8-AEF2-A2B8B455CB6D
         * resReqGuid : 49542BD3-CA44-43F2-AAB7-0480087FA4B4
         * resUserGuid : 王泽东1
         * resAcceptTime : 1523712945067
         * resAcceptAddress :
         * resServiceStartTime : null
         * resServiceEndTime : null
         * resTypeGuidProcessStatus : 撤销
         * resEvaluate : null
         * resEarnCurrency : null
         * resProcessUserGuid : null
         */

        private String resGuid;
        private String resReqGuid;
        private String resUserGuid;
        private String resAcceptTime;
        private String resAcceptAddress;
        private String resServiceStartTime;
        private String resServiceEndTime;
        private String resTypeGuidProcessStatus;
        private String resEvaluate;
        private String resEarnCurrency;
        private String resProcessUserGuid;
        private String resReqTitle;
        private String resReqAddr;

        @Override
        public String toString() {
            return "VolunteerBean{" +
                    "resGuid='" + resGuid + '\'' +
                    ", resReqGuid='" + resReqGuid + '\'' +
                    ", resUserGuid='" + resUserGuid + '\'' +
                    ", resAcceptTime='" + resAcceptTime + '\'' +
                    ", resAcceptAddress='" + resAcceptAddress + '\'' +
                    ", resServiceStartTime='" + resServiceStartTime + '\'' +
                    ", resServiceEndTime='" + resServiceEndTime + '\'' +
                    ", resTypeGuidProcessStatus='" + resTypeGuidProcessStatus + '\'' +
                    ", resEvaluate='" + resEvaluate + '\'' +
                    ", resEarnCurrency='" + resEarnCurrency + '\'' +
                    ", resProcessUserGuid='" + resProcessUserGuid + '\'' +
                    ", resReqTitle='" + resReqTitle + '\'' +
                    ", resReqAddr='" + resReqAddr + '\'' +
                    '}';
        }

        public String getResGuid() {
            return resGuid;
        }

        public void setResGuid(String resGuid) {
            this.resGuid = resGuid;
        }

        public String getResReqGuid() {
            return resReqGuid;
        }

        public void setResReqGuid(String resReqGuid) {
            this.resReqGuid = resReqGuid;
        }

        public String getResUserGuid() {
            return resUserGuid;
        }

        public void setResUserGuid(String resUserGuid) {
            this.resUserGuid = resUserGuid;
        }

        public String getResAcceptTime() {
            return resAcceptTime;
        }

        public void setResAcceptTime(String resAcceptTime) {
            this.resAcceptTime = resAcceptTime;
        }

        public String getResAcceptAddress() {
            return resAcceptAddress;
        }

        public void setResAcceptAddress(String resAcceptAddress) {
            this.resAcceptAddress = resAcceptAddress;
        }

        public String getResServiceStartTime() {
            return resServiceStartTime;
        }

        public void setResServiceStartTime(String resServiceStartTime) {
            this.resServiceStartTime = resServiceStartTime;
        }

        public String getResServiceEndTime() {
            return resServiceEndTime;
        }

        public void setResServiceEndTime(String resServiceEndTime) {
            this.resServiceEndTime = resServiceEndTime;
        }

        public String getResTypeGuidProcessStatus() {
            return resTypeGuidProcessStatus;
        }

        public void setResTypeGuidProcessStatus(String resTypeGuidProcessStatus) {
            this.resTypeGuidProcessStatus = resTypeGuidProcessStatus;
        }

        public String getResEvaluate() {
            return resEvaluate;
        }

        public void setResEvaluate(String resEvaluate) {
            this.resEvaluate = resEvaluate;
        }

        public String getResEarnCurrency() {
            return resEarnCurrency;
        }

        public void setResEarnCurrency(String resEarnCurrency) {
            this.resEarnCurrency = resEarnCurrency;
        }

        public String getResProcessUserGuid() {
            return resProcessUserGuid;
        }

        public void setResProcessUserGuid(String resProcessUserGuid) {
            this.resProcessUserGuid = resProcessUserGuid;
        }
        public String getResReqTitle() {
            return resReqTitle;
        }

        public void setResReqTitle(String resReqTitle) {
            this.resReqTitle = resReqTitle;
        }

        public String getResReqAddr() {
            return resReqAddr;
        }

        public void setResReqAddr(String resReqAddr) {
            this.resReqAddr = resReqAddr;
        }
    }
}
