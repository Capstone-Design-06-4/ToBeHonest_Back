package Team4.TobeHonest.repo.image;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Repository

public interface ImageRepository {
//    저장하고 URL 리텅
    public String saveImg(MultipartFile file, Long messageId);

}
