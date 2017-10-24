package uk.ac.ebi.uniprot.taxonomyservice.restful.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.servlet.http.HttpServletRequest;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This class will make sure that URL are being generated correctly in TaxonomyNode response object.
 *
 * Created by lgonzales on 22/04/16.
 */
public class URLUtilTest {

    @Test
    public void getCurrentURLWithoutQueryParameter() {
        StringBuffer bufferedURL = new StringBuffer("https://ebi.ac.uk/uniprot/services/restful/taxonomy/id/12345");
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURL()).thenReturn(bufferedURL);
        when(request.getQueryString()).thenReturn(null);

        String currentURL = URLUtil.getCurrentURL(request);

        assertThat(currentURL,equalTo(bufferedURL.toString()));
    }

    @Test
    public void getCurrentURLWithQueryParameter() {
        StringBuffer bufferedURL = new StringBuffer("https://ebi.ac.uk/uniprot/services/restful/taxonomy/relationship");
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURL()).thenReturn(bufferedURL);
        when(request.getQueryString()).thenReturn("from=100&to=200");

        String currentURL = URLUtil.getCurrentURL(request);
        assertThat(currentURL,notNullValue());
        assertThat(currentURL,equalTo(bufferedURL.toString()+"?from=100&to=200"));
    }


    @Test
    public void getCurrentURLWithQueryParameterEncoded() throws UnsupportedEncodingException {
        StringBuffer bufferedURL = new StringBuffer("https://ebi.ac.uk/uniprot/services/restful/taxonomy/relationship");
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURL()).thenReturn(bufferedURL);
        when(request.getQueryString()).thenReturn(URLEncoder.encode("from=100&to=200","UTF-8"));

        String currentURL = URLUtil.getCurrentURL(request);
        assertThat(currentURL,notNullValue());
        assertThat(currentURL,equalTo(bufferedURL.toString()+"?from=100&to=200"));
    }

    @Test
    public void getTaxonomyIdBasePath() {
        StringBuffer bufferedURL = new StringBuffer("https://ebi.ac.uk/uniprot/services/restful/taxonomy/relationship");
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURL()).thenReturn(bufferedURL);

        String basePath = URLUtil.getTaxonomyIdBasePath(request);
        assertThat(basePath,notNullValue());
        assertThat(basePath,equalTo("https://ebi.ac.uk/uniprot/services/restful/taxonomy/id/"));
    }

    @Test
    public void getNewRedirectHeaderLocationURL() {
        String currentURL = "https://ebi.ac.uk/uniprot/services/restful/taxonomy/id/12345";
        String redirectURL = URLUtil.getNewRedirectHeaderLocationURL(currentURL,null,12345,54321);
        assertThat(redirectURL,notNullValue());
        assertThat(redirectURL,equalTo("https://ebi.ac.uk/uniprot/services/restful/taxonomy/id/54321"));
    }

    @Test
    public void getNewRedirectHeaderLocationURLFromAndToParameters() {
        String currentURL = "https://ebi.ac.uk/uniprot/services/restful/taxonomy/relationship?from=9&to=99";
        String redirectURL = URLUtil.getNewRedirectHeaderLocationURL(currentURL,"from",9,999);
        redirectURL = URLUtil.getNewRedirectHeaderLocationURL(redirectURL,"to",99,9999);
        assertThat(redirectURL,notNullValue());
        assertThat(redirectURL,equalTo("https://ebi.ac" +
                ".uk/uniprot/services/restful/taxonomy/relationship?from=999&to=9999"));
    }

    @Test
    public void getNewRedirectHeaderLocationURLWithNullCurrentURL() {
        String redirectURL = URLUtil.getNewRedirectHeaderLocationURL(null,null,0,0);
        assertThat(redirectURL,nullValue());
    }
}