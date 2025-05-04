package vn.clickwork.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.clickwork.entity.Support;

public interface SupportRepository extends JpaRepository<Support, Long> {
    // Các phương thức truy vấn có thể thêm vào nếu cần
}
