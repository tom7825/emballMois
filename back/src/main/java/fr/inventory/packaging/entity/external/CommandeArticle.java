package fr.inventory.packaging.entity.external;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "GC_COMMANDEFOUR")
public class CommandeArticle {

    @Id
    @Column(name = "NUM_PIECE")
    private Double numPiece;

}
