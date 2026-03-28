package org.example.cec;

import java.text.Normalizer;

public interface NormalizeName {

    static String set(String s) {
        if (s == null) return "";
        // Normalize to composed form, replace NBSP with normal space, remove invisible chars
        String t = Normalizer.normalize(s, Normalizer.Form.NFKC);
        t = t.replace('\u00A0', ' ');                // NBSP -> normal space
        t = t.replaceAll("[\\u200B\\uFEFF\\p{Cf}]", ""); // zero-width + other format chars
        t = t.replaceAll("[*?]", "");                // keep existing removal of * and ?
        // Remove diacritics, collapse multiple whitespace and trim
        t = Normalizer.normalize(t, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        t = t.replaceAll("\\s+", " ").trim();
        return t.toUpperCase();
    }
}
