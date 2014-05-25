package de.sensiolabs.productsearch.Dao;


public class ProductVariant {

    public String sku;

    public String size;

    public String color;

    public int price;

    public String getSize() {
        return size;
    }

    public ProductVariant(String sku, String size, String color, int price)
    {
        this.sku = sku;
        this.size = size;
        this.color = color;
        this.price = price;
    }

    public String getSku() {
        return sku;
    }

    public String getColor() {
        return color;
    }

    public int getPrice() {
        return price;
    }
}
