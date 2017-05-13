package wci.frontend.pascal;

import wci.frontend.Source;
import wci.frontend.Token;

/**
 * PascalToken
 * <p>
 * Base class for Pascal token classes.
 */
public class PascalToken extends Token {
    /**
     * Constructor.
     *
     * @param source the source from where to fetch the token's characters.
     * @throws Exception if an error occurred.
     */
    protected PascalToken(Source source) throws Exception {
        super(source);
    }
}
