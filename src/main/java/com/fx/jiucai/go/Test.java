//package com.fx.jiucai.go;
//
//import com.alibaba.fastjson.JSONObject;
//import com.fx.jiucai.SystemConstant;
//import org.jsoup.Connection;
//import org.jsoup.Connection.Response;
//import org.jsoup.Jsoup;
//
///**
// * Created by lt on 2018/9/17 0017.
// */
//public class Test {
//
//    public static void main(String[] args) throws Exception {
//       while (true) {
//           System.out.println("获取验证码");
//           Thread[] threads = new Thread[SystemConstant.CREATE_ORDER_THREAD_COUNT];
//           for (int i = 0; i < SystemConstant.CREATE_ORDER_THREAD_COUNT; i++) {
//               threads[i] = new Thread(new Runnable() {
//                   @Override
//                   public void run() {
//                       try {
//                           Response createOrderResponse = Jsoup.connect("https://mall.phicomm.com/order-create-is_fastbuy.html").method(Connection.Method.POST).timeout(SystemConstant.TIME_OUT).ignoreContentType(true)
//                               .cookies(cookies)
//                               .header("X-Requested-With", "XMLHttpRequest")
//                               .data("cart_md5", cart_md5)
//                               .data("addr_id", addr_id)
//                               .data("dlytype_id", "1")
//                               .data("payapp_id", "alipay")
//                               .data("yougouma", "")
//                               .data("invoice_type", "")
//                               .data("invoice_title", "")
//                               .data("useVcNum", "23900")
////	    	            .data("useVcNum", "0")
//                               .data("need_invoice2", "on")
//                               .data("useDdwNum", "0")
//                               .data("memo", "")
//                               .data("vcode", JSONObject.parseObject(vcCodeJson).getString("Result"))
//                               .execute();
//
//                           System.err.println("==========================================================");
//                           System.err.println(createOrderResponse.body());
//                           if(createOrderResponse.body().contains("success")) {
//                               System.err.println("success!!!!");
//                               System.exit(-1);
//                           }
//                       } catch (Exception e) {}
//                   }
//               });
//               threads[i].start();
//           }
//            for (int i = 0; i < SystemConstant.CREATE_ORDER_THREAD_COUNT; i++) {
//                threads[i].join();
//            }
//           System.out.println("失败");
//       }
//    }
//}
