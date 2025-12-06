package com.bituan.ai_rag_document_search.service;

import com.bituan.ai_rag_document_search.exception.ServerException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class DocumentProcessingServiceImpl implements DocumentProcessingService {
    @Override
    public String extractText(MultipartFile file) {
        AutoDetectParser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        InputStream inputStream;

        try {
            inputStream = file.getInputStream();
            parser.parse(inputStream, handler, metadata);
        } catch (IOException | SAXException | TikaException e) {
            throw new ServerException(HttpStatusCode.valueOf(500), "Unable to extract text");
        }

        return handler.toString();
    }

    @Override
    public void chunkText(String text) {
        TokenTextSplitter splitter = TokenTextSplitter.builder().withChunkSize(400).build(); // Configure chunk size, overlap etc.
        Document doc = new Document(text);
        List<Document> chunks = splitter.apply(List.of(doc));
    }

    @Override
    public void generateEmbeddings(String text) {

    }

    private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        // Create a temporary file with a unique name
        File tempFile = File.createTempFile("upload-", multipartFile.getOriginalFilename());

        // Transfer the content of the MultipartFile to the new File
        multipartFile.transferTo(tempFile);

        // Delete the temporary file on JVM exit
        tempFile.deleteOnExit();

        return tempFile;
    }

    public String extractText(InputStream inputStream) throws Exception {
        AutoDetectParser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata(); // Optional: to extract metadata

        parser.parse(inputStream, handler, metadata);
        return handler.toString();
    }
}
