package com.wissen.training.springrestbasics.dto;

import com.wissen.training.springrestbasics.models.Product;

import javax.servlet.http.HttpServletResponse;
import java.util.function.Function;

public class UserResponse  implements Function<Product,Integer> {
    @Override
    public Integer apply(Product product) {
        return null;
    }
}
