package com.zzq;

import com.alibaba.fastjson.JSONObject;
import utils.OperateOracle;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * @author zhangzhiqiang
 * @date 2018-08-06 15:47
 * &Desc 寻常生活签到领积分流程
 */
public class Uhomecp_flow {

    public void flow(User user) throws IOException{
        OneTLSPool2 oneTLSPool2 = new OneTLSPool2();
        Map<String,String> map = new HashMap<>();
        map.put("password",user.getPassword());
        map.put("tel",user.getTel());
        String url = "https://www.uhomecp.com/uhomecp-sso/v3/userApp/login";
        String  login_result = oneTLSPool2.oneWayAuthorizationAcceptedPost(map,url);
        JSONObject finaJson = JSONObject.parseObject(login_result);
        String userId = finaJson.getJSONObject("data").getString("userId");
        map.clear();
        map.put("code","login");
        url = "https://www.uhomecp.com/integral-api/behavior/analyseBehavior";
        oneTLSPool2.oneWayAuthorizationAcceptedPost(map,url);
        map.clear();
        map.put("actId","322");
        map.put("userId",userId);
        map.put("communityId","1");
        map.put("provinceId","1");
        map.put("cityId","2");
        map.put("region","4");

        url = "https://www.uhomecp.com/act-api/actvityBehavior/sign";
        oneTLSPool2.oneWayAuthorizationAcceptedPostJson(map,url);
        url = "https://www.uhomecp.com/uhomecp-sso/v1/balalce/getBanalces.json";
        String banalcesStr = oneTLSPool2.oneWayAuthorizationAcceptedGet(map,url);
        JSONObject banalcesJson = JSONObject.parseObject(banalcesStr);
        String balance = banalcesJson.getJSONArray("data").getJSONObject(0).getString("balance");
        user.setScore(Integer.parseInt(balance));
        OperateOracle operateOracle = new OperateOracle();
        operateOracle.updateUserData("寻常生活",user);
    }

    public static void main(String[] args) {
        Uhomecp_flow uhomecp_flow = new Uhomecp_flow();

        try {
            uhomecp_flow.flow(new User());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
