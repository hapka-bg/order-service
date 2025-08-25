package sit.tuvarna.bg.orderservice.restaurantTable.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import sit.tuvarna.bg.orderservice.tableSession.model.TableSession;

import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "restaurant_tables")
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "table_number",unique = true)
    private Long tableNumber;

    @Column(name = "smoking")
    private Boolean smoking;

    @Column(name = "outdoor")
    private Boolean outdoor;

    @Column(name = "occupied")
    private Boolean occupied;

    private Integer capacity;

    @OneToOne(cascade =  CascadeType.ALL)
    @JoinColumn(name = "session_id")
    private TableSession session;

    private String notes;
}
