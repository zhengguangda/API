package cn.laymm.BuiltIn.lmUplod.controller;

import cn.laymm.BuiltIn.lmUplod.pojo.LmUpload;
import cn.laymm.Common.PathCommon;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(value = "/upload")
public class LmUplodController {
	
    //上传图片
    @RequestMapping(value = "/images", method = RequestMethod.POST)
    public LmUpload uploadImages(@RequestParam("file") MultipartFile file) throws IOException {
        //名称
        String fileName = UUID.randomUUID().toString() + file.getOriginalFilename();
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
        //获取跟目录
        File path = new File(ResourceUtils.getURL("classpath:").getPath());
        if (!path.exists()) {
            path = new File("");
        }
        //如果上传目录为/static/images/upload/,则可以如下获取
        File fileDir = new File(path.getAbsolutePath(), "/static/images/upload/");
        if (!fileDir.exists()) {
            fileDir.mkdirs();
            System.out.println(fileDir.getAbsolutePath());
            //在开发测试模式时，得到地址为：{项目跟目录}/target/static/images/upload/
            //在打成jar正式发布时，得到的地址为:{发布jar包目录}/static/images/upload/
        }
        file.transferTo(new File(fileDir.getAbsolutePath(), fileName));
        Map<String, String> result = new HashMap<String, String>();
        result.put("name", file.getOriginalFilename());
        result.put("size", String.valueOf(file.getSize()));
        LmUpload json = new LmUpload();
        json.setCode(200);
        json.setFilename(fileName);
        json.setName("null");
        return json;
    }

}
