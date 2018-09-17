package com.fx.jiucai.go;


import com.alibaba.fastjson.JSONObject;
import com.fx.jiucai.RuoKuaiUtils;
import com.fx.jiucai.SystemConstant;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("all")
public class OrderUtil18 {
	
	private static volatile String rsbody = null;
	
	public static void main(String[] args) throws Exception {
		Map<String, String> cookies = getCookies("13648045607", "li5201314");

		initBody(cookies);

		while (rsbody == null) {
//        	System.out.println("等待初始化中");
		}

		Document document = Jsoup.parse(rsbody);
        String cart_md5 = getCartMd5(document);
        String addr_id = getAddrId(document);
		String vcCodeUrl = "https://mall.phicomm.com" + document.getElementById("local-vcode-img").attr("onclick").split("'")[1].split("\\?")[0];
		System.out.println("cart_md5: " + cart_md5 + " addr_id: " + addr_id + " vcCodeUrl: " + vcCodeUrl);

        
        while(true) {
	        try {
	        	String vcCodeJson = RuoKuaiUtils.createByPost("2980364030", "li5201314", "3040", "9500", "112405", "e68297ecf19c4f418184df5b8ce1c31e",
	    	            Jsoup.connect(vcCodeUrl)
	    	            .ignoreContentType(true)
	    	            .cookies(cookies)
	    	            .timeout(SystemConstant.TIME_OUT).execute().bodyAsBytes());

				System.out.println(vcCodeJson);

	    	    // ===========           下单         ==============
	    	        Response createOrderResponse = Jsoup.connect("https://mall.phicomm.com/order-create-is_fastbuy.html").method(Connection.Method.POST).timeout(SystemConstant.TIME_OUT).ignoreContentType(true)
	    	            .cookies(cookies)
	    	            .header("X-Requested-With", "XMLHttpRequest")
	    	            .data("cart_md5", cart_md5)
	    	            .data("addr_id", addr_id)
	    	            .data("dlytype_id", "1")
	    	            .data("payapp_id", "alipay")
	    	            .data("yougouma", "")
	    	            .data("invoice_type", "")
	    	            .data("invoice_title", "")
	    	            .data("useVcNum", "23900")
//	    	            .data("useVcNum", "0")
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


	private static synchronized void updateRsBody(String body) {
		if(rsbody == null) {
			rsbody = body;
			System.out.println("==============设置成功==============");
		}
	}

	private static String initBody(Map<String, String> cookies) throws Exception {
		while(rsbody == null) {
			Thread.sleep(SystemConstant.THREAD_WAIT_TIME);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
//						String body = Jsoup.connect("https://mall.phicomm.com/cart-fastbuy-197-1.html").method(Connection.Method.GET).timeout(SystemConstant.TIME_OUT).cookies(cookies).followRedirects(true).execute().body();
						String body = Jsoup.connect("https://mall.phicomm.com/cart-fastbuy-12-1.html").method(Connection.Method.GET).timeout(SystemConstant.TIME_OUT).cookies(cookies).followRedirects(true).execute().body();
						if(body.contains("库存不足,当前最多可售数量")) {
							System.err.println("库存不足 - " + new Date().toLocaleString());
						} else if(body.contains("返回商品详情") || body.contains("cart_md5")) {
				        	updateRsBody(body);
				        }
					} catch (Exception e) {
//						e.printStackTrace();
					}
				}
			}).start();
		}
		System.out.println("成功");
		return rsbody;
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
        Response pageResponse = Jsoup.connect("https://mall.phicomm.com/passport-login.html").method(Connection.Method.GET).timeout(SystemConstant.TIME_OUT).execute();
        Map<String, String> pageCookies = pageResponse.cookies();

        Response loginResponse = Jsoup.connect("https://mall.phicomm.com/passport-post_login.html").method(Connection.Method.POST)
            .cookies(pageCookies)
            .timeout(SystemConstant.TIME_OUT)
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
