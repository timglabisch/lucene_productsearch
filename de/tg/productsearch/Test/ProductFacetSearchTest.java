package de.tg.productsearch.Test;


import de.tg.productsearch.Dao.Product;
import de.tg.productsearch.Dao.ProductVariant;
import de.tg.productsearch.ProductFacetSearch;
import de.tg.productsearch.ProductIndex;
import de.tg.productsearch.ProductSearch;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class ProductFacetSearchTest {


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
    public void testFacetEmpty() throws Exception
    {
        FacetResult fr = this.productFacetSearch.search(new MatchAllDocsQuery(), "sizes", 10);
        Assert.assertEquals(null, fr);
    }

    @Test
    public void testBasicFacet() throws Exception
    {
        this.productIndex.save(
            new Product("1", "ProductOne", new ProductVariant[] {
                new ProductVariant("2_1","s","blue",5),
                new ProductVariant("2_1", "s", "blue", 5),
                new ProductVariant("2_3", "m", "yellow", 6)
            })
        );

        FacetResult fr = this.productFacetSearch.search(new MatchAllDocsQuery(), "sizes", 10);

        Assert.assertEquals(2, fr.childCount); // m, s

        Assert.assertEquals("s", fr.labelValues[0].label);
        // there are 2 variants with size "s",
        // but here we are counting products, not variants!
        Assert.assertEquals(1, fr.labelValues[0].value);

        Assert.assertEquals("m", fr.labelValues[1].label);
        Assert.assertEquals(1, fr.labelValues[1].value);
    }

    @Test
    public void testMultipleFacets() throws Exception
    {
        this.productIndex.save(
                new Product("1", "ProductOne", new ProductVariant[] {
                        new ProductVariant("2_1","s","",1),
                        new ProductVariant("2_1", "s", "", 1),
                        new ProductVariant("2_3", "m", "", 1)
                })
        );

        this.productIndex.save(
                new Product("2", "ProductTwo", new ProductVariant[] {
                        new ProductVariant("2_1","xl","", 1),
                        new ProductVariant("2_1", "s", "", 1),
                })
        );

        FacetResult fr = this.productFacetSearch.search(new MatchAllDocsQuery(), "sizes", 10);

        Assert.assertEquals(3, fr.childCount); // s, m, xl

        Assert.assertEquals("s", fr.labelValues[0].label);
        // there are 3 variants with size "s",
        // but here we are counting products, not variants!
        Assert.assertEquals(2, fr.labelValues[0].value);

        Assert.assertEquals("m", fr.labelValues[1].label);
        Assert.assertEquals(1, fr.labelValues[1].value);

        Assert.assertEquals("xl", fr.labelValues[2].label);
        Assert.assertEquals(1, fr.labelValues[2].value);
    }

    @Test
    public void testFacetsQuery() throws Exception
    {

        this.productIndex.save(
                new Product("1", "fghdfgh", new ProductVariant[] {
                        new ProductVariant("2_1","s","",1),
                        new ProductVariant("2_1", "s", "", 1),
                })
        );

        this.productIndex.save(
                new Product("2", "dfgjfjfghj", new ProductVariant[] {
                        new ProductVariant("2_1", "s", "", 1),
                })
        );

        Query q = new TermQuery(new Term("name", "dfgjfjfghj")); // take care, std. analyzer will lowercase all the stuff

        FacetResult fr = this.productFacetSearch.search(q, "sizes", 10);
        Assert.assertEquals(1, fr.childCount);
    }

}
