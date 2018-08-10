package var;

/**
 * @author zhangzhiqiang
 * @date 2018-08-09 11:43
 * &Desc 寻常生活的 枚举变量
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

public enum a {
    A("purchanceProduct", "购买商品"),
    B("saleService", "购买服务"),
    C("cancelLike", "取消点赞"),
    D("cancelCollect", "取消收藏"),
    a("signUp", "注册"),
    b("login", "登录"),
    c("auth", "认证"),
    d("completeInformation", "完善资料"),
    e("updateProduct", "更新产品"),
    f("postForHelp", "帮帮发帖"),
    g("topic", "话题发帖"),
    h("idle", "闲置发帖"),
    i("pgc", "pgc发帖"),
    j("postForCarpooling", "拼车发帖"),
    k("reply", "回复"),
    l("like", "点赞"),
    m("beLike", "被点赞"),
    n("addToFavorites", "文章收藏"),
    o("accessToPersonal", "访问个人资料页"),
    p("personalInfarmation", "个人资料页被访问"),
    q("share", "分享"),
    r("fornumIsDeleted", "帖子被删除"),
    s("replyIsDeleted", "回复被删除"),
    t("commendSeller", "评论商家"),
    u("commentService", "评论服务"),
    v("openDoor", "一键开门"),
    w("payManagementFee", "物业缴费"),
    x("repair", "维修报障"),
    y("questionNaire", "问卷调查"),
    z("visitor", "访客放行");

    private final String E;
    private final String F;

    private a(String var3, String var4) {
        this.E = var3;
        this.F = var4;
    }

    public String a() {
        return this.E;
    }
}
