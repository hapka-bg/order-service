package sit.tuvarna.bg.orderservice.restaurantTable.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import sit.tuvarna.bg.orderservice.tableSession.model.TableSession;

import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "restaurant_table")
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "table_number")
    private Long tableNumber;

    @Column(name = "is_smoking")
    private Boolean isSmoking;

    @Column(name = "is_outdoor")
    private Boolean isOutdoor;

    @Column(name = "is_occupied")
    private Boolean isOccupied;

    private Long capacity;

    @OneToOne
    @JoinColumn(name = "session_id")
    private TableSession session;

    private String notes;


}
