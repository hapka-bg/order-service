package sit.tuvarna.bg.orderservice.productCustomization.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sit.tuvarna.bg.orderservice.productCustomization.module.ProductCustomization;

import java.util.UUID;

@Repository
public interface ProductCustomizationRepository extends JpaRepository<ProductCustomization, UUID> {
}
