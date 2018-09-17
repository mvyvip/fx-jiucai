package com.fx.jiucai.go;


import com.alibaba.fastjson.JSONObject;
import com.fx.jiucai.RuoKuaiUtils;
import com.fx.jiucai.SystemConstant;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.*;

@SuppressWarnings("all")
public class InitAddress {
	
	private static volatile String rsbody = null;
	
	public static void main(String[] args) throws Exception {

		String s = "15873850419 \n" +
				"15719614003 \n" +
				"13404331351 \n" +
				"18419371937 \n" +
				"13282098402 \n" +
				"13438145037 \n" +
				"18200499178 \n" +
				"13550642234 \n" +
				"15246077242 \n" +
				"15904355145 \n" +
				"15943502249 \n" +
				"13332799704 \n" +
				"15730973733 \n" +
				"15884284860 \n" +
				"18482388203 \n" +
				"13059724348 \n" +
				"15769395500 \n" +
				"13694354587 \n" +
				"18402859692 \n" +
				"18281050433 \n" +
				"15883644344 \n" +
				"18280548247 \n" +
				"13944599642\n" +
				"18224021075  \n" +
				"18246840227  \n" +
				"18298403964 \n" +
				"13894514057 \n" +
				"15834564293\n" +
				"18780140771\n" +
				"18482360620\n" +
				"13438148305\n" +
				"13295705740\n" +
				"13282094326\n" +
				"15164524467\n" +
				"13946995405\n" +
				"18474674185\n" +
				"13045704286\n" +
				"13647465473\n" +
				"18843559043\n" +
				"18343241379\n" +
				"18708166911\n" +
				"18328535164";

		List<String> strings = Arrays.asList(s.split("\n"));
		for (String string : strings) {

			Map<String, String> cookies = getCookies(string.trim(), "li5201314");

			Response execute = Jsoup.connect("https://mall.phicomm.com/my-receiver-save.html").method(Connection.Method.POST).cookies(cookies)
					.timeout(SystemConstant.TIME_OUT)
					.ignoreContentType(true)
					//.header("X-Requested-With", "XMLHttpRequest")
					//.header("Content-Type", "application/x-www-form-urlencoded")
					//.header("Upgrade-Insecure-Requests", "1")
					.data("maddr[name]", "张雷")
					.data("maddr[mobile]", "15950690342")
					.data("maddr[area]", "mainland:江苏省/徐州市/铜山县:922")
					.data("maddr[addr]", "江苏省徐州市铜山区城区铜山新区北京北路41-8号圆通快递")
					.data("maddr[is_default]", "true")
					.execute();
			System.out.println(execute.body());

		}

	/*	*/

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
						String body = Jsoup.connect(SystemConstant.PRODUCT_URL).method(Connection.Method.GET).timeout(SystemConstant.TIME_OUT).cookies(cookies).followRedirects(true).execute().body();
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
