package simplifier;

import java.util.List;

// Interface that defines the behavior of a logic function simplifier.
// Allows flexibility for different simplification methods (e.g., Quine–McCluskey, Karnaugh map).
public interface Simplifier {

// Simplifies a Boolean function based on a list of minterms.
// @param minterms a list of minterms (as integers)
// @return a list of simplified terms representing the minimized function
    List<Term> simplify(List<Integer> minterms);
}
