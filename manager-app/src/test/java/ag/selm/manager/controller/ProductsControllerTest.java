package ag.selm.manager.controller;

import ag.selm.manager.client.BadRequestException;
import ag.selm.manager.client.ProductsRestClient;
import ag.selm.manager.controller.payload.NewProductPayload;
import ag.selm.manager.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ConcurrentModel;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Модульные тесты ProductsController")
class ProductsControllerTest {

    @Mock
    ProductsRestClient productsRestClient;

    @InjectMocks
    ProductsController productsController;

    @Test
    @DisplayName("createProduct создаст новый товар и перенаправит на страницу товара")
    void createProduct_RequestIsValid_ReturnsRedirectionToProductPage() {
        // given
        var payload = new NewProductPayload("Новый товар", "Описание нового товара");
        var model = new ConcurrentModel();
        var response = new MockHttpServletResponse();
        doReturn(new Product(1, "Новый товар", "Описание нового товара"))
                .when(this.productsRestClient).createProduct(notNull(), any());
        //when
        var result = this.productsController.createProduct(payload, model, response);
        //then
        assertEquals("redirect:catalogue/products/1", result);

        verify(this.productsRestClient).createProduct("Новый товар", "Описание нового товара");
        verifyNoMoreInteractions(this.productsRestClient);

    }

    @Test
    @DisplayName("createProduct вернет страницу с ошибками, если запрос не валиден")
    void createProduct_RequestIsInvalid_ReturnsProductFormWithError() {
        // given
        var payload = new NewProductPayload("   ", null);
        var model = new ConcurrentModel();
        var response = new MockHttpServletResponse();
       doThrow(new BadRequestException(List.of("Ошибка 1", "Ошибка 2")))
                .when(this.productsRestClient).createProduct("   ", null);
        //when
        var result = this.productsController.createProduct(payload, model, response);

        //then
        assertEquals("/catalogue/products/new_product", result);
        assertEquals(payload, model.getAttribute("payload"));
        assertEquals(List.of("Ошибка 1", "Ошибка 2"), model.getAttribute("errors"));

        verify(this.productsRestClient).createProduct("   ", null);
        verifyNoMoreInteractions(this.productsRestClient);

    }


}