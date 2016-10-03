package frontpage.bind.auth;

/**
 * @author willstuckey
 * <p></p>
 */
public interface UserManager {
    /**
     * attempts to authenticate user
     * @param email email
     * @param tok any token capable of authenticating the email
     * @return if security context is valid, tok for future auth
     *         if security context is invalid, empty string
     * @throws InvalidCredentialsException if credentials are invalid
     * @throws UserAuthenticationException if other errors occur while trying
     *                                     to authenticate credentials
     */
    String authenticateUser(final String email, final String tok)
            throws UserAuthenticationException, InvalidDataException;

    /**
     * creates a user
     * @param un username
     * @param pw password
     * @param email email
     * @param firstname firstname
     * @param lastname lastname
     * @return success
     * @throws InvalidDataException
     */
    boolean createUser(String un, char[] pw, String email, String firstname,
                       String lastname) throws InvalidDataException;
}