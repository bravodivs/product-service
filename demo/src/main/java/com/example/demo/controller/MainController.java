package com.example.demo.controller;

import com.example.demo.exception.ErrorResponse;
import com.example.demo.exception.CustomException;
import com.example.demo.model.Product;
import com.example.demo.service.ProductsService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
public class MainController {
    @Autowired
    private ProductsService productsService;

    //    : concerning the logs
    private Logger logger = LoggerFactory.getLogger(MainController.class);

    @GetMapping(value = {"/show_products", "/show_products/{id}"})
    public ResponseEntity<?> products(@PathVariable(required = false) String id, @RequestParam(defaultValue = "") String searchKey) {
        if (id == null || id.isBlank() || id.isEmpty()) {
            logger.info("Products list displayed");
            return new ResponseEntity<>(productsService.getAllProducts(searchKey), HttpStatus.OK);
        } else {
            //: on returning null, return exception and in it say product not present
            return new ResponseEntity<>(productsService.getProductById(id), HttpStatus.OK);
        }
    }

    //    receive a json data object and return the list as POJO.
    //    :make it accept more than one product at a time. See the format for it.
    //    @Valid to validate the data
    @PostMapping(value = "/add", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Product> addProduct(
//            @Validated({Product.ValidationGroupTwo.class})
            @Valid @RequestBody Product prodObj) {
        return new ResponseEntity<>(productsService.saveProduct(prodObj, false), HttpStatus.CREATED);
    }

    @PostMapping(value = "/addAll", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Product>> addAllProducts(@RequestBody List<Product> prods) {
        return new ResponseEntity<>(productsService.saveAllProducts(prods), HttpStatus.CREATED);
    }


    @PutMapping(value = "/update/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Product> updateProduct(@RequestBody Product product, @PathVariable String id) {
        logger.info("Product updated successfully");
        return new ResponseEntity<>(productsService.updateProduct(id, product), HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<List<Product>> deleteProduct(@PathVariable String id) {
//: say custom error if product not found using custom exception
        logger.info("Product deleted successfully");
        return new ResponseEntity<>(productsService.deleteProduct(id), HttpStatus.ACCEPTED);
    }

    @GetMapping("/check")
    public ResponseEntity<Product> check_exception() {
        throw new CustomException("checking the error", HttpStatus.ALREADY_REPORTED, "2");
    }

//    REVIEW COMMENTS
//    : change from conflict to generic exception. use apt https status codes
//    : use responseEntity for returning response and errors.
//    : refactor the model(change name) and controller(move update code to service)
//    : creating a new errorHandler that should handle every specific and other unavoidable errors in that class
//    : on adding, return only added json with code 201.
//    : different handlers for adding, adding all;
//    : change from requestParam to pathVariable.
//    : make id autogenerated. make it String for UUID. <-then cant use built-in methods like findbyid etc
//    : add contextual path through properties, such as /api/products/view etc
//

//    NEW TASKS
//    : data validation of json responses(can be done using annotation)
//    : ->handle annotation failure error.
//    : Logging/logs concepts.

//    : add /search based on name, desc
//    TODO: JUnit testcases

//    26/9/23 comments
//    : use annotation for date created and modified. dont do it manually
//    : instead of service, make controller return response entity. service returns only data.
//    : for show products also, use path variable. use request param for only searching etc.
//    : able to add null values. change that. Validation, NotNull, etc.
}

//  27/09/23
// :  learn-> spring active profiles
//  TODO: learn-> queueing mechanism-rabitMQ, r kafka.
//  TODO: use command line arguments <- spring boot <- connection done but data not accessible
//  : add a list of strings and try to validate if each value is not empty.
//  : on updating a product, createdAt becoming null. Fix it.
//  : on validating, id required so skip its validation.
