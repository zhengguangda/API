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
	
    //单文件上传
    @RequestMapping(value = "/images", method = RequestMethod.POST)
    public Map<String, Object> uploadImages(@RequestParam("file") MultipartFile file) throws IOException {
        //获取跟目录
        File path = new File(ResourceUtils.getURL("classpath:").getPath());
        if (!path.exists()) {
            path = new File("");
        }
        //如果上传目录为/static/images/upload/,则可以如下获取
        File fileDir = new File(path.getAbsolutePath(), PathCommon.uploadWebImages());
        if (!fileDir.exists()) {
            fileDir.mkdirs();
            System.out.println(fileDir.getAbsolutePath());
            //在开发测试模式时，得到地址为：{项目跟目录}/target/static/images/upload/
            //在打成jar正式发布时，得到的地址为:{发布jar包目录}/static/images/upload/
        }
	//获取文件名称
        String fileName = UUID.randomUUID().toString() + file.getOriginalFilename();
	//获取文件类型
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
	//储存文件
        file.transferTo(new File(fileDir.getAbsolutePath(), fileName));
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("code", 200);
        result.put("filename",fileName);
        result.put("size", String.valueOf(file.getSize()));
        return result;
    }

    //多文件上传
    @RequestMapping(value = "/fileAll",method = RequestMethod.POST)
    public List<Map<String, Object>> FileAll (@RequestParam("file") MultipartFile[] file) throws IOException {
        //获取跟目录
        File path = new File(ResourceUtils.getURL("classpath:").getPath());
        if (!path.exists()) {
            path = new File("");
        }
        //如果上传目录为/static/images/upload/,则可以如下获取
        File fileDir = new File(path.getAbsolutePath(), PathCommon.uploadWebImages());
        if (!fileDir.exists()) {
            fileDir.mkdirs();
            System.out.println(fileDir.getAbsolutePath());
            //在开发测试模式时，得到地址为：{项目跟目录}/target/static/images/upload/
            //在打成jar正式发布时，得到的地址为:{发布jar包目录}/static/images/upload/
        }
        List<Map<String,Object>> mapList = new ArrayList<>();
        for (int i = 0;i<file.length;i++){
            //获取文件名称，并修改成新文件名
            String FileName = UUID.randomUUID().toString() + file[i].getOriginalFilename();
            //将当前文件存放到目录里
            file[i].transferTo(new File(fileDir.getAbsolutePath(),FileName));
            //创建一个map对象
            Map<String,Object> map= new HashMap<>();
            map.put("code",200);
            map.put("filename",FileName);
            map.put("size",file[i].getSize());
            mapList.add(map);
        }
        return mapList;
    }
}
