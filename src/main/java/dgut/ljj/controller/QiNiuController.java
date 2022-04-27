package dgut.ljj.controller;

import dgut.ljj.utils.QiNiuUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
public class QiNiuController {

    @Value("${qiniu.dir}")
    private String dir; //指定文件夹

    //文件上传
    @PostMapping("/fileUpload")
    public Map<String,Object> upload(@RequestParam("file") MultipartFile file) {

        Map<String,Object> result = new LinkedHashMap<>();

        //判断文件是否为空
        if (file.isEmpty()) {
            result.put("code",-1);
            result.put("msg","文件为空");
            return result;
        }

        //获取UUID+date文件名
        String fileName = QiNiuUtil.getUUIDFileNameNow(Objects.requireNonNull(file.getOriginalFilename()));
        //指定文件路径
        String file_path = dir + fileName;


        try {
            //获取文件上传七牛云后的url
            String fileUrl = QiNiuUtil.saveFile(file, file_path);
            if(fileUrl == null){
                result.put("code",-1);
                result.put("msg","上传服务器异常");
                return result;
            }else {
                result.put("code",200);
                result.put("msg","上传成功");
                Map<String,Object> data = new LinkedHashMap<>();
                data.put("url",fileUrl);
                result.put("data",data);
                return result;
            }
        } catch (IOException e) {
            log.error("七牛异常:" + e.getMessage());
            result.put("code",-1);
            result.put("msg",e.getMessage());
            return result;
        }
    }

    //文件删除
    @DeleteMapping("/fileDelete")
    public Map<String, Object> delete(@RequestParam("fileName") String fileName) {
        String file = fileName.substring(22);
        boolean flag = QiNiuUtil.deleteFile(file);
        Map<String,Object> result = new LinkedHashMap<>();
        if(flag){
            result.put("code",200);
            result.put("msg","删除成功");
            return result;
        }else {
            result.put("code",-1);
            result.put("msg","删除失败");
            return result;
        }
    }
}
