package com.elderbyte.server.vidada.tags.autoTag;

import com.elderbyte.server.vidada.agents.MediaAgent;
import com.elderbyte.server.vidada.media.MediaItem;
import com.elderbyte.server.vidada.media.source.MediaSource;
import com.elderbyte.server.vidada.tags.Tag;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final String splitRegEx = "[^a-zA-Z\\d]";
    private static final String splitRegExWithoutDot = "[^a-zA-Z\\d\\.]";
	private static final String splitPathRegex = "/|\\\\";
    private static final Pattern bracketMatchRegex = Pattern.compile("\\[(.*)\\]");

    private static final int MAX_RECOMBINATION_DEPTH = 4;

	private Set<String> knownTags = new HashSet<>();

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

	/**
	 * Creates a new KeywordBasedTagGuesser
	 * @param knownTags
	 */
	public KeywordBasedTagGuesser(Collection<Tag> knownTags){
        for (Tag knownTag : knownTags) {
            this.knownTags.add(knownTag.getName());
        }

        logger.info(String.format("Creating Tag guesser with %s known tags!", knownTags.size()));
	}

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

	@Override
	public Set<String> guessTags(MediaItem media) {
		Set<String> matchingTags = new HashSet<>();

		final Set<String> possibleTags = getPossibleTagStrings(media);

        for (String possibleTag : possibleTags) {
            if(knownTags.contains(possibleTag)){
                matchingTags.add(possibleTag);
            }
        }

        Set<String> tags = extractTagBrackets(media);

        matchingTags.addAll(tags);

		return matchingTags;
	}

    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/


    /**
     * Supports file names with tag brackets such as: 'Im a Video [1080p star.wars ep1]'
     * In contrast to the other guessed tags, tags in the brackets are always added, no matter if the tag is new.
     * @return
     */
    private Set<String> extractTagBrackets(MediaItem media){
        Set<String> tags = new HashSet<>();

        for(MediaSource source : media.getSources()){
            String path = mediaSourceToString(source);

            // Now find contents of all brackets [...]
            Matcher m = bracketMatchRegex.matcher(path);
            while (m.find()) {
                String tagsString = m.group(1);
                String[] rawTags = tagsString.split(splitRegExWithoutDot);
                for (String rawTag : rawTags ) {
                    if(!rawTag.isEmpty() && rawTag.length() > 2) {
                        tags.add(rawTag);
                    }
                }
            }
        }
        return tags;
    }


    /**
     * Returns a set of possible tag-words (tokens) derived from this media item.
     *
     * @param media
     * @return
     */
    private static Set<String> getPossibleTagStrings(MediaItem media) {
        Set<String> words = new HashSet<>();
        Set<MediaSource> sources = media.getSources();

        for (MediaSource source : sources) {
            String path = mediaSourceToString(source);
            words.addAll(getPossibleTagStrings(path));
        }
        return words;
    }


    private static String mediaSourceToString(MediaSource source){
        if (source != null && source.getResourceLocation() != null) {
            String path = source.getResourceLocation().toString();
            try {
                path = URLDecoder.decode(path, "utf-8");
            } catch (UnsupportedEncodingException e) {
                logger.error("Decoding " + path + " failed.", e);
            } catch (IllegalArgumentException e) {
                logger.error("Decoding " + path + " failed.", e);
            }
           return path;
        }
        return "";
    }

    /**
     * Returns all possible String tokens which might be a Tag
     * @return
     */
	private static Set<String> getPossibleTagStrings(String path){

        Set<String> possibleTags = new HashSet<>();

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
