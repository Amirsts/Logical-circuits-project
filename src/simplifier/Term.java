package simplifier;

import java.util.*;

// Represents a single logic term (e.g., "1-0") used in Quine–McCluskey simplification.
// Each term tracks its binary representation, a set of minterms it covers, and whether it has been used in combinations.
public class Term {
    private final String binary;            // Binary representation (with '-', e.g., "1-0")
    private final Set<Integer> minterms;    // Set of minterms covered by this term
    private boolean used;                   // Whether this term was used in combination

// Constructor for a Term object.
// @param binary the binary string (e.g., "01-", "1-1")
// @param minterms the set of decimal minterms this term represents
    public Term(String binary, Set<Integer> minterms) {
        this.binary = binary;
        this.minterms = new TreeSet<>(minterms);
        this.used = false;
    }

    public String getBinary() {
        return binary;
    }

    public Set<Integer> getMinterms() {
        return minterms;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

// Determines if this term can be combined with another term.
// Two terms can be combined if they differ by exactly one bit (ignoring '-').
// @param other the term to compare with
// @return true if they can be combined
    public boolean canCombineWith(Term other) {
        int diff = 0;
        for (int i = 0; i < binary.length(); i++) {
            char a = binary.charAt(i);
            char b = other.binary.charAt(i);
            if (a != b) {
                if (a != '-' && b != '-') {
                    diff++;
                } else {
                    return false;
                }
            }
        }
        return diff == 1;
    }

// Combines this term with another term into a new one with a '-' at the differing bit.
// @param other the term to combine with
// @return a new combined Term
    public Term combineWith(Term other) {
        StringBuilder combined = new StringBuilder();
        for (int i = 0; i < binary.length(); i++) {
            char a = binary.charAt(i);
            char b = other.binary.charAt(i);
            if (a == b) {
                combined.append(a);
            } else {
                combined.append('-');
            }
        }
        Set<Integer> newMinterms = new TreeSet<>(minterms);
        newMinterms.addAll(other.getMinterms());
        return new Term(combined.toString(), newMinterms);
    }

// Converts this binary term to a readable logic expression (e.g., A'B or AB'C).
// A = bit 0, B = bit 1, C = bit 2, etc.
// @return a string representing the term as a logic expression
    public String toExpression() {
        StringBuilder expr = new StringBuilder();
        char var = 'A';
        for (int i = 0; i < binary.length(); i++) {
            char c = binary.charAt(i);
            if (c == '-') continue;
            expr.append((char) (var + i));
            if (c == '0') expr.append("'");
        }
        return expr.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Term term) {
            return this.binary.equals(term.binary);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return binary.hashCode();
    }

    @Override
    public String toString() {
        return binary + " -> " + minterms;
    }
}
