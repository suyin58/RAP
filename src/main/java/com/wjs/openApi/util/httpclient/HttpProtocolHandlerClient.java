package com.wjs.openApi.util.httpclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.util.IdleConnectionTimeoutThread;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

public class HttpProtocolHandlerClient {

    private static String DEFAULT_CHARSET = "GBK";

    /** 连接超时时间，由bean factory设置，缺省为8秒钟 */
    private int defaultConnectionTimeout = 8000;

    /** 回应超时时间, 由bean factory设置，缺省为30秒钟 */
    private int defaultSoTimeout = 30000;

    /** 闲置连接超时时间, 由bean factory设置，缺省为60秒钟 */
    private int defaultIdleConnTimeout = 60000;

    private int defaultMaxConnPerHost = 30;

    private int defaultMaxTotalConn = 80;

    // /** 默认等待HttpConnectionManager返回连接超时（只有在达到最大连接数时起作用）：1秒 */
    // private static final long defaultHttpConnectionManagerTimeout = 3 * 1000;

    /**
     * HTTP连接管理器，该连接管理器必须是线程安全的.
     */
    private HttpConnectionManager connectionManager;

    private static HttpProtocolHandlerClient httpProtocolHandler = new HttpProtocolHandlerClient();


    /**
     * 工厂方法
     * 
     * @return
     */
    public static HttpProtocolHandlerClient getInstance() {
        if (httpProtocolHandler == null) {
            httpProtocolHandler = new HttpProtocolHandlerClient();
        }
        return httpProtocolHandler;
    }

    /**
     * 私有的构造方法
     */
    private HttpProtocolHandlerClient() {
        // 创建一个线程安全的HTTP连接池
        connectionManager = new MultiThreadedHttpConnectionManager();
        connectionManager.getParams().setDefaultMaxConnectionsPerHost(
                defaultMaxConnPerHost);
        connectionManager.getParams().setMaxTotalConnections(
                defaultMaxTotalConn);

        IdleConnectionTimeoutThread ict = new IdleConnectionTimeoutThread();
        ict.addConnectionManager(connectionManager);
        ict.setConnectionTimeout(defaultIdleConnTimeout);

        ict.start();
    }

    /**
     * 
     * @param request
     * @param uploadFileNameList
     * @param uploadFileList
     * @param sslSkip
     * @return
     * @throws HttpException
     * @throws IOException
     */
    public String executeWihtUploadFile4(HttpRequest request,
            List<String> uploadFileNameList, List<File> uploadFileList,boolean sslSkip)
            throws HttpException, IOException {
        HttpClient httpclient = new DefaultHttpClient();
        // 是否跳过ssl验证
        if(sslSkip) {
            enableSSL(httpclient);
        }
        
        // 设置连接超时
        int connectionTimeout = defaultConnectionTimeout;
        if (request.getConnectionTimeout() > 0) {
            connectionTimeout = request.getConnectionTimeout();
        }
        httpclient.getParams().setParameter(
                CoreConnectionPNames.CONNECTION_TIMEOUT, connectionTimeout);

        // 设置回应超时
        int soTimeout = defaultSoTimeout;
        if (request.getTimeout() > 0) {
            soTimeout = request.getTimeout();
        }
        httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
                soTimeout);
        
        String charset = request.getCharset();
        charset = charset == null ? DEFAULT_CHARSET : charset;

        HttpPost httppost = new HttpPost(request.getUrl());
        MultipartEntity reqEntity = new MultipartEntity();

        // post模式且带上传文件
        for (int i = 0; i < request.getParameters().length; i++) {
            StringBody stringBody = new StringBody(
                    request.getParameters()[i].getValue(),
                    Charset.forName(charset));
            reqEntity.addPart(request.getParameters()[i].getName(), stringBody);
        }
        // 增加文件参数，strParaFileName是参数名，使用本地文件
        int fileCount = uploadFileNameList.size();
        for (int i = 0; i < fileCount; i++) {
            FileBody fileBody = new FileBody(uploadFileList.get(i));
            reqEntity.addPart(uploadFileNameList.get(i), fileBody);
        }

        // 设置请求体
        httppost.setEntity(reqEntity);
        httppost.setHeader("User-Agent", "Mozilla/4.0");
        org.apache.http.HttpResponse response = null;
        org.apache.http.HttpEntity responseEntity = null;
        String stringResult = "";
        try {
            response = httpclient.execute(httppost);
            responseEntity = response.getEntity();
            BufferedReader reader = new BufferedReader(new InputStreamReader(responseEntity.getContent(), "UTF-8"));
            String line = null;
            while ((line = reader.readLine()) != null) {
                stringResult = stringResult + line;
            }
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
            return null;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return stringResult;
    }
    
