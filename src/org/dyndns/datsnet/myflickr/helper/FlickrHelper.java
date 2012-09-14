package org.dyndns.datsnet.myflickr.helper;

import javax.xml.parsers.ParserConfigurationException;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.REST;
import com.googlecode.flickrjandroid.RequestContext;
import com.googlecode.flickrjandroid.interestingness.InterestingnessInterface;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.photos.PhotosInterface;
import com.googlecode.flickrjandroid.uploader.Uploader;

public final class FlickrHelper {

    private static FlickrHelper instance = null;
    private static final String API_KEY = "e6ed45ec0ed168287b4fa21a4189e26c"; //$NON-NLS-1$
    public static final String API_SEC = "0ac6960f1389d061"; //$NON-NLS-1$

    private FlickrHelper() {

    }

    public static FlickrHelper getInstance() {
            if (instance == null) {
                    instance = new FlickrHelper();
            }

            return instance;
    }

    public Flickr getFlickr() {
            try {
                    Flickr f = new Flickr(API_KEY, API_SEC, new REST());
                    return f;
            } catch (ParserConfigurationException e) {
                    return null;
            }
    }

    public Flickr getFlickrAuthed(String token, String secret) {
            Flickr f = getFlickr();
            RequestContext requestContext = RequestContext.getRequestContext();
            OAuth auth = new OAuth();
            auth.setToken(new OAuthToken(token, secret));
            requestContext.setOAuth(auth);
            return f;
    }

    public InterestingnessInterface getInterestingInterface() {
            Flickr f = getFlickr();
            if (f != null) {
                    return f.getInterestingnessInterface();
            } else {
                    return null;
            }
    }

    public PhotosInterface getPhotosInterface() {
            Flickr f = getFlickr();
            if (f != null) {
                    return f.getPhotosInterface();
            } else {
                    return null;
            }
    }

    public Uploader getUploader() {
        Flickr f = getFlickr();
        if (f != null) {
                return f.getUploader();
        } else {
                return null;
        }
    }

}
