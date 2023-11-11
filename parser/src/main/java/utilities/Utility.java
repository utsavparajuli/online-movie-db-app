package utilities;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Utility {

     private static Map<String, String> genreNamesMap;

    public static float getPriceForMovies() {
        Random random = new Random();
        float minValue = 5.0f;
        float maxValue = 10.0f;

        return minValue + random.nextFloat() * (maxValue - minValue);
    }
//    {1=Action, 2=Adult, 3=Adventure, 4=Animation, 5=Biography, 6=Comedy, 7=Crime,
//    8=Documentary, 9=Drama, 10=Family, 11=Fantasy, 12=History, 13=Horror, 14=Music,
//    15=Musical, 16=Mystery, 17=Reality-TV, 18=Romance, 19=Sci-Fi, 20=Sport, 21=Thriller, 22=War, 23=Western}


    public static String getFullGenre(String s) {
        if (genreNamesMap.containsKey(s)) {
            return genreNamesMap.get(s);
        }

        return null;
    }
    //mapping of the cat -> full category name
    static {
        genreNamesMap = new HashMap<>();

        genreNamesMap.put("Comd", "Comedy");
        genreNamesMap.put("Epic", "Epic");
        genreNamesMap.put("S.F.", "Sci-Fi");
        genreNamesMap.put("stage musical", "Stage Musical");
        genreNamesMap.put("Dram.Actn", "Drama and Acting");
        genreNamesMap.put("Myst", "Mystery");
        genreNamesMap.put("susp", "Suspense");
        genreNamesMap.put("Axtn", "Action");
        genreNamesMap.put("SxFi", "Sci-Fi");
        genreNamesMap.put("Scfi", "Sci-Fi");
        genreNamesMap.put("dram", "Drama");
        genreNamesMap.put("DRAM", "Drama");
        genreNamesMap.put("DraM", "Drama");
        genreNamesMap.put("BiopP", "Biography");
        genreNamesMap.put("Actn", "Action");
        genreNamesMap.put("Muscl", "Musical");
        genreNamesMap.put("Hor", "Horror");
        genreNamesMap.put("Draam", "Drama");
        genreNamesMap.put("anti-Dram", "Anti-Drama");
        genreNamesMap.put("comd", "Comedy");
        genreNamesMap.put("actn", "Action");
        genreNamesMap.put("Expm", "Experimental");
        genreNamesMap.put("verite", "Verite");
        genreNamesMap.put("BioPP", "Biography");
        genreNamesMap.put("Musc", "Musical");
        genreNamesMap.put("Advt ", "Adventure");
        genreNamesMap.put("Psych Dram", "Psych Drama");
        genreNamesMap.put("BioG", "Biography");
        genreNamesMap.put("Sati", "Satire");
        genreNamesMap.put("Muscl ", "Musical");
        genreNamesMap.put("SciF", "Sci-Fi");
        genreNamesMap.put("Psyc", "Psych Drama");
        genreNamesMap.put("Kinky", "Kinky");
        genreNamesMap.put("Susp", "Suspense");
        genreNamesMap.put("porn", "Porn");
        genreNamesMap.put("Comdx", "Comedy");
        genreNamesMap.put("Weird", "Weird");
        genreNamesMap.put("Romt Dram", "Romantic Drama");
        genreNamesMap.put("Advt", "Adventure");
        genreNamesMap.put("Romt. Comd", "Romantic Comedy");
        genreNamesMap.put("Docu Dram", "Documentary");
        genreNamesMap.put("Hist", "History");
        genreNamesMap.put("surreal", "Surreal");
        genreNamesMap.put("Surr", "Surreal");
        genreNamesMap.put("Susp ", "Suspense");
        genreNamesMap.put("BioP ", "Biography");
        genreNamesMap.put("Romt ", "Romance");
        genreNamesMap.put("Allegory", "Allegory");
        genreNamesMap.put("Romt Actn", "Romantic Action");
        genreNamesMap.put("RomtAdvt", "Romantic Adventure");
        genreNamesMap.put("Horr ", "Horror");
        genreNamesMap.put("Act", "Action");
        genreNamesMap.put("ScFi", "Sci-Fi");
        genreNamesMap.put("BioP", "Biography");
        genreNamesMap.put("Docu", "Documentary");
        genreNamesMap.put("Porn ", "Porn");
        genreNamesMap.put("TV", "TV");
        genreNamesMap.put("Romtx", "Romance");
        genreNamesMap.put("Scat", "Scat");
        genreNamesMap.put("Horr", "Horror");
        genreNamesMap.put("Comd West", "Comedy West");
        genreNamesMap.put("romt", "Romantic");
        genreNamesMap.put("Docu ", "Documentary");
        genreNamesMap.put("Romt Comd", "Romantic Comedy");
        genreNamesMap.put("Mystp", "Mystery");
        genreNamesMap.put("Biop", "Biography");
        genreNamesMap.put("noir", "Noir");
        genreNamesMap.put("Faml", "Family");
        genreNamesMap.put("TVmini", "TV");
        genreNamesMap.put("Dramn", "Drama");
        genreNamesMap.put("Porb", "Porn");
        genreNamesMap.put("Noir Comd Romt", "Noir");
        genreNamesMap.put("Drama", "Drama");
        genreNamesMap.put("Comd Noir", "Noir");
        genreNamesMap.put("Dramd", "Drama");
        genreNamesMap.put("Cult", "Cultural");
        genreNamesMap.put("West1", "West");
        genreNamesMap.put("Porn", "Porn");
        genreNamesMap.put("Fant", "Fantasy");
        genreNamesMap.put("Ducu", "Documentary");
        genreNamesMap.put("Muusc", "Musical");
        genreNamesMap.put("musc", "Musical");
        genreNamesMap.put("Sctn", "Sci-Fi");
        genreNamesMap.put("Crim", "Crime");
        genreNamesMap.put("Romt Fant", "Romantic Fantasy");
        genreNamesMap.put("Viol", "Violence");
        genreNamesMap.put("Romt", "Romance");
        genreNamesMap.put("sports", "Sport");
        genreNamesMap.put("West", "West");
        genreNamesMap.put("Myst ", "Mystery");
        genreNamesMap.put("Bio", "Biography");
        genreNamesMap.put("fant", "Fantasy");
        genreNamesMap.put("Noir", "Noir");
        genreNamesMap.put("Noir Comd", "Noir");
        genreNamesMap.put("Dram Docu", "Drama");
        genreNamesMap.put("Dram", "Drama");
        genreNamesMap.put("Dicu", "Documentary");
        genreNamesMap.put("Duco", "Documentary");
    }
}
