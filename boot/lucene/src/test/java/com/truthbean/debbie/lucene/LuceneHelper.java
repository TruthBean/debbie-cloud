package com.truthbean.debbie.lucene;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.TreeSet;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.1
 * Created on 2020-04-28 22:47
 */
public class LuceneHelper {

    private final Directory directory;

    public LuceneHelper() throws IOException {
        URL resource = LuceneHelper.class.getResource(".");
        String path = resource.getPath();
        String indexPath = path.substring(1) + "index";
        LOGGER.debug(() -> "indexPath: " + indexPath);
        directory = new NIOFSDirectory(Path.of(indexPath));
    }

    public String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    public void createIndex(Feature feature) throws IOException {
        IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig());

        var featureValue = feature.getFeature();
        byte[] featureData = FeatureUtils.doubleArray2bytes(featureValue);
        FieldType fieldType = new FieldType();
        fieldType.setStored(true);
        Field featureField = new Field("feature", featureData, fieldType);
        Field idField = new StringField("id", String.valueOf(feature.getId()), Field.Store.YES);
        Field dateField = new StringField("date", formatDate(feature.getDate()), Field.Store.YES);

        Document document = new Document();
        document.add(idField);
        document.add(featureField);
        document.add(dateField);

        indexWriter.addDocument(document);
        indexWriter.flush();
        indexWriter.commit();
        indexWriter.close();
    }

    public void createTestIndex() throws IOException {
        IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig());

        FieldType fieldType = new FieldType();
        fieldType.setStored(true);
        Field idField = new StringField("id", String.valueOf(123), Field.Store.YES);
        Field dateField = new StringField("date", "20201220", Field.Store.YES);

        Document document = new Document();
        document.add(idField);
        document.add(dateField);

        indexWriter.addDocument(document);
        indexWriter.flush();
        indexWriter.commit();
        indexWriter.close();
    }

    public void queryIndex(LocalDate date) throws IOException {
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        Query query = new TermQuery(new Term("date", formatDate(date)));

        TopDocs topDocs = indexSearcher.search(query, 1000);
        LOGGER.debug("totalHits: " + topDocs.totalHits);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            LOGGER.debug("score: " + scoreDoc.score);
            LOGGER.debug("shardIndex: " + scoreDoc.shardIndex);
            LOGGER.debug("scoreDoc: " + scoreDoc.toString());
            Document doc = indexSearcher.doc(scoreDoc.doc);
            String id = doc.get("id");
            LOGGER.debug("id: " + id);
            BytesRef featureBytesRef = doc.getBinaryValue("feature");
            if (featureBytesRef != null) {
                LOGGER.debug("featureBytesRef: " + Arrays.toString(FeatureUtils.bytes2doubleArray(featureBytesRef.bytes, 512)));
            }
            IndexableField dateStr = doc.getField("date");
            LOGGER.debug("date: " + dateStr.stringValue());
        }

        indexReader.close();
    }

    public void queryIndex() throws IOException {
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        Query query = new MatchAllDocsQuery();

        TopDocs topDocs = indexSearcher.search(query, 1000);
        LOGGER.debug("totalHits: " + topDocs.totalHits);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            LOGGER.debug("score: " + scoreDoc.score);
            LOGGER.debug("shardIndex: " + scoreDoc.shardIndex);
            LOGGER.debug("scoreDoc: " + scoreDoc.toString());
            Document doc = indexSearcher.doc(scoreDoc.doc);
            String id = doc.get("id");
            LOGGER.debug("id: " + id);
            BytesRef featureBytesRef = doc.getBinaryValue("feature");
            LOGGER.debug("featureBytesRef: " + Arrays.toString(FeatureUtils.bytes2doubleArray(featureBytesRef.bytes, 512)));
            IndexableField date = doc.getField("date");
            LOGGER.debug("date: " + date.stringValue());
        }

        indexReader.close();
    }

    public DistanceFeature queryIndex(double[] feature) throws IOException {
        IndexReader indexReader = DirectoryReader.open(directory);

        // Bits liveDocs = MultiFields.getLiveDocs(indexReader);
        int docs = indexReader.numDocs();
        LinkedHashMap<Long, double[]> featureCache = new LinkedHashMap<>(docs);

        TreeSet<DistanceFeature> features = new TreeSet<>();

        Document document;
        for (int i = 0; i < docs; i++) {
            // if (!(indexReader.hasDeletions() && !liveDocs.get(i))) {
            if (!indexReader.hasDeletions()) {
                document = indexReader.document(i);
                if (document.getField("id") != null) {
                    var id = Long.parseLong(document.getField("id").stringValue());
                    var date = LocalDate.parse(document.getField("date").stringValue(), DateTimeFormatter.ofPattern("yyyyMMdd"));
                    var featureValue = FeatureUtils.bytes2doubleArray(document.getField("feature").binaryValue().bytes, 512);
                    featureCache.put(id, featureValue);
                    DistanceFeature distanceFeature = new DistanceFeature();
                    distanceFeature.setId(id);
                    distanceFeature.setDistance(FeatureUtils.getDistance(feature, featureValue));
                    distanceFeature.setDate(date);
                    distanceFeature.setFeature(featureValue);
                    features.add(distanceFeature);
                }
            }
        }

        indexReader.close();

        return features.iterator().next();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(LuceneHelper.class);
}
