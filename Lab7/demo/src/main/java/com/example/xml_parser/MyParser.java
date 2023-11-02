package com.example.xml_parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.example.db_controller.DatabaseManager;
import com.example.objects.Author;
import com.example.objects.Book;

public class MyParser {
    private final String xmlPath;
    private final String xsdPath;
    private DatabaseManager dbManager;

    public MyParser(String path, String xsdPath, DatabaseManager dbManager) {
        this.xmlPath = path;
        this.xsdPath = xsdPath;
        this.dbManager = dbManager;
    }

    public void parseSAX() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            MyHandler handler = new MyHandler();
            parser.parse(xmlPath, handler);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public Document getXml() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new File(xmlPath));
        } catch (SAXException | IOException | ParserConfigurationException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private Element convertAuthor(Author author, Document doc) {
        Element newAuthor = doc.createElement("author");
        newAuthor.setAttribute("id", author.getId());
        newAuthor.setAttribute("firstname", author.getFirstName());
        newAuthor.setAttribute("lastname", author.getLastName());
        List<Book> books = author.getBooks();
        for (Book book : books) {
            newAuthor.appendChild(convertBook(book, doc));
        }
        return newAuthor;
    }

    private Element convertBook(Book book, Document doc) {
        Element newBook = doc.createElement("book");
        newBook.setAttribute("id", book.getId());
        Element elem = doc.createElement("name");
        elem.setTextContent(book.getName());
        newBook.appendChild(elem);
        elem = doc.createElement("price");
        elem.setTextContent(String.valueOf(book.getPrice()));
        newBook.appendChild(elem);
        elem = doc.createElement("genre");
        elem.setTextContent(book.getGenre());
        newBook.appendChild(elem);
        return newBook;
    }

    private boolean validateXml(Document doc) {
        SchemaFactory factory = SchemaFactory.newInstance(xsdPath);
        try {
            Schema schema = factory.newSchema(new File(xsdPath));
            Validator validator = schema.newValidator();
            DefaultHandler errorHandler = new DefaultHandler() {
                @Override
                public void error(SAXParseException e) throws SAXException {
                    System.err.println("Validation Error: " + e.getMessage());
                }
            };

            validator.setErrorHandler(errorHandler);
            validator.validate(new StreamSource(new File(xmlPath)));
            return true;
        } catch (SAXException | IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public void addAuthor(Author author, Document doc) {
        if (doc == null) {
            return;
        }
        Element list = doc.getDocumentElement();
        list.appendChild(convertAuthor(author, doc));
        writeXML(doc);
    }

    private void writeXML(Document doc){
        if(validateXml(doc)){
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer;
            try {
                transformer = transformerFactory.newTransformer();
                transformer.transform(new javax.xml.transform.dom.DOMSource(doc), new StreamResult(new File(xmlPath)));
            } catch (TransformerException e) {
                System.out.println(e.getMessage());
            }   
        }else{
            System.out.println("\nXSD error: current state of XML file can not be validated by relative XSD, thus can not be updated");
        }
    }

    public void addBook(Book book, Document doc) {
        Element author = doc.getElementById(book.getAuthor());
        if (author == null) {
            System.out.println("\nAuthor not found in XNL!");
            return;
        }
        author.appendChild(convertBook(book, doc));
        writeXML(doc);
    }

    public void updateAuthor() {

    }

    public void updateBook() {

    }

    public void deleteAuthor() {

    }

    public void deleteBook() {

    }

    class MyHandler extends DefaultHandler {
        private StringBuilder curValue;
        private Author curAuthor;
        private Book curBook;

        @Override
        public void startElement(String uri, String lName, String qName, Attributes attr) throws SAXException {
            if (qName.equals("author")) {
                curAuthor = new Author(attr.getValue("firstname"), attr.getValue("lastname"), attr.getValue("id"));
            } else if (qName.equals("book")) {
                curBook = new Book(attr.getValue("id"));
            } else if (qName.equals("name") || qName.equals("price") || qName.equals("genre")) {
                curValue = new StringBuilder();
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            switch (qName) {
                case "author":
                    dbManager.addAuthor(curAuthor);
                    break;
                case "book":
                    curBook.setAuthor(curAuthor.getId());
                    dbManager.addBook(curBook);
                    break;
                case "name":
                    curBook.setName(curValue.toString());
                    break;
                case "price":
                    curBook.setPrice(Double.parseDouble(curValue.toString()));
                    break;
                case "genre":
                    curBook.setGenre(Book.Genre.valueOf(curValue.toString()));
                    break;
                default:
                    break;
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (curValue == null) {
                curValue = new StringBuilder();
            } else {
                curValue.append(ch, start, length);
            }
        }
    }
}
