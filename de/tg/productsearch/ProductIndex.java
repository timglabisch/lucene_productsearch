package de.tg.productsearch;


import de.tg.productsearch.Dao.Product;
import de.tg.productsearch.Dao.ProductVariant;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.facet.FacetField;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.facet.taxonomy.TaxonomyWriter;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProductIndex {

    protected Directory productDirectory;
    protected Directory productFacetDirectory;
    protected IndexWriter indexWriter;
    protected TaxonomyWriter taxonomyWriter;
    protected TaxonomyReader taxonomyReader;
    protected IndexSearcher indexSearcher;

    public ProductIndex() throws IOException
    {
        this.productDirectory = new RAMDirectory();
        this.productFacetDirectory = new RAMDirectory();

        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_CURRENT, analyzer);
        this.indexWriter = new IndexWriter(productDirectory, config);
    }

    protected Document getDocumentByProductVariant(ProductVariant productVariant, Product parentProduct)
    {
        Document d = new Document();
        d.add(new TextField("_productId", parentProduct.getId(), Field.Store.YES));
        d.add(new TextField("sku", productVariant.getSku(), Field.Store.YES));
        d.add(new TextField("size", productVariant.getSize(), Field.Store.YES));
        d.add(new TextField("color", productVariant.getColor(), Field.Store.YES));
        d.add(new TextField("type", "productVariant", Field.Store.YES));
        d.add(new IntField("price", productVariant.getPrice(), IntField.TYPE_STORED));
        return d;
    }

    public FacetsConfig getProductFacetConfiguration()
    {
        FacetsConfig facetsConfig = new FacetsConfig();
        facetsConfig.setIndexFieldName("sizes", "sizes");
        facetsConfig.setMultiValued("sizes", true);
        facetsConfig.setIndexFieldName("name", "name");
        facetsConfig.setIndexFieldName("colors", "colors");
        return facetsConfig;
    }



    protected List<Document> getDocumentByProduct(Product product) throws IOException
    {
        List<Document> documents = new ArrayList<Document>();

        for(ProductVariant variant : product.getVariants())
        {
            documents.add(this.getDocumentByProductVariant(variant, product));
        }

        Document d = new Document();
        d.add(new TextField("id", product.getId(), Field.Store.YES));
        d.add(new TextField("name", product.getName(), Field.Store.YES));
        for(String v : product.extractUniqueVariantSizes())
        {
            d.add(new FacetField("sizes", v));
        }
        //d.add(new FacetField("colors", product.extractUniqueVariantColors()));
        d.add(new TextField("type", "product", Field.Store.YES));
        d.add(new TextField("description", product.getDescription(), Field.Store.YES));
        documents.add(this.getProductFacetConfiguration().build(this.getTaxonomyWriter(), d));

        //documents.add(d);

        return documents;
    }

    protected TaxonomyWriter getTaxonomyWriter() throws IOException
    {
        if(this.taxonomyWriter == null)
        {
            this.taxonomyWriter = new DirectoryTaxonomyWriter(this.productFacetDirectory);
        }

        return this.taxonomyWriter;
    }


    protected IndexSearcher getIndexSearcher() throws IOException
    {
        if(this.indexSearcher == null) {
            DirectoryReader ireader = DirectoryReader.open(this.indexWriter, true);
            this.indexSearcher = new IndexSearcher(ireader);

        }

        return this.indexSearcher;
    }

    protected TaxonomyReader getTaxonomyReader() throws IOException
    {
        if(this.taxonomyReader == null)
        {
            this.getTaxonomyWriter().close();
            this.taxonomyWriter = null;
            this.taxonomyReader = new DirectoryTaxonomyReader(this.productFacetDirectory);
        }

        return this.taxonomyReader;
    }



    public void save(Product product) throws IOException
    {
        List<Document> document = this.getDocumentByProduct(product);
        this.indexWriter.addDocuments(document);

        this.indexSearcher = null;
    }

    public void save(List<Product> products) throws IOException{
        for (Product product : products)
        {
            this.save(product);
        }
    }

}
