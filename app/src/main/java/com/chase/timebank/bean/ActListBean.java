package com.chase.timebank.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by chase on 2018/5/2.
 */

public class ActListBean implements Serializable{

    /**
     * rows : [{"activityGuid":"3D288153-4623-489B-82B4-1EF799E97176","activityTitle":"村上春树","activityDesp":"根深蒂固","activityComment":"电话费","activityStartTime":1520190180000,"activityEndTime":1520190180000,"activityProcessUserGuid":"A395AC24-091D-410D-BD9B-5DBCCBCC1226","activityAddress":"发射点","activityFromCommGuid":"山科大小区","activityTypeProcessStatus":"33333333-94E3-4EB7-AAD3-333333333333","activityTargetsUserGuid":null,"activityPersonNum":2},{"activityGuid":"E28ED062-5A02-4E59-9962-4DECAC1F66C7","activityTitle":"还给你","activityDesp":"宁国府","activityComment":"方方面面","activityStartTime":1520190180000,"activityEndTime":1520190180000,"activityProcessUserGuid":"A395AC24-091D-410D-BD9B-5DBCCBCC1226","activityAddress":"规划和你","activityFromCommGuid":"山科大小区","activityTypeProcessStatus":"33333333-94E3-4EB7-AAD3-333333333333","activityTargetsUserGuid":"[\"A395AC24-091D-410D-BD9B-5DBCCBCC1232\"]","activityPersonNum":12},{"activityGuid":"BB6A9706-1A53-449D-B8BA-9B681BFF09F9","activityTitle":"你好啊啊啊啊啊","activityDesp":"你好啊打发打发f","activityComment":"似的发射点发生","activityStartTime":1520259720000,"activityEndTime":1520259840000,"activityProcessUserGuid":"A395AC24-091D-410D-BD9B-5DBCCBCC1226","activityAddress":"山科004","activityFromCommGuid":"山科大小区","activityTypeProcessStatus":"33333333-94E3-4EB7-AAD3-333333333333","activityTargetsUserGuid":null,"activityPersonNum":24}]
     * total : 3
     */

    private int total;
    private ArrayList<RowsBean> rows;

    @Override
    public String toString() {
        return "ActListBean{" +
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

    public static class RowsBean implements Serializable {
        /**
         * activityGuid : 3D288153-4623-489B-82B4-1EF799E97176
         * activityTitle : 村上春树
         * activityDesp : 根深蒂固
         * activityComment : 电话费
         * activityStartTime : 1520190180000
         * activityEndTime : 1520190180000
         * activityProcessUserGuid : A395AC24-091D-410D-BD9B-5DBCCBCC1226
         * activityAddress : 发射点
         * activityFromCommGuid : 山科大小区
         * activityTypeProcessStatus : 33333333-94E3-4EB7-AAD3-333333333333
         * activityTargetsUserGuid : null
         * activityPersonNum : 2
         */

        private String activityGuid;
        private String activityTitle;
        private String activityDesp;
        private String activityComment;
        private String activityStartTime;
        private String activityEndTime;
        private String activityProcessUserGuid;
        private String activityAddress;
        private String activityFromCommGuid;
        private String activityTypeProcessStatus;
        private String activityTargetsUserGuid;
        private String activityPersonNum;

        public String getActivityGuid() {
            return activityGuid;
        }

        public void setActivityGuid(String activityGuid) {
            this.activityGuid = activityGuid;
        }

        public String getActivityTitle() {
            return activityTitle;
        }

        public void setActivityTitle(String activityTitle) {
            this.activityTitle = activityTitle;
        }

        public String getActivityDesp() {
            return activityDesp;
        }

        public void setActivityDesp(String activityDesp) {
            this.activityDesp = activityDesp;
        }

        public String getActivityComment() {
            return activityComment;
        }

        public void setActivityComment(String activityComment) {
            this.activityComment = activityComment;
        }

        public String getActivityStartTime() {
            return activityStartTime;
        }

        public void setActivityStartTime(String activityStartTime) {
            this.activityStartTime = activityStartTime;
        }

        public String getActivityEndTime() {
            return activityEndTime;
        }

        public void setActivityEndTime(String activityEndTime) {
            this.activityEndTime = activityEndTime;
        }

        public String getActivityProcessUserGuid() {
            return activityProcessUserGuid;
        }

        public void setActivityProcessUserGuid(String activityProcessUserGuid) {
            this.activityProcessUserGuid = activityProcessUserGuid;
        }

        public String getActivityAddress() {
            return activityAddress;
        }

        public void setActivityAddress(String activityAddress) {
            this.activityAddress = activityAddress;
        }

        public String getActivityFromCommGuid() {
            return activityFromCommGuid;
        }

        public void setActivityFromCommGuid(String activityFromCommGuid) {
            this.activityFromCommGuid = activityFromCommGuid;
        }

        public String getActivityTypeProcessStatus() {
            return activityTypeProcessStatus;
        }

        public void setActivityTypeProcessStatus(String activityTypeProcessStatus) {
            this.activityTypeProcessStatus = activityTypeProcessStatus;
        }

        public String getActivityTargetsUserGuid() {
            return activityTargetsUserGuid;
        }

        public void setActivityTargetsUserGuid(String activityTargetsUserGuid) {
            this.activityTargetsUserGuid = activityTargetsUserGuid;
        }

        public String getActivityPersonNum() {
            return activityPersonNum;
        }

        public void setActivityPersonNum(String activityPersonNum) {
            this.activityPersonNum = activityPersonNum;
        }

        @Override
        public String toString() {
            return "GatherBean{" +
                    "activityGuid='" + activityGuid + '\'' +
                    ", activityTitle='" + activityTitle + '\'' +
                    ", activityDesp='" + activityDesp + '\'' +
                    ", activityComment='" + activityComment + '\'' +
                    ", activityStartTime='" + activityStartTime + '\'' +
                    ", activityEndTime='" + activityEndTime + '\'' +
                    ", activityProcessUserGuid='" + activityProcessUserGuid + '\'' +
                    ", activityAddress='" + activityAddress + '\'' +
                    ", activityFromCommGuid='" + activityFromCommGuid + '\'' +
                    ", activityTypeProcessStatus='" + activityTypeProcessStatus + '\'' +
                    ", activityTargetsUserGuid='" + activityTargetsUserGuid + '\'' +
                    ", activityPersonNum='" + activityPersonNum + '\'' +
                    '}';
        }
    }
}
