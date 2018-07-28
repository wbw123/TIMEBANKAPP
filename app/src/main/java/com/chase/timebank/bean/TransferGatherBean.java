package com.chase.timebank.bean;

import java.util.ArrayList;

/**
 * Created by chase on 2018/5/16.
 */

public class TransferGatherBean {


    @Override
    public String toString() {
        return "TransferGatherBean{" +
                "total=" + total +
                ", rows=" + rows +
                '}';
    }

    /**
     * rows : [{"transGuid":"4C03CAAF-AE6F-41B7-9575-A9C33727F1D5","transFromUserGuid":"DEAE6A6C-5948-49D4-BEC2-ED99D34561F2","transToUserGuid":"王泽东1","transDesp":"","transIssueTime":1523262176303,"transTypeGuidProcessStatus":"已完成","transProcessTime":null,"transProcessDesp":null,"transCurrency":10}]
     * total : 1
     */

    private int total;
    private ArrayList<GatherBean> rows;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public ArrayList<GatherBean> getRows() {
        return rows;
    }

    public void setRows(ArrayList<GatherBean> rows) {
        this.rows = rows;
    }

    public static class GatherBean {
        /**
         * transGuid : 4C03CAAF-AE6F-41B7-9575-A9C33727F1D5
         * transFromUserGuid : DEAE6A6C-5948-49D4-BEC2-ED99D34561F2
         * transToUserGuid : 王泽东1
         * transDesp :
         * transIssueTime : 1523262176303
         * transTypeGuidProcessStatus : 已完成
         * transProcessTime : null
         * transProcessDesp : null
         * transCurrency : 10.0
         */

        private String transGuid;
        private String transFromUserGuid;
        private String transToUserGuid;
        private String transDesp;
        private long transIssueTime;
        private String transTypeGuidProcessStatus;
        private String transProcessTime;
        private String transProcessDesp;
        private double transCurrency;

        public String getTransGuid() {
            return transGuid;
        }

        public void setTransGuid(String transGuid) {
            this.transGuid = transGuid;
        }

        public String getTransFromUserGuid() {
            return transFromUserGuid;
        }

        public void setTransFromUserGuid(String transFromUserGuid) {
            this.transFromUserGuid = transFromUserGuid;
        }

        public String getTransToUserGuid() {
            return transToUserGuid;
        }

        public void setTransToUserGuid(String transToUserGuid) {
            this.transToUserGuid = transToUserGuid;
        }

        public String getTransDesp() {
            return transDesp;
        }

        public void setTransDesp(String transDesp) {
            this.transDesp = transDesp;
        }

        public long getTransIssueTime() {
            return transIssueTime;
        }

        public void setTransIssueTime(long transIssueTime) {
            this.transIssueTime = transIssueTime;
        }

        public String getTransTypeGuidProcessStatus() {
            return transTypeGuidProcessStatus;
        }

        public void setTransTypeGuidProcessStatus(String transTypeGuidProcessStatus) {
            this.transTypeGuidProcessStatus = transTypeGuidProcessStatus;
        }

        public String getTransProcessTime() {
            return transProcessTime;
        }

        public void setTransProcessTime(String transProcessTime) {
            this.transProcessTime = transProcessTime;
        }

        public String getTransProcessDesp() {
            return transProcessDesp;
        }

        public void setTransProcessDesp(String transProcessDesp) {
            this.transProcessDesp = transProcessDesp;
        }

        public double getTransCurrency() {
            return transCurrency;
        }

        public void setTransCurrency(double transCurrency) {
            this.transCurrency = transCurrency;
        }

        @Override
        public String toString() {
            return "GatherBean{" +
                    "transGuid='" + transGuid + '\'' +
                    ", transFromUserGuid='" + transFromUserGuid + '\'' +
                    ", transToUserGuid='" + transToUserGuid + '\'' +
                    ", transDesp='" + transDesp + '\'' +
                    ", transIssueTime=" + transIssueTime +
                    ", transTypeGuidProcessStatus='" + transTypeGuidProcessStatus + '\'' +
                    ", transProcessTime='" + transProcessTime + '\'' +
                    ", transProcessDesp='" + transProcessDesp + '\'' +
                    ", transCurrency=" + transCurrency +
                    '}';
        }
    }
}
