package com.example.AuthService.controller;

import com.example.AuthService.configuration.UserServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@RestController
public class OrderController {


    @Autowired
    private  RestTemplate restTemplate;

    @Autowired
    private RestClient restClient;

    @Autowired
    private UserServiceClient userServiceClient;


//    @Autowired
//    private DiscoveryClient discoveryClient;


    @GetMapping("/api/order/{productId}")
    public String placeOrder(@PathVariable Integer productId) {
        String ans =  restTemplate.getForObject("http://localhost:8083/api/product/"+productId,String.class);

        if(ans.equals("Product " + productId)){
            return "Order Placed";
        }else{
            return "Order Not Placed";
        }
    }

    @GetMapping("/api/order2/{productId}")
    public String placeOrder2(@PathVariable Integer productId) {
        org.springframework.http.ResponseEntity<String> entity =  restClient.get()
                .uri("http://localhost:8083/api/product/{productId}",productId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError , (req,res)->{
                    throw new RuntimeException(res.getBody().toString());
                })
                .toEntity(String.class) ;

        String ans = entity.getBody();

        if(ans.equals("Product with id " + productId)){
            return "Order Placed";
        }else{
            return "Order Not Placed";
        }
    }

    @GetMapping("/api/order3/{productId}")
    public String placeOrder3(@PathVariable Integer productId) {
       return    restClient.get()
                .uri("http://localhost:8083/api/product/{productId}",productId)
                .exchange((req,res)->{

                    if(res.getBody().toString().equals("Product with id " + productId)){
                        return "Order Placed";
                    }
                    else {
                        return "Order Not Placed";
                    }
                });


    }

    @GetMapping("/api/order4/{productId}")
    public String placeOrder4(@PathVariable Integer productId) {



        return userServiceClient.getProduct(productId).getBody() ;

    }


}
