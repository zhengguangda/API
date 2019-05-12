package cn.laymm.Pay.pojo;

public class Pay {

    private Integer site;  //域名
    private String name; //名称
    private double price; //价格
    private Integer type;  //类型  1：支付宝 2：QQ钱包 3：微信
    private Integer uid;    //支付人的唯一标识
    private String param;   //自定义一些参数 支付后返回
    private Integer strat; //状态

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSite() {
        return site;
    }

    public void setSite(Integer site) {
        this.site = site;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public Integer getStrat() {
        return strat;
    }

    public void setStrat(Integer strat) {
        this.strat = strat;
    }
}
