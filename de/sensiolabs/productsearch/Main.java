package de.sensiolabs.productsearch;


import com.sun.org.apache.xpath.internal.operations.Bool;
import de.sensiolabs.productsearch.Dao.Product;
import de.sensiolabs.productsearch.Dao.ProductVariant;
import de.sensiolabs.productsearch.Dao.SearchResult;
import org.apache.lucene.document.Document;
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

        {
            Product p = new Product("1", "ProductOne");
            p.addVariant(new ProductVariant("1_1", "s", "blue", 5));
            p.addVariant(new ProductVariant("1_2", "s", "yellow", 5));
            p.addVariant(new ProductVariant("1_3", "m", "yellow", 6));
            products.add(p);
        }

        {
            Product p = new Product("2", "ProductTwo");
            p.addVariant(new ProductVariant("2_1", "s", "blue", 5));
            p.addVariant(new ProductVariant("2_2", "s", "yellow", 5));
            p.addVariant(new ProductVariant("2_3", "m", "yellow", 6));
            products.add(p);
        }

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
