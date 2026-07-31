package ec.edu.espe.agrosmart.service;

import ec.edu.espe.agrosmart.domain.Producto;
import ec.edu.espe.agrosmart.domain.ProductoFilters;
import ec.edu.espe.agrosmart.exception.ProductoNoEncontradoException;
import ec.edu.espe.agrosmart.mapper.ProductoMapper;
import ec.edu.espe.agrosmart.persistence.ProductoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductoService {

    private static final Producto PRODUCTO_GENERICO = new Producto(
            0L,
            "PRODUCTO GENERICO SIN DISPONIBILIDAD",
            "Flores",
            BigDecimal.ONE,
            List.of("contacto@agrosmart.ec")
    );

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public Flux<Producto> obtenerProductosComercializables() {
        // Mono.fromCallable difiere la consulta JPA hasta que alguien se suscribe al flujo.
        return Mono.fromCallable(productoRepository::findAll)
                // subscribeOn con boundedElastic mueve la llamada bloqueante de Hibernate fuera del event loop de Netty.
                .subscribeOn(Schedulers.boundedElastic())
                // flatMapMany transforma la lista bloqueante ya materializada en un Flux elemento por elemento.
                .flatMapMany(Flux::fromIterable)
                // map convierte la entidad mutable de Hibernate en mi modelo de dominio inmutable.
                .map(ProductoMapper::toDominio)
                // map aplica una transformación funcional creando nuevos productos con el nombre en mayúsculas.
                .map(ProductoFilters.A_MAYUSCULAS)
                // filter descarta productos no comercializables: precio cero o correos vacíos.
                .filter(ProductoFilters.IS_VALID)
                // doOnNext deja una traza por consola sin modificar los productos del flujo.
                .doOnNext(ProductoFilters.LOG_PRODUCTO)
                // defaultIfEmpty evita devolver un Flux vacío si todos los productos fueron filtrados.
                .defaultIfEmpty(PRODUCTO_GENERICO);
    }

    public Mono<Producto> buscarPorId(Long id) {
        // Mono.fromCallable difiere la llamada bloqueante a findById hasta la suscripción.
        return Mono.fromCallable(() -> productoRepository.findById(id))
                // boundedElastic evita que la consulta JPA bloquee el event loop de WebFlux.
                .subscribeOn(Schedulers.boundedElastic())
                // flatMap con justOrEmpty convierte Optional.empty en un Mono vacío sin usar null ni block().
                .flatMap(Mono::justOrEmpty)
                // map transforma la entidad encontrada al modelo de dominio inmutable.
                .map(ProductoMapper::toDominio)
                // switchIfEmpty convierte el caso "no encontrado" en error dentro del flujo reactivo.
                .switchIfEmpty(Mono.error(new ProductoNoEncontradoException(id)));
    }
}