package cn.laymm.Utils;

import cn.laymm.BuiltIn.lmAdminModule.pojo.LmAdminModule;
import cn.laymm.BuiltIn.lmAdminModule.service.ILmAdminModuleService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by laymm.cn on 2019/5/14 0014.
 */
@RestController
@RequestMapping("/poi")
public class PoiTest {

    @Resource
    ExportExcelUtil exportExcelUtil;
    @RequestMapping("/test")
    public String index() throws Exception {
        String title = "模块表";
        String[] name = {"序号","名称","排序","状态"};
        //获取数据
        Connection connection = exportExcelUtil.getConnection();
        Statement statement = connection.createStatement();
        String sql = "SELECT MID,mname,START from `lm_admin_module`";
        ResultSet rs = statement.executeQuery(sql);
        exportExcelUtil.exportExcel(title,name,rs);
        return "执行结束";
    }
}
