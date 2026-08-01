package ec.edu.espe.agrosmart.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductoFiltersTest {

    @Test
    void isValid_conPrecioMayorACeroYCorreos_debeRetornarTrue() {
        // Arrange
        Producto producto = new Producto(
                1L,
                "Rosas",
                "Flores",
                new BigDecimal("24.50"),
                List.of("ventas@rosas.ec")
        );

        // Act
        boolean resultado = ProductoFilters.IS_VALID.test(producto);

        // Assert
        assertTrue(resultado);
    }

    @Test
    void isValid_conPrecioCero_debeRetornarFalse() {
        // Arrange
        Producto producto = new Producto(
                2L,
                "Girasoles sin precio",
                "Flores",
                BigDecimal.ZERO,
                List.of("alertas@girasoles.ec")
        );

        // Act
        boolean resultado = ProductoFilters.IS_VALID.test(producto);

        // Assert
        assertFalse(resultado);
    }

    @Test
    void isValid_conCorreosVacios_debeRetornarFalse() {
        // Arrange
        Producto producto = new Producto(
                3L,
                "Tulipanes sin notificación",
                "Flores",
                new BigDecimal("21.10"),
                List.of()
        );

        // Act
        boolean resultado = ProductoFilters.IS_VALID.test(producto);

        // Assert
        assertFalse(resultado);
    }
}