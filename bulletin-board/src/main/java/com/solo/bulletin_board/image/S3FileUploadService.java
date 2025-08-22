package com.solo.bulletin_board.image;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.solo.bulletin_board.exception.BusinessLogicException;
import com.solo.bulletin_board.exception.ExceptionCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3FileUploadService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Autowired
    private AmazonS3 amazonS3;


    public String uploadImageFile(MultipartFile multipartFile, String fileForm){

        String fileName = UUID.randomUUID() + "." + fileForm;

        try{
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(multipartFile.getContentType());
            metadata.setContentLength(multipartFile.getSize());
            metadata.setContentDisposition("inline");

            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, multipartFile.getInputStream(), metadata));

        }
        catch(IOException e){
            throw new BusinessLogicException(ExceptionCode.FILE_INPUT_STREAM_ERROR);
        }

        String imageUrl = amazonS3.getUrl(bucketName, fileName).toString();

        return imageUrl;
    }

    public void deleteImageFile(String imageUrl){

        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

        amazonS3.deleteObject(bucketName, fileName);

    }

}
