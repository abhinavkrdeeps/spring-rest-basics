package com.wissen.training.springrestbasics.controller;

import com.wissen.training.springrestbasics.models.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 *  The Class SpringRestBasics is for demonstrating basics concepts of spring boot rest
 *  like Returning ResponseEntity, HttpHeaders, using QueryParams .
 */
@RestController
@Produces("application/json")
public class SpringRestBasics {

    private final Logger logger =
            LoggerFactory.getLogger(SpringRestBasics.class);

    /**
     * @return Response Entity With Body as String and HttpStatus of ok (200)
     */
    @GetMapping("/body")
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> getResponseEntityAsOutput(){

        return new ResponseEntity<>("Returned Body With Status", HttpStatus.OK);
    }

    /**
     * @return Response Entity Along With Headers And HttpStatus
     */
    @GetMapping("/body/headers")
    public ResponseEntity<String> getResponseEntityWithHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.add("test-header-name","test-header-value");
        return new ResponseEntity<>("Returned Body With Headers And Status",headers,HttpStatus.ACCEPTED);
    }

    /**
     * @return Example of using query params with URI  (http://localhost:9088/queryParams?id=99)
     */
    @GetMapping("/queryParams")
    public ResponseEntity<String> queryParams(@RequestParam String id){
        logger.info("Parameter in Query is id with value : {}",id);
        return new ResponseEntity<>("Used Query Params",HttpStatus.OK);
    }

    /**
     * @param product
     * Use of RequestBody. Spring will try to convert request body as Product.
     * If it is not able to convert it to product HttpMessageNotReadableException occurs
     * @return
     */
    @PostMapping("/products")
    public ResponseEntity<Product> addProducts(@RequestBody Product product){
        logger.info(product.toString());
        return new ResponseEntity<>(product,HttpStatus.CREATED);
    }

}
