package cn.laymm.Pay.controller;

import cn.laymm.Pay.pojo.Pay;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by 郑光达 on 2019/1/13 0028.85999
 */
@RestController
@RequestMapping("/Pay")
public class PayController {

    @RequestMapping("/CreatPay")
    private void CreatPay(Pay pay, HttpServletResponse response) throws IOException {
        /**
         * 接收参数 创建订单
         */
        String token = "va5Ljepd7NsHeCD9SktqfSh3VADNF4ud"; //记得更改 http://codepay.fateqq.com 后台可设置
        String codepay_id = "214121";//记得更改 http://codepay.fateqq.com 后台可获得
        pay.setUid(123);
        pay.setParam(UUID.randomUUID().toString());
        String notify_url = "https://www.laymm.cn/Pay/CodePay";//通知地址
        String return_url = "https://www.laymm.cn/Pay/CodePay";//支付后同步跳转地址

        if (pay.getPrice() <= 0) {
            pay.setPrice(1);
        }
        //参数有中文则需要URL编码
        String url = "http://codepay.fateqq.com:52888/creat_order?id=" + codepay_id + "&pay_id=" + pay.getUid() + "&price=" + pay.getPrice() + "&type=" + pay.getType() + "&token=" + token + "&param=" + pay.getParam() + "&notify_url=" + notify_url + "&return_url=" + return_url;

        response.sendRedirect(url);
    }

    @RequestMapping("/CodePay")
    private ModelAndView CodePay(Model model, Pay pay, HttpServletRequest request, HttpServletResponse response) throws IOException, NoSuchAlgorithmException {
        /**
         *验证通知 处理自己的业务
         */
        String key = "0MnsUnGbC0RLNkXxi9zNQ27wxgO8uJac"; //记得更改 http://codepay.fateqq.com 后台可设置
        Map<String, String> params = new HashMap<String, String>(); //申明hashMap变量储存接收到的参数名用于排序
        Map requestParams = request.getParameterMap(); //获取请求的全部参数
        String valueStr = ""; //申明字符变量 保存接收到的变量
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            valueStr = values[0];
            //乱码解决，这段代码在出现乱码时使用。如果sign不相等也可以使用这段代码转化
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);//增加到params保存
        }
        List<String> keys = new ArrayList<String>(params.keySet()); //转为数组
        Collections.sort(keys); //重新排序
        String prestr = "";
        String sign = params.get("sign"); //获取接收到的sign 参数

        for (int i = 0; i < keys.size(); i++) { //遍历拼接url 拼接成a=1&b=2 进行MD5签名
            String key_name = keys.get(i);
            String value = params.get(key_name);
            if (value == null || value.equals("") || key_name.equals("sign")) { //跳过这些 不签名
                continue;
            }
            if (prestr.equals("")) {
                prestr = key_name + "=" + value;
            } else {
                prestr = prestr + "&" + key_name + "=" + value;
            }
        }
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update((prestr + key).getBytes());
        String mySign = new BigInteger(1, md.digest()).toString(16).toLowerCase();
        if (mySign.length() != 32) mySign = "0" + mySign;
        if (mySign.equals(sign)) {
            //编码要匹配 编码不一致中文会导致加密结果不一致
            //参数合法处理业务
            String pay_no = request.getParameter("pay_no");//流水号
//request.getParameter("pay_id") 用户唯一标识
            String money = request.getParameter("money");//付款金额
            String param = request.getParameter("param");//参数
            String price = request.getParameter("price");//提交的金额
            model.addAttribute("money", money);
            model.addAttribute("param", param);
            model.addAttribute("price", price);
            model.addAttribute("pay_no", pay_no);
            return new ModelAndView("templates/pay/success", "model", model);
        }

        return new ModelAndView("templates/pay/error", "model", model);
    }
}
