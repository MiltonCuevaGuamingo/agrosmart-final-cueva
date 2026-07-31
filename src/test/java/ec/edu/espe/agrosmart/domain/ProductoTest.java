package ec.edu.espe.agrosmart.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductoTest {

    @Test
    void getters_conDatosDelConstructor_debenDevolverLosMismosValores() {
        // Arrange
        List<String> correos = List.of("ventas@rosas.ec");

        // Act
        Producto producto = new Producto(1L, "Rosas", "Flores", new BigDecimal("24.50"), correos);

        // Assert
        assertEquals(1L, producto.getId());
        assertEquals("Rosas", producto.getNombre());
        assertEquals("Flores", producto.getCategoria());
        assertEquals(new BigDecimal("24.50"), producto.getPrecioUsd());
        assertEquals(correos, producto.getCorreosNotificacion());
    }

    @Test
    void getCorreosNotificacion_alMutarLaListaOriginal_noDebeAfectarAlProducto() {
        // Arrange
        List<String> correos = new ArrayList<>();
        correos.add("ventas@rosas.ec");
        Producto producto = new Producto(1L, "Rosas", "Flores", new BigDecimal("24.50"), correos);

        // Act
        correos.add("intruso@mail.com");

        // Assert
        assertEquals(1, producto.getCorreosNotificacion().size());
        assertNotSame(correos, producto.getCorreosNotificacion());
    }

    @Test
    void getCorreosNotificacion_alIntentarModificarLaLista_devuelveListaInmodificable() {
        // Arrange
        List<String> correos = new ArrayList<>();
        correos.add("ventas@rosas.ec");
        Producto producto = new Producto(1L, "Rosas", "Flores", new BigDecimal("24.50"), correos);

        // Act
        List<String> correosDelProducto = producto.getCorreosNotificacion();

        // Assert
        assertNotSame(correos, correosDelProducto);
        assertThrows(UnsupportedOperationException.class,
                () -> correosDelProducto.add("otro@mail.com"));
    }
}