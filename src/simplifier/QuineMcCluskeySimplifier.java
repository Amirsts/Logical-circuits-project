package simplifier;

import java.util.*;

// Implements the Quine–McCluskey algorithm to simplify Boolean logic functions.
// Supports full minimization using Petrick's Method for covering remaining minterms.
public class QuineMcCluskeySimplifier implements Simplifier {

    private int numVariables;

// Simplifies a list of minterms into minimized logic terms using Quine–McCluskey method.
// @param minterms a list of minterms (e.g., 1, 3, 5, 7)
// @return a list of simplified Terms
    @Override
    public List<Term> simplify(List<Integer> minterms) {
        if (minterms.isEmpty()) return List.of();

        this.numVariables = getVariableCount(minterms);

// Step 1: Convert minterms to binary Terms
        List<Term> initialTerms = new ArrayList<>();
        for (int m : minterms) {
            String binary = toBinary(m, numVariables);
            initialTerms.add(new Term(binary, Set.of(m)));
        }

// Step 2: Find all prime implicants
        List<Term> primeImplicants = findPrimeImplicants(initialTerms);

// Step 3: Find essential prime implicants
        Set<Term> essentialPrimes = findEssentialPrimeImplicants(primeImplicants, minterms);

// Step 4: Cover remaining minterms using Petrick’s method
        Set<Integer> covered = new HashSet<>();
        for (Term term : essentialPrimes) {
            covered.addAll(term.getMinterms());
        }

        List<Integer> remaining = new ArrayList<>();
        for (int m : minterms) {
            if (!covered.contains(m)) remaining.add(m);
        }

        Set<Term> finalCover = new HashSet<>(essentialPrimes);

        if (!remaining.isEmpty()) {
            Set<Term> nonEssentialPrimes = new HashSet<>(primeImplicants);
            nonEssentialPrimes.removeAll(essentialPrimes);
            Set<Term> cover = petrickMethod(nonEssentialPrimes, remaining);
            finalCover.addAll(cover);
        }

        return new ArrayList<>(finalCover);
    }

// Calculates the number of variables based on the largest minterm.
    private int getVariableCount(List<Integer> minterms) {
        int max = Collections.max(minterms);
        return Integer.toBinaryString(max).length();
    }

// Converts a number to binary string with fixed length.
    private String toBinary(int num, int length) {
        String bin = Integer.toBinaryString(num);
        return "0".repeat(length - bin.length()) + bin;
    }

// Finds all prime implicants by iteratively combining terms.
    private List<Term> findPrimeImplicants(List<Term> terms) {
        List<Term> current = terms;
        List<Term> next = new ArrayList<>();
        List<Term> primes = new ArrayList<>();

        while (!current.isEmpty()) {
            boolean[] used = new boolean[current.size()];
            Map<String, Term> combinedMap = new HashMap<>();

            for (int i = 0; i < current.size(); i++) {
                for (int j = i + 1; j < current.size(); j++) {
                    Term t1 = current.get(i);
                    Term t2 = current.get(j);

                    // Check if terms can be combined
                    if (t1.canCombineWith(t2)) {
                        Term combined = t1.combineWith(t2);
                        String key = combined.getBinary();
                        combinedMap.putIfAbsent(key, combined);
                        used[i] = true;
                        used[j] = true;
                    }
                }
            }

            // Add unused terms as prime implicants
            for (int i = 0; i < current.size(); i++) {
                if (!used[i]) primes.add(current.get(i));
            }

            next = new ArrayList<>(combinedMap.values());
            current = next;
        }

        return primes;
    }

// Finds essential prime implicants from the list of all prime implicants.
    private Set<Term> findEssentialPrimeImplicants(List<Term> primes, List<Integer> minterms) {
        Map<Integer, List<Term>> chart = new HashMap<>();

// Build coverage chart: minterm -> list of covering terms
        for (int m : minterms) chart.put(m, new ArrayList<>());

        for (Term t : primes) {
            for (int m : t.getMinterms()) {
                if (chart.containsKey(m)) {
                    chart.get(m).add(t);
                }
            }
        }

// A term is essential if it is the only one covering a minterm.
        Set<Term> essential = new HashSet<>();
        for (int m : chart.keySet()) {
            List<Term> list = chart.get(m);
            if (list.size() == 1) {
                essential.add(list.get(0));
            }
        }

        return essential;
    }

// Uses Petrick's Method to find the minimal cover of remaining minterms
// using the non-essential prime implicants.
// @param candidates all non-essential prime implicants
// @param remainingMinterms list of minterms that are not yet covered
// @return a minimal set of terms that covers all remaining minterms
    private Set<Term> petrickMethod(Set<Term> candidates, List<Integer> remaining) {
        Map<Integer, List<Term>> table = new HashMap<>();

// Build a table: minterm -> list of terms that cover it
        for (int m : remaining) {
            List<Term> covers = new ArrayList<>();
            for (Term t : candidates) {
                if (t.getMinterms().contains(m)) {
                    covers.add(t);
                }
            }
            table.put(m, covers);
        }

// Initialize product of sums with first minterm's terms
        Set<Set<Term>> expression = new HashSet<>();
        for (Term t : table.get(remaining.get(0))) {
            expression.add(Set.of(t));
        }

// Multiply expressions (AND) with the rest of minterm clauses (OR terms)
        for (int i = 1; i < remaining.size(); i++) {
            Set<Set<Term>> next = new HashSet<>();
            for (Set<Term> product : expression) {
                for (Term t : table.get(remaining.get(i))) {
                    Set<Term> newProduct = new HashSet<>(product);
                    newProduct.add(t);
                    next.add(newProduct);
                }
            }
            expression = next;
        }

// Find the simplest product (i.e., the set with the least number of terms)
        int minSize = expression.stream().mapToInt(Set::size).min().orElse(Integer.MAX_VALUE);
        return expression.stream().filter(set -> set.size() == minSize).findFirst().orElse(Set.of());
    }
}
