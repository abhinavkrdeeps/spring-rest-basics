package com.wissen.training.springrestbasics.models;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private String productId;
    private String productName;
    private Double price;

    public String toString(){
        return "";
    }


}
