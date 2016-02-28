package com.elderbyte.server.vidada.tags;

import archimedes.core.util.UriUtil;
import com.elderbyte.server.vidada.media.MediaItem;
import com.elderbyte.server.vidada.media.MovieMediaItem;
import com.elderbyte.server.vidada.media.libraries.MediaLibrary;
import com.elderbyte.server.vidada.media.source.MediaSource;
import com.elderbyte.server.vidada.tags.autoTag.ITagGuessingStrategy;
import com.elderbyte.server.vidada.tags.autoTag.KeywordBasedTagGuesser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Set;

/**
 *
 */
public class TagGuessingStrategyTest {



    private ITagGuessingStrategy testee;

    @Before
    public void init(){
        Set<Tag> knownTags = Tag.buildTags("car", "bmw", "audi", "action", "too.cool", "game.of.thrones", "720p", "1080p");
        testee = new KeywordBasedTagGuesser(knownTags);
    }


    @Test
    public void testNormalPath() throws URISyntaxException {

        MediaItem mediaItem = mediaWithPath("/max/action/first/my 720p movie.avi");

        Set<String> guessedTags = testee.guessTags(mediaItem);

        expectTag("action", guessedTags);
        expectTag("720p", guessedTags);

        expectNotTag("max", guessedTags);
        expectNotTag("first", guessedTags);
    }

    @Test
    public void testBracketPath() throws URISyntaxException {

        MediaItem mediaItem = mediaWithPath("/max/[action]/foobar/first/[star.wars]/my 720p movie.avi");

        Set<String> guessedTags = testee.guessTags(mediaItem);

        expectTag("action", guessedTags);
        expectTag("star.wars", guessedTags);
        expectTag("720p", guessedTags);

        expectNotTag("max", guessedTags);
        expectNotTag("first", guessedTags);
        expectNotTag("my", guessedTags);
    }

    @Test
    public void testBracketComplexPath() throws URISyntaxException {

        MediaItem mediaItem = mediaWithPath("/max/huhu [action star.wars pete]/abc.avi");

        Set<String> guessedTags = testee.guessTags(mediaItem);

        expectTag("action", guessedTags);
        expectTag("star.wars", guessedTags);
        expectTag("pete", guessedTags);

        expectNotTag("max", guessedTags);
        expectNotTag("huhu", guessedTags);
        expectNotTag("abc", guessedTags);
        expectNotTag("abc.avi", guessedTags);
    }

    @Test
    public void testBracketComplex2Path() throws URISyntaxException {

        MediaItem mediaItem = mediaWithPath("/max/huhu [action star.wars] max [pete]/1234/abc.avi");

        Set<String> guessedTags = testee.guessTags(mediaItem);

        expectTag("action", guessedTags);
        expectTag("star.wars", guessedTags);
        expectTag("pete", guessedTags);

        expectNotTag("max", guessedTags);
        expectNotTag("1234", guessedTags);
        expectNotTag("abc", guessedTags);
        expectNotTag("abc.avi", guessedTags);
    }



    @Test
    public void testComplexGuessing() throws URISyntaxException {

        MediaItem mediaItem = mediaWithPath("/john/[action]/max/action.01/nice too.cool/Game of Thrones (Season 1) [action super.hero 720p] cool.avi");

        Set<String> guessedTags = testee.guessTags(mediaItem);

        expectTag("action", guessedTags);

        expectTag("game.of.thrones", guessedTags);

        expectTag("too.cool", guessedTags);

        expectNotTag("/", guessedTags);
        expectNotTag("john", guessedTags);
        expectNotTag("max", guessedTags);
        expectNotTag("action.01", guessedTags);
    }

    private void expectTag(String expected, Set<String> tags){
        Assert.assertTrue("Tag " + expected + " was expected to be in set: " + tags.toString(), tags.contains( expected ) );
    }

    private void expectNotTag(String expected, Set<String> tags){
        Assert.assertTrue("Tag " + expected + " was NOT expected to be in set: " + tags.toString(), !tags.contains( expected ) );
    }

    private MediaItem mediaWithPath(String path){
        MediaLibrary moviesLibrary = new MediaLibrary("Movies", new File("/root/movies"));
        MediaSource source = new MediaSource(moviesLibrary, UriUtil.createUri(new File(path)));
        return new MovieMediaItem(source, "hash12345");
    }
}
