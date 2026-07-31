package ec.edu.espe.agrosmart.mapper;

import ec.edu.espe.agrosmart.domain.Producto;
import ec.edu.espe.agrosmart.persistence.ProductoEntity;

import java.util.Arrays;
import java.util.List;

public final class ProductoMapper {

    private ProductoMapper() {
    }

    public static Producto toDominio(ProductoEntity entity) {
        return new Producto(
                entity.getIdProducto(),
                entity.getNombreProducto(),
                entity.getCategoria(),
                entity.getPrecioUsd(),
                parseCorreos(entity.getCorreosNotificacion())
        );
    }

    private static List<String> parseCorreos(String correos) {
        if (correos == null || correos.isBlank()) {
            return List.of();
        }

        return Arrays.stream(correos.split(","))
                .map(String::trim)
                .filter(correo -> !correo.isBlank())
                .toList();
    }
}