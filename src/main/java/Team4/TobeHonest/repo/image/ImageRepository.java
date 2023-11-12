package Team4.TobeHonest.repo.image;

import Team4.TobeHonest.domain.MessageImg;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Repository

public interface ImageRepository extends JpaRepository<MessageImg, Long> {
//    저장하고 URL 리텅



}
