package uk.ac.ebi.uniprot.taxonomyservice.restful.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is able to build URL that will be in the response body of taxonomy rest
 *
 * Created by lgonzales on 15/04/16.
 */
public class URLUtil {

    private static final Logger logger = LoggerFactory.getLogger(URLUtil.class);

    public static String getCurrentURL(HttpServletRequest request){
        String currentURL = request.getRequestURL().toString();
        if (request.getQueryString() != null && !request.getQueryString().isEmpty()) {
            try {
                currentURL+= "?"+(URLDecoder.decode(request.getQueryString(),"UTF-8"));
            } catch (UnsupportedEncodingException e) {
                logger.error("Error encoding ErrorMessage.requestedURL: ",e);
            }
        }
        return currentURL;
    }

    public static String getTaxonomyIdBasePath(HttpServletRequest request){
        String currentURL = request.getRequestURL().toString();
        String contextPath = request.getContextPath()+request.getServletPath();
        currentURL = currentURL.substring(0, currentURL.indexOf(contextPath)+contextPath.length())+"/id/";
        return currentURL;
    }

    public static String getNewRedirectHeaderLocationURL(String currentURL,long oldId, long newId) {
        if(currentURL == null){
            return null;
        }else{
            return currentURL.replace(""+oldId,""+newId);
        }

    }


}
