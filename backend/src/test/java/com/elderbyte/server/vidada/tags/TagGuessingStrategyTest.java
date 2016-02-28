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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

/**
 *
 */
public class TagGuessingStrategyTest {



    private ITagGuessingStrategy testee;

    @Before
    public void init(){
        Set<Tag> tags = Tag.buildTags("car", "bmw", "audi", "action", "too.cool", "game.of.thrones", "720p", "1080p");
        testee = new KeywordBasedTagGuesser(tags);
    }

    @Test
    public void testCovers() throws URISyntaxException {

        MediaLibrary moviesLibrary = new MediaLibrary("Movies", new File("/root/movies"));
        MediaSource source = new MediaSource(moviesLibrary, UriUtil.createUri(new File("/john/action/nice too.cool/Game of Thrones (Season 1) [action super.hero 720p] cool.avi")));

        MediaItem mediaItem = new MovieMediaItem(source, "hash12345");

        Set<String> guessedTags = testee.guessTags(mediaItem);

        expectTag("action", guessedTags);

        expectTag("game.of.thrones", guessedTags);

        expectTag("too.cool", guessedTags);

        expectNotTag("/", guessedTags);
        expectNotTag("john", guessedTags);
    }

    private void expectTag(String expected, Set<String> tags){
        Assert.assertTrue("Tag " + expected + " was expected to be in set: " + tags.toString(), tags.contains( expected ) );
    }

    private void expectNotTag(String expected, Set<String> tags){
        Assert.assertTrue("Tag " + expected + " was NOT expected to be in set: " + tags.toString(), !tags.contains( expected ) );
    }
}
