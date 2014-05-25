package de.tg.productsearch;


import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

import java.io.IOException;

public class ProductFacetSearch {

    protected ProductIndex productIndex;

    public ProductFacetSearch(ProductIndex productIndex)
    {
        this.productIndex = productIndex;
    }

    protected IndexSearcher getIndexSearcher() throws IOException
    {
        return this.productIndex.getIndexSearcher();
    }

    public FacetResult search(Query productQuery, String field, int size) throws IOException {
        FacetsCollector facetsCollector = new FacetsCollector();

        FacetsCollector.search(this.getIndexSearcher(), productQuery, 10, facetsCollector);

        Facets author = new FastTaxonomyFacetCounts(field, this.productIndex.getTaxonomyReader(), this.productIndex.getProductFacetConfiguration(), facetsCollector);
        return author.getTopChildren(size, field);
    }

}
