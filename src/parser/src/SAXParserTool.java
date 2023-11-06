package parser.src;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.Year;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import parser.src.dto.Film;
import parser.src.dto.Genre;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class SAXParserTool extends DefaultHandler {

//    List<DirectorFilms> directorFilms;
    List<Film> filmsList;
    private String tempVal;

    //to maintain context
    private Film tempFilm;
//    private DirectorFilms tempDirFilm;
    private Genre tempGenre;

    PrintWriter outputWriter;
    PrintWriter movieInconsistencyReport;


    public SAXParserTool() {
//        directorFilms = new ArrayList<DirectorFilms>();
        filmsList = new ArrayList<Film>();
        try {
            movieInconsistencyReport = new PrintWriter("src/parser/movie_inconsistency.txt", StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void runExample() {
        parseDocument();
        printData();
        movieInconsistencyReport.close();
    }

    private void parseDocument() {
        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("src/parser/stanford-movies/mains243.xml", this);
//            sp.parse("src/parser/stanford-movies/sample.xml", this);


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
        try {
            outputWriter = new PrintWriter("src/parser/out.txt", StandardCharsets.UTF_8);

            outputWriter.println("No of Films read '" + filmsList.size() + "'.");

            Iterator<Film> it = filmsList.iterator();
            while (it.hasNext()) {
                outputWriter.println(it.next().toString());
            }
            outputWriter.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("film")) {
            //create a new instance of employee
            tempFilm = new Film();
        } else if (qName.equalsIgnoreCase("cat")) {
            //create a new instance of employee
            tempGenre = new Genre();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("dirn")) {
            if (tempVal.equals("")) {
                System.out.println("Dir empty");
            }
            tempFilm.setDirector(tempVal);
            //tempFilm.setDirector(tempVal);
        } else if (qName.equalsIgnoreCase("fid")) {
            if (tempVal.equals("")) {
                System.out.println("fid empty");
            }
            tempFilm.setId(tempVal);
        } else if (qName.equalsIgnoreCase("t")) {
            if (tempVal.equals("")) {
                System.out.println("title empty " + tempFilm.getId());

            }
            tempFilm.setTitle(tempVal);
        } else if (qName.equalsIgnoreCase("year")) {
            if (tempVal.equals("")) {
                System.out.println("year empty");
            }
            try {
                tempFilm.setYear(Year.parse(tempVal));
            } catch (Exception e) {
                System.out.println("inconsistent movie year " + tempVal) ;
            }
        } else if (qName.equalsIgnoreCase("film")) {
//            tempFilm.setDirector(tempDirFilm.getName());
            if (filmsList.contains(tempFilm)) {
                movieInconsistencyReport.println("DUPLICATE   " + tempFilm.toString());
            }
            else if (tempFilm.hasError()) {
                movieInconsistencyReport.println("NULL ITEMS  " + tempFilm.toString());
            }
            else if (tempFilm.getGenres().isEmpty()) {
                movieInconsistencyReport.println("GENRE ERROR " + tempFilm.toString());
            }
            else {
                filmsList.add(tempFilm);
            }
        } else if (qName.equalsIgnoreCase("cat")) {
            if (tempVal.equals("")) {
                System.out.println("cat empty");
            }
            tempGenre.setName(tempVal);
            tempFilm.addGenre(tempGenre);
        }


    }

    public static void main(String[] args) {
        SAXParserTool spe = new SAXParserTool();
        spe.runExample();
    }

}
