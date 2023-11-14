package com.example.dbManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.example.Entities.Author;
import com.example.Entities.Book;

public class MyParser {

    // private DatabaseManager dbManager;
    private final String xsdPath = "D:\\Java\\DS_Labs\\Lab7\\demo\\src\\main\\java\\resources\\xml\\Schema.xsd";

    // public MyParser(DatabaseManager dbManager){
    // this.dbManager = dbManager;
    // }

    public List<Author> parseSAX(String xmlPath) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            if (validateXml(xmlPath)) {
                SAXParser parser = factory.newSAXParser();
                MyHandler handler = new MyHandler();
                parser.parse(xmlPath, handler);
                return handler.list;
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public boolean tryOpen(String path){
        File file = new File(path);
        if(file.exists() && file.canWrite() && file.canRead()){
            return true;
        }
        return false;
    }

    public void writeXML(String xmlPath, List<Author> list){
        try {
            XMLStreamWriter out = XMLOutputFactory.newInstance().createXMLStreamWriter(new OutputStreamWriter(new FileOutputStream(new File(xmlPath)), "utf-8"));
            out.writeStartDocument();
            out.writeStartElement("list");
            for(Author author : list){
                out.writeStartElement("author");
                out.writeAttribute("id", author.getId());
                out.writeAttribute("firstname", author.getFirstName());
                out.writeAttribute("lastname", author.getLastName());
                List<Book> books = author.getBooks();
                for(Book book : books){
                    out.writeStartElement("book");
                    out.writeAttribute("id", book.getId());
                    out.writeStartElement("name");
                    out.writeCData(book.getName());
                    out.writeEndElement();
                    out.writeStartElement("price");
                    out.writeCData(String.valueOf(book.getPrice()));
                    out.writeEndElement();
                    out.writeStartElement("genre");
                    out.writeCData(book.getGenre());
                    out.writeEndElement();
                    out.writeEndElement();
                }
                out.writeEndElement();
            }
            out.writeEndElement();
            out.writeEndDocument();
            out.close();
        } catch (FileNotFoundException | XMLStreamException | FactoryConfigurationError | UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        }
    }

    private boolean validateXml(String xmlPath) {
        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(new File(xsdPath));
            Validator validator = schema.newValidator();

            // Custom error handler to handle validation errors
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

    class MyHandler extends DefaultHandler {
        private StringBuilder curValue;
        private Author curAuthor;
        private Book curBook;
        private List<Author> list = new ArrayList<>();

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
                    list.add(curAuthor);
                    break;
                case "book":
                    curBook.setAuthor(curAuthor.getId());
                    curAuthor.addBook(curBook);
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
