//業務邏輯層
package com.daniel.demo.service;

import com.daniel.demo.converter.ProductConverter;
import com.daniel.demo.entity.Product;
import com.daniel.demo.entity.ProductRequest;
import com.daniel.demo.entity.ProductResponse;
import com.daniel.demo.exception.ConflictException;
import com.daniel.demo.exception.NotFoundException;
import com.daniel.demo.parameter.ProductQueryParameter;
import com.daniel.demo.repository.ProductRepository;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProductService {


    private ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Product getProduct(String id){
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Can't find product."));
    }

    public ProductResponse getProductResponse(String id){
        Product product = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Can't find product."));
        return ProductConverter.toProductResponse(product);
    }

    public ProductResponse createProdcut(ProductRequest request){

        List<Product> products = repository.findByNameLikeIgnoreCase(request.getName());

        boolean hasProduct = false;

        for (Product productcheck : products){
            if (productcheck.getName().equals(request.getName())){
                hasProduct = true;
            }
        }

        if (hasProduct){
            throw new ConflictException("The id of the product is duplicated");
        }
        else{
            Product product = ProductConverter.toProduct(request);
            product = repository.insert(product);

            return ProductConverter.toProductResponse(product);
        }
    }

    public ProductResponse replaceProduct(String id, ProductRequest request){

        Product oldProduct = getProduct(id);

        Product newProduct = ProductConverter.toProduct(request);
        newProduct.setId(oldProduct.getId());
        repository.save(newProduct);

        return ProductConverter.toProductResponse(newProduct);

    }

    public ProductResponse deleteProduct(String id){
        Product product = getProduct(id);

        repository.deleteById(id);

        return ProductConverter.toProductResponse(product);
    }


    public List<ProductResponse> getProducts(ProductQueryParameter param) {

        String nameKeyword = Optional.ofNullable(param.getKeyword()).orElse("");
        int priceFrom = Optional.ofNullable(param.getPriceFrom()).orElse(0);
        int priceTo = Optional.ofNullable(param.getPriceTo()).orElse(Integer.MAX_VALUE);

        Sort sort = configureSort(param.getOrderBy(), param.getSortRule());

        List<Product> products = repository.findByPriceBetweenAndNameLikeIgnoreCase(priceFrom, priceTo, nameKeyword, sort);

        return products.stream().map(ProductConverter::toProductResponse).collect(Collectors.toList());
    }

    private Sort configureSort(String orderBy, String sortRule) {
        Sort sort = Sort.unsorted();
        if (Objects.nonNull(orderBy) && Objects.nonNull(sortRule)) {
            Sort.Direction direction = Sort.Direction.fromString(sortRule);
            sort = new Sort(direction, orderBy);
        }

        return sort;
    }
}
