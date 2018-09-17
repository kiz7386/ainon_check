/*******************************************************************************
 * Project Key : CPPII
 * Create on 2013-10-11 上午9:44:52
 * Copyright (c) 2008 - 2011.深圳市商联商用科技有限公司版权所有. 粤ICP备08118666号
 * 注意：本内容仅限于深圳市商联商用科技服务有限公司内部传阅，禁止外泄以及用于其他的商业目的
 ******************************************************************************/
package com.ainon.check_account.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <P>字符串工具类</P>
 * 
 * @version 1.0
 * @author 黄雄星（13077862552） 2013-10-11 上午9:44:52
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

	private static final Pattern pattern = Pattern.compile("\\{([\\d0-9a-zA-Z]+)\\}");

	private static final String regexIP = "(((((2[0-4]\\d)|(25[0-5]))|(1\\d{2})|([1-9]\\d)|(\\d))|[*]).){3}((((2[0-4]\\d)|(25[0-5]))|(1\\d{2})|([1-9]\\d)|(\\d))|[*])";
	private static final String regexProj = "^[^\\/<>\".?*]+$";
	private static final String regexDir = "^[^\\/?*\"><:|]*$";
	private static final String regexNum = "-?[0-9]+.?[0-9]+";
	private static String hexString = "0123456789ABCDEF";

	public static boolean isIP(String ip) {
		return ip.matches(
				"(((((2[0-4]\\d)|(25[0-5]))|(1\\d{2})|([1-9]\\d)|(\\d))|[*]).){3}((((2[0-4]\\d)|(25[0-5]))|(1\\d{2})|([1-9]\\d)|(\\d))|[*])");
	}

	public static boolean compareIP(String ip, String securityIp) {
		String[] address = ip.split("\\.");
		String[] securityAddress = securityIp.split("\\.");
		for (int i = 0; i < 4; i++) {
			if ((!securityAddress[i].equals(address[i])) && (!"*".equals(securityAddress[i])))
				return false;
		}
		return true;
	}

	public static boolean isLegalProj(String str) {
		return str.matches("^[^\\/<>\".?*]+$");
	}

	public static boolean isNumber(String str) {
		return org.apache.commons.lang3.math.NumberUtils.isDigits(str);
	}

	public static char getFirstLegalCharProj(String str) {
		for (int i = 0; i < str.length(); i++) {
			char c;
			if (!isLegalProj(String.valueOf(c = str.charAt(i))))
				return c;
		}
		return '\000';
	}

	public static boolean isLegalDir(String str) {
		return str.matches("^[^\\/?*\"><:|]*$");
	}

	public static char getFirstLegalCharDir(String str) {
		for (int i = 0; i < str.length(); i++) {
			char c;
			if ((!isLegalDir(String.valueOf(c = str.charAt(i)))) && ((1 != i) || (':' != c)))
				return c;
		}
		return '\000';
	}

	public static boolean isAsCharAll(String str, char c) {
		return !str.matches(".*[^" + c + "].*");
	}

	public static InputStream stringToInputStream(String str) {
		return new ByteArrayInputStream(str.getBytes());
	}

	public static Integer stringToPositiveInteger(String str) {
		Integer i = new Integer(1);
		try {
			i = Integer.valueOf(str.trim());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return i.intValue() > 0 ? i : new Integer(1);
	}

	public static Integer stringToNonNegativeInteger(String str) {
		Integer i = new Integer(0);
		try {
			i = Integer.valueOf(str.trim());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return i.intValue() > -1 ? i : new Integer(0);
	}

	public static String lpadStringByZero(String src, int length) {
		return repeatString("0", length - src.getBytes().length) + src;
	}

	public static String repeatString(String src, int repeats) {
		if ((src == null) || (repeats <= 0)) {
			return src;
		}
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < repeats; i++) {
			buffer.append(src);
		}
		return buffer.toString();
	}

	public static String trimDotAndComma(String str) {
		if (str == null) {
			return "";
		}
		return str.replace(" ", "").replaceAll("[.]", "").replaceAll("[,]", "");
	}

	public static String trimComma(String str) {
		if (str == null) {
			return "";
		}
		return str.replace(" ", "").replaceAll("[,]", "");
	}

	public static String trimFrontZero(String str) {
		str = str.trim();
		while ((!"".equals(str)) && ('0' == str.charAt(0)) && (1 != str.length()) && ('.' != str.charAt(1))) {
			str = str.substring(1);
		}
		return str;
	}

	public static String formatText(String str, String format) {
		if ((str == null) || (format == null))
			return null;
		StringBuffer buffer = new StringBuffer("");
		int i = 0;
		for (int j = 0; (i < format.length()) && (j < str.length()); i++) {
			char c = format.charAt(i);
			if ('?' == c) {
				buffer.append(str.charAt(j));
				j++;
			} else {
				if ('*' == c) {
					buffer.append(str.substring(j));
					buffer.append(format.substring(i + 1));
					break;
				}
				if (('{' == c) && (format.substring(i).length() > 2)) {
					String sign = format.substring(i + 1, i + 3);
					if ("*}".equals(sign)) {
						buffer.append('*');
						i += 2;
						continue;
					}
					if ("?}".equals(sign)) {
						buffer.append('?');
						i += 2;
						continue;
					}
				}
				buffer.append(c);
			}
		}
		return buffer.toString();
	}

	public static boolean contain(String s, String sCh) {
		for (int i = 0; i < sCh.length(); i++) {
			if (-1 != s.indexOf(sCh.charAt(i)))
				return true;
		}
		return false;
	}

	/**
	 * 字符串转换成十六进制字符串
	 * 
	 * @param String str 待转换的ASCII字符串
	 * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
	 */
	public static String str2HexStr(String str) {

		char[] chars = "0123456789ABCDEF".toCharArray();
		StringBuilder sb = new StringBuilder("");
		byte[] bs = str.getBytes();
		int bit;

		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(chars[bit]);
			bit = bs[i] & 0x0f;
			sb.append(chars[bit]);
			sb.append(' ');
		}
		return sb.toString().trim();
	}

	/**
	 * 十六进制转换字符串
	 * 
	 * @param String str Byte字符串(Byte之间无分隔符 如:[616C6B])
	 * @return String 对应的字符串
	 */
	public static String hexStr2Str(String hexStr) {
		String str = "0123456789ABCDEF";
		char[] hexs = hexStr.toCharArray();
		byte[] bytes = new byte[hexStr.length() / 2];
		int n;

		for (int i = 0; i < bytes.length; i++) {
			n = str.indexOf(hexs[2 * i]) * 16;
			n += str.indexOf(hexs[2 * i + 1]);
			bytes[i] = (byte) (n & 0xff);
		}
		return new String(bytes);
	}

	/**
	 * bytes字符串转换为Byte值
	 * 
	 * @param String src Byte字符串，每个Byte之间没有分隔符
	 * @return byte[]
	 */
	public static byte[] hexStr2Bytes(String src) {
		int m = 0, n = 0;
		int l = src.length() / 2;
		System.out.println(l);
		byte[] ret = new byte[l];
		for (int i = 0; i < l; i++) {
			m = i * 2 + 1;
			n = m + 1;
			ret[i] = Byte.decode("0x" + src.substring(i * 2, m) + src.substring(m, n));
		}
		return ret;
	}

	/**
	 * String的字符串转换成unicode的String
	 * 
	 * @param String strText 全角字符串
	 * @return String 每个unicode之间无分隔符
	 * @throws Exception
	 */
	public static String strToUnicode(String strText) throws Exception {
		char c;
		StringBuilder str = new StringBuilder();
		int intAsc;
		String strHex;
		for (int i = 0; i < strText.length(); i++) {
			c = strText.charAt(i);
			intAsc = c;
			strHex = Integer.toHexString(intAsc);
			if (intAsc > 128)
				str.append("\\u" + strHex);
			else
				// 低位在前面补00
				str.append("\\u00" + strHex);
		}
		return str.toString();
	}

	/**
	 * unicode的String转换成String的字符串
	 * 
	 * @param String hex 16进制值字符串 （一个unicode为2byte）
	 * @return String 全角字符串
	 */
	public static String unicodeToString(String hex) {
		int t = hex.length() / 6;
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < t; i++) {
			String s = hex.substring(i * 6, (i + 1) * 6);
			// 高位需要补上00再转
			String s1 = s.substring(2, 4) + "00";
			// 低位直接转
			String s2 = s.substring(4);
			// 将16进制的string转为int
			int n = Integer.valueOf(s1, 16) + Integer.valueOf(s2, 16);
			// 将int转换为字符
			char[] chars = Character.toChars(n);
			str.append(new String(chars));
		}
		return str.toString();
	}

	public static String encodeHex(String str) {
		byte[] bytes = str.getBytes();
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			sb.append(hexString.charAt((bytes[i] & 0xF0) >> 4));
			sb.append(hexString.charAt((bytes[i] & 0xF) >> 0));
		}
		return sb.toString();
	}

	public static String decodeHex(String str) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream(str.length() / 2);
		for (int i = 0; i < str.length(); i += 2) {
			stream.write(hexString.indexOf(str.charAt(i)) << 4 | hexString.indexOf(str.charAt(i + 1)));
		}
		byte[] bytes = stream.toByteArray();
		try {
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String(bytes);
	}

	/**
	 * 例如：style = "00000000" 要显示的数字的格式,格式化为8位固定长度
	 * 
	 * @param value
	 * @param style
	 * @return
	 * @author 张泽豪（13826587335） 2014-8-4 上午9:43:02
	 */
	public static String formatNumber(Object value, String style) {
		DecimalFormat df = new DecimalFormat();
		df.applyPattern(style);// 将格式应用于格式化器
		return df.format(value);
	}

	/**
	 * 
	 * <p>依据正则表达式替换字符串</p>
	 * 
	 * @param regex:正则表达式
	 * @param source:原String
	 * @param replaceString：替换的String
	 * @return
	 * @author 黄雄星（13077862552） 2014-8-8 下午3:52:07
	 */
	public static String replaceByRegex(String regex, String source, String replaceString) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(source);
		while (matcher.find()) {
			source = source.replace(matcher.group(), replaceString);
		}
		return source;
	}

	public static boolean isInteger(String str) {
		if (StringUtils.isBlank(str))
			return false;
		return match("^[-\\+]?[\\d]*$", str);
	}

	public static boolean isDouble(String str) {
		if (StringUtils.isBlank(str))
			return false;
		return match("^[-\\+]?[.\\d]*$", str);
	}

	/**
	 * 验证邮箱
	 * 
	 * @param 待验证的字符串
	 * @return 如果是符合的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean isEmail(String str) {
		String regex = "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		return match(regex, str);
	}

	/**
	 * 验证电话号码
	 * 
	 * @param 待验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean IsTelephone(String str) {
		String regex = "^(\\d{3,4}-)?\\d{6,8}$";
		return match(regex, str);
	}

	/**
	 * 验证输入手机号码<新>
	 * 
	 * @param 待验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean isPhoneNumber(String str) {
		if (str == null)
			return false;
		String regex = "^(1)[0-9]{10}$";
		return match(regex, str);
	}

	/**
	 * 验证输入邮编
	 * 
	 * @param 待验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean isPostCode(String str) {
		if (str == null)
			return false;
		String re = "^[1-9][0-9]{5}$";
		return match(re, str);
	}

	/**
	 * 验证输入身份证号
	 * 
	 * @param 待验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean IsIDcard(String str) {
		String regex = "(^\\d{17}(\\d{1}|x|X)$)|(^\\d{14}(\\d{1}|x|X)$)";
		return match(regex, str);
	}

	/**
	 * @param regex 正则表达式字符串
	 * @param str 要匹配的字符串
	 * @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
	 */
	private static boolean match(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	/**
	 * 
	 * <p>掩码身份证号码</p>
	 * 
	 * @author 聂文海 2016-5-5 下午3:51:46
	 */
	public static String convertCredNo(String credNo) {
		if (isNotBlank(credNo)) {
			if (credNo.length() >= 7 && credNo.length() <= 14) {
				return credNo.substring(0, 7) + "*******";
			} else if (credNo.length() >= 7 && credNo.length() > 14) {
				return credNo.substring(0, 7) + "*******" + credNo.substring(14, credNo.length());
			} else {
				return credNo;
			}
		} else {
			return null;
		}
	}

	/**
	 * 
	 * <p>掩码银行卡号</p>
	 * 
	 * @param accNo
	 * @return
	 * @author 朱建谱（15626573212） 2017-2-27 下午2:21:11
	 */
	public static String convertAccNo(String accNo) {
		if (StringUtils.isNotBlank(accNo)) {
			if (accNo.length() >= 4) {
				return "*******" + accNo.substring(accNo.length() - 4, accNo.length());
			} else {
				return accNo;
			}
		} else {
			return "";
		}
	}

	/**
	 * 
	 * <p>电话号码掩码</p>
	 * 
	 * @param telNo
	 * @return
	 * @author 朱建谱（15626573212） 2017-3-1 上午9:59:17
	 */
	public static String maskTelNo(String telNo) {
		if (StringUtils.isNotBlank(telNo)) {
			if (telNo.length() >= 7) {
				return telNo.substring(0, 3) + "****" + telNo.substring(7);
			} else {
				return telNo;
			}
		} else {
			return "";
		}
	}

	/**
	 * 
	 * <p>邮箱掩码</p>
	 * 
	 * @param telNo
	 * @return
	 * @author
	 */
	public static String maskEmail(String email) {
		if (StringUtils.isNotBlank(email)) {
			if (isEmail(email)) {
				return email.substring(0, email.indexOf("@") / 2) + "******" + email.substring(email.indexOf("@"));
			} else {
				return email;
			}
		} else {
			return "";
		}
	}

	/**
	 * 
	 * <p>URL 格式化</p><p>url: dd/{dd}/dfd/{ff}/{oo} , pathParams: "11", "33", "666" return
	 * dd/11/dfd/33/666</p> <p>url: dd/{dd}/dfd/{ff}/{ff} , pathParams: "11", "33", "666" return
	 * dd/11/dfd/33/33</p>
	 * 
	 * @param url
	 * @param pathParams
	 * @return
	 * @author 黄雄星 2015-8-18 下午8:38:38
	 */
	public static String formatUrl(String url, String... pathParams) {
		if (url.indexOf("{") > 0) {
			if (pathParams.length > 0) {
				Matcher matcher = pattern.matcher(url);
				int i = 0;
				while (matcher.find()) {
					for (int j = 0; j < pathParams.length; j++) {
						if (i == j) {
							url = url.replace(matcher.group(), pathParams[j]);
						}
					}
					i++;
				}

			} else {
				url = url.substring(0, url.indexOf("{") - 1);
			}
			return url;
		}
		return url;
	}

	public static String numberToString(Object o) {
		if (o == null) {
			return null;

		}
		return o.toString();
	}

	public static void main(String[] args) {
		String splitReqString = "sgsdfgg/dfgdfgdfg/{923933544}/{DFS32DSFS}";
		String uid = replaceByRegex("\\{([\\d0-9a-zA-Z]+)\\}", splitReqString, "fff");
		System.out.println(uid);
		System.out.println(formatNumber(1, "00000000"));
		System.out.println(convertAccNo("123456789"));
		System.out.println(maskTelNo("123"));
		System.out.println(maskEmail("654474108@qq.com"));
		System.out.println(isNumber("3 "));

	}

}
