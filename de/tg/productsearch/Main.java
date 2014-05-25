package de.tg.productsearch;


import de.tg.productsearch.Dao.Product;
import de.tg.productsearch.Dao.ProductVariant;
import de.tg.productsearch.Dao.SearchResult;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.TermQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    ProductIndex productIndex;
    ProductSearch productSearch;
    ProductFacetSearch productFacetSearch;

    public List<Product> getProducts()
    {
        List<Product> products = new ArrayList<Product>();

        products.add(new Product("1", "ProductOne", new ProductVariant[] {
            new ProductVariant("1_1", "s", "blue", 5),
            new ProductVariant("1_2", "s", "yellow", 5),
            new ProductVariant("1_3", "m", "yellow", 6)
        }));

        products.add(new Product("2", "ProductTwo", new ProductVariant[]{
            new ProductVariant("2_1", "s", "blue", 5),
            new ProductVariant("2_2", "s", "yellow", 5),
            new ProductVariant("2_3", "m", "yellow", 6)
        }));

        return products;
    }

    public BooleanQuery getProductQuery()
    {
        BooleanQuery query = new BooleanQuery();
        query.add(new MatchAllDocsQuery(), BooleanClause.Occur.MUST);
        return query;
    }

    public BooleanQuery getProductVariantsQuery()
    {
        BooleanQuery query = new BooleanQuery();
        query.add(new TermQuery(new Term("color", "yellow")), BooleanClause.Occur.MUST);
        return query;
    }

    public void run() throws IOException
    {
        this.productIndex = new ProductIndex();
        this.productSearch = new ProductSearch(this.productIndex);
        this.productFacetSearch = new ProductFacetSearch(this.productIndex);

        this.productIndex.save(this.getProducts());
        SearchResult d = this.productSearch.search(this.getProductQuery(), this.getProductVariantsQuery());
        FacetResult fr1 = this.productFacetSearch.search(new MatchAllDocsQuery(), "sizes", 10);

        this.productIndex.save(this.getProducts());
        SearchResult d2 = this.productSearch.search(this.getProductQuery(), this.getProductVariantsQuery());
        FacetResult fr2 = this.productFacetSearch.search(new MatchAllDocsQuery(), "sizes", 10);


        int a = 0;

    }

}
