package sit.tuvarna.bg.orderservice.tableSession.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import sit.tuvarna.bg.orderservice.restaurantTable.model.RestaurantTable;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "table_session")
public class TableSession {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "table_id")
    private RestaurantTable table;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private TableStatus status;
}
