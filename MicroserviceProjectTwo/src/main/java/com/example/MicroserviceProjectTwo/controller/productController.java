package com.example.MicroserviceProjectTwo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class productController {


    @GetMapping("/api/product/{productId}")
    public ResponseEntity<String> getProduct (@PathVariable Integer productId){

        if(productId >= 1 && productId <= 10){
            return new ResponseEntity<>("Product with id " + productId, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("Product not available", HttpStatus.NOT_FOUND);
        }

    }
}
