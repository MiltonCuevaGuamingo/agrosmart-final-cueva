package ec.edu.espe.agrosmart.service;

import ec.edu.espe.agrosmart.ai.AgroSmartAIService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;

class PublicidadServiceTest {

    @Test
    void generarPublicidad_cuandoProveedorResponde_debeEmitirTextoGenerado() {
        // Arrange
        AgroSmartAIService aiService = Mockito.mock(AgroSmartAIService.class);
        Mockito.when(aiService.generarPublicidad("Rosas", "floristerías premium"))
                .thenReturn("Rosas premium para vitrinas que quieren destacar.");
        PublicidadService service = new PublicidadService(aiService);

        // Act
        Mono<String> resultado = service.generarPublicidad("Rosas", "floristerías premium");

        // Assert
        StepVerifier.create(resultado)
                .expectNext("Rosas premium para vitrinas que quieren destacar.")
                .verifyComplete();
    }

    @Test
    void generarPublicidad_cuandoProveedorFalla_debeEmitirMensajeDeRespaldo() {
        // Arrange
        AgroSmartAIService aiService = Mockito.mock(AgroSmartAIService.class);
        Mockito.when(aiService.generarPublicidad(any(), any()))
                .thenThrow(new RuntimeException("429 Too Many Requests"));
        PublicidadService service = new PublicidadService(aiService);

        // Act
        Mono<String> resultado = service.generarPublicidad("Rosas", "floristerías premium");

        // Assert
        StepVerifier.create(resultado)
                .expectNextMatches(texto -> texto.contains("Publicidad no disponible")
                        && texto.contains("RuntimeException"))
                .verifyComplete();
    }
}