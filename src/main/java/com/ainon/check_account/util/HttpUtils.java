/*******************************************************************************
 ******************************************************************************/
package com.ainon.check_account.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <P>TODO：统一解决特殊字符转义问题</P>
 * 
 * @version 1.0
 * @author
 */
public class HttpUtils {

	private final static Logger log = LoggerFactory.getLogger(HttpUtils.class);

	// 毫秒
	private final static int DEFAULT_TIME_OUT = 30 * 1000;

	public final static String IO_EXCEPTION = "QT_IO_EXCEPTION";

	public final static String CONTENT_TYPE_JSON_STR = "application/json; charset=utf-8";
	public final static int CONTENT_TYPE_JSON = 1;

	/**
	 * 
	 * <p>get请求 TODO:需要解决特殊字符问题</p>
	 * 
	 * @param url 请求url
	 * @param params url参数
	 * @param retryCount 重试次数
	 * @return
	 * @author 黄雄星（13077862552） 2013-12-31 下午2:07:06
	 */
	public static String urlGetForString(String url, Map<String, Object> params, int retryCount) {
		return urlGetForString(url, params, retryCount, true);
	}

	public static String urlGetForString(String url, Map<String, Object> params, int retryCount, boolean noParamsLog) {
		if (noParamsLog) {
			log.info("请求urlGetForString方法,url[{}]", url);
			if (log.isDebugEnabled())
				log.debug("收到url请求:url[{}]params[{}]", url, params);
		}
		boolean paramsIsNotEmpty = CollectionUtils.isNotEmpty(params);
		StringBuilder urlBuilder = new StringBuilder();
		if (url.indexOf("?") <= 0 && paramsIsNotEmpty) {
			url += "?";
		}
		if (paramsIsNotEmpty) {
			boolean flag = true;
			for (Object key : params.keySet().toArray()) {
				if (flag) {
					urlBuilder.append((String) key + "=" + params.get(key));
					flag = false;
				} else {
					urlBuilder.append("&" + (String) key + "=" + params.get(key));
				}
			}
		}
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			String paramsUrl = URLEncoder.encode(urlBuilder.toString(), "UTF-8");
			String requestUrl = url + paramsUrl;
			if (log.isDebugEnabled() && noParamsLog) {
				log.debug("请求url为requestUrl[{}]", requestUrl);
			}
			// 创建httpget.
			HttpGet httpget = new HttpGet(requestUrl);
			// 执行get请求.
			CloseableHttpResponse response = httpclient.execute(httpget);
			try {
				// 获取响应实体
				HttpEntity entity = response.getEntity();
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode != HttpStatus.SC_OK) {
					log.error("请求状态不为成功" + statusCode + "url:" + url);
				} else {
					String result = EntityUtils.toString(entity);
					if (log.isDebugEnabled())
						log.debug("请求成功result[{}]", result);
					return result;
				}
			} finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			log.error(String.format("请检查请求协议", retryCount), e);
			throw new RuntimeException("请检查请求协议");
		} catch (IOException e) {
			log.error(String.format("网络异常,已经重试了[%s]次", retryCount), e);
			throw new RuntimeException(IO_EXCEPTION, e);
		} finally {
			// 关闭连接,释放资源
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 
	 * <p>get请求，默认重试3次</p>
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @author 黄雄星（13077862552） 2013-12-31 下午2:29:44
	 */
	public static String urlGetForString(String url, Map<String, Object> params) {
		return urlGetForString(url, params, 3);
	}

	/**
	 * 
	 * <p>不带url参数的get请求，默认重试3次</p>
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @author 黄雄星（13077862552） 2013-12-31 下午2:29:44
	 */
	public static String urlGetForString(String url) {
		return urlGetForString(url, null, 3);
	}

	/**
	 * 
	 * <p>post请求，默认重试3次</p>
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @author 黄雄星（13077862552） 2013-12-31 下午2:29:44
	 */
	public static String urlPostForString(String url, Map<String, Object> params) {
		return urlPostForString(url, params, 3);
	}

	/**
	 * 
	 * <p>不带url参数的get请求，默认重试3次</p>
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @author 黄雄星（13077862552） 2013-12-31 下午2:29:44
	 */
	public static String urlPostForString(String url) {
		return urlPostForString(url, null, 3);
	}

	public static String urlPostForString(String url, Map<String, Object> params, int retryCount) {
		return urlPostForString(url, params, null, retryCount, DEFAULT_TIME_OUT, null, null, null);
	}

	public static String urlPostStrForString(String url, String paramStr, int retryCount, String authorization,
			Integer contentType) {
		return urlPostForString(url, null, paramStr, retryCount, DEFAULT_TIME_OUT, authorization, contentType, 30000);
	}

	public static String urlPostForString(String url, Map<String, Object> params, int retryCount, Integer connTimeOut) {
		return urlPostForString(url, params, null, retryCount, connTimeOut, null, null, null);
	}

	/**
	 * 
	 * <p>post请求</p>
	 * 
	 * @param url 请求url
	 * @param params url参数
	 * @param retryCount 重试次数
	 * @return
	 * @author 黄雄星（13077862552） 2013-12-31 下午4:41:19
	 */
	public static String urlPostForString(String url, Map<String, Object> params, String paramStr, int retryCount,
			Integer connTimeOut, String authorization, Integer contentType, Integer soTimeOut) {
		log.info("请求urlPostForString方法,url[{}]", url);
		if (log.isDebugEnabled())
			log.debug("收到url请求:url[{}]params[{}]", url, params);
		if (url.startsWith("https")) {
			return urlPostHttps(url, params);
		} else {
			// 创建默认的httpClient实例.
			CloseableHttpClient httpclient = HttpClients.createDefault();
			try {
				// TODO：完善此参数
				RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(connTimeOut)
						.setConnectionRequestTimeout(1000).setSocketTimeout(soTimeOut == null ? 5000 : soTimeOut)
						.build();
				// 创建httppost
				HttpPost httppost = new HttpPost(url);
				if (StringUtils.isNotBlank(authorization))
					httppost.setHeader("Authorization", authorization);
				if (contentType != null && CONTENT_TYPE_JSON == contentType) {
					httppost.addHeader("Content-type", CONTENT_TYPE_JSON_STR);
					httppost.setHeader("Accept", "application/json");
				}
				httppost.setConfig(requestConfig);
				if (CollectionUtils.isNotEmpty(params)) {
					Object[] keys = params.keySet().toArray();
					// 创建参数队列
					List<NameValuePair> formparams = new ArrayList<NameValuePair>();
					for (int i = 0; i < keys.length; i++) {
						String value = (String) params.get(keys[i]);
						formparams.add(new BasicNameValuePair((String) keys[i], value));
					}
					UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
					httppost.setEntity(uefEntity);
				}

				if (StringUtils.isNotBlank(paramStr))
					httppost.setEntity(new StringEntity(paramStr, Charset.forName("UTF-8")));

				CloseableHttpResponse response = httpclient.execute(httppost);
				try {
					HttpEntity entity = response.getEntity();
					int statusCode = response.getStatusLine().getStatusCode();
					if (statusCode != HttpStatus.SC_OK) {
						log.error("请求状态不为成功statusCode[{}],url[{}]", statusCode, url);
					} else {
						String result = EntityUtils.toString(entity, "UTF-8");
						if (log.isDebugEnabled())
							log.debug("请求成功result[{}]", result);
						return result;
					}
				} finally {
					response.close();
				}
			} catch (ClientProtocolException e) {
				log.error("请检查请求协议", e);
				throw new RuntimeException("请检查请求协议", e);
			} catch (UnsupportedEncodingException e1) {
				log.error("请检查编码格式", e1);
				throw new RuntimeException("请检查编码格式", e1);
			} catch (IOException e) {
				log.error(String.format("网络异常,已经重试了[%s]次", retryCount), e);
				if (e.getMessage().startsWith("Connection refused") || e.getMessage().contains("拒绝连接")) {
					throw new RuntimeException("网络异常，连接不上服务器，直接失败", e);
				} else {
					throw new RuntimeException(IO_EXCEPTION, e);
				}
			} catch (Exception e) {
				log.error("系统异常", e);
				throw new RuntimeException("系统异常", e);
			} finally {
				// 关闭连接,释放资源
				try {
					httpclient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return null;
		}
	}

	public static String postHttps(String url, Map<String, String> params) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.putAll(params);
		return urlPostHttps(url, map);
	}

	/**
	 * <p>https请求</p>
	 * 
	 * @param url 请求URL
	 * @param params 请求参数
	 * @return
	 * @version V1.0
	 * @author 林仙龙
	 * @date 2014-8-15下午5:11:08
	 */
	public static String urlPostHttps(String url, Map<String, Object> params) {
		log.info("请求urlPostHttps方法,url[{}]", url);
		if (!url.startsWith("https")) {
			return urlPostForString(url, params);
		} else {
			return postNoValidateHttps(url, params);
		}
	}

	private static String postNoValidateHttps(String url, Map<String, Object> params) {
		return postNoValidateHttps(url, params, null, null);
	}

	private static String postNoValidateHttps(String url, Map<String, Object> params, String contentType,
			String authorization) {
		HttpsURLConnection conn = null;
		try {
			String postParams = "";
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				postParams += entry.getKey() + "=" + entry.getValue() + "&";
			}
			if (postParams.length() > 0) {
				postParams = postParams.substring(0, postParams.lastIndexOf("&"));
			}
			postParams = postParams.replaceAll("\\+", "%2B");
			SSLContext context = SSLContext.getInstance("SSL");
			context.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new SecureRandom());
			URL curl = new URL(url);
			conn = (HttpsURLConnection) curl.openConnection();
			conn.setConnectTimeout(DEFAULT_TIME_OUT);
			conn.setReadTimeout(DEFAULT_TIME_OUT);
			conn.setSSLSocketFactory(context.getSocketFactory());
			conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
			conn.setRequestProperty("Cache-Control", "no-cache");
			if (StringUtils.isBlank(contentType))
				contentType = "application/x-www-form-urlencoded";
			conn.setRequestProperty("Content-Type", contentType);
			if (StringUtils.isNotBlank(authorization))
				conn.setRequestProperty("Authorization", authorization);
			// conn.addRequestProperty("X-Forwarded-For", "211.154.153.167");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.getOutputStream().write(postParams.getBytes());
			conn.getOutputStream().flush();
			conn.getOutputStream().close();
			conn.connect();
			int statusCode = conn.getResponseCode();
			if (HttpStatus.SC_OK == statusCode) {
				InputStream is = conn.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);
				BufferedReader br = new BufferedReader(new InputStreamReader(bis));
				StringBuffer buff = new StringBuffer();
				String line = null;
				while (null != (line = br.readLine())) {
					buff.append(line);
				}
				br.close();
				return buff.toString();
			} else {
				log.error("请求状态不为成功statusCode[{}]", statusCode);
			}
		} catch (IOException e) {
			log.error("io异常", e);
			throw new RuntimeException(IO_EXCEPTION, e);
		} catch (KeyManagementException e) {
			log.error("KeyManagementException异常", e);
		} catch (NoSuchAlgorithmException e) {
			log.error("算法不存在异常", e);
		} finally {
			if (null != conn) {
				conn.disconnect();
			}
		}
		return null;

	}

	/**
	 * <p>发送https请求，请求参数是xml格式</p>
	 * 
	 * @param url
	 * @param params
	 * @param charset
	 * @return
	 * @author 谢志平 2016-1-7 上午11:37:33
	 */
	public static String postHttpsForXml(String url, Map<String, Object> params, String charset) {
		HttpsURLConnection conn = null;
		try {
			String postParams = XmlUtils.parseXML(params);
			log.debug("http发送内容：" + postParams);
			SSLContext context = SSLContext.getInstance("TLSv1");
			context.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new SecureRandom());
			URL curl = new URL(url);
			conn = (HttpsURLConnection) curl.openConnection();
			conn.setConnectTimeout(DEFAULT_TIME_OUT);
			conn.setReadTimeout(DEFAULT_TIME_OUT);

			conn.setSSLSocketFactory(context.getSocketFactory());
			conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
			conn.setRequestProperty("Cache-Control", "no-cache");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.getOutputStream().write(postParams.getBytes(charset));
			conn.getOutputStream().flush();
			conn.getOutputStream().close();
			conn.connect();
			int statusCode = conn.getResponseCode();
			if (HttpStatus.SC_OK == statusCode) {
				InputStream is = conn.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);
				BufferedReader br = new BufferedReader(new InputStreamReader(bis, charset));
				StringBuffer buff = new StringBuffer();
				String line = null;
				while (null != (line = br.readLine())) {
					buff.append(line);
				}
				br.close();
				return buff.toString();
			} else {
				log.error("请求状态不为成功statusCode[{}]", statusCode);
			}
		} catch (IOException e) {
			log.error("io异常", e);
			throw new RuntimeException(IO_EXCEPTION, e);
		} catch (KeyManagementException e) {
			log.error("KeyManagementException异常", e);
		} catch (NoSuchAlgorithmException e) {
			log.error("算法不存在异常", e);
		} finally {
			if (null != conn) {
				conn.disconnect();
			}
		}
		return null;

	}

	/**
	 * <p>发送http请求，请求参数是json格式</p>
	 * 
	 * @param url
	 * @param jsonParam
	 * @return
	 * @author 谢志平 2016-3-11 上午8:54:10
	 */
	public static String postHttpForJsonparam(String url, String jsonStr) {
		HttpURLConnection conn = null;
		try {
			URL curl = new URL(url);
			conn = (HttpURLConnection) curl.openConnection();
			conn.setConnectTimeout(DEFAULT_TIME_OUT);
			conn.setReadTimeout(DEFAULT_TIME_OUT);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setUseCaches(false);
			conn.setInstanceFollowRedirects(true);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.connect();

			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			out.writeBytes(jsonStr);
			out.flush();
			out.close();

			int statusCode = conn.getResponseCode();
			if (HttpStatus.SC_OK == statusCode) {
				InputStream is = conn.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);
				BufferedReader br = new BufferedReader(new InputStreamReader(bis, "UTF-8"));
				StringBuffer buff = new StringBuffer();
				String line = null;
				while (null != (line = br.readLine())) {
					buff.append(line);
				}
				br.close();
				return buff.toString();
			} else {
				log.error("请求状态不为成功statusCode[{}]", statusCode);
			}
		} catch (Exception e) {
			log.error("http请求异常", e);
		} finally {
			if (null != conn) {
				conn.disconnect();
			}
		}
		return null;

	}

	private static class TrustAnyTrustManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[] {};
		}
	}

	private static class TrustAnyHostnameVerifier implements HostnameVerifier {

		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	public static String createAutoSubmitForm(String url, Map<String, Object> paramsMap) {
		StringBuffer sf = new StringBuffer();
		sf.append("<!DOCTYPE HTML>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\" />\n" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n" +
                "    <title>跳转中...</title>\n" +
                "    <style type=\"text/css\">\n" +
                "        #not_open_js{\n" +
                "            width: 800px;\n" +
                "            height: 100px;\n" +
                "            margin: 50px auto;\n" +
                "            border: 1px solid white;\n" +
                "            line-height: 100px; \n" +
                "            text-align: center; \n" +
                "            overflow: hidden;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div class=\"login-box\">\n" +
                "    <!-- /.login-logo -->\n" +
                "    <div class=\"login-box-body\">\n" +
                "        <div id=\"not_open_js\">对不起，你的浏览器没有打开JavaScript支持！欲使用本服务请开启JavaScript支持，谢谢!</div>\n");
		sf.append("<form id = \"sform\" action=\"" + url + "\" method=\"post\">");
		if (CollectionUtils.isNotEmpty(paramsMap)) {
			for (String key : paramsMap.keySet()) {
				sf.append("<input type=\"hidden\" name=\"" + key + "\" id=\"" + key + "\" value=\"" + paramsMap.get(key)
						+ "\"/>");
			}
		}
		sf.append("</form>");
		sf.append(" </div>\r\n" + 
				"</div>\r\n" + 
				"\r\n" + 
				"<script>\r\n" + 
				"    window.onload = function(){\r\n" + 
				"        document.getElementById(\"not_open_js\").style.display='none';\r\n" + 
				"        document.getElementById(\"sform\").submit();\r\n" + 
				"    };\r\n" + 
				"</script>\r\n" + 
				"</body>\r\n" + 
				"</html>");
		return sf.toString();
	}

	public static void main(String[] args) {
		Map<String, Object> map = new HashMap<String, Object>();
		// System.out.println(HttpUtils.postHttpsForXml("https://baidu.com", map, "UTF-8"));

		// map.put("requestTime", "20160615135054");
		map.put("a", "似懂非懂是");
		String returnStr = HttpUtils.urlGetForString("http://172.22.2.234:5657/pcmp-atx-mock/command.json", map, 1);
	}
}