package com.chase.timebank.home;

import java.util.ArrayList;

/**
 * Created by chase on 2017/10/23.
 */

public class EMCData {

    /**
     * data : {"EMCData":[{"id":11008,"type":"2","title":"某产品EMC测试辐射超标分析及整改方案","author":"佚名","from":"安规与电磁兼容网","date":"2017-01-23","imgUrl1":"/dcjr/EMC/EMCImg/EMC_image8_1.jpg","imgUrl2":"/dcjr/EMC/EMCImg/EMC_image8_2.jpg","imgUrl3":"/dcjr/EMC/EMCImg/EMC_image8_3.jpg","url":"/dcjr/EMC/listEMC8.jpg"},{"id":11007,"type":"2","title":"汽车电子设备电磁兼容性改进措施","author":"佚名","from":"安规与电磁兼容网","date":"2016-05-27","imgUrl1":"/dcjr/EMC/EMCImg/EMC_image7_1.jpg","imgUrl2":"/dcjr/EMC/EMCImg/EMC_image7_2.jpg","imgUrl3":"/dcjr/EMC/EMCImg/EMC_image7_3.jpg","url":"/dcjr/EMC/listEMC7.jpg"},{"id":11006,"type":"2","title":"探讨高频开关电源设计中的电磁兼容问题","author":"佚名","from":"安规与电磁兼容网","date":"2016-05-10","imgUrl1":"/dcjr/EMC/EMCImg/EMC_image6_1.jpg","imgUrl2":"/dcjr/EMC/EMCImg/EMC_image6_2.jpg","imgUrl3":"/dcjr/EMC/EMCImg/EMC_image6_3.jpg","url":"/dcjr/EMC/listEMC6.jpg"},{"id":11005,"type":"2","title":"EMC技术在DSP控制系统中的应用","author":"张寅孩 刘维霞 杨俊秀","from":"浙江理工大学 电气电子教学学报","date":"2016-05-06","imgUrl1":"/dcjr/EMC/EMCImg/EMC_image5_1.jpg","imgUrl2":"/dcjr/EMC/EMCImg/EMC_image5_2.jpg","imgUrl3":"/dcjr/EMC/EMCImg/EMC_image5_3.jpg","url":"/dcjr/EMC/listEMC5.jpg"},{"id":11004,"type":"2","title":"开关电源变压器屏蔽层抑制共模EMI的研究","author":"佚名","from":"安规与电磁兼容网","date":"2016-04-29","imgUrl1":"/dcjr/EMC/EMCImg/EMC_image4_1.jpg","imgUrl2":"/dcjr/EMC/EMCImg/EMC_image4_2.jpg","imgUrl3":"/dcjr/EMC/EMCImg/EMC_image4_3.jpg","url":"/dcjr/EMC/listEMC4.jpg"},{"id":11003,"type":"2","title":"抗电磁干扰片式铁氧体磁珠的原理及其应用","author":"佚名","from":"安规与电磁兼容网","date":"2016-01-04","imgUrl1":"/dcjr/EMC/EMCImg/EMC_image3_1.jpg","imgUrl2":"/dcjr/EMC/EMCImg/EMC_image3_2.jpg","imgUrl3":"/dcjr/EMC/EMCImg/EMC_image3_3.jpg","url":"/dcjr/EMC/listEMC3.jpg"},{"id":11002,"type":"2","title":"正确排查EMI问题的实用性技巧","author":"佚名","from":"安规与电磁兼容网","date":"2015-10-20","imgUrl1":"/dcjr/EMC/EMCImg/EMC_image2_1.jpg","imgUrl2":"/dcjr/EMC/EMCImg/EMC_image2_2.jpg","imgUrl3":"/dcjr/EMC/EMCImg/EMC_image2_3.jpg","url":"/dcjr/EMC/listEMC2.jpg"},{"id":11001,"type":"2","title":"COOL MOSFET在反激式转换器中的EMI设计指南","author":"佚名","from":"安规与电磁兼容网","date":"2015-08-24","imgUrl1":"/dcjr/EMC/EMCImg/EMC_image1_1.jpg","imgUrl2":"/dcjr/EMC/EMCImg/EMC_image1_2.jpg","imgUrl3":"/dcjr/EMC/EMCImg/EMC_image1_3.jpg","url":"/dcjr/EMC/listEMC1.jpg"}],"topEMC":[{"id":1101,"type":"1","title":"如何有效地进行无线辐射杂散调试","author":"李松","from":"安规与电磁兼容网","date":"2017-02-17","imgUrl":"/dcjr/EMC/topEMCImg/topEMC_image1.jpg","url":"/dcjr/EMC/topEMC1.jpg"},{"id":1102,"type":"1","title":"浅谈投射式电容触摸屏的电磁干扰问题的解决方案","author":"佚名","from":"与非网","date":"2017-02-06","imgUrl":"/dcjr/EMC/topEMCImg/topEMC_image2.jpg","url":"/dcjr/EMC/topEMC2.jpg"},{"id":1103,"type":"1","title":"电动汽车的系统级EMC设计","author":"高新杰 李燕侠 李国珍 李兴华 朱光福 邹明","from":"安规与电磁兼容网","date":"2017-01-04","imgUrl":"/dcjr/EMC/topEMCImg/topEMC_image3.jpg","url":"/dcjr/EMC/topEMC3.jpg"},{"id":1104,"type":"1","title":"EMC的预测试技术是保证产品质量不可少的手段","author":"鲁维德","from":"电源世界","date":"2016-06-03","imgUrl":"/dcjr/EMC/topEMCImg/topEMC_image4.jpg","url":"/dcjr/EMC/topEMC4.jpg"},{"id":1105,"type":"1","title":"LED驱动器的可靠性和电磁兼容性测试方案","author":"佚名","from":"安规与电磁兼容网","date":"2016-05-19","imgUrl":"/dcjr/EMC/topEMCImg/topEMC_image5.jpg","url":"/dcjr/EMC/topEMC5.jpg"},{"id":1106,"type":"1","title":"降低噪声与电磁干扰的24个窍门","author":"佚名","from":"EDN","date":"2016-04-05","imgUrl":"/dcjr/EMC/topEMCImg/topEMC_image6.jpg","url":"/dcjr/EMC/topEMC6.jpg"}]}
     * retcode : 200
     */

