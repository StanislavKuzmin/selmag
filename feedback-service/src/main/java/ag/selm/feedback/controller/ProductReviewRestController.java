package ag.selm.feedback.controller;

import ag.selm.feedback.controller.payload.NewProductReviewPayload;
import ag.selm.feedback.entity.ProductReview;
import ag.selm.feedback.service.ProductReviewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/feedback-api/product-reviews")
@RequiredArgsConstructor
@Slf4j
public class ProductReviewRestController {

    private final ProductReviewsService productReviewsService;

    @GetMapping("/by-product-id/{productId:\\d+}")
    @Operation(
            security = @SecurityRequirement(name = "keycloak")
    )
    public Flux<ProductReview> findProductReviewsByProductId(@PathVariable(name = "productId") int productId) {
        return this.productReviewsService.findProductReviewsByProduct(productId);
    }

    @PostMapping
    public Mono<ResponseEntity<ProductReview>> createProductReview(@Valid @RequestBody Mono<NewProductReviewPayload> payloadMono,
                                                                   UriComponentsBuilder uriComponentsBuilder,
                                                                   Mono<JwtAuthenticationToken> authenticationTokenMono) {
        return authenticationTokenMono.flatMap(token -> payloadMono
                .flatMap(payload -> this.productReviewsService.createProductReview(payload.productId(),
                        payload.rating(),
                        payload.review(),
                        token.getToken().getSubject()))
                .map(productReview -> ResponseEntity.created(uriComponentsBuilder.replacePath("/feedback-api/product-reviews/{id}")
                                .build(Map.of("id", productReview.getId())))
                        .body(productReview)));
    }
}
