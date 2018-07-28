package com.chase.timebank.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by chase on 2018/4/27.
 */

public class ReqMyBean implements Serializable{


    /**
     * rows : [{"reqGuid":"06944C72-DAC7-4699-A482-7CA7E6EAF2A7","reqIssueUserGuid":"F96D1D9D-F343-4DD2-B51A-F5F28DCF1F4A","reqIssueTime":1524811652567,"reqAddress":"004","reqTitle":"测试插入数据库","reqDesp":"应该可以成功","reqComment":"啦啦！","reqDispatchTime":null,"reqTypeGuidClass":"做饭","reqAvailableStartTime":1517590980000,"reqAvailableEndTime":null,"reqRreDurationTime":666,"reqPreCunsumeCurrency":null,"reqStartTime":null,"reqEndTime":null,"reqActualConsumeCurrency":null,"reqTypeApproveStatus":"待审核","reqTargetsUserGuid":null,"reqActualDurationTime":null,"reqPersonNum":null,"reqTypeGuidUrgency":"不急","reqFromWeightGuid":null,"reqTypeGuidProcessStatus":"待启动","reqProcessUserGuid":null},{"reqGuid":"990D498B-A582-444A-B80F-DA3B1F300441","reqIssueUserGuid":"F96D1D9D-F343-4DD2-B51A-F5F28DCF1F4A","reqIssueTime":1524792301867,"reqAddress":"苦啊","reqTitle":"中药好苦","reqDesp":"太苦了","reqComment":"我想吃糖","reqDispatchTime":null,"reqTypeGuidClass":"做饭","reqAvailableStartTime":1517587200000,"reqAvailableEndTime":1517795725000,"reqRreDurationTime":11,"reqPreCunsumeCurrency":null,"reqStartTime":null,"reqEndTime":null,"reqActualConsumeCurrency":null,"reqTypeApproveStatus":"待审核","reqTargetsUserGuid":null,"reqActualDurationTime":null,"reqPersonNum":1,"reqTypeGuidUrgency":"紧急","reqFromWeightGuid":null,"reqTypeGuidProcessStatus":"待启动","reqProcessUserGuid":null}]
     * total : 2
     */

    private int total;
    private ArrayList<RowsBean> rows;

    @Override
    public String toString() {
        return "ReqMyBean{" +
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

    public ArrayList<RowsBean> getRows() {
        return rows;
    }

    public void setRows(ArrayList<RowsBean> rows) {
        this.rows = rows;
    }

    public static class RowsBean implements Serializable{
        /**
         * reqGuid : 06944C72-DAC7-4699-A482-7CA7E6EAF2A7
         * reqIssueUserGuid : F96D1D9D-F343-4DD2-B51A-F5F28DCF1F4A
         * reqIssueTime : 1524811652567
         * reqAddress : 004
         * reqTitle : 测试插入数据库
         * reqDesp : 应该可以成功
         * reqComment : 啦啦！
         * reqDispatchTime : null
         * reqTypeGuidClass : 做饭
         * reqAvailableStartTime : 1517590980000
         * reqAvailableEndTime : null
         * reqRreDurationTime : 666
         * reqPreCunsumeCurrency : null
         * reqStartTime : null
         * reqEndTime : null
         * reqActualConsumeCurrency : null
         * reqTypeApproveStatus : 待审核
         * reqTargetsUserGuid : null
         * reqActualDurationTime : null
         * reqPersonNum : null
         * reqTypeGuidUrgency : 不急
         * reqFromWeightGuid : null
         * reqTypeGuidProcessStatus : 待启动
         * reqProcessUserGuid : null
         */

        private String reqGuid;
        private String reqIssueUserGuid;
        private String reqIssueTime;
        private String reqAddress;
        private String reqTitle;
        private String reqDesp;
        private String reqComment;
        private String reqDispatchTime;
        private String reqTypeGuidClass;
        private String reqAvailableStartTime;
        private String reqAvailableEndTime;
        private String reqRreDurationTime;
        private String reqPreCunsumeCurrency;
        private String reqStartTime;
        private String reqEndTime;
        private String reqActualConsumeCurrency;
        private String reqTypeApproveStatus;
        private String reqTargetsUserGuid;
        private String reqActualDurationTime;
        private String reqPersonNum;
        private String reqTypeGuidUrgency;
        private String reqFromWeightGuid;
        private String reqTypeGuidProcessStatus;
        private String reqProcessUserGuid;

        public String getReqGuid() {
            return reqGuid;
        }

        public void setReqGuid(String reqGuid) {
            this.reqGuid = reqGuid;
        }

        public String getReqIssueUserGuid() {
            return reqIssueUserGuid;
        }

        public void setReqIssueUserGuid(String reqIssueUserGuid) {
            this.reqIssueUserGuid = reqIssueUserGuid;
        }

        public String getReqIssueTime() {
            return reqIssueTime;
        }

        public void setReqIssueTime(String reqIssueTime) {
            this.reqIssueTime = reqIssueTime;
        }

        public String getReqAddress() {
            return reqAddress;
        }

        public void setReqAddress(String reqAddress) {
            this.reqAddress = reqAddress;
        }

        public String getReqTitle() {
            return reqTitle;
        }

        public void setReqTitle(String reqTitle) {
            this.reqTitle = reqTitle;
        }

        public String getReqDesp() {
            return reqDesp;
        }

        public void setReqDesp(String reqDesp) {
            this.reqDesp = reqDesp;
        }

        public String getReqComment() {
            return reqComment;
        }

        public void setReqComment(String reqComment) {
            this.reqComment = reqComment;
        }

        public String getReqDispatchTime() {
            return reqDispatchTime;
        }

        public void setReqDispatchTime(String reqDispatchTime) {
            this.reqDispatchTime = reqDispatchTime;
        }

        public String getReqTypeGuidClass() {
            return reqTypeGuidClass;
        }

        public void setReqTypeGuidClass(String reqTypeGuidClass) {
            this.reqTypeGuidClass = reqTypeGuidClass;
        }

        public String getReqAvailableStartTime() {
            return reqAvailableStartTime;
        }

        public void setReqAvailableStartTime(String reqAvailableStartTime) {
            this.reqAvailableStartTime = reqAvailableStartTime;
        }

        public String getReqAvailableEndTime() {
            return reqAvailableEndTime;
        }

        public void setReqAvailableEndTime(String reqAvailableEndTime) {
            this.reqAvailableEndTime = reqAvailableEndTime;
        }

        public String getReqRreDurationTime() {
            return reqRreDurationTime;
        }

        public void setReqRreDurationTime(String reqRreDurationTime) {
            this.reqRreDurationTime = reqRreDurationTime;
        }

        public String getReqPreCunsumeCurrency() {
            return reqPreCunsumeCurrency;
        }

        public void setReqPreCunsumeCurrency(String reqPreCunsumeCurrency) {
            this.reqPreCunsumeCurrency = reqPreCunsumeCurrency;
        }

        public String getReqStartTime() {
            return reqStartTime;
        }

        public void setReqStartTime(String reqStartTime) {
            this.reqStartTime = reqStartTime;
        }

        public String getReqEndTime() {
            return reqEndTime;
        }

        public void setReqEndTime(String reqEndTime) {
            this.reqEndTime = reqEndTime;
        }

        public String getReqActualConsumeCurrency() {
            return reqActualConsumeCurrency;
        }

        public void setReqActualConsumeCurrency(String reqActualConsumeCurrency) {
            this.reqActualConsumeCurrency = reqActualConsumeCurrency;
        }

        public String getReqTypeApproveStatus() {
            return reqTypeApproveStatus;
        }

        public void setReqTypeApproveStatus(String reqTypeApproveStatus) {
            this.reqTypeApproveStatus = reqTypeApproveStatus;
        }

        public String getReqTargetsUserGuid() {
            return reqTargetsUserGuid;
        }

        public void setReqTargetsUserGuid(String reqTargetsUserGuid) {
            this.reqTargetsUserGuid = reqTargetsUserGuid;
        }

        public String getReqActualDurationTime() {
            return reqActualDurationTime;
        }

        public void setReqActualDurationTime(String reqActualDurationTime) {
            this.reqActualDurationTime = reqActualDurationTime;
        }

        public String getReqPersonNum() {
            return reqPersonNum;
        }

        public void setReqPersonNum(String reqPersonNum) {
            this.reqPersonNum = reqPersonNum;
        }

        public String getReqTypeGuidUrgency() {
            return reqTypeGuidUrgency;
        }

        public void setReqTypeGuidUrgency(String reqTypeGuidUrgency) {
            this.reqTypeGuidUrgency = reqTypeGuidUrgency;
        }

        public String getReqFromWeightGuid() {
            return reqFromWeightGuid;
        }

        public void setReqFromWeightGuid(String reqFromWeightGuid) {
            this.reqFromWeightGuid = reqFromWeightGuid;
        }

        public String getReqTypeGuidProcessStatus() {
            return reqTypeGuidProcessStatus;
        }

        public void setReqTypeGuidProcessStatus(String reqTypeGuidProcessStatus) {
            this.reqTypeGuidProcessStatus = reqTypeGuidProcessStatus;
        }

        public String getReqProcessUserGuid() {
            return reqProcessUserGuid;
        }

        public void setReqProcessUserGuid(String reqProcessUserGuid) {
            this.reqProcessUserGuid = reqProcessUserGuid;
        }

        @Override
        public String toString() {
            return "GatherBean{" +
                    "reqGuid='" + reqGuid + '\'' +
                    ", reqIssueUserGuid='" + reqIssueUserGuid + '\'' +
                    ", reqIssueTime='" + reqIssueTime + '\'' +
                    ", reqAddress='" + reqAddress + '\'' +
                    ", reqTitle='" + reqTitle + '\'' +
                    ", reqDesp='" + reqDesp + '\'' +
                    ", reqComment='" + reqComment + '\'' +
                    ", reqDispatchTime='" + reqDispatchTime + '\'' +
                    ", reqTypeGuidClass='" + reqTypeGuidClass + '\'' +
                    ", reqAvailableStartTime='" + reqAvailableStartTime + '\'' +
                    ", reqAvailableEndTime='" + reqAvailableEndTime + '\'' +
                    ", reqRreDurationTime='" + reqRreDurationTime + '\'' +
                    ", reqPreCunsumeCurrency='" + reqPreCunsumeCurrency + '\'' +
                    ", reqStartTime='" + reqStartTime + '\'' +
                    ", reqEndTime='" + reqEndTime + '\'' +
                    ", reqActualConsumeCurrency='" + reqActualConsumeCurrency + '\'' +
                    ", reqTypeApproveStatus='" + reqTypeApproveStatus + '\'' +
                    ", reqTargetsUserGuid='" + reqTargetsUserGuid + '\'' +
                    ", reqActualDurationTime='" + reqActualDurationTime + '\'' +
                    ", reqPersonNum='" + reqPersonNum + '\'' +
                    ", reqTypeGuidUrgency='" + reqTypeGuidUrgency + '\'' +
                    ", reqFromWeightGuid='" + reqFromWeightGuid + '\'' +
                    ", reqTypeGuidProcessStatus='" + reqTypeGuidProcessStatus + '\'' +
                    ", reqProcessUserGuid='" + reqProcessUserGuid + '\'' +
                    '}';
        }
    }
}
