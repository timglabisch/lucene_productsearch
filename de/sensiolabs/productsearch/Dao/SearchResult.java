package de.sensiolabs.productsearch.Dao;


import java.util.ArrayList;

public class SearchResult {

    protected ArrayList<ProductSearchResult> productSearchResults = new ArrayList<ProductSearchResult>();

    public void addProductSearchResult(ProductSearchResult productSearchResult)
    {
        this.productSearchResults.add(productSearchResult);
    }

    public ArrayList<ProductSearchResult> getProductSearchResults()
    {
        return this.productSearchResults;
    }

}
