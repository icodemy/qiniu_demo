package dgut.ljj.utils;

import com.alibaba.fastjson.JSONObject;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import dgut.ljj.config.QiNiuConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
public class QiNiuUtil {

    @Autowired
    private QiNiuConfig config;

    private static QiNiuConfig qiNiuConfig;

    //初始化静态参数
    //通过@PostConstruct实现初始化bean之前进行的操作
    @PostConstruct
    public void init() {
        qiNiuConfig = config;
    }

    /*
     * 上传文件
     * @author liangjinju
     * @date 2022/4/27 15:56
     * @param file 上传的文件
     * @param fileName 文件名称
     * @return java.lang.String
     */
    public static String saveFile(MultipartFile file, String fileName) throws IOException {
        try {
            // 调用put方法上传
            Response res = qiNiuConfig.getUploadManager().put(file.getBytes(), fileName, qiNiuConfig.getUpToken());
            // 打印返回的信息
            if (res.isOK() && res.isJson()) {
                // 返回这张存储照片的地址
                return qiNiuConfig.getPath() + JSONObject.parseObject(res.bodyString()).get("key");

            } else {
                log.error("七牛异常:" + res.bodyString());
                return null;
            }
        } catch (QiniuException e) {
            // 请求失败时打印的异常的信息
            log.error("七牛异常:" + e.getMessage());
            return null;
        }
    }

    //删除图片文件
    public static boolean deleteFile(String fileName) {
        BucketManager bucketManager = qiNiuConfig.getBucketManager();
        try {
            //七牛云删除
            bucketManager.delete(qiNiuConfig.getBucketname(), fileName);
            return true;
        } catch (QiniuException ex) {
            log.error("七牛异常:" + ex.getMessage());
            return false;
        }
    }

    //获取UUID+Date的文件名
    public static String getUUIDFileNameNow(String fileName) {
        int dotPos = fileName.lastIndexOf(".");
        if (dotPos < 0) {
            return null;
        }//获取文件后缀
        String fileExt = fileName.substring(dotPos + 1).toLowerCase();
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
        String nowStr = "-" + format.format(date);
        return UUID.randomUUID().toString().replaceAll("-", "") + nowStr + "." + fileExt;
    }
}
