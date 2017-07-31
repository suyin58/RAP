package com.wjs.openApi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wjs.openApi.util.HashUtil;
import com.wjs.openApi.util.RSAUtil;
import com.wjs.openApi.util.httpclient.HttpProtocolHandlerClient;
import com.wjs.openApi.util.httpclient.HttpRequest;
import com.wjs.openApi.util.httpclient.HttpResponse;
import com.wjs.openApi.util.httpclient.HttpResultType;

/**
 * openApi util
 *
 */
public class OpenApiMockRequest {
    
   
    
    /**
     * 展示只支持utf-8
     */
    public static String charset = "utf-8";
    
    /**
     * 服务版本号，目前只支持1.0.0
     */
    public static String version = "1.0.0";
    
    /**
     * 接入机构的RSA公钥
     */
    public static String appPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3GFaW5S8GI7ejxhnVjhC6is3k5ZXY+eHK7hV42W7aSPuQ1dg72XZ2D/cLIdv4wNf8H3vf0e2O0YNwuwst6rD/BWey0yBUnToTm6xsJg5dCMYNQCocgtDrBTgHYSkZY/eno0MMn1KdRN0ILvAvz4BmENOkfuD3TEHgzXZS+prDZOKIHfW0HUYNEGk3LQC6VKQawKY+QO7k188wokV85FzmYFvjkkOE0UHuFL/p2zGnwVv+ZHq34TzZQaQNnO3/INQqxi+HiPfpD2oTJDY1+cAqukCD46hM+4qPx4t6A1fe+Tr8GE4DeGW3+MIqcuCs79cGj1Xtca8AoGCqK/Nx2Y0JwIDAQAB";
    
    /**
     * 接入机构的RSA私钥
     */
    public static String appPrivateKey = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDcYVpblLwYjt6PGGdWOELqKzeTlldj54cruFXjZbtpI+5DV2DvZdnYP9wsh2/jA1/wfe9/R7Y7Rg3C7Cy3qsP8FZ7LTIFSdOhObrGwmDl0Ixg1AKhyC0OsFOAdhKRlj96ejQwyfUp1E3Qgu8C/PgGYQ06R+4PdMQeDNdlL6msNk4ogd9bQdRg0QaTctALpUpBrApj5A7uTXzzCiRXzkXOZgW+OSQ4TRQe4Uv+nbMafBW/5kerfhPNlBpA2c7f8g1CrGL4eI9+kPahMkNjX5wCq6QIPjqEz7io/Hi3oDV975OvwYTgN4Zbf4wipy4Kzv1waPVe1xrwCgYKor83HZjQnAgMBAAECggEAJWZYIUaijUBhwMMRdm5h3L+s1N0kw42dQOwtl0PChFtWqhMAHmCYkbx0rxHlCQ+fjn6w0FbpNDH1T+koxZqzW+qHYlT/dXDlo7nhaejLh0wVZZlQ/NmwiFmalyfVhm7eBuZE9aSRqEC+6ncyhMIPHzn88YVPoZAaiEfxMpL7y/e3UAXfnzMFXqVi2ihMbtu6w1FUMgnWjy8WqqwgoTFVdR6GjWUROAtfTUwZe6Fw+KVm2+KKgMWPPE2SPxXp3q0T6AwI6Eg1RbIjUYUsl/9DkUdXJBt9G6eaNbY7WkeRrN8HKMCA1zOIv2sJWEnHkg2N6FgTHkSWIBXjiJ5vsphXUQKBgQDvcd0+J0PT1qAphgg3xTjQBim7MOsFG2VvQH0r72Fj4qy2BOsRyuotBJBQlgyq/csyduHew/068qWCCQb534dTvVtOXXQ1rSvyIM/UjFM0/5BExtKluz1zm2tQt0KDEOyZ1OnbGryuV8Ap8ftAfy1XkiVeqo1E9T6ej1kRzopABQKBgQDrngzRKsQPKDaCHb7Wn4QxYq5u8bYXn/EymynOjJcGxeJ/KotFCcemWFlWjIsi/k0sCJ3yWATARqCd7eSYcW/AmNpe2b/g4w699WvlSWq+E5hCA/GVwQpwRJSYN/PhDqdkBRngJ/AD8lf6CEGxG3SaMYCfXCkdFYwYYgdRGfUXOwKBgA3ERiwkpcmwNVUt15sdQ77yG8Qfc+O/R321/3xfLwJHLhbpAXrsZ7pe4M1BU0khfmVQYHwmWJDjEpD/Y99J8sXlxTIkPWI4qqYpLMnTp5UMfIb3x3Sv50CWVv01DCXs+y19CFUInICJmwrOVtvGdBzs0ik3NRgZ4ZfMNhrH/TrhAoGAB55BndW7Jx5OvOBHVlssBAjDyRSJpbPnMZKwxFvpWi+1xhTTEfVh/i/nG5RJv2Tni9/vc3GDHdBqyxBxDrjEOz71+JEj0hqlVGEGDxDTobeyeZf1DLmEI+MjxtQwT3uQz/wWPRgte4MvcwcnUJmpqH6nQP/S2Hzk3bj1sZqcQRcCgYBG4JCI7b1qJnvOPyP4Eqt6GcZ3OpbwkwKkiYQ2lwgn+wHT/7l3HfpTpBKZFMSknrbD11MzE2Q6niP6luG+HZPBsLyEQduipqaFe/GLmTGjljnj2wG3981sQluyrNlNPbDrTQKQERkcy6v8L6NHnIsYrUT/uHC6UJTqVYWjk1WVeQ==";
    
