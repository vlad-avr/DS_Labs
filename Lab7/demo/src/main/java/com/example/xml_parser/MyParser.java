package com.example.xml_parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
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

    private Element getAuthorById(String ID, Document doc) {
        NodeList authors = doc.getElementsByTagName("author");
        for (int i = 0; i < authors.getLength(); i++) {
            Element author = (Element) authors.item(i);
            if (author.getAttribute("id").equals(ID)) {
                return author;
            }
        }
        return null;
    }

    private Element getBookById(String ID, Document doc) {
        NodeList books = doc.getElementsByTagName("book");
        for (int i = 0; i < books.getLength(); i++) {
            Element book = (Element) books.item(i);
            if (book.getAttribute("id").equals(ID)) {
                return book;
            }
        }
        return null;
    }

    public Document getXml() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlPath);
            return doc;
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

    public void addAuthor(Author author, Document doc) {
        Element list = doc.getDocumentElement();
        list.appendChild(convertAuthor(author, doc));
        writeXML(doc);
    }

    private void writeXML(Document doc) {
        if (validateXml(doc)) {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer;
            try {
                transformer = transformerFactory.newTransformer();
                transformer.transform(new javax.xml.transform.dom.DOMSource(doc), new StreamResult(new File(xmlPath)));
            } catch (TransformerException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println(
                    "\nXSD error: current state of XML file can not be validated by relative XSD, thus can not be updated");
        }
    }

    public void addBook(Book book, Document doc) {
        Element author = getAuthorById(book.getAuthor(), doc);
        if (author == null) {
            System.out.println("\nAuthor not found in XNL!");
            return;
        }
        author.appendChild(convertBook(book, doc));
        writeXML(doc);
    }

    public void updateAuthor(Author author, Document doc) {
        Element authorElem = getAuthorById(author.getId(), doc);
        if (authorElem != null) {
            authorElem.setAttribute("firstname", author.getFirstName());
            authorElem.setAttribute("lastname", author.getLastName());
            List<Book> books = author.getBooks();
            for (Book book : books) {
                updateBook(book, doc, false);
            }
            writeXML(doc);
            return;
        }
        System.out.println("\nAuthor not found");
    }

    public void updateBook(Book book, Document doc, boolean andWrite) {
        Element bookElem = getBookById(book.getId(), doc);
        if (bookElem != null) {
            bookElem.getElementsByTagName("name").item(0).setTextContent(book.getName());
            bookElem.getElementsByTagName("price").item(0).setTextContent(String.valueOf(book.getPrice()));
            bookElem.getElementsByTagName("genre").item(0).setTextContent(book.getGenre());
            if (andWrite) {
                writeXML(doc);
            }
            return;
        }
        System.out.println("\nBook " + book.getId() + " Not Found");
    }

    public List<Author> getAuthors(Document doc) {
        List<Author> res = new ArrayList<>();
        NodeList authors = doc.getElementsByTagName("author");
        for (int i = 0; i < authors.getLength(); i++) {
            Author authorObj = new Author(((Element) authors.item(i)).getAttribute("firstname"),
                    ((Element) authors.item(i)).getAttribute("lastname"),
                    ((Element) authors.item(i)).getAttribute("id"));
            NodeList booksList = ((Element) authors.item(i)).getElementsByTagName("book");
            for (int j = 0; j < booksList.getLength(); j++) {
                Element book = (Element) booksList.item(j);
                authorObj.addBook(
                        new Book(book.getAttribute("id"), book.getElementsByTagName("name").item(0).getTextContent(),
                                Double.parseDouble(book.getElementsByTagName("price").item(0).getTextContent()),
                                Book.Genre.valueOf(book.getElementsByTagName("genre").item(0).getTextContent()),
                                authorObj.getId()));
            }
            res.add(authorObj);
        }
        return res;
    }

    public List<Book> getBooks(Document doc) {
        List<Book> res = new ArrayList<>();
        NodeList booksList = doc.getElementsByTagName("book");
        for (int j = 0; j < booksList.getLength(); j++) {
            Element book = (Element) booksList.item(j);
            Element parent = (Element)book.getParentNode();
            res.add(
                    new Book(book.getAttribute("id"), book.getElementsByTagName("name").item(0).getTextContent(),
                            Double.parseDouble(book.getElementsByTagName("price").item(0).getTextContent()),
                            Book.Genre.valueOf(book.getElementsByTagName("genre").item(0).getTextContent()),
                            parent.getAttribute("id")));
        }
        return res;
    }

    public Author getAuthor(String ID, Document doc) {
        Element author = getAuthorById(ID, doc);
        if (author != null) {
            Author authorObj = new Author(author.getAttribute("firstname"),
                    author.getAttribute("lastname"), ID);
            NodeList booksList = author.getElementsByTagName("book");
            for (int i = 0; i < booksList.getLength(); i++) {
                Element book = (Element) booksList.item(i);
                authorObj.addBook(
                        new Book(book.getAttribute("id"), book.getElementsByTagName("name").item(0).getTextContent(),
                                Double.parseDouble(book.getElementsByTagName("price").item(0).getTextContent()),
                                Book.Genre.valueOf(book.getElementsByTagName("genre").item(0).getTextContent()),
                                authorObj.getId()));
            }
            return authorObj;
        }
        System.out.println("\nAuthor " + ID + " Not Found");
        return null;
    }

    public Book getBook(String ID, Document doc) {
        Element book = getBookById(ID, doc);
        if (book != null) {
            Element parent = (Element) book.getParentNode();
            Book bookObj = new Book(book.getAttribute("id"), book.getElementsByTagName("name").item(0).getTextContent(),
                    Double.parseDouble(book.getElementsByTagName("price").item(0).getTextContent()),
                    Book.Genre.valueOf(book.getElementsByTagName("genre").item(0).getTextContent()),
                    parent.getAttribute("id"));
            return bookObj;
        }
        System.out.println("\nBook " + ID + " Not Found");
        return null;
    }

    public void deleteAuthor(String ID, Document doc) {
        Element root = doc.getDocumentElement();
        Element author = getAuthorById(ID, doc);
        if (author != null) {
            root.removeChild(author);
            writeXML(doc);
            return;
        }
        System.out.println("\nAuthor " + ID + " Not Found");
    }

    public void deleteBook(String ID, Document doc) {
        Element book = getBookById(ID, doc);
        if (book != null) {
            Element author = (Element) book.getParentNode();
            author.removeChild(book);
            writeXML(doc);
            return;
        }
        System.out.println("\nBook " + ID + " Not Found");
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
                    dbManager.getAuthorsGenerator().addId(curAuthor.getId());
                    break;
                case "book":
                    curBook.setAuthor(curAuthor.getId());
                    dbManager.getBooksGenerator().addId(curBook.getId());
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
