package com.fx.jiucai;


import com.alibaba.fastjson.JSONObject;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

@SuppressWarnings("all")
public class OrderUtil4_end {

	public static void main(String[] args) throws Exception {
        Map<String, String> cookies = getCookies("18383854031", "li5201314");

        while(true) {
	        Response orderResult = Jsoup.connect("https://mall.phicomm.com/checkout.html").cookies(cookies).timeout(SystemConstant.TIME_OUT).execute();
	        String body = orderResult.body();
	        if(doCheck(body)) {
	        	return;
	        }
	
	//        System.out.println(body);
	        Document document = Jsoup.parse(body);
	        String cart_md5 = getCartMd5(document);
	        String addr_id = getAddrId(document);
	        String vcCodeUrl = "https://mall.phicomm.com" + document.getElementById("local-vcode-img").attr("onclick").split("'")[1].split("\\?")[0];
        
	        try {
	        	String vcCodeJson = RuoKuaiUtils.createByPost("2980364030", "li5201314", "3040", "9500", "112405", "e68297ecf19c4f418184df5b8ce1c31e",
	    	            Jsoup.connect(vcCodeUrl)
	    	            .ignoreContentType(true)
	    	            .cookies(cookies)
	    	            .timeout(SystemConstant.TIME_OUT).execute().bodyAsBytes());
	    	
	    	    // ===========           下单         ==============
	    	        Connection.Response createOrderResponse = Jsoup.connect("https://mall.phicomm.com/order-create.html").method(Connection.Method.POST).timeout(SystemConstant.TIME_OUT).ignoreContentType(true)
	    	            .cookies(cookies)
	    	            .header("X-Requested-With", "XMLHttpRequest")
	    	            .data("cart_md5", cart_md5)
	    	            .data("addr_id", addr_id)
	    	            .data("dlytype_id", "1")
	    	            .data("payapp_id", "alipay")
	    	            .data("yougouma", "")
	    	            .data("invoice_type", "")
	    	            .data("invoice_title", "")
	    	            .data("useVcNum", "23600")
	    	            .data("need_invoice2", "on")
	    	            .data("useDdwNum", "0")
	    	            .data("memo", "")
	    	            .data("vcode", JSONObject.parseObject(vcCodeJson).getString("Result"))
	    	            .execute();
	    	
	    	        System.err.println("==========================================================");
	    	        System.err.println(createOrderResponse.body());
	    	        if(createOrderResponse.body().contains("success")) {
	    	            System.err.println("success!!!!");
	    	            System.exit(-1);
	    	        }
			} catch (Exception e) {
				e.printStackTrace();
			}
        }

    }

	private static String getAddrId(Document document) {
		try {
            String addr_id = document.select("input[name=addr_id]").get(0).val();
            if(addr_id != null && addr_id.length() > 0) {
//                System.err.println("addr_id获取成功："  + addr_id);
            	return addr_id;
            }
        } catch (Exception e) {
            for (Element element : document.getElementsByTag("input")) {
                if(element.attr("name").equals("addr_id")) {
                	String addr_id = element.attr("value");
//                    System.err.println("addr_id获取成功："  + addr_id);
                    return addr_id;
                }
            }
        }
		throw new RuntimeException("addr_id获取失败");
	}
	
	private static String getCartMd5(Document document) {
		for (Element element : document.getElementsByTag("input")) {
            if(element.attr("name").equals("cart_md5")) {
                System.err.println("cart_md5获取成功："  + element.attr("value"));
                return element.attr("value");
            }
        }
		throw new RuntimeException("cart_md5获取失败");
	}
	
	private static boolean doCheck(String body) {
        if(body.contains("购物车为空")) {
        	System.out.println("购物车为空 - " + new Date().toLocaleString());
        	return true;
        }
        if(body.indexOf("库存不足") != -1) {
        	System.out.println("库存不足 - " + new Date().toLocaleString());
        	return true;
        }
        return false;
	}

    private static Map<String, String> getCookies(String username, String password) throws Exception {
        Connection.Response pageResponse = Jsoup.connect("https://mall.phicomm.com/passport-login.html").method(Connection.Method.GET).execute();
        Map<String, String> pageCookies = pageResponse.cookies();

        Connection.Response loginResponse = Jsoup.connect("https://mall.phicomm.com/passport-post_login.html").method(Connection.Method.POST)
            .cookies(pageCookies)
            .timeout(10000)
            .ignoreContentType(true)
            .header("X-Requested-With", "XMLHttpRequest")
            .data("forward", "")
            .data("uname", username)
            .data("password", password)
            .execute();


        if(loginResponse.body().contains("error")) {
            throw new RuntimeException("帐号或密码不正确");
        }

        Map<String, String> cks = new HashMap<>();
        cks.putAll(pageCookies);
        cks.putAll(loginResponse.cookies());
        return cks;
    }

}
