package com.ainon.check_account.controller;

import java.util.Base64;
import java.util.TreeMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ainon.check_account.util.HttpUtils;
import com.ainon.check_account.util.MapJavaObjectConverter;

@RestController
public class CheckAccountController {
	
	private final static Logger logger = LoggerFactory.getLogger(CheckAccountController.class);
	
	@RequestMapping("/")
	public String helloPage() {
		return "Hello! Spring Boot!";
	}
	
	@RequestMapping(value = "/aaa" , method = RequestMethod.POST ,produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public String getAccount( @RequestParam("account") String account , @RequestParam("password") String password) {
		
		TreeMap<String, Object> paramsMap = new TreeMap<String, Object>();

		// paramsMap.put("signMethod", "MD5");
		// paramsMap.put("signature", "");
		paramsMap.put("version", "1.0.0");
		paramsMap.put("txnType", "71");
		paramsMap.put("txnSubType", "00");
		paramsMap.put("merId", account);
//		paramsMap.put("merId", "929000095023462");

		// 將Map物件轉換成字串
		String paramSrc = MapJavaObjectConverter.mapObjectKeySortToLinkString(paramsMap, false);
		logger.info("paramSrc[{}]",paramSrc);
		// 路徑+上金鑰
//		byte[] signature1 = DigestUtils.md5(paramSrc + "8WYNr8RGuXDa7kuzhAXHR5BaAzzZtTUZ");
		byte[] signature1 = DigestUtils.md5(paramSrc + password);
		String signature = Base64.getEncoder().encodeToString(signature1);
		signature = signature.replaceAll("\\+", "%2B");
		logger.info("signature[{}]" ,signature);
//		signature1 = ((String) paramsMap.get("customerInfo")).getBytes();

//		String customer = Base64.getEncoder().encodeToString(signature1);
//		paramsMap.put("customerInfo", customer);
//		System.out.println(customer);

		// 再將sign字串存到Map物件
		paramsMap.put("signature", signature);
		paramsMap.put("signMethod", "MD5");
		paramSrc = MapJavaObjectConverter.mapObjectKeySortToLinkString(paramsMap, false);

		// paramSrc = DataMapping.mpaToJSon(paramsMap);
		// System.out.println(paramSrc);
		logger.info("http://gpay.chinagpay.com/bas/BgTrans?" + paramSrc);
		String responseData = HttpUtils.urlPostForString("http://gpay.chinagpay.com/bas/BgTrans", paramsMap);
//		String aa = "5o6l5Y+X6YCa55+l5oiQ5YqfKOWVhuaIt+i0puaIt+S9memineafpeivouaIkOWKnyk=";
//		aa = new String(Base64.getDecoder().decode(aa));
//		System.out.println("aa :" +aa);
		
		logger.info("取得愛儂傳回參數:" + responseData);

		
		return responseData;
	}

	
}
