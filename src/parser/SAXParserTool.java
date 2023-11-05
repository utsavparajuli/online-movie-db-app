package parser;

import java.io.IOException;
import java.time.Year;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import dto.Film;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class SAXParserTool extends DefaultHandler {

    List<Film> films;
    private String tempVal;

    //to maintain context
    private Film tempFilm;

    public SAXParserTool() {
        films = new ArrayList<Film>();
    }

    public void runExample() {
        parseDocument();
        printData();
    }

    private void parseDocument() {
        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("../stanford-movies/sample.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Iterate through the list and print
     * the contents
     */
    private void printData() {

        System.out.println("No of Films '" + films.size() + "'.");

        Iterator<Film> it = films.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("directorfilms")) {
            //create a new instance of employee
            tempFilm = new Film();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("directorfilms")) {
            //add it to the list
            films.add(tempFilm);

        } else if (qName.equalsIgnoreCase("dirname")) {
            tempFilm.setDirector(tempVal);
        } else if (qName.equalsIgnoreCase("fid")) {
            tempFilm.setId(tempVal);
        } else if (qName.equalsIgnoreCase("t")) {
            tempFilm.setTitle(tempVal);
        } else if (qName.equalsIgnoreCase("year")) {
            tempFilm.setYear(Year.parse(tempVal));
        }


    }

    public static void main(String[] args) {
        SAXParserTool spe = new SAXParserTool();
        spe.runExample();
    }

}
