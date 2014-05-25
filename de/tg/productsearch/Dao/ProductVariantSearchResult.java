package de.tg.productsearch.Dao;


public class ProductVariantSearchResult {

    protected String sku;

    protected boolean sale;

    public ProductVariantSearchResult(String sku, boolean sale)
    {
        this.sku = sku;
        this.sale = sale;
    }

    public String getSku() {
        return sku;
    }
}
