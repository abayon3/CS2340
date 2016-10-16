package frontpage.backend.profile;

import frontpage.backend.rest.RESTHandler;
import frontpage.backend.rest.RESTReport;
import frontpage.bind.profile.ProfileManagementException;
import frontpage.bind.profile.ProfileManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author willstuckey
 * @date 10/3/16
 * <p></p>
 */
public class RemoteProfileManager implements ProfileManager {
    public Map<String, String> getProfile(final String email,
                                          final String tok)
            throws ProfileManagementException {
        if (email == null || tok == null) {
            throw new ProfileManagementException("invalid credentials",
                    new NullPointerException());
        }

        Map<String, String> attribs = new HashMap<>(2);
        attribs.put("action", "GET");
        attribs.put("email", email);
        attribs.put("tok", tok);
        RESTReport rr = RESTHandler.apiRequest(
                RESTHandler.RestAction.POST,
                RESTHandler.ACCOUNT_PROFILE_ENTRY_POINT,
                attribs);
        if (rr.rejected()) {
            throw new ProfileManagementException(rr.toString());
        }

        if (!rr.success()) {
            throw new ProfileManagementException(rr.getResponseValue("message"));
        }

        Map<String, String> ret = new HashMap<>(rr.getResponseValues());
        for (String s : ret.keySet()) {
            if (ret.get(s) == null
                    || ret.get(s).length() == 0
                    || ret.get(s).equalsIgnoreCase("null")) {
                ret.put(s, "");
            }
        }

        return ret;
    }

    public boolean setProfile(final String email,
                              final String tok,
                              final Map<String, String> profiles)
            throws ProfileManagementException {
        if (email == null || tok == null || profiles == null) {
            throw new ProfileManagementException("invalid credentials",
                    new NullPointerException());
        }

        for (String s : profiles.keySet()) {
            if (profiles.get(s) == null
                    || profiles.get(s).length() == 0) {
                profiles.put(s, "NULL");
            }
        }

        Map<String, String> attribs = new HashMap<>(profiles);
        attribs.put("email", email);
        attribs.put("tok", tok);
        attribs.put("action", "PUT");
        RESTReport rr = RESTHandler.apiRequest(
                RESTHandler.RestAction.POST,
                RESTHandler.ACCOUNT_PROFILE_ENTRY_POINT,
                attribs);
        if (rr.rejected()) {
            throw new ProfileManagementException(rr.toString());
        }

        if (!rr.success()) {
            throw new ProfileManagementException(rr.getResponseValue("message"));
        }

        return true;
    }
}