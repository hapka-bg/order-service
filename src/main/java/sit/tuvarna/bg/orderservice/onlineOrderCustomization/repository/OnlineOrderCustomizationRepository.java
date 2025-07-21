package sit.tuvarna.bg.orderservice.onlineOrderCustomization.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sit.tuvarna.bg.orderservice.onlineOrderCustomization.model.OnlineOrderCustomization;

import java.util.UUID;

@Repository
public interface OnlineOrderCustomizationRepository extends JpaRepository<OnlineOrderCustomization, UUID> {
}
