package de.sensiolabs.productsearch.Dao;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Product {

    protected String id;

    protected String name;

    protected String description = "";

    protected ProductVariant[] variants;

    public Product(String id, String name, ProductVariant[] variants)
    {
        this.name = name;
        this.id = id;
        this.variants = variants;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ProductVariant[] getVariants()
    {
        return this.variants;
    }

    public String[] extractUniqueVariantSizes()
    {
        HashSet<String> buffer = new HashSet<String>();

        for(ProductVariant variant : this.variants)
        {
            if(buffer.contains(variant.getSize())) {
                continue;
            }

            buffer.add(variant.getSize());
        }

        return buffer.toArray(new String[buffer.size()]);
    }

    public String[] extractUniqueVariantColors()
    {
        HashSet<String> buffer = new HashSet<String>();

        for(ProductVariant variant : this.variants)
        {
            if(buffer.contains(variant.getColor())) {
                continue;
            }

            buffer.add(variant.getColor());
        }

        return buffer.toArray(new String[buffer.size()]);
    }

}
