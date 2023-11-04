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
import com.example.db_controller.IDGenerator;
import com.example.objects.Author;
import com.example.objects.Book;

public class MyParser {
    private Document curDoc = null;
    private String curXmlPath;
    private final String xsdPath;
    private IDGenerator authorGenerator = new IDGenerator("A");
    private IDGenerator bookGenerator = new IDGenerator("B");
    private DatabaseManager dbManager;

    public MyParser(String xsdPath, DatabaseManager dbManager) {
        this.xsdPath = xsdPath;
        this.dbManager = dbManager;
    }

    public IDGenerator getBookGenerator() {
        return bookGenerator;
    }

    public IDGenerator getAuthorGenerator() {
        return authorGenerator;
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

    private Element getAuthorById(String ID) {
        NodeList authors = curDoc.getElementsByTagName("author");
        for (int i = 0; i < authors.getLength(); i++) {
            Element author = (Element) authors.item(i);
            if (author.getAttribute("id").equals(ID)) {
                return author;
            }
        }
        return null;
    }

    private Element getBookById(String ID) {
        NodeList books = curDoc.getElementsByTagName("book");
        for (int i = 0; i < books.getLength(); i++) {
            Element book = (Element) books.item(i);
            if (book.getAttribute("id").equals(ID)) {
                return book;
            }
        }
        return null;
    }

    public boolean isOk() {
        if (curDoc != null) {
            return true;
        }
        return false;
    }

    public void getXml(String xmlPath) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlPath);
            if (validateXml(xmlPath)) {
                getIds(doc);
                curDoc = doc;
                curXmlPath = xmlPath;
                return;
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
            System.out.println(e.getMessage());
        }
        curDoc = null;
    }

    private void getIds(Document doc) {
        NodeList authors = doc.getElementsByTagName("author");
        for (int i = 0; i < authors.getLength(); i++) {
            authorGenerator.addId(((Element) authors.item(i)).getAttribute("id"));
        }
        NodeList books = doc.getElementsByTagName("book");
        for (int i = 0; i < books.getLength(); i++) {
            bookGenerator.addId(((Element) books.item(i)).getAttribute("id"));
        }
    }

    private Element convertAuthor(Author author) {
        Element newAuthor = curDoc.createElement("author");
        newAuthor.setAttribute("id", author.getId());
        newAuthor.setAttribute("firstname", author.getFirstName());
        newAuthor.setAttribute("lastname", author.getLastName());
        List<Book> books = author.getBooks();
        for (Book book : books) {
            newAuthor.appendChild(convertBook(book));
        }
        return newAuthor;
    }

    private Element convertBook(Book book) {
        Element newBook = curDoc.createElement("book");
        newBook.setAttribute("id", book.getId());
        Element elem = curDoc.createElement("name");
        elem.setTextContent(book.getName());
        newBook.appendChild(elem);
        elem = curDoc.createElement("price");
        elem.setTextContent(String.valueOf(book.getPrice()));
        newBook.appendChild(elem);
        elem = curDoc.createElement("genre");
        elem.setTextContent(book.getGenre());
        newBook.appendChild(elem);
        return newBook;
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

    public void addAuthor(Author author) {
        Element list = curDoc.getDocumentElement();
        list.appendChild(convertAuthor(author));
        writeXML();
    }

    private void writeXML() {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = transformerFactory.newTransformer();
            transformer.transform(new javax.xml.transform.dom.DOMSource(curDoc),
                    new StreamResult(new File(curXmlPath)));
        } catch (TransformerException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addBook(Book book) {
        Element author = getAuthorById(book.getAuthor());
        if (author == null) {
            System.out.println("\nAuthor not found in XNL!");
            return;
        }
        author.appendChild(convertBook(book));
        writeXML();
    }

    public void updateAuthor(Author author) {
        Element authorElem = getAuthorById(author.getId());
        if (authorElem != null) {
            authorElem.setAttribute("firstname", author.getFirstName());
            authorElem.setAttribute("lastname", author.getLastName());
            List<Book> books = author.getBooks();
            for (Book book : books) {
                updateBook(book, false);
            }
            writeXML();
            return;
        }
        System.out.println("\nAuthor not found");
    }

    public void updateBook(Book book, boolean andWrite) {
        Element bookElem = getBookById(book.getId());
        if (bookElem != null) {
            bookElem.getElementsByTagName("name").item(0).setTextContent(book.getName());
            bookElem.getElementsByTagName("price").item(0).setTextContent(String.valueOf(book.getPrice()));
            bookElem.getElementsByTagName("genre").item(0).setTextContent(book.getGenre());
            if (andWrite) {
                writeXML();
            }
            return;
        }
        System.out.println("\nBook " + book.getId() + " Not Found");
    }

    public List<Author> getAuthors(String toContain) {
        List<Author> res = new ArrayList<>();
        NodeList authors = curDoc.getElementsByTagName("author");
        for (int i = 0; i < authors.getLength(); i++) {
            Author authorObj = new Author(((Element) authors.item(i)).getAttribute("firstname"),
                    ((Element) authors.item(i)).getAttribute("lastname"),
                    ((Element) authors.item(i)).getAttribute("id"));
            if (authorObj.getFirstName().contains(toContain) || authorObj.getLastName().contains(toContain)) {
                NodeList booksList = ((Element) authors.item(i)).getElementsByTagName("book");
                for (int j = 0; j < booksList.getLength(); j++) {
                    Element book = (Element) booksList.item(j);
                    authorObj.addBook(
                            new Book(book.getAttribute("id"),
                                    book.getElementsByTagName("name").item(0).getTextContent(),
                                    Double.parseDouble(book.getElementsByTagName("price").item(0).getTextContent()),
                                    Book.Genre.valueOf(book.getElementsByTagName("genre").item(0).getTextContent()),
                                    authorObj.getId()));
                }
                res.add(authorObj);
            }
        }
        return res;
    }

    public List<Author> getAuthors(int minNum, int maxNum) {
        List<Author> res = new ArrayList<>();
        NodeList authors = curDoc.getElementsByTagName("author");
        for (int i = 0; i < authors.getLength(); i++) {
            Author authorObj = new Author(((Element) authors.item(i)).getAttribute("firstname"),
                    ((Element) authors.item(i)).getAttribute("lastname"),
                    ((Element) authors.item(i)).getAttribute("id"));
            NodeList booksList = ((Element) authors.item(i)).getElementsByTagName("book");
            if (booksList.getLength() >= minNum && booksList.getLength() <= maxNum) {
                for (int j = 0; j < booksList.getLength(); j++) {
                    Element book = (Element) booksList.item(j);
                    authorObj.addBook(
                            new Book(book.getAttribute("id"),
                                    book.getElementsByTagName("name").item(0).getTextContent(),
                                    Double.parseDouble(book.getElementsByTagName("price").item(0).getTextContent()),
                                    Book.Genre.valueOf(book.getElementsByTagName("genre").item(0).getTextContent()),
                                    authorObj.getId()));
                }
                res.add(authorObj);
            }
        }
        return res;
    }

    public List<Author> getAuthors() {
        List<Author> res = new ArrayList<>();
        NodeList authors = curDoc.getElementsByTagName("author");
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

    public List<Book> getBooks(String toContain) {
        List<Book> res = new ArrayList<>();
        NodeList booksList = curDoc.getElementsByTagName("book");
        for (int j = 0; j < booksList.getLength(); j++) {
            Element book = (Element) booksList.item(j);
            Element parent = (Element) book.getParentNode();
            if (book.getElementsByTagName("name").item(0).getTextContent().contains(toContain)) {
                res.add(
                        new Book(book.getAttribute("id"), book.getElementsByTagName("name").item(0).getTextContent(),
                                Double.parseDouble(book.getElementsByTagName("price").item(0).getTextContent()),
                                Book.Genre.valueOf(book.getElementsByTagName("genre").item(0).getTextContent()),
                                parent.getAttribute("id")));
            }
        }
        return res;
    }

    public List<Book> getBooks(double minPrice, double maxPrice) {
        List<Book> res = new ArrayList<>();
        NodeList booksList = curDoc.getElementsByTagName("book");
        for (int j = 0; j < booksList.getLength(); j++) {
            Element book = (Element) booksList.item(j);
            Element parent = (Element) book.getParentNode();
            if (Double.parseDouble(book.getElementsByTagName("price").item(0).getTextContent()) >= minPrice
                    && Double.parseDouble(book.getElementsByTagName("price").item(0).getTextContent()) <= maxPrice) {
                res.add(
                        new Book(book.getAttribute("id"), book.getElementsByTagName("name").item(0).getTextContent(),
                                Double.parseDouble(book.getElementsByTagName("price").item(0).getTextContent()),
                                Book.Genre.valueOf(book.getElementsByTagName("genre").item(0).getTextContent()),
                                parent.getAttribute("id")));
            }
        }
        return res;
    }

    public List<Book> getBooks(Book.Genre genre) {
        List<Book> res = new ArrayList<>();
        NodeList booksList = curDoc.getElementsByTagName("book");
        for (int j = 0; j < booksList.getLength(); j++) {
            Element book = (Element) booksList.item(j);
            Element parent = (Element) book.getParentNode();
            if (Book.Genre.valueOf(book.getElementsByTagName("genre").item(0).getTextContent()) == genre) {
                res.add(
                        new Book(book.getAttribute("id"), book.getElementsByTagName("name").item(0).getTextContent(),
                                Double.parseDouble(book.getElementsByTagName("price").item(0).getTextContent()),
                                Book.Genre.valueOf(book.getElementsByTagName("genre").item(0).getTextContent()),
                                parent.getAttribute("id")));
            }
        }
        return res;
    }

    public List<Book> getBooks() {
        List<Book> res = new ArrayList<>();
        NodeList booksList = curDoc.getElementsByTagName("book");
        for (int j = 0; j < booksList.getLength(); j++) {
            Element book = (Element) booksList.item(j);
            Element parent = (Element) book.getParentNode();
            res.add(
                    new Book(book.getAttribute("id"), book.getElementsByTagName("name").item(0).getTextContent(),
                            Double.parseDouble(book.getElementsByTagName("price").item(0).getTextContent()),
                            Book.Genre.valueOf(book.getElementsByTagName("genre").item(0).getTextContent()),
                            parent.getAttribute("id")));
        }
        return res;
    }

    public Author getAuthor(String ID) {
        Element author = getAuthorById(ID);
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

    public Book getBook(String ID) {
        Element book = getBookById(ID);
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

    public void deleteAuthor(String ID) {
        Element root = curDoc.getDocumentElement();
        List<Book> books = getAuthor(ID).getBooks();
        Element author = getAuthorById(ID);
        if (author != null) {
            root.removeChild(author);
            writeXML();
            authorGenerator.removeId(ID);
            for (Book book : books) {
                bookGenerator.removeId(book.getId());
            }
            return;
        }
        System.out.println("\nAuthor " + ID + " Not Found");
    }

    public void deleteBook(String ID) {
        Element book = getBookById(ID);
        if (book != null) {
            Element author = (Element) book.getParentNode();
            author.removeChild(book);
            writeXML();
            bookGenerator.removeId(ID);
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
                    dbManager.getAuthorGenerator().addId(curAuthor.getId());
                    break;
                case "book":
                    curBook.setAuthor(curAuthor.getId());
                    dbManager.getBookGenerator().addId(curBook.getId());
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
