package ec.edu.espe.agrosmart.service;

import ec.edu.espe.agrosmart.domain.Producto;
import ec.edu.espe.agrosmart.exception.ProductoNoEncontradoException;
import ec.edu.espe.agrosmart.persistence.ProductoEntity;
import ec.edu.espe.agrosmart.persistence.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

class ProductoServiceTest {

    @Test
    void obtenerProductosComercializables_conTresValidosYDosInvalidos_debeEmitirSoloLosValidos() {
        // Arrange
        ProductoRepository repository = Mockito.mock(ProductoRepository.class);
        Mockito.when(repository.findAll()).thenReturn(datosDePrueba());
        ProductoService service = new ProductoService(repository);

        // Act
        Flux<Producto> flujo = service.obtenerProductosComercializables();

        // Assert
        StepVerifier.create(flujo)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void obtenerProductosComercializables_conTodosInvalidos_debeEmitirProductoGenerico() {
        // Arrange
        ProductoRepository repository = Mockito.mock(ProductoRepository.class);
        Mockito.when(repository.findAll()).thenReturn(datosInvalidos());
        ProductoService service = new ProductoService(repository);

        // Act
        Flux<Producto> flujo = service.obtenerProductosComercializables();

        // Assert
        StepVerifier.create(flujo)
                .expectNextMatches(producto -> producto.getId().equals(0L)
                        && producto.getNombre().equals("PRODUCTO GENERICO SIN DISPONIBILIDAD"))
                .verifyComplete();
    }

    @Test
    void buscarPorId_conIdInexistente_debeTerminarEnProductoNoEncontradoException() {
        // Arrange
        ProductoRepository repository = Mockito.mock(ProductoRepository.class);
        Mockito.when(repository.findById(9999L)).thenReturn(Optional.empty());
        ProductoService service = new ProductoService(repository);

        // Act
        Mono<Producto> resultado = service.buscarPorId(9999L);

        // Assert
        StepVerifier.create(resultado)
                .expectError(ProductoNoEncontradoException.class)
                .verify();
    }

    private List<ProductoEntity> datosDePrueba() {
        return List.of(
                new ProductoEntity("Rosas de exportación", new BigDecimal("24.50"), 120, "Flores", "ventas@rosas.ec,logistica@rosas.ec"),
                new ProductoEntity("Claveles premium", new BigDecimal("18.75"), 90, "Flores", "comercial@claveles.ec"),
                new ProductoEntity("Orquídeas seleccionadas", new BigDecimal("32.40"), 45, "Flores", "orquideas@agrosmart.ec"),
                new ProductoEntity("Girasoles sin precio", BigDecimal.ZERO, 60, "Flores", "alertas@girasoles.ec"),
                new ProductoEntity("Tulipanes sin notificación", new BigDecimal("21.10"), 70, "Flores", "")
        );
    }

    private List<ProductoEntity> datosInvalidos() {
        return List.of(
                new ProductoEntity("Producto sin precio", BigDecimal.ZERO, 10, "Flores", "correo@flores.ec"),
                new ProductoEntity("Producto sin correos", new BigDecimal("10.00"), 10, "Flores", "")
        );
    }
}