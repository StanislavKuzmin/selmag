package ag.selm.feedback.controller;

import ag.selm.feedback.controller.payload.NewFavouriteProductPayload;
import ag.selm.feedback.entity.FavouriteProduct;
import ag.selm.feedback.service.FavouriteProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/feedback-api/favourite-products")
@RequiredArgsConstructor
public class FavouriteProductsRestController {

    private final FavouriteProductService favouriteProductService;

    @GetMapping
    public Flux<FavouriteProduct> findFavouriteProducts() {
        return favouriteProductService.findFavouriteProducts();
    }

    @GetMapping("/by-product-id/{productId:\\d+}")
    public Mono<FavouriteProduct> findFavouriteProductByProductId(@PathVariable("productId") int productId) {
        return this.favouriteProductService.findFavouriteProductByProduct(productId);
    }

    @PostMapping
    public Mono<ResponseEntity<FavouriteProduct>> addProductToFavourites(
            @Valid @RequestBody Mono<NewFavouriteProductPayload> payloadMono,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        return payloadMono
                .flatMap(payload -> this.favouriteProductService.addProductToFavourites(payload.productId()))
                .map(favouriteProduct -> ResponseEntity.created(uriComponentsBuilder.replacePath("/feedback-api/favourite-products/{id}")
                                .build(favouriteProduct.getId()))
                        .body(favouriteProduct));
    }

    @DeleteMapping("/by-product-id/{productId:\\d+}")
    public Mono<ResponseEntity<Void>> removeProductFromFavourites(@PathVariable("productId") int productId) {
        return this.favouriteProductService.removeProductFromFavourites(productId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