    public DataBean data;
    public int retcode;

    @Override
    public String toString() {
        return "EMCData{" +
                "data=" + data +
                ", retcode=" + retcode +
                '}';
    }

    public class DataBean {
        public ArrayList<EMCDataBean> EMCData;
        public ArrayList<TopEMCBean> topEMC;

        @Override
        public String toString() {
            return "DataBean{" +
                    "EMCData=" + EMCData +
                    ", topEMC=" + topEMC +
                    '}';
        }

        public class EMCDataBean {
            /**
             * id : 11008
             * type : 2
             * title : 某产品EMC测试辐射超标分析及整改方案
             * author : 佚名
             * from : 安规与电磁兼容网
             * date : 2017-01-23
             * imgUrl1 : /dcjr/EMC/EMCImg/EMC_image8_1.jpg
             * imgUrl2 : /dcjr/EMC/EMCImg/EMC_image8_2.jpg
             * imgUrl3 : /dcjr/EMC/EMCImg/EMC_image8_3.jpg
             * url : /dcjr/EMC/listEMC8.jpg
             */

            public String id;
            public int type;
            public String title;
            public String author;
            public String from;
            public String date;
            public String imgUrl1;
            public String imgUrl2;
            public String imgUrl3;
            public String url;

            @Override
            public String toString() {
                return "EMCDataBean{" +
                        "id='" + id + '\'' +
                        ", type='" + type + '\'' +
                        ", title='" + title + '\'' +
                        ", author='" + author + '\'' +
                        ", from='" + from + '\'' +
                        ", date='" + date + '\'' +
                        ", imgUrl1='" + imgUrl1 + '\'' +
                        ", imgUrl2='" + imgUrl2 + '\'' +
                        ", imgUrl3='" + imgUrl3 + '\'' +
                        ", url='" + url + '\'' +
                        '}';
            }
        }

        public class TopEMCBean {
            /**
             * id : 1101
             * type : 1
             * title : 如何有效地进行无线辐射杂散调试
             * author : 李松
             * from : 安规与电磁兼容网
             * date : 2017-02-17
             * imgUrl : /dcjr/EMC/topEMCImg/topEMC_image1.jpg
             * url : /dcjr/EMC/topEMC1.jpg
             */

            public String id;
            public int type;
            public String title;
            public String author;
            public String from;
            public String date;
            public String imgUrl;
            public String url;

            @Override
            public String toString() {
                return "TopEMCBean{" +
                        "id=" + id +
                        ", type='" + type + '\'' +
                        ", title='" + title + '\'' +
                        ", author='" + author + '\'' +
                        ", from='" + from + '\'' +
                        ", date='" + date + '\'' +
                        ", imgUrl='" + imgUrl + '\'' +
                        ", url='" + url + '\'' +
                        '}';
            }
        }
    }
}
