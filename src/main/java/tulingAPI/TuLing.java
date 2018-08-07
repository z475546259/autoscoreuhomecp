package tulingAPI;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import utils.RecordToFile;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author zhangzhiqiang
 * @date 2018-08-07 10:55
 * &Desc 图灵api
 */
public class TuLing {
    public static HttpClient httpClient = new DefaultHttpClient();

    public static String getMessageByInput(String input){
        try {
            String responseMessage = "";
            String apikey = "7e4c24cb18904993a18c657cfdc762fa";
            String inputJson = "{\n" +
                    "\t\"reqType\":0,\n" +
                    "    \"perception\": {\n" +
                    "        \"inputText\": {\n" +
                    "            \"text\": \""+input+"\"\n" +
                    "        }\n" +
                    "    },\n" +
                    "    \"userInfo\": {\n" +
                    "        \"apiKey\": "+apikey+",\n" +
                    "        \"userId\": \"304836\"\n" +
                    "    }\n" +
                    "}";
            HttpPost httpPost = new HttpPost("http://openapi.tuling123.com/openapi/api/v2");
            httpPost.setHeader("Accept","application/json");
            httpPost.addHeader(HTTP.CONTENT_TYPE,"application/json; charset=UTF-8");
            httpPost.setEntity(new StringEntity(inputJson, Charset.forName("UTF-8")));

            try {
                HttpResponse response = httpClient.execute(httpPost);
                responseMessage = EntityUtils.toString(response.getEntity(),"UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            //System.out.println("图灵机器人返回=="+responseMessage);
            JSONObject responseJson = JSONObject.parseObject(responseMessage);
            responseMessage = responseJson.getJSONArray("results").getJSONObject(0).getJSONObject("values").getString("text");
            System.out.println("截取后的图灵返回=="+responseMessage);
            return responseMessage;
        }catch (Exception e){
            RecordToFile.record(new String[]{e.getMessage()},"errorLog.txt");
            return null;
        }
    }

    public static void main(String[] args) {
        TuLing.getMessageByInput("对未来生活的所有向往");
    }

}
