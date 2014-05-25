package de.tg.productsearch.Test;


import de.tg.productsearch.Dao.Product;
import de.tg.productsearch.Dao.ProductVariant;
import de.tg.productsearch.Dao.ProductVariantSearchResult;
import de.tg.productsearch.Dao.SearchResult;
import de.tg.productsearch.ProductFacetSearch;
import de.tg.productsearch.ProductIndex;
import de.tg.productsearch.ProductSearch;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class ProductSearchTest {


    ProductIndex productIndex;
    ProductSearch productSearch;
    ProductFacetSearch productFacetSearch;

    @Before
    public void setUp() throws IOException
    {
        this.productIndex = new ProductIndex();
        this.productSearch = new ProductSearch(this.productIndex);
        this.productFacetSearch = new ProductFacetSearch(this.productIndex);
    }

    @Test
    public void testEmpty() throws Exception
    {
        SearchResult res = this.productSearch.search(null, null);
        Assert.assertEquals(0, res.getProductSearchResults().size());
    }

    @Test
    public void testMatchAll() throws Exception
    {
        this.productIndex.save(
                new Product("product_one", "ProductOne", new ProductVariant[] {
                        new ProductVariant("1_1","s","",1),
                        new ProductVariant("1_2", "s", "", 1),
                        new ProductVariant("1_3", "m", "", 1)
                })
        );

        this.productIndex.save(
                new Product("product_two", "ProductTwo", new ProductVariant[] {
                        new ProductVariant("2_1","xl","", 1),
                        new ProductVariant("2_2", "s", "", 1),
                })
        );

        BooleanQuery q = new BooleanQuery();
        q.add(new MatchAllDocsQuery(), BooleanClause.Occur.MUST);

        SearchResult res = this.productSearch.search(null, null);
        Assert.assertEquals(2, res.getProductSearchResults().size());


        Assert.assertEquals("product_one", res.getProductSearchResults().get(0).getId());
        List<ProductVariantSearchResult> matchedVariants1 = res.getProductSearchResults().get(0).getMatchedVariants();
        Assert.assertEquals(matchedVariants1.size(), 2); // we just collect 2 items, may one is a SALE Product.
        Assert.assertEquals(matchedVariants1.get(0).getSku(), "1_1");
        Assert.assertEquals(matchedVariants1.get(1).getSku(), "1_2");


        Assert.assertEquals("product_two", res.getProductSearchResults().get(1).getId());
        List<ProductVariantSearchResult> matchedVariants2 = res.getProductSearchResults().get(1).getMatchedVariants();
        Assert.assertEquals(matchedVariants2.get(0).getSku(), "2_1");
        Assert.assertEquals(matchedVariants2.get(1).getSku(), "2_2");

    }

    @Test
    public void testQueryProduct() throws Exception
    {
        this.productIndex.save(
                new Product("product_one", "ProductOne", new ProductVariant[] {
                        new ProductVariant("1_1","s","",1),
                        new ProductVariant("1_2", "s", "", 1),
                        new ProductVariant("1_3", "m", "", 1)
                })
        );

        this.productIndex.save(
                new Product("product_two", "product_two_name", new ProductVariant[] {
                        new ProductVariant("2_1","xl","", 1),
                        new ProductVariant("2_2", "s", "", 1),
                })
        );

        BooleanQuery q = new BooleanQuery();
        q.add(new TermQuery(new Term("name", "product_two_name")), BooleanClause.Occur.MUST);

        SearchResult res = this.productSearch.search(q, null);
        Assert.assertEquals(1, res.getProductSearchResults().size());
    }

    @Test
    public void testQueryProductVariant() throws Exception
    {
        this.productIndex.save(
                new Product("product_one", "ProductOne", new ProductVariant[] {
                        new ProductVariant("1_1","s","",1),
                        new ProductVariant("1_2", "s", "", 1),
                        new ProductVariant("1_3", "m", "", 1)
                })
        );

        this.productIndex.save(
                new Product("product_two", "product_two_name", new ProductVariant[] {
                        new ProductVariant("2_1","xl","", 1),
                        new ProductVariant("2_2", "s", "", 1),
                })
        );

        BooleanQuery q = new BooleanQuery();
        q.add(new TermQuery(new Term("size", "xl")), BooleanClause.Occur.MUST);

        SearchResult res = this.productSearch.search(null, q);
        Assert.assertEquals(1, res.getProductSearchResults().size());
        Assert.assertEquals("product_two", res.getProductSearchResults().get(0).getId());
    }

}
