package com.atguigu.file;

import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import io.minio.errors.MinioException;

import java.io.FileInputStream;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/7/10 22:43 周一
 * description:
 */
public class FileUploader {
    public static void main(String[] args) throws Exception {
        try {
            //使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
            MinioClient minioClient = new MinioClient("http://192.168.2.129:9000", "enjoy6288", "enjoy6288");
            //检查存储桶是否已经存在---是否创建了数据库
            boolean isExist = minioClient.bucketExists("java0212");
            if(isExist) {
                System.out.println("Bucket already exists.");
            } else {
                //创建一个名为asiatrip的存储桶，用于存储照片的zip文件。
                minioClient.makeBucket("java0212");
            }
            //1.文件放到哪个桶里面  2.文件上传成功之后名称叫什么 3.文件流 4.文件参数设置
            FileInputStream inputStream = new FileInputStream("C:\\temp\\苹果手机\\白色1.jpg");
            PutObjectOptions putObjectOptions = new PutObjectOptions(inputStream.available(), -1);
            putObjectOptions.setContentType("image/jpeg");
            minioClient.putObject("java0212","new.jpg", inputStream,putObjectOptions);
            System.out.println("上传成功");
        } catch(MinioException e) {
            System.out.println("Error occurred: " + e);
        }
    }
}
