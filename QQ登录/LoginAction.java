package cn.laymm.TheThirdParty.controller;

import cn.laymm.BuiltIn.lmSiteInfo.pojo.LmSiteInfo;
import cn.laymm.BuiltIn.lmSiteInfo.service.ILmSiteInfoService;
import cn.laymm.BuiltIn.lmSysNew.pojo.LmSysNew;
import cn.laymm.BuiltIn.lmSysNew.service.ILmSysNewService;
import cn.laymm.BuiltIn.lmSystem.pojo.LmSystem;
import cn.laymm.BuiltIn.lmSystem.service.ILmSystemService;
import cn.laymm.BuiltIn.lmUserNav.pojo.LmUserNav;
import cn.laymm.BuiltIn.lmUserNav.service.ILmUserNavService;
import cn.laymm.BuiltIn.lmUserQqlogin.pojo.LmUserQqlogin;
import cn.laymm.BuiltIn.lmUserQqlogin.service.ILmUserQqloginService;
import cn.laymm.BuiltIn.lmWebNav.pojo.LmWebNav;
import cn.laymm.BuiltIn.lmWebNav.service.ILmWebNavService;
import cn.laymm.BuiltIn.lmWebUser.pojo.LmWebUser;
import cn.laymm.BuiltIn.lmWebUser.service.ILmWebUserService;
import cn.laymm.Common.PathCommon;
import cn.laymm.Forward.pojo.ReturnJson;
import cn.laymm.TheThirdParty.pojo.OAuthProperties;
import cn.laymm.TheThirdParty.pojo.QQDTO;
import cn.laymm.TheThirdParty.pojo.QQOpenidDTO;
import cn.laymm.Utils.CommonUtil;
import cn.laymm.Utils.HttpsUtils;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;

@RestController
@RequestMapping("/user")
public class LoginAction {

    //用户表
    @Resource
    ILmWebUserService lmWebUserService;
    //qqlogin
    @Resource
    ILmUserQqloginService lmUserQqloginService;
    @Resource
    CommonUtil commonUtil;
    @Autowired
    private OAuthProperties oauth;

    public LmSysNew GetQQAPI() {
        return (LmSysNew) lmSysNewService.findAll().get(0);
    }

