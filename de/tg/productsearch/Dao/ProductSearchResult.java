package de.tg.productsearch.Dao;

import java.util.List;

public class ProductSearchResult {

    protected String id;

    protected List<ProductVariantSearchResult> matchedVariants;

    public ProductSearchResult(String id, List<ProductVariantSearchResult> matchedVariants)
    {
        this.id = id;
        this.matchedVariants = matchedVariants;
    }

    public String getId() {
        return id;
    }

    public List<ProductVariantSearchResult> getMatchedVariants() {
        return matchedVariants;
    }
}
