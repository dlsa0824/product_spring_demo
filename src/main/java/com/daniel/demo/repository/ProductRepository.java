package com.daniel.demo.repository;

import com.daniel.demo.entity.Product;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {


    @Query("{'$and': [{'price': {'$gte': ?0, '$lte': ?1}}, {'name': {'$regex': ?2, '$options': 'i'}}]}")
    List<Product> findByPriceBetweenAndNameLikeIgnoreCase(int priceFrom, int priceTo, String name, Sort sort);

    // 查詢price欄位在特定範圍的文件（參數亦可使用Date）
    // 語法中的「?0」、「?1」等符號，代表函式庫會分別填入方法的第一、二個參數，來產生資料庫能使用的語法。
    // gte：大於等於；lte：小於等於；Between：大於及小於，兩者略有差異。
    @Query("{'price': {'$gte': ?0, '$lte': ?1}}")
    List<Product> findByPriceBetween(int from, int to);

    // 查詢name字串欄位有包含參數的文件，不分大小寫
    @Query("{'name': {'$regex': ?0, '$options': 'i'}}")
    List<Product> findByNameLikeIgnoreCase(String name);

    // 查詢同時符合上述兩個條件的文件
    @Query("{'$and': [{'price': {'$gte': ?0, '$lte': ?1}}, {'name': {'$regex': ?2, '$options': 'i'}}]}")
    List<Product> findByPriceBetweenAndNameLikeIgnoreCase(int priceFrom, int priceTo, String name);

    // 回傳id欄位值有包含在參數之中的文件數量
    @Query(value = "{'_id': {'$in': ?0}}", count = true)
    int countByIdIn(List<String> ids);

    // 回傳是否有文件的id欄位值包含在參數之中
    @Query(value = "{'_id': {'$in': ?0}}", exists = true)
    boolean existsByIdIn(List<String> ids);

    // 刪除id欄位值包含在參數之中的文件
    @Query(delete = true)
    void deleteByIdIn(List<String> ids);

    // 找出id欄位值有包含在參數之中的文件，並先後做name欄位遞增與price欄位遞減的排序
    @Query(sort = "{'name': 1, 'price': -1}")
    List<Product> findByIdInOrderByNameAscPriceDesc(List<String> ids);
}
