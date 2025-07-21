package sit.tuvarna.bg.orderservice.drink.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import sit.tuvarna.bg.orderservice.tableSession.model.TableSession;

import java.util.UUID;

@Data
@NoArgsConstructor
@Table(name = "drinks")
@Entity
public class Drink {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private TableSession session;

    private String name;

    @Enumerated(EnumType.STRING)
    private DrinkStatus status;

    private String recipe;

    @Column(name = "special_instructions")
    private String specialInstructions;
}
