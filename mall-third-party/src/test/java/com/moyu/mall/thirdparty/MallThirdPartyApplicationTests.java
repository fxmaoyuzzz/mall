package com.moyu.mall.thirdparty;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MallThirdPartyApplicationTests {

    @Test
    public void contextLoads() {
    }


    @Autowired
    private OSSClient ossClient;

    @Test
    public void uploadTest() throws FileNotFoundException {
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        //String endpoint = "oss-cn-shanghai.aliyuncs.com";
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        //String accessKeyId = "LTAI5tBk4iZ1MLp8nqG7nFMG";
        //String accessKeySecret = "45KVhMWQfsV7PpG8qQj4Xma5LLKwcq";
        // 填写Bucket名称，例如examplebucket。
        String bucketName = "mall-zzz";
        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
        String objectName = "上传测试728272.jpg";
        // 填写本地文件的完整路径，例如D:\\localpath\\examplefile.txt。
        // 如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
        String filePath = "/Users/lizenghai/Desktop/d811277d94eee9f74006b4a91e046078.jpg";

        // 创建OSSClient实例。
        //OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            InputStream inputStream = new FileInputStream(filePath);
            // 创建PutObject请求。
            ossClient.putObject(bucketName, objectName, inputStream);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

}
