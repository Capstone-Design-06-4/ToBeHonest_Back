package Team4.TobeHonest.repo.image;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Repository
@RequiredArgsConstructor
public class ImageRepostoryLocal implements ImageRepository{

    private final EntityManager em;
    private final JPAQueryFactory jqf;
    private static final String PATH = "C:\\Users\\alswn\\TobeHonest\\src\\main\\resources";
    private static Long count = 0l;
    @Override
    public String saveImg(MultipartFile file, Long messageId) {
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String savedFileName = messageId + "/" + count + fileExtension;

        Path path = Paths.get(PATH, savedFileName);

        // Ensure the directory exists.
        if (!Files.exists(path.getParent())) {
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to create directory");
            }
        }

        // Save the file.
        try {
            file.transferTo(new File(path.toString()));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save image");
        }

        // Increment the count.
        count++;

        return path.toString();
    }
}
