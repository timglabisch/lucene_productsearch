package de.sensiolabs.productsearch;


import com.sun.org.apache.xpath.internal.operations.Bool;
import de.sensiolabs.productsearch.Dao.ProductSearchResult;
import de.sensiolabs.productsearch.Dao.ProductVariantSearchResult;
import de.sensiolabs.productsearch.Dao.SearchResult;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.TermFilter;
import org.apache.lucene.search.*;
import org.apache.lucene.search.grouping.TopGroups;
import org.apache.lucene.search.join.FixedBitSetCachingWrapperFilter;
import org.apache.lucene.search.join.ScoreMode;
import org.apache.lucene.search.join.ToParentBlockJoinCollector;
import org.apache.lucene.search.join.ToParentBlockJoinQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProductSearch {

    protected ProductIndex productIndex;

    public ProductSearch(ProductIndex productIndex)
    {
        this.productIndex = productIndex;
    }

    protected IndexSearcher getIndexSearcher() throws IOException
    {
        return this.productIndex.getIndexSearcher();
    }

    public SearchResult search(BooleanQuery productQuery, BooleanQuery productVariantQuery) throws IOException
    {
        Filter productParentFilter = new FixedBitSetCachingWrapperFilter(
                new TermFilter(new Term("type", "product"))
        );

        ToParentBlockJoinQuery skuJoinQuery = new ToParentBlockJoinQuery(
                productVariantQuery,
                productParentFilter,
                ScoreMode.Max
        );

        BooleanQuery query = new BooleanQuery();
        query.add(productQuery, BooleanClause.Occur.MUST);
        query.add(skuJoinQuery, BooleanClause.Occur.MUST);

        ToParentBlockJoinCollector joinCollector = new ToParentBlockJoinCollector(Sort.RELEVANCE, 10, true, false);

        this.getIndexSearcher().search(query, joinCollector);

        TopGroups hits = joinCollector.getTopGroups(
                skuJoinQuery,
                Sort.RELEVANCE,
                0,   // offset
                2,  // maxDocsPerGroup
                0,   // withinGroupOffset
                true // fillSortFields
        );

        List<Document> documents = new ArrayList<Document>();

        SearchResult result = new SearchResult();

        if(hits == null)
        {
            return result;
        }

        for (int i = 0; i < hits.groups.length; i++) {

            String productId = "";
            List<ProductVariantSearchResult> productVariantSearchResults = new ArrayList<ProductVariantSearchResult>();

            for (int j = 0; j < hits.groups[i].scoreDocs.length; j++) {
                Document hitDoc = getIndexSearcher().doc(hits.groups[i].scoreDocs[j].doc);

                productId = hitDoc.get("_productId");

                productVariantSearchResults.add(new ProductVariantSearchResult(
                        hitDoc.get("sku"),
                        false // todo salse
                ));

                documents.add(hitDoc);
            }

            result.addProductSearchResult(new ProductSearchResult(productId, productVariantSearchResults));

        }

        return result;
    }

}
