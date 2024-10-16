package ee.taltech.iti03022024backend.invjug.controller;

import lombok.Data;

@Data
public class Product {

    private Long id;
    private String title;
    private Long price;
    private Long quantity;

}
