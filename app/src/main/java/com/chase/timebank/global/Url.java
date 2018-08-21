package com.chase.timebank.global;

/**
 * Created by chase on 2018/4/19.
 */

public class Url {
//    public static final String BASE_URL = "http://192.168.141.81:8080";//wbw
    public static final String BASE_URL = "http://192.168.1.106:8080";//wbw科技园
//    public static final String BASE_URL = "http://192.168.1.03:8080";//wbw TP-LINK 004
//    public static final String BASE_URL = "http://192.168.141.122:8080";//wzd
//    public static final String BASE_URL = "http://192.168.141.3:8080";//yl
//    public static final String BASE_URL = "http://192.168.141.11:8080";//sxq
    //登录
    public static final String LOGIN_URL = BASE_URL+"/appLoginUser";
    //注册
    public static final String REGISTER_URL = BASE_URL+"/appRegisterUser";
    //查看用户信息
    public static final String USER_INFO_URL = BASE_URL+"/appUserInfo";
    //获取小区
    public static final String GET_COM_URL = BASE_URL+"/appGetCom";
    //完善用户信息
    public static final String UPDATE_USER_INFO_URL = BASE_URL+"/appUpdateUserInfo";
    //插入请求
    public static final String INSERT_REQ_URL = BASE_URL+"/appInsertReq";
    //更新当前登录时用户位置 经纬度+地址
//    public static final String UPDATE_CURRENTADDR_URL = BASE_URL+"/appUpdateCurrentAddr";

    //首页
    public static final String QUERY_NEARBY_REQ = BASE_URL+"/appQueryNearbyReq";


    /*请求模块*/
    /*我的请求相关*/
    public static final String QUERY_REQ_MY_URL = BASE_URL+"/appQueryReqMy";
    public static final String UPDATE_REQ_URL = BASE_URL+"/appUpdateReq";
    public static final String CANCEL_REQ_URL = BASE_URL+"/appCancelReq";
    public static final String QUERY_VOLUNTEER_URL = BASE_URL+"/appQueryVolunteer";
    public static final String START_REQ_URL = BASE_URL+"/appStartReq";
    public static final String COMPLETED_REQ_URL = BASE_URL+"/appCompletedReq";
    public static final String INCOMPLETED_REQ_URL = BASE_URL+"/appIncompletedReq";
    /*服务列表相关*/
    public static final String QUERY_NEW_LIST_URL = BASE_URL+"/appQueryNewList";
    public static final String QUERY_SERVICE_LIST_URL = BASE_URL+"/appQueryServiceList";
    public static final String INSERT_RES_URL = BASE_URL+"/appInsertRes";
    /*我的服务相关*/
    public static final String QUERY_SERVICE_MY_URL = BASE_URL+"/appQueryServiceMy";
    public static final String CANCEL_RES_MY_URL = BASE_URL+"/appCancelResMy";
    public static final String UPDATE_RES_MY_URL = BASE_URL+"/appUpdateResMy";
    public static final String UPDATE_RES_URL = BASE_URL+"/appUpdateRes";

    /*活动模块*/
    public static final String QUERY_ACTIVITY_URL = BASE_URL+"/appQueryActivityByUser";
    public static final String INSERT_ACTPART_URL = BASE_URL+"/appInsertActpart";
    public static final String QUERY_ACTIVITY_MY_URL = BASE_URL+"/appQueryActivityMyByUser";

    /*转账模块*/
    /*收款列表相关*/
    public static final String INSERT_TRANSFER_URL = BASE_URL+"/appInsertTransfer";
    public static final String QUERY_TRANSFER_GATHER_URL = BASE_URL+"/appQueryTransferGather";
    public static final String TRANSFER_GATHER_OK_URL = BASE_URL+"/appTranGatherOk";
    public static final String TRANSFER_GATHER_CANCEL_URL = BASE_URL+"/appTranGatherCancel";
    /*汇款列表相关*/
    public static final String QUERY_TRANSFER_REMIT_URL = BASE_URL+"/appQueryTransferRemit";



    public static final String EMCJSON_URL = BASE_URL+"/dcjr/emc.json";
    //上传图片
    public static final String IMAGE_UPLOAD_URL = BASE_URL+"/imageUpload";




}
