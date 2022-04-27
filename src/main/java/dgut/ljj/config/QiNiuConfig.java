package dgut.ljj.config;

import com.qiniu.common.Zone;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Data
@org.springframework.context.annotation.Configuration
//从配置文件里获取
@ConfigurationProperties(prefix = "qiniu")
public class QiNiuConfig {

    // 设置好账号的ACCESS_KEY
    private String ACCESS_KEY;

    // 设置好账号的SECRET_KEY
    private String SECRET_KEY;

    // 设置七牛要上传的空间
    private String bucketname;

    // 设置关联七牛的域名
    private String path;

    // 密钥配置
    public Auth getAuth() {
        // 构造一个带指定Zone对象的配置类,不同的七云牛存储区域调用不同的zone
        return Auth.create(ACCESS_KEY, SECRET_KEY);
    }

    /* 构造一个带指定Zone对象的配置类,不同的七云牛存储区域调用不同的zone
        华东：zone0
        华北：zone1
        华南：zone2
        北美：zoneNa0
     */
    public Configuration getConfiguration() {
        return new Configuration(Zone.zone2());
    }

    // 构造一个七牛manager
    public UploadManager getUploadManager() {
        return new UploadManager(getConfiguration());
    }

    // 构造一个七牛manager
    public BucketManager getBucketManager() {
        return new BucketManager(getAuth(), getConfiguration());
    }

    // 简单上传，使用默认策略
    // 只需要设置上传的空间名就可以了
    public String getUpToken() {
        return getAuth().uploadToken(getBucketname());
    }
}
