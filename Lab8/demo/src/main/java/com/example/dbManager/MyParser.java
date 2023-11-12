package com.example.dbManager;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.example.Entities.Author;
import com.example.Entities.Book;

public class MyParser {

    private DatabaseManager dbManager;

    public MyParser(DatabaseManager dbManager){
        this.dbManager = dbManager;
    }

    public void parseSAX(String xmlPath) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            MyHandler handler = new MyHandler();
            parser.parse(xmlPath, handler);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.out.println(e.getMessage());
        }
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
                    //dbManager.getAuthorGenerator().addId(curAuthor.getId());
                    break;
                case "book":
                    curBook.setAuthor(curAuthor.getId());
                    //dbManager.getBookGenerator().addId(curBook.getId());
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
