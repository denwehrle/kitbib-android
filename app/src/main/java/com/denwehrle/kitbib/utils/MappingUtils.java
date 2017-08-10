package com.denwehrle.kitbib.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dennis Wehrle
 */
public class MappingUtils {

    public static Map<String, String> getLocation = new HashMap<String, String>() {
        {
            put("LSG", "KIT-Bibliothek");
            put("LST", "KIT-Bibliothek");
            put("LSW", "KIT-Bibliothek");
            put("LSM", "KIT-Bibliothek");
            put("LSN", "KIT-Bibliothek");
            put("LBS", "KIT-Bibliothek");
            put("FBC", "Fachbibliothek");
            put("LAF", "Fachbibliothek");
            put("FBW", "Fachbibliothek");
            put("FBP", "Fachbibliothek");
            put("FBI", "Fachbibliothek");
            put("FBM", "Fachbibliothek");
            put("FBA", "Fachbibliothek");
            put("BIB-N", "Campus Nord");
            put("FBH", "Campus Moltke");
            put("FBD", "DHBW Karlsruhe");
            put("BLB", "Badische Landesbibliothek");
            put("WIS", "Badische Landesbibliothek");
            put("TheaBib", "Bad. Staatstheater");
        }
    };

    public static Map<String, Integer> getIndex = new HashMap<String, Integer>() {
        {
            put("LSG", 0);
            put("LST", 1);
            put("LSW", 2);
            put("LSM", 3);
            put("LSN", 4);
            put("LBS", 5);
            put("FBC", 6);
            put("FBP", 7);
            put("LAF", 8);
            put("FBA", 9);
            put("FBI", 10);
            put("FBM", 11);
            put("FBW", 12);
            put("BIB-N", 13);
            put("FBH", 14);
            put("FBD", 15);
            put("BLB", 16);
            put("WIS", 17);
            put("TheaBib", 18);
        }
    };

    public static Map<String, String> getLongName = new HashMap<String, String>() {
        {
            put("LSG", "3. OG (N) Lesesaal Geisteswissenschaften");
            put("LST", "2. OG (N) Lesesaal Technik");
            put("LSW", "1. OG (N) Lesesaal Wirtschaftswissenschaften und Informatik");
            put("LSM", "3. OG (A) Lesesaal Medienzentrum");
            put("LSN", "2. OG (A) Lesesaal Naturwissenschaften");
            put("LBS", "1. OG (A) Lehrbuchsammlung");
            put("FBC", "Fachbibliothek Chemie");
            put("FBP", "Fachbibliothek Physik");
            put("LAF", "Lernzentrum am Fasanenschlösschen");
            put("FBA", "Fachbibliothek Architektur");
            put("FBI", "Fachbibliothek Informatik");
            put("FBM", "Fachbibliothek Mathematik");
            put("FBW", "Fachbibliothek Wirtschaftswissenschaftliche");
            put("BIB-N", "KIT-Bibliothek Nord");
            put("FBH", "Fachbibliothek HsKA");
            put("FBD", "Fachbibliothek DHBW Karlsruhe");
            put("BLB", "Hauptgebäude");
            put("WIS", "Wissenstor");
            put("TheaBib", "TheaBib & Bar");
        }
    };
}
