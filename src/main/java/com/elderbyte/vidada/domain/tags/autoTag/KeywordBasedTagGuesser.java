package com.elderbyte.vidada.domain.tags.autoTag;

import com.elderbyte.vidada.domain.media.MediaItem;
import com.elderbyte.vidada.domain.media.source.MediaSource;
import com.elderbyte.vidada.domain.tags.Tag;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

/**
 * This Tag-Guessing strategy finds matching tags based on keywords.
 *
 * @author IsNull
 *
 */
public class KeywordBasedTagGuesser  implements ITagGuessingStrategy {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private static final Logger logger = LogManager.getLogger(KeywordBasedTagGuesser.class.getName());

    private static final String splitRegEx = "\\W|_";
	private static final String splitPathRegex = "/|\\\\";
    private static final int MAX_RECOMBINATION_DEPTH = 4;

	private Collection<Tag> tags;

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

	/**
	 * Creates a new KeywordBasedTagGuesser
	 * @param tags
	 */
	public KeywordBasedTagGuesser(Collection<Tag> tags){
		this.tags = tags;
	}

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

	@Override
	public Set<Tag> guessTags(MediaItem media) {
		Set<Tag> matchingTags = new HashSet<>();

		final Set<String> possibleTags = getPossibleTagStrings(media);

		for (Tag tag : tags) {
			if(possibleTags.contains(tag.getName())){
				matchingTags.add(tag);
			}
		}

		return matchingTags;
	}

    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/

    /**
     * Returns a set of possible tag-words (tokens) derived from this media item.
     *
     * @param media
     * @return
     */
    private static Set<String> getPossibleTagStrings(MediaItem media) {
        Set<String> words = new HashSet<String>();
        Set<MediaSource> sources = media.getSources();

        for(MediaSource source : sources){
            if(source != null && source.getResourceLocation() != null) {
                String path = source.getResourceLocation().toString();
                try {
                    path = URLDecoder.decode(path, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    logger.error("Decoding " + path + " failed.",e);
                } catch (IllegalArgumentException e){
                    logger.error("Decoding " + path + " failed.",e);
                }
                words.addAll(getPossibleTagStrings(path));
            }
        }

        return words;
    }

    /**
     * Returns all possible String tokens which might be a Tag
     * @return
     */
	private static Set<String> getPossibleTagStrings(String path){

        Set<String> possibleTags = new HashSet<String>();

        path = path.toLowerCase();

        //split the path in node tokens
        String[] pathParts = path.split(splitPathRegex);

        for(String pathPart : pathParts ){
            if(pathPart.isEmpty()) continue;

            //split the part in single words
            String[] words = pathPart.split(splitRegEx);

            // each word can be a tag
            for (String word : words) {
                if(!word.isEmpty()) {
                    possibleTags.add(word);
                }
            }

            // Recombine ranges

            recombineTags(words, possibleTags, MAX_RECOMBINATION_DEPTH);
        }

		return possibleTags;
	}

    /**
     * Recombine neighbour words into connected tags.
     *
     * Example: "Game of Thrones" will generate:
     * ----------------
     * game.of
     * game.of.thrones
     * of.thrones
     * ----------------
     * @param words The base words to recombine
     * @param store The Set to which a recombination shall be appended
     * @param maxDeepness Recombination max length
     */
    private static void recombineTags(String[] words, Set<String> store, int maxDeepness){
        String combined;
        for (int i = 0; i < words.length; i++) {
            combined = words[i];
            if(!combined.isEmpty()) {
                for (int j = i + 1; j < words.length; j++) {

                    if(j-i > maxDeepness) break;

                    if(!words[j].isEmpty()) {
                        combined += "." + words[j];
                        store.add(combined);
                    }
                }
            }
        }
    }

}
