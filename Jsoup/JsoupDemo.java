package cn.laymm.Utils;

import cn.laymm.BuiltIn.lmDhActicle.pojo.LmDhActicle;
import cn.laymm.BuiltIn.lmDhActicle.service.ILmDhActicleService;
import cn.laymm.BuiltIn.lmDhGuize.pojo.LmDhGuize;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static sun.net.www.protocol.http.HttpURLConnection.userAgent;

/**
 * Created by Administrator on 2019/4/22 0022.
 */
@RestController
@RequestMapping("/test/demo")
public class JsoupDemo {
    //导航文章
    @Resource
    ILmDhActicleService lmDhActicleService;
    @Resource
    CommonUtil commonUtil;

    private static List<LmDhActicle> GetCaiJiAct(LmDhGuize guize) {
        List<LmDhActicle> acticles = new ArrayList<>();
        try {
            //获取连接
            Connection con = Jsoup.connect(guize.getGsite());
            //模拟浏览器访问
            con.header("User-Agent", userAgent);
            //获取网页源代码
            Document doc = con.timeout(guize.getTimeout()).get();
            //获取需要循环的列表
            Elements li = doc.body().select(guize.getLi());
            for (Element e : li) {
                //获取标题
                String title = e.select(guize.getTitle()).text();
                //获取链接
                String href = e.select(guize.getHref()).attr("href");
                //根据链接获取内容
                Connection con2 = Jsoup.connect(href);
                con2.header("User-Agent", userAgent);
                Document condoc = con2.timeout(1000).get();
                Elements content = condoc.select(guize.getContent());
                LmDhActicle acticle = new LmDhActicle();
                acticle.setAname(title);
                acticle.setUrl(href);
                acticle.setContent(content.html());
                acticles.add(acticle);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return acticles;
    }

    @RequestMapping("/test/demo/caiji")
    private void main() {
        LmDhGuize guize = new LmDhGuize();
        //采集地址
        guize.setGsite("https://me.csdn.net/qq_19898283");
        //响应时间
        guize.setTimeout(1000);
        //定位list列表
        guize.setLi(".my_tab_page_con > .tab_page_list");
        //定位标题
        guize.setTitle("dt > h3 > a");
        //定位链接
        guize.setHref("dt > h3 > a");
        //定位内容
        guize.setContent("#content_views");
        List<LmDhActicle> acticles = GetCaiJiAct(guize);
        for (int i = 0; i < acticles.size(); i++) {
            System.out.println(acticles.get(i).toString());
            acticles.get(i).setSort(0);
            acticles.get(i).setStrat(1);
            acticles.get(i).setCreattime(commonUtil.dateToStr(new Date()));
            lmDhActicleService.insert(acticles.get(i));
        }
    }

}
