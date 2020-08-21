package com.daniel.demo.controller;

import com.daniel.demo.entity.Product;
import com.daniel.demo.entity.ProductRequest;
import com.daniel.demo.entity.ProductResponse;
import com.daniel.demo.parameter.ProductQueryParameter;
import com.daniel.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping(value = "/products", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductController {


    @Autowired
    private ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable("id") String id) {

        ProductResponse product = productService.getProductResponse(id);
        return ResponseEntity.ok(product);
    }
//    用parameter去get的方法
//    @GetMapping
//    public ResponseEntity<ProductResponse> getProduct(
//        @RequestParam(value = "id", defaultValue = "") String id) {
//        ProductResponse product = productService.getProductResponse(id);
//        return ResponseEntity.ok(product);
//    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProducts(@ModelAttribute ProductQueryParameter param) {
        List<ProductResponse> products= productService.getProducts(param);
        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {

        ProductResponse product = productService.createProdcut(request);


//        建立一個回傳成功的值，沒有回傳不會怎樣
//
//        這裡透過「ServletUriComponentsBuilder」來建立URI。
//        fromCurrentRequest：以目前呼叫的資源路徑為基礎來建立URI，此處為「http://…/products」。
//        path：以目前的資源路徑再做延伸，定義新的路徑格式，此處為「http://…/products/{id}」。
//        buildAndExpand：將參數填入路徑，產生真實的資源路徑，此處為「http://…/products/實際產品編號」。

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(product.getId())
                .toUri();

        return ResponseEntity.created(location).body(product);
    }

//    //用parameter去post的方法
//    @PostMapping
//    public ResponseEntity<ProductResponse> createProduct(
//            @RequestParam(value = "name") String name, @RequestParam(value = "price") int price) {
//        ProductRequest request = new ProductRequest();
//        request.setName(name);
//        request.setPrice(price);
//        ProductResponse product = productService.createProdcut(request);
//
//        URI location = ServletUriComponentsBuilder
//                .fromCurrentRequest()
//                .path("/{id}")
//                .buildAndExpand(product.getId())
//                .toUri();
//
//        return ResponseEntity.created(location).body(product);
//    }

    @PutMapping("{id}")
    public ResponseEntity<ProductResponse> replaceProduct(
            @PathVariable("id") String id, @Valid @RequestBody ProductRequest request) {
        ProductResponse product = productService.replaceProduct(id, request);

        return ResponseEntity.ok().body(product);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ProductResponse> deleteProduct(@PathVariable("id") String id){

        ProductResponse product = productService.deleteProduct(id);
//        String response = "product name : " + product.getName() +
//                "\nproduct price : " + product.getPrice() + "\nis deleted.";
        return ResponseEntity.ok(product);
    }
}
