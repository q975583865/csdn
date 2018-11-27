package utils;

import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpClientUtil {

	public static String doGet(String url, Map<String, String> requestMap) {

		// 创建Httpclient对象
		CloseableHttpClient httpclient = HttpClients.createDefault();

		String resultString = "";
		CloseableHttpResponse response = null;
		try {
			// 创建uri
			URIBuilder builder = new URIBuilder(url);
			if (requestMap != null) {
				for (String key : requestMap.keySet()) {
					builder.addParameter(key, requestMap.get(key));
				}
			}
			URI uri = builder.build();

			// 创建http GET请求
			HttpGet httpGet = new HttpGet(uri);

			// 执行请求
			response = httpclient.execute(httpGet);
			// 判断返回状态是否为200
			if (response.getStatusLine().getStatusCode() == 200) {
				resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null) {
					response.close();
				}
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resultString;
	}

	public static String doGet(String url) {
		return doGet(url, null);
	}

	public static String doPost(String url, Map<String, Object> requestMap) {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);
			// 创建参数列表
			if (requestMap != null) {
				List<NameValuePair> paramList = new ArrayList<>();
				for (String key : requestMap.keySet()) {
					paramList.add(new BasicNameValuePair(key, requestMap.get(key).toString()));
				}
				// 模拟表单
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList);
				httpPost.setEntity(entity);
			}
			// 执行http请求
			response = httpClient.execute(httpPost);
			resultString = EntityUtils.toString(response.getEntity(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return resultString;
	}

	public static String doPost(String url) {
		return doPost(url, null);
	}
	
	
	/**
	 * 绕过证书
	 * 模拟请求
	 * 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static String doHttpsPost(String url, Map<String,String> requestMap,String encoding) throws KeyManagementException, NoSuchAlgorithmException, ClientProtocolException, IOException {
		String body = "";
		//采用绕过验证的方式处理https请求
		SSLContext sslcontext = createIgnoreVerifySSL();
		
        // 设置协议http和https对应的处理socket链接工厂的对象
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", PlainConnectionSocketFactory.INSTANCE)
            .register("https", new SSLConnectionSocketFactory(sslcontext))
            .build();
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        HttpClients.custom().setConnectionManager(connManager);

        //创建自定义的httpclient对象
		CloseableHttpClient client = HttpClients.custom().setConnectionManager(connManager).build();
//		CloseableHttpClient client = HttpClients.createDefault();
		
		//创建post方式请求对象
		HttpPost httpPost = new HttpPost(url);
		
		//装填参数
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		if(requestMap!=null){
			for (Entry<String, String> entry : requestMap.entrySet()) {
				nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}
		//设置参数到请求对象中
		httpPost.setEntity(new UrlEncodedFormEntity(nvps, encoding));

		System.out.println("请求地址："+url);
		System.out.println("请求参数："+nvps.toString());
		
		//设置header信息
		//指定报文头【Content-type】、【User-Agent】
		httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
		httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
		
		//执行请求操作，并拿到结果（同步阻塞）
		CloseableHttpResponse response = client.execute(httpPost);
		//获取结果实体
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			//按指定编码转换结果实体为String类型
			body = EntityUtils.toString(entity, encoding);
		}
		EntityUtils.consume(entity);
		//释放链接
		response.close();
        return body;
	}
	
	
	 /** 绕过证书
     * 模拟GET请求 
     * @param client 
     * @param url 
     * @throws IOException 
     * @throws ClientProtocolException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
     */  
    public static String doHttpsGet(String url) throws IOException,  
            ClientProtocolException, KeyManagementException, NoSuchAlgorithmException {  
    	String body = "";
		//采用绕过验证的方式处理https请求
		SSLContext sslcontext = createIgnoreVerifySSL();
		
        // 设置协议http和https对应的处理socket链接工厂的对象
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", PlainConnectionSocketFactory.INSTANCE)
            .register("https", new SSLConnectionSocketFactory(sslcontext))
            .build();
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        HttpClients.custom().setConnectionManager(connManager);

        //创建自定义的httpclient对象
		CloseableHttpClient client = HttpClients.custom().setConnectionManager(connManager).build();
    	
    	
        System.out.println("======GET:"+url+"===========================");  
        HttpGet get = new HttpGet(url);  
        CloseableHttpResponse response = client.execute(get);  
      //获取结果实体
      		HttpEntity entity = response.getEntity();
      		if (entity != null) {
      			//按指定编码转换结果实体为String类型
      			body = EntityUtils.toString(entity, "utf-8");
      		}
      		EntityUtils.consume(entity);
      		//释放链接
      		response.close();
              return body; 
    }  
    
    /**
	 * 绕过验证
	 * 	
	 * @return
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */
	private static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
		SSLContext sc = SSLContext.getInstance("SSLv3");

		// 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
		X509TrustManager trustManager = new X509TrustManager() {
			@Override
			public void checkClientTrusted(
					java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
					String paramString) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(
					java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
					String paramString) throws CertificateException {
			}

			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};

		sc.init(null, new TrustManager[] { trustManager }, null);
		return sc;
	}
}


