package ec.edu.espe.agrosmart.config;

import ec.edu.espe.agrosmart.persistence.ProductoEntity;
import ec.edu.espe.agrosmart.persistence.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ProductoRepository productoRepository;

    public DataSeeder(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public void run(String... args) {
        if (productoRepository.count() == 0) {
            productoRepository.saveAll(List.of(
                    new ProductoEntity(
                            "Rosas de exportación",
                            new BigDecimal("24.50"),
                            120,
                            "Flores",
                            "ventas@rosas.ec,logistica@rosas.ec"
                    ),
                    new ProductoEntity(
                            "Claveles premium",
                            new BigDecimal("18.75"),
                            90,
                            "Flores",
                            "comercial@claveles.ec"
                    ),
                    new ProductoEntity(
                            "Orquídeas seleccionadas",
                            new BigDecimal("32.40"),
                            45,
                            "Flores",
                            "orquideas@agrosmart.ec"
                    ),
                    new ProductoEntity(
                            "Girasoles sin precio",
                            BigDecimal.ZERO,
                            60,
                            "Flores",
                            "alertas@girasoles.ec"
                    ),
                    new ProductoEntity(
                            "Tulipanes sin notificación",
                            new BigDecimal("21.10"),
                            70,
                            "Flores",
                            ""
                    )
            ));
        }
    }
}