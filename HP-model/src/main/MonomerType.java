package main;

/**
 * The chemical identity of a monomer,
 */
public enum MonomerType {
    H,P;
    public static MonomerType byChar(char c) {
        if ((c == 'H') | (c == 'h' )) return H;
        if ((c == 'P') | (c == 'p' )) return P;
        throw new RuntimeException("Weird parameter to MonomerType.byChar "+c);
    }
}
    

