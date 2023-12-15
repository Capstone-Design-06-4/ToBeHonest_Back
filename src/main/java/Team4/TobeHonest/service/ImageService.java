package Team4.TobeHonest.service;


import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.domain.Message;
import Team4.TobeHonest.domain.MessageImg;
import Team4.TobeHonest.repo.image.ImageRepository;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional(readOnly = true)
public class ImageService {

    private final AmazonS3Client amazonS3Client;
    private final ImageRepository imageRepository;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;


    @Transactional
    public String changeProfileImg(MultipartFile file, Member member) throws IOException {
        String fileName = "profile/" + member.getId();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        amazonS3Client.putObject(bucket, fileName, file.getInputStream(), metadata);

        String encodedReturnURL = URLEncoder.encode("https://" + bucket + ".s3.amazonaws.com/" + fileName, StandardCharsets.UTF_8.toString());
        member.changeProfileImg(encodedReturnURL);
        return encodedReturnURL;

    }

    @Transactional
    public void saveMessageImg(List<MultipartFile> files, Message message) throws IOException {

        files.forEach(file -> {
            try {
                saveImgLogic(file, message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });




    }

    private void saveImgLogic(MultipartFile file,Message message) throws IOException {
        String fileName = "msg/" + message.getId() + "/" + UUID.randomUUID();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        amazonS3Client.putObject(bucket, fileName, file.getInputStream(), metadata);
        String encodedReturnURL = "https://" + bucket + ".s3.amazonaws.com/" + fileName;
        message.addImage(encodedReturnURL);
        MessageImg messageImg = MessageImg.builder().imgURL(encodedReturnURL).message(message).build();
        //imageRepository.save(messageImg);
    }

}