    /**
     * 网金社公钥
     */
    public static String wjsPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAneBTIR72RkQQDLkap9GZr7zRQZGMhrH/TTmkvgjAIBweIm/nIgxN5Qn2GR4YtL1dojDASGp8GjXr+HNLruFsnhS0JDAFG4Fv4iKPqUfMaHI6PUT7E8TdCDi+CqUnkyzNjEGSp8n3wboKCLdeZmc84o+pXF8tgiP9sHDSoODcOr9YmfBLfsBIKpT1XgEmkwBUo5iTUJ+GiMvBac5ZfAAz/Z3lEAjDlhvO5Q21PUQrNGKgBX6OtTMmI/imyL4VsMsnGSSKyhcCrBJ+LVYgeVBXoET+A4EoxM6v4Jb58IFTQQ4mBTimfJapEvm34RwQPsPl/nydgk2pSpt/jWUx2Hl+QwIDAQAB";

    public static void main(String[] args) throws Exception, Exception{
        
    	 /**
         * 网金社网关地址
         * 生产环境：http://openapi.wjs.com/openapi.api/gateway.do
         * 测试环境：http://openapi.pc.trunk.wjs-test.com/openapi.api/gateway.do
         */
        String gateway = "http://openapi.pc.trunk.wjs-test.com/openapi.api/gateway.do";
        
        /**
         * 网金社为接入机构分配的接入标志
         */
        String appId = "35336601687595250";
        //组装业务参数
        Map<String,Object> bizMap = new HashMap<String, Object>();
        bizMap.put("mobileNo", "13811111111");
        bizMap.put("password", "abc123456");
        
        //将业务参数组装成json格式的字符串
        String bizParam = JSON.toJSONString(bizMap);
        String service = "userRegister";
        
        System.out.println(sendOpenApi(gateway, appId, service,  bizParam));
    }

	public static String sendOpenApi(String gateway, String appId, String serviceName, String bizParam)
			throws Exception, UnsupportedEncodingException, HttpException, IOException {
		//组装公共参数
        Map<String,String> paramMap = new HashMap<String, String>();
        paramMap.put("appId", appId);
        paramMap.put("charset", "utf-8");
        paramMap.put("service", serviceName);
        paramMap.put("version", "1.0.0");
        
        
        //使用网金社公钥对业务参数进行加密
        String bizParamEncrypt = HashUtil.encryptBASE64(RSAUtil.encryptByPublicKey(bizParam.getBytes("utf-8"), wjsPublicKey));
        paramMap.put("bizParams", URLEncoder.encode(bizParamEncrypt, "utf-8"));
        
        //计算并组装签名
        String sign = RSAUtil.sign(bizParam.getBytes("utf-8"), appPrivateKey);
        paramMap.put("sign", URLEncoder.encode(sign,"utf-8"));
        
        HttpProtocolHandlerClient httpClient = HttpProtocolHandlerClient.getInstance();
        HttpRequest httpRequest = new HttpRequest(HttpResultType.STRING);
        
        httpRequest.setUrl(gateway);
        httpRequest.setCharset(charset);
        httpRequest.setParameters(generatNameValuePair(paramMap));
        
        HttpResponse response = httpClient.executeQuery4(httpRequest, true);
        //返回结果
        String res = response.getStringResult();
        System.out.println(res);
        
        JSONObject jsonObject = JSON.parseObject(res);
        boolean success = jsonObject.getBooleanValue("success");
        if(success){
            String bizResponse = RSAUtil.decryptByPrivateKeyToString(jsonObject.getString("bizResponse"), appPrivateKey);
            //使用网金社公钥验签，验证是否是网金社返回的数据
            boolean checkSign = RSAUtil.verify(bizResponse.getBytes("utf-8"), wjsPublicKey, jsonObject.getString("sign"));
            if(checkSign){
                return bizResponse;
            }else{
            	throw new Exception("验签失败");
            }
        }else{
            String errorCode = jsonObject.getString("errorCode");
            String errorMessage = jsonObject.getString("errorMessage");
            System.out.println("出错了，errorCode:" + errorCode + ",errorMessage：" + errorMessage);
            return "出错了，errorCode:" + errorCode + ",errorMessage：" + errorMessage;
        }
	}
    
    private static NameValuePair[] generatNameValuePair(Map<String, String> properties) {
        NameValuePair[] nameValuePair = new NameValuePair[properties.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : properties.entrySet()) {
          nameValuePair[i++] = new NameValuePair(entry.getKey(), entry.getValue());
        }

        return nameValuePair;
    }
    
}