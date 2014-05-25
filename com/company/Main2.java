package com.company;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.facet.*;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.TermFilter;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.search.grouping.TopGroups;
import org.apache.lucene.search.join.FixedBitSetCachingWrapperFilter;
import org.apache.lucene.search.join.ScoreMode;
import org.apache.lucene.search.join.ToParentBlockJoinCollector;
import org.apache.lucene.search.join.ToParentBlockJoinQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main2 {


    private static Document createProduct(String name, String description)
    {
        Document d = new Document();
        d.add(new Field("name", name, TextField.TYPE_STORED));
        d.add(new Field("docType", "product", StringField.TYPE_NOT_STORED));
        d.add(new Field("description", description, TextField.TYPE_STORED));
        d.add(new Field("type", "product", TextField.TYPE_STORED));
        return d;
    }

    private static Document createProductItem(String color, String size, int price)
    {
        Document d = new Document();
        d.add(new Field("color", color, TextField.TYPE_STORED));
        d.add(new Field("size", size, TextField.TYPE_STORED));
        d.add(new IntField("price", price, IntField.TYPE_STORED));

        return d;
    }

    public static void main(String[] args) throws IOException, ParseException {




        if(true) {
            return;
        }

        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);

        // Store the index in memory:
        Directory directory = new RAMDirectory();
        Directory taxoDirectory = new RAMDirectory();

        // To store an index on disk, use this instead:
        //Directory directory = FSDirectory.open("/tmp/testindex");
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_CURRENT, analyzer);
        IndexWriter iwriter = new IndexWriter(directory, config);

        List<Document> documents = new ArrayList<Document>();

        documents.add(createProductItem("black", "m", 5));
        documents.add(createProductItem("yellow", "s", 5));
        documents.add(createProduct("fancy_shirt", "some fancy shirt"));
        iwriter.addDocuments(documents);
        documents.clear();


        documents.add(createProductItem("green", "m", 5));
        documents.add(createProductItem("yellow", "s", 5));
        documents.add(createProduct("awesome_shirt", "some awesome shirt"));
        iwriter.addDocuments(documents);
        documents.clear();


        DirectoryTaxonomyWriter taxoWriter = new DirectoryTaxonomyWriter(taxoDirectory);

        FacetsConfig facetsConfig = new FacetsConfig();
        facetsConfig.setIndexFieldName("Author", "author");

        Document doc = new Document();
        doc.add(new FacetField("Author", "Bob"));
        iwriter.addDocument(facetsConfig.build(taxoWriter, doc));

        Document docc = new Document();
        docc.add(new FacetField("Author", "Boby"));
        iwriter.addDocument(facetsConfig.build(taxoWriter, docc));

        taxoWriter.close();
        iwriter.close();

        // Now search the index:
        DirectoryReader ireader = DirectoryReader.open(directory);
        IndexSearcher isearcher = new IndexSearcher(ireader);










        FacetsCollector facetsCollector = new FacetsCollector();
        DirectoryTaxonomyReader taxoReader = new DirectoryTaxonomyReader(taxoDirectory);

        FacetsCollector.search(isearcher, new MatchAllDocsQuery(), 10, facetsCollector);

        Facets author = new FastTaxonomyFacetCounts("author", taxoReader, facetsConfig, facetsCollector);
        FacetResult fr = author.getTopChildren(10, "Author");





        Filter products = new FixedBitSetCachingWrapperFilter(
                new TermFilter(new Term("type", "product"))
        );


        BooleanQuery skuQuery = new BooleanQuery();
        skuQuery.add(new TermQuery(new Term("color", "black")), BooleanClause.Occur.MUST);

        ToParentBlockJoinQuery skuJoinQuery = new ToParentBlockJoinQuery(
                skuQuery,
                products,
                ScoreMode.None
        );

        BooleanQuery query = new BooleanQuery();
        query.add(new TermQuery(new Term("description", "some")), BooleanClause.Occur.MUST);
        query.add(skuJoinQuery, BooleanClause.Occur.MUST);



        ToParentBlockJoinCollector c = new ToParentBlockJoinCollector(Sort.RELEVANCE, 10, false, false);
        isearcher.search(query, c);





        TopGroups hits = c.getTopGroups(
                skuJoinQuery,
                Sort.INDEXORDER,
                0,   // offset
                2,  // maxDocsPerGroup
                0,   // withinGroupOffset
                true // fillSortFields
        );


        for (int i = 0; i < hits.groups.length; i++) {
            for (int j = 0; j < hits.groups[i].scoreDocs.length; j++) {
                Document hitDoc = isearcher.doc(hits.groups[i].scoreDocs[j].doc);

                System.out.println(hitDoc.get("size"));
            }

        }

        ireader.close();
        directory.close();
        System.out.println("Uyy");

    }
}
