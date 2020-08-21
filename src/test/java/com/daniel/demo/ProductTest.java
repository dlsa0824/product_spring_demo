package com.daniel.demo;

import com.daniel.demo.entity.Product;
import com.daniel.demo.repository.ProductRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProductTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    private HttpHeaders httpHeaders;

    @Before
    public void init(){
        //使用Postman時會在「Body」頁籤選擇「raw」與「JSON」，再輸入JSON字串作為請求主體。因此在「Headers」頁籤能發現相關資料。
        // 在測試程式中同樣會準備這些資料，我們以「HttpHeaders」與「JSONObject」提供。
        httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
    }

    @After
    public void clear() {
        productRepository.deleteAll();
    }

    @Test
    public void testCreateProduct() throws Exception{

        JSONObject request = new JSONObject();
        request.put("name", "Harry Potter");
        request.put("price", 450);

        RequestBuilder requestBuilder =
                MockMvcRequestBuilders
                        .post("/products") //「post」方法代表發送POST請求，參數需傳入API路徑。
                        .headers(httpHeaders)           //「headers」方法可附加請求標頭。
                        .content(request.toString());   //「content」方法可加入請求主體，此處傳入JSON字串作為參數。

        mockMvc.perform(requestBuilder)
                .andDo(print()) //使用「andDO(print())」方法，能將測試程式的請求與回應詳情印在console視窗。
                .andExpect(status().isCreated())  //驗證HTTP狀態碼應為201。讀者可自行探索其他狀態，如「isOk」、「isNotFound」等。或透過「is」方法直接傳入狀態碼。
                .andExpect(jsonPath("$.id").hasJsonPath())  //指定JSON欄位。以「$」符號開始，使用「.」符號深入下一層的路徑。「hasJsonPath()」：驗證某個JSON欄位存在。
                .andExpect(jsonPath("$.name").value(request.getString("name"))) //「value()」：驗證某個JSON欄位值為何。
                .andExpect(jsonPath("$.price").value(request.getInt("price")))
                .andExpect(header().exists("Location")) //「header().exists()」：驗證回應標頭中的某欄位存在。
                .andExpect(header().string("Content-Type", "application/json;charset=UTF-8")); //「header().string()」：驗證回應標頭中的某欄位值為何。

                //透過「andExpect」方法進行回應資料的驗證
    }

    @Test
    public void testGetProduct() throws Exception {
        Product product = createProduct("Economics", 450);
        productRepository.insert(product);

        mockMvc.perform(get("/products/" + product.getId())
                .headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.name").value(product.getName()))
                .andExpect(jsonPath("$.price").value(product.getPrice()));
    }

    @Test
    public void testReplaceProduct() throws Exception {
        Product product = createProduct("Economics", 450);
        productRepository.insert(product);

        JSONObject request = new JSONObject();
        request.put("name", "Macroeconomics");
        request.put("price", 550);

        mockMvc.perform(put("/products/" + product.getId())
                .headers(httpHeaders)
                .content(request.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.name").value(request.getString("name")))
                .andExpect(jsonPath("$.price").value(request.getInt("price")));
    }

    @Test(expected = RuntimeException.class)
    public void testDeleteProduct() throws Exception {
        Product product = createProduct("Economics", 450);
        productRepository.insert(product);

        mockMvc.perform(delete("/products/" + product.getId())
                .headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isOk());

        productRepository.findById(product.getId())
                .orElseThrow(RuntimeException::new);
    }

    private Product createProduct(String name, int price) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);

        return product;
    }

    @Test
    public void testSearchProductsSortByPriceAsc() throws Exception{
        Product p1 = createProduct("Operation Management", 350);
        Product p2 = createProduct("Market Management", 200);
        Product p3 = createProduct("Human Resource Management", 420);
        Product p4 = createProduct("Finance Management", 400);
        Product p5 = createProduct("Enterprise Resource Planning", 440);
        productRepository.insert(Arrays.asList(p1, p2, p3, p4, p5));

//        //用andExpect去判斷回傳值是否正確
//        mockMvc.perform(get("/products")
//                .headers(httpHeaders)
//                .param("keyword", "Manage")
//                .param("orderBy", "price")
//                .param("sortRule", "asc"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(4)))
//                .andExpect(jsonPath("$[0].id").value(p2.getId()))
//                .andExpect(jsonPath("$[1].id").value(p1.getId()))
//                .andExpect(jsonPath("$[2].id").value(p4.getId()))
//                .andExpect(jsonPath("$[3].id").value(p3.getId()));


        //取出MockMvc的執行結果，並搭配「JUnit」提供的斷言（assert）方法。
        //透過MockMvc的「andReturn」方法，能得到「MvcResult」物件。
        // 再透過它的「getResponse」方法，得到「MockHttpServletResponse」物件。
        // 該物件會有一些資料可以運用，讓我們做更細部的檢查。
        MvcResult result = mockMvc.perform(get("/products")
                .headers(httpHeaders)
                .param("keyword", "Manage")
                .param("orderBy", "price")
                .param("sortRule", "asc"))
                .andReturn();

        //請在發出請求後，取得MockHttpServletResponse物件
        // 。透過它的「getContentAsString」方法取出回應主體的JSON字串，再轉為JSONArray。
        MockHttpServletResponse mockHttpResponse = result.getResponse();
        String responseJSONStr = mockHttpResponse.getContentAsString();
        JSONArray productJSONArray = new JSONArray(responseJSONStr);

        //由於只要驗證回應主體中都是預期的產品id，所以接著將JSONArray中的產品id給收集起來。
        List<String> productIds = new ArrayList<>();
        for (int i = 0; i < productJSONArray.length(); i++) {
            JSONObject productJSON = productJSONArray.getJSONObject(i);
            productIds.add(productJSON.getString("id"));
        }

        //收集完產品id後，下面使用斷言方法來驗證資料。首先驗證應該要有4個產品被查詢到。
        // 接著根據預期的排序結果，驗證第1~4個產品的id。
        Assert.assertEquals(4, productIds.size());
        Assert.assertEquals(p2.getId(), productIds.get(0));
        Assert.assertEquals(p1.getId(), productIds.get(1));
        Assert.assertEquals(p4.getId(), productIds.get(2));
        Assert.assertEquals(p3.getId(), productIds.get(3));

        Assert.assertEquals(HttpStatus.OK.value(), mockHttpResponse.getStatus());
        Assert.assertEquals("application/json;charset=UTF-8", mockHttpResponse.getContentType());

        //若這個請求沒有指定要排序，可以用如下的方式驗證。
//        Assert.assertTrue(productIds.contains(p1.getId()));
//        Assert.assertTrue(productIds.contains(p2.getId()));
//        Assert.assertTrue(productIds.contains(p3.getId()));
//        Assert.assertTrue(productIds.contains(p4.getId()));
    }

    //撰寫失敗測試案例
    @Test
    public void get400WhenCreateProductWithEmptyName() throws Exception {
        JSONObject request = new JSONObject();
        request.put("name", "");
        request.put("price", 100);

        mockMvc.perform(post("/products")
                .headers(httpHeaders)
                .content(request.toString()))
                .andExpect(status().isBadRequest());
    }

    //撰寫失敗測試案例
    @Test
    public void get400WhenReplaceProductWithNegativePrice() throws Exception {
        Product product = createProduct("Computer Science", 350);
        productRepository.insert(product);

        JSONObject request = new JSONObject();
        request.put("name", "Computer Science");
        request.put("price", -100);

        mockMvc.perform(put("/products/" + product.getId())
                .headers(httpHeaders)
                .content(request.toString()))
                .andExpect(status().isBadRequest());
    }

}
