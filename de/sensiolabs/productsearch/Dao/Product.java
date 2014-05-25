package de.sensiolabs.productsearch.Dao;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Product {

    protected String id;

    protected String name;

    protected String description = "";

    protected List<ProductVariant> variants = new ArrayList<ProductVariant>();

    public Product(String id, String name)
    {
        this.name = name;
        this.id = id;
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

    public void addVariant(ProductVariant variant)
    {
        this.variants.add(variant);
    }

    public List<ProductVariant> getVariants()
    {
        return this.variants;
    }

    public String[] extractUniqueVariantSizes()
    {
        HashSet<String> buffer = new HashSet<String>();

        for(ProductVariant variant : this.getVariants())
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

        for(ProductVariant variant : this.getVariants())
        {
            if(buffer.contains(variant.getColor())) {
                continue;
            }

            buffer.add(variant.getColor());
        }

        return buffer.toArray(new String[buffer.size()]);
    }

}
