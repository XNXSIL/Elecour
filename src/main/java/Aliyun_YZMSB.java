import com.aliyun.api.gateway.demo.util.HttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import java.util.HashMap;
import java.util.Map;

public class Aliyun_YZMSB {
    private static final String host = "http://txyzmsb.market.alicloudapi.com";
    private static final String path = "/yzm";
    private static final String method = "POST";
    private static final String appcode = "3bace5846ef84819896a8b7f13a7220c";
    public static String getContext(String imgBase64){
        String YZM = null;
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("v_pic", imgBase64);
        bodys.put("v_type", "ne4");
        try {

            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            //获取返回body部分
            String mess = EntityUtils.toString(response.getEntity());
            //获取验证码
            YZM = mess.substring(25,29);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return YZM;
    }
}
