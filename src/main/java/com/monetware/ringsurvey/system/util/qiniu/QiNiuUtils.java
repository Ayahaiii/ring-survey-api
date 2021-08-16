package com.monetware.ringsurvey.system.util.qiniu;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * @author Simo
 * @date 2019-12-25
 */
@Slf4j
@Component
public class QiNiuUtils {


    public String accessKey = "xwjbgWDUFwWetuAuyyphift_UWO3oSGapH0Z7n1R";
    public String secretKey = "2gsRz-5BhwrVcSf0SArRql_mQo-abPumJyEH4jJi";
    public static String bucket = "ringdata";

    /**
     * @Author: Cookie
     * @Date: 2019-05-27 15:38
     * @Description: 上传文件
     */
    public String uploadFile(MultipartFile file, Integer type) {
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Zone.zone0());

        //其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);

        //文件名：文件名+年月日时分秒
        String firstName = "";
        if (type == 1) {
            firstName = "survey/img/";
        } else {
            firstName = "survey/attach/";
        }
        String key = firstName + System.currentTimeMillis() + file.getOriginalFilename();
        try {
            InputStream inputStream = file.getInputStream();
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(this.bucket);

            try {
                Response response = uploadManager.put(inputStream, key, upToken, null, file.getContentType());
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                return putRet.key;
            } catch (QiniuException ex) {
                Response r = ex.response;
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    log.error(ex.getMessage());
                }
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return null;
    }

    /**
     * @Author: Cookie
     * @Date: 2019-05-27 18:05
     * @Description: 删除文件
     */
    public void deleteFile(String fileName, Integer type) {
        Configuration cfg = new Configuration(Zone.zone0());
        //其他参数参考类注释
        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            if (type == 1) {
                bucketManager.delete(bucket, "survey/img/" + fileName);
            } else {
                bucketManager.delete(bucket, "survey/attach/" + fileName);
            }
        } catch (QiniuException ex) {
            //如果遇到异常，说明删除失败
            System.err.println(ex.code());
            System.err.println(ex.response.toString());
        }
    }

}