    /**
     * 执行Http请求
     * 
     * @param request 请求数据
     * @return 
     * @throws HttpException, IOException 
     */
    public com.wjs.openApi.util.httpclient.HttpResponse executeQuery4(HttpRequest request,boolean sslSkip) throws HttpException, IOException {
        HttpClient httpclient = new DefaultHttpClient();
        // 是否跳过ssl验证
        if(sslSkip) {
            enableSSL(httpclient);
        }
        
        // 设置连接超时
        int connectionTimeout = defaultConnectionTimeout;
        if (request.getConnectionTimeout() > 0) {
            connectionTimeout = request.getConnectionTimeout();
        }
        httpclient.getParams().setParameter(
                CoreConnectionPNames.CONNECTION_TIMEOUT, connectionTimeout);

        // 设置回应超时
        int soTimeout = defaultSoTimeout;
        if (request.getTimeout() > 0) {
            soTimeout = request.getTimeout();
        }
        httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
                soTimeout);
        
        String charset = request.getCharset();
        charset = charset == null ? DEFAULT_CHARSET : charset;
        
        HttpPost httppost = new HttpPost(request.getUrl());
        
        httppost.setEntity(new UrlEncodedFormEntity(convertNameValuePair(request.getParameters()), charset));
        
        httppost.setHeader("Content-Type", "application/x-www-form-urlencoded; text/html; charset=" + charset);
        // 设置Http Header中的User-Agent属性
        httppost.setHeader("User-Agent", "Mozilla/4.0");

        com.wjs.openApi.util.httpclient.HttpResponse response = new com.wjs.openApi.util.httpclient.HttpResponse();
        
        try {
            org.apache.http.HttpResponse clientResponse = httpclient.execute(httppost);
            if (request.getResultType().equals(HttpResultType.STRING)) {
                response.setStringResult(EntityUtils.toString(clientResponse.getEntity()));
            } else if (request.getResultType().equals(HttpResultType.BYTES)) {
                response.setByteResult(EntityUtils.toByteArray(clientResponse.getEntity()));
            }
            response.setResponseHeaders(convertHeader(clientResponse.getAllHeaders()));
        } catch (UnknownHostException ex) {
            return null;
        } catch (IOException ex) {
            return null;
        } catch (Exception ex) {
            return null;
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return response;
    }
    
    private List<org.apache.http.NameValuePair> convertNameValuePair(NameValuePair[] param) {
        if(param == null) {
            return null;
        }
        
        List<org.apache.http.NameValuePair> nameValuePairs = new ArrayList<org.apache.http.NameValuePair>();
        
        for(NameValuePair pair : param) {
            nameValuePairs.add(new BasicNameValuePair(pair.getName(), pair.getValue()));
        }
        
        return nameValuePairs;
    }
    
    private Header[] convertHeader(org.apache.http.Header[] headers) {
        if(headers == null) {
            return null;
        }
        
        Header[] headersRes = new Header[headers.length];
        
        for(int i=0;i<headers.length;i++) {
            Header headerRes = new Header(headers[i].getName(), headers[i].getValue());
            headersRes[i] = headerRes;
        }
        
        return headersRes;
    }

    /**
     * 将NameValuePairs数组转变为字符串
     * 
     * @param nameValues
     * @return
     */
    protected String toString(NameValuePair[] nameValues) {
        if (nameValues == null || nameValues.length == 0) {
            return "null";
        }

        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < nameValues.length; i++) {
            NameValuePair nameValue = nameValues[i];

            if (i == 0) {
                buffer.append(nameValue.getName() + "=" + nameValue.getValue());
            } else {
                buffer.append("&" + nameValue.getName() + "="
                        + nameValue.getValue());
            }
        }

        return buffer.toString();
    }

    /**
     * 访问https的网站
     * 
     * @param httpclient
     */
    private static void enableSSL(HttpClient httpclient) {
        // 调用ssl
        try {
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[] { truseAllManager }, null);
            SSLSocketFactory sf = new SSLSocketFactory(sslcontext);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            Scheme https = new Scheme("https", sf, 443);
            httpclient.getConnectionManager().getSchemeRegistry()
                    .register(https);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重写验证方法，取消检测ssl
     */
    private static TrustManager truseAllManager = new X509TrustManager() {

        public void checkClientTrusted(
                java.security.cert.X509Certificate[] arg0, String arg1) {
            // TODO Auto-generated method stub

        }

        public void checkServerTrusted(
                java.security.cert.X509Certificate[] arg0, String arg1) {
            // TODO Auto-generated method stub

        }

        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            // TODO Auto-generated method stub
            return null;
        }

    };
}