    //QQ登陆对外接口，只需将该接口放置html的a标签href中即可
    @RequestMapping("/qqlogin")
    public void loginQQ(HttpServletResponse response) {
        try {
            LmSysNew aNew = GetQQAPI();
            response.sendRedirect(oauth.getQQ().getCode_callback_uri() + //获取code码地址
                    "?client_id=" + aNew.getQqaid()//appid
                    + "&state=" + UUID.randomUUID() + //这个说是防攻击的，就给个随机uuid吧
                    "&redirect_uri=" + aNew.getQqhuidiao() +//这个很重要，这个是回调地址，即就收腾讯返回的code码
                    "&response_type=code");//授权模式，授权码模式
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //接收回调地址带过来的code码
    @RequestMapping("/qqcode")
    public ModelAndView authorizeQQ(String code, HttpSession session, Model model) throws UnknownHostException {
        LmSysNew aNew = GetQQAPI();
        ReturnJson json = new ReturnJson();
        HashMap<String, Object> params = new HashMap<>();
        System.out.println(code);
        params.put("code", code);
        params.put("grant_type", "authorization_code");
        params.put("redirect_uri", aNew.getQqhuidiao());
        params.put("client_id", aNew.getQqaid());
        params.put("client_secret", aNew.getQqkey());
        //获取access_token如：access_token=9724892714FDF1E3ED5A4C6D074AF9CB&expires_in=7776000&refresh_token=9E0DE422742ACCAB629A54B3BFEC61FF
        String result = HttpsUtils.doGet(oauth.getQQ().getAccess_token_callback_uri(), params);
        //对拿到的数据进行切割字符串
        String[] strings = result.split("&");
        //切割好后放进map
        Map<String, String> reulsts = new HashMap<>();
        for (String str : strings) {
            String[] split = str.split("=");
            if (split.length > 1) {
                reulsts.put(split[0], split[1]);
            }
        }
        //到这里access_token已经处理好了
        //下一步获取openid，只有拿到openid才能拿到用户信息
        String openidContent = HttpsUtils.doGet(oauth.getQQ().getOpenid_callback_uri() + "?access_token=" + reulsts.get("access_token"));
        //接下来对openid进行处理
        //截取需要的那部分json字符串
        String openid = openidContent.substring(openidContent.indexOf("{"), openidContent.indexOf("}") + 1);
        Gson gson = new Gson();
        //将返回的openid转换成DTO
        QQOpenidDTO qqOpenidDTO = gson.fromJson(openid, QQOpenidDTO.class);
        //接下来说说获取用户信息部分
        //登陆的时候去数据库查询用户数据对于openid是存在，如果存在的话，就不用拿openid获取用户信息了，而是直接从数据库拿用户数据直接认证用户，
        // 否则就拿openid去腾讯服务器获取用户信息，并存入数据库，再去认证用户
        //下面关于怎么获取用户信息，并登陆
        LmWebUser user = lmWebUserService.findByIsNotApenid(qqOpenidDTO.getOpenid());
        LmSysNew sysNew = (LmSysNew) lmSysNewService.findAll().get(0);
        model.addAttribute("sysNew", sysNew);
        if (user != null) {
            session.setAttribute("UserSession", user);
        } else {
            params.clear();
            params.put("access_token", reulsts.get("access_token"));//设置access_token
            params.put("openid", qqOpenidDTO.getOpenid());//设置openid
            params.put("oauth_consumer_key", qqOpenidDTO.getClient_id());//设置appid
            //获取用户信息
            String userInfo = HttpsUtils.doGet(oauth.getQQ().getUser_info_callback_uri(), params);
            QQDTO qqDTO = gson.fromJson(userInfo, QQDTO.class);
            //这里拿用户昵称，作为用户名，openid作为密码（正常情况下，在开发时候用openid作为用户名，再自己定义个密码就可以了）
            try {
				//创建一个用户数据，并入库
                LmWebUser NewUser = new LmWebUser();
                NewUser.setCname(qqDTO.getNickname());
                NewUser.setLogoip(qqDTO.getFigureurl_qq_2());
                NewUser.setCreattime(commonUtil.dateToStr(new Date()));
                NewUser.setLasttime(commonUtil.dateToStr(new Date()));
                NewUser.setStrat(1);
                NewUser.setMoney(0.0);
                NewUser.setSex(qqDTO.getGender().equals("男") ? 1 : 2);
                NewUser.setOpenid(qqOpenidDTO.getOpenid());
                lmWebUserService.insert(NewUser);
                LmUserQqlogin qqlogin = new LmUserQqlogin();
                qqlogin.setOpenid(qqOpenidDTO.getOpenid());
                qqlogin.setHead(qqDTO.getFigureurl_qq_1());
                qqlogin.setStrat(1);
                qqlogin.setCreattime(commonUtil.dateToStr(new Date()));
                LmWebUser Apenid = lmWebUserService.findByIsNotApenid(qqOpenidDTO.getOpenid());
                qqlogin.setUid(Apenid.getUid());
                lmUserQqloginService.insert(qqlogin);
                session.setAttribute("UserSession", Apenid);
            } catch (Exception e) {
                System.err.println(e.getMessage());//打印异常原因                   ==》  一般给用户看
                System.err.println(e.toString());//打印异常名称以及异常原因  ==》 很少使用
                e.printStackTrace();
                model.addAttribute("code", 400);
                System.out.println(json.toString());
                return new ModelAndView(SeaTempUrl() + "user_login", "model", model);
            }
        }
        GetCommon(model);
        model.addAttribute("code", 200);
        return new ModelAndView(SeaTempUrl() + "user_login", "model", model);
    }

}
