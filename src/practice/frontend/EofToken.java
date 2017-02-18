package practice.frontend;

/**
 * <h1>EofToken</h1>
 * <p>The generic end-of-file token.</p>
 */
public class EofToken extends Token {
    /**
     * Constructor.
     *
     * @param source the source from where to fetch subsequent characters.
     * @throws Exception if an error occurred.
     */
    public EofToken(Source source) throws Exception {
        super(source);
    }

    /**
     * Do nothing.Do not consume any source characters.
     *
     * @param source the source from where to fetch the token's characters.
     * @throws Exception if an error occurred.
     */
    @Override
    protected void extract(Source sout) throws Exception { }
}
