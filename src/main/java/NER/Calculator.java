/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NER;

/**
 *
 * @author n
 */
import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;

@BuildParseTree 
class CalculatorParser extends BaseParser<Object> {

    Rule Expression() {
        return Sequence(
            Term(),
            ZeroOrMore(AnyOf("+-"), Term())
        );
    }

    Rule Term() {
        return Sequence(
            Factor(),
            ZeroOrMore(AnyOf("*/"), Factor())
        );
    }

    Rule Factor() {
        return FirstOf(
            Number(),
            Sequence('(', Expression(), ')')
        );
    }

    Rule Number() {
        return OneOrMore(CharRange('0', '9'));
    }
}