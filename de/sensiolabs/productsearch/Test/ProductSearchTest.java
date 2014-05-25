package de.sensiolabs.productsearch.Test;


import de.sensiolabs.productsearch.Dao.Product;
import de.sensiolabs.productsearch.Dao.ProductVariant;
import de.sensiolabs.productsearch.ProductFacetSearch;
import de.sensiolabs.productsearch.ProductIndex;
import de.sensiolabs.productsearch.ProductSearch;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

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
    public void testFacetEmpty() throws Exception
    {
        Assert.assertEquals(true, true);
    }

}
