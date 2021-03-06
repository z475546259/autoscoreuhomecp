package com.zzq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import utils.OperateOracle;
import utils.RecordToFile;
import utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author zhangzhiqiang
 * @date 2018-08-06 15:47
 * &Desc 寻常生活签到领积分流程
 */
public class Uhomecp_flow {

    public void flow(User user) throws IOException{
        OneTLSPool2 oneTLSPool2 = new OneTLSPool2();
        Map<String,String> map = new HashMap<>();
        //1 登录
        map.put("password",user.getPassword());
        map.put("tel",user.getTel());
        String url = "https://www.uhomecp.com/uhomecp-sso/v3/userApp/login";
        String  login_result = oneTLSPool2.oneWayAuthorizationAcceptedPost(map,url);
        JSONObject finaJson = JSONObject.parseObject(login_result);
        String userId = finaJson.getJSONObject("data").getString("userId");
        //2 获取用户信息
        url = "https://www.uhomecp.com/uhomecp-sso/v3/userApp/profile.json";
        map.clear();
        String userInfo = oneTLSPool2.oneWayAuthorizationAcceptedGet(map,url);
        JSONObject userData = JSONObject.parseObject(userInfo).getJSONObject("data");
        String communityName = userData.getString("communityName");
        String cityId = userData.getString("cityId");
        String provinceId = userData.getString("provinceId");
        String regionId = userData.getString("regionId");
        String communityId = userData.getString("jobCommunity");
        //3 领取登录积分
        getScore(oneTLSPool2,"login");
        map.clear();
        //4 七天签到活动
        String actId ="";
        //先判断今天是不是星期一,是星期一就读取文件，看文件里面是否已经有更新过的actId，否则就，爬取actId放进去
        Calendar calendar = Calendar.getInstance();
        int xq = calendar.get(Calendar.DAY_OF_WEEK);
        InputStream inputStream = new FileInputStream("actId.txt");
        String actIdandTime = Utils.getStringFromStream(inputStream);
        if(xq==2){
            String dateStr = actIdandTime.split(" ")[1];
            SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
            String todayStr =  sdf.format(calendar.getTime());
            if(todayStr.equalsIgnoreCase(dateStr)){
                actId = actIdandTime.split(" ")[0];
            }else {
                //爬取今天星期一最新的actId
                url = "https://www.uhomecp.com/uhomecp-sso/v1/msg/getMsgV3?cityId=2&provinceId=1";
                String actJsonStr = oneTLSPool2.oneWayAuthorizationAcceptedGet(map,url);
                JSONArray data = JSON.parseObject(actJsonStr).getJSONArray("data");
                for (Object obj:data) {
                    JSONObject json = (JSONObject) obj;
                    if(json.getString("type").equals("50004")&&json.getString("createTime").split(" ")[0].equals(todayStr)){
                            actId = json.getString("turnTip");
                            RecordToFile.record(new String[]{actId+" "+todayStr},"actId.txt",false);
                            break;
                    }
                }
            }
        }else{
            actId = actIdandTime.split(" ")[0];
        }




        //从文件中读取活动id
//        InputStream inputStream = new FileInputStream("actId.txt");
//         Utils.getStringFromStream(inputStream);
        System.out.println("actId=="+actId);
        try {
            map.put("actId",actId);
            map.put("userId",userId);
            map.put("communityId",communityId);
            map.put("provinceId",provinceId);
            map.put("cityId",cityId);
            map.put("region",regionId);
            url = "https://www.uhomecp.com/act-api/actvityBehavior/sign";
            oneTLSPool2.oneWayAuthorizationAcceptedPostJson(map,url);
        }catch (Exception e){

        }
        //5 七天活动抽奖
        map.clear();
        try {
            map.put("triggerEventId","235");
            map.put("actId",actId);
            map.put("lotteryId","226");
            map.put("userId",userId);
            map.put("communityId","1");
            map.put("provinceId","1");
            map.put("cityId","2");
            map.put("region","4");
            url = "https://www.uhomecp.com/act-api/actvityBehavior/userLottery";
            oneTLSPool2.oneWayAuthorizationAcceptedPostJson(map,url);
        }catch (Exception e){

        }



        //8 领取参与主题积分
        getScore(oneTLSPool2,"topic");

        //12 领取评论文章积分
        getScore(oneTLSPool2,"pgc");

        //13 分享文章
        getScore(oneTLSPool2,"share");

        //14 点赞文章
        for(int i=0;i<10;i++){
            getScore(oneTLSPool2,"like");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //15 收藏文章
        for(int i=0;i<10;i++) {
            getScore(oneTLSPool2,"addToFavorites");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //16 回复评论
        getScore(oneTLSPool2,"reply");

        //17 开门
        for(int i=0;i<3;i++) {
            getScore(oneTLSPool2,"openDoor");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //18 集市发帖
        getScore(oneTLSPool2,"idle");
        //19 购买商品
        getScore(oneTLSPool2,"purchanceProduct");
        //20 购买服务
        getScore(oneTLSPool2,"saleService");
        //21 认证
        getScore(oneTLSPool2,"auth");
        //22 完善资料
        getScore(oneTLSPool2,"completeInformation");
        //23 更新产品
        for(int i=0;i<30;i++) {
            getScore(oneTLSPool2,"updateProduct");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //24 帮帮发帖
        getScore(oneTLSPool2,"postForHelp");
        //25 拼车发帖
        getScore(oneTLSPool2,"postForCarpooling");
        //26 被点赞
        for(int i=0;i<20;i++) {
            getScore(oneTLSPool2,"beLike");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //27 评论商家
        getScore(oneTLSPool2,"commendSeller");
        //28 评论服务
        getScore(oneTLSPool2,"commentService");
        //29 物业缴费
        getScore(oneTLSPool2,"payManagementFee");
        //30 维修报障
        getScore(oneTLSPool2,"repair");
        //31 问卷调查
        getScore(oneTLSPool2,"questionNaire");
        //32 访客放行
        for(int i=0;i<3;i++) {
            getScore(oneTLSPool2,"visitor");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 最后一步 查看积分余额
        map.clear();
        url = "https://www.uhomecp.com/uhomecp-sso/v1/balalce/getBanalces.json";
        String banalcesStr = oneTLSPool2.oneWayAuthorizationAcceptedGet(map,url);
        JSONObject banalcesJson = JSONObject.parseObject(banalcesStr);
        String balance = banalcesJson.getJSONArray("data").getJSONObject(0).getString("balance");
        user.setEarn(Integer.parseInt(balance)-user.getScore());
        user.setScore(Integer.parseInt(balance));
        user.setUserId(userId);
        OperateOracle operateOracle = new OperateOracle();
        operateOracle.updateUserData("寻常生活",user);
    }

    public static void main(String[] args) {
        Uhomecp_flow uhomecp_flow = new Uhomecp_flow();
        User user = new User();
        user.setTel("15213114321");
        user.setPassword("mazhaotong");
        try {
            uhomecp_flow.flow(user);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void getScore(OneTLSPool2 oneTLSPool2,String type){
        Map<String,String> map = new HashMap<>();
        map.put("code", type);
        String url = "https://www.uhomecp.com/integral-api/behavior/analyseBehavior";
        try {
            oneTLSPool2.oneWayAuthorizationAcceptedPost(map, url);
        }catch (Exception e){
            RecordToFile.record(new String[]{""},"errorlog.txt");
        }
    }

}

//每天参与话题
//5 获取话题列表
//        map.clear();
//        url = "https://www.uhomecp.com/uhomecp-cbs-api/bbs/queryRecommend.json?pageLimit=100&pageNo=1";
//        String topicsStr = oneTLSPool2.oneWayAuthorizationAcceptedGet(map,url);
//        JSONObject topicsJson = JSONObject.parseObject(topicsStr);
//        JSONArray recommend = topicsJson.getJSONObject("data").getJSONArray("recommend");
//        List<Integer> selectedTopics = new ArrayList<>();
//        for (Object topicObj:recommend) {
//            JSONObject topicJson = (JSONObject) topicObj;
//            //浏览量大 评论数才可能大，且类型是5话题 6是活动
//            if(topicJson.getIntValue("browseCount")>1500&&topicJson.getIntValue("objType")==5){
//                selectedTopics.add(topicJson.getInteger("objId"));
//            }
//        }
//        //从筛选后的话题中随机选出一个话题
//        int randomTopic = new Random().nextInt(selectedTopics.size());
//        int topicId = selectedTopics.get(randomTopic);
//        selectedTopics.remove(randomTopic);
//        //6 进入这个话题
//        url = "https://www.uhomecp.com/uhomecp-cbs-api/bbs/queryPictorialDetailById.json?id="+topicId;
//        String topicStr = oneTLSPool2.oneWayAuthorizationAcceptedGet(map,url);
//        //获取话题标题
//        String topicTittle = JSONObject.parseObject(topicStr).getJSONObject("data").getString("title");
//        //向图灵机器人获取聊天信息
//        String quizContent = TuLing.getMessageByInput(topicTittle);
//        //7 参与话题 发表评论
//        map.clear();
//        map.put("linkTitle",topicTittle);
//        map.put("objType","5");
//        map.put("quizContent",quizContent);
//        map.put("objId",topicId+"");
//        map.put("quizPic","");
//        map.put("organId",communityId);
//        map.put("communityId",communityId);
//        map.put("regionId",cityId);
//        map.put("userId",userId);
//        map.put("quizTypeId","10011");
//        map.put("quizRangeId","3");
//        map.put("linkUrl",topicId+"");
//        map.put("communityName",communityName);
//        url = "https://www.uhomecp.com/uhomecp-cbs/quiz/saveQuiz.json";
//        String saveQuizResult = oneTLSPool2.oneWayAuthorizationAcceptedPost(map,url);

//        //9 获取文章列表
//        int[] quizTypeIds = {1011,10016,10012,10013,10014,10015,17,5,10,15,4,7,14};
//        map.clear();
//        map.put("actIndex","");
//        map.put("cityId",cityId);
//        map.put("communityId",communityId);
//        map.put("isRecommend","0");
//        map.put("pageNo","1");
//        map.put("pageSize","100");
//        map.put("provinceId",provinceId);
//        //10 随机获取一个类型的文章列表 获取100条数据
//        map.put("quizTypeId",quizTypeIds[new Random().nextInt(quizTypeIds.length)]+"");
//        map.put("regionId",regionId);
//        map.put("type","1");
//        map.put("userId",userId);
//        url = "https://www.uhomecp.com/uhomecp-cbs/pictorial/list.json";
//        String articlString = oneTLSPool2.oneWayAuthorizationAcceptedPost(map,url);
////        List<String> articlList = new ArrayList<>();
//        Map<String,String> articlList = new HashMap<>();
//        JSONArray   pictorialList = JSONObject.parseObject(articlString).getJSONObject("data").getJSONArray("pictorialList");
//        for (Object obj:pictorialList) {
//            JSONObject pictorial = (JSONObject) obj;
//            articlList.put(pictorial.getString("pictorialId"),pictorial.getString("pictorialTitle"));
//        }
//        String[] keys = articlList.keySet().toArray(new String[0]);
//        String articleId  = keys[new Random().nextInt(keys.length)];
//        String objTitleForMsg = articlList.get(articleId);
//        String commentContent = TuLing.getMessageByInput(objTitleForMsg);
//        //11 评论文章
//        map.clear();
//        map.put("byReviewUserId","");
//        map.put("commentContent",commentContent);
//        map.put("communityId",communityId);
//        map.put("communityName",communityName);
//        map.put("objId",articleId);
//        map.put("objPicUrlForMsg","");
//        map.put("objTitleForMsg",objTitleForMsg);
//        map.put("objType","1");
//        map.put("parentObjId",articleId);
//        map.put("parentObjType","1");
//        map.put("userId",userId);
//        url = "https://www.uhomecp.com/uhomecp-cbs/comment/save.json";
//        oneTLSPool2.oneWayAuthorizationAcceptedPost(map,url);
