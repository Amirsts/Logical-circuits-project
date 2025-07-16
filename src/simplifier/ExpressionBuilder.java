package simplifier;

import java.util.List;
import java.util.stream.Collectors;

// A helper class to convert a list of Terms into a human-readable Boolean SOP expression.
public class ExpressionBuilder {

// Builds a simplified Boolean SOP expression string from a list of logic terms.
// @param terms the list of simplified logic terms
// @return the SOP string (e.g., A'B + BC')
    public static String buildSOP(List<Term> terms) {
        if (terms == null || terms.isEmpty()) {

            // If the result is empty, function is always 0
            return "0";
        }

        return terms.stream()
                .map(Term::toExpression)
                .filter(expr -> !expr.isEmpty())
                .collect(Collectors.joining(" + "));
    }
}
