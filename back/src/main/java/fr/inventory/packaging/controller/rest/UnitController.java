package fr.inventory.packaging.controller.rest;

import fr.inventory.packaging.entity.Unit;
import fr.inventory.packaging.entity.dto.response.ApiResponse;
import fr.inventory.packaging.repository.UnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/rest/unit")
@CrossOrigin("${front.url}")
public class UnitController {

    /////////////////
    /// Attributs ///
    /////////////////

    private UnitRepository unitRepository;

    /////////////////
    /// Endpoints ///
    /////////////////

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<?>> findAllUnits() {
        List<Unit> units = unitRepository.findAll();
        if(!units.isEmpty()){
            return ResponseEntity.ok(new ApiResponse<>("Liste des unités", units));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>("Aucune unité trouvée"));
        }
    }

    ///////////////
    /// Setters ///
    ///////////////

    @Autowired
    public void setUnitRepository(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }
}
