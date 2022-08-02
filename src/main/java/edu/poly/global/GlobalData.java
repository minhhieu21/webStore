package edu.poly.global;


import java.util.ArrayList;
import java.util.List;

import edu.poly.domain.Product;

public class GlobalData {
    //tao bien toan cuc
    public static List<Product> cart;

    static {
        cart = new ArrayList<>();
    }

}
