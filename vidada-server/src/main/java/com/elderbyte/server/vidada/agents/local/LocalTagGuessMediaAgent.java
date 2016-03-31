package com.elderbyte.server.vidada.agents.local;

import com.elderbyte.server.vidada.agents.MediaAgent;
import com.elderbyte.server.vidada.agents.MediaMetadataDto;
import com.elderbyte.server.vidada.media.MediaItem;
import com.elderbyte.server.vidada.media.MediaType;
import com.elderbyte.server.vidada.tags.autoTag.CachedTagGuessingBuildService;
import com.elderbyte.server.vidada.tags.autoTag.ITagGuessingStrategy;

import java.util.HashSet;
import java.util.Set;

/**
 * Uses local data to guess tags for a media item.
 *
 */
public class LocalTagGuessMediaAgent implements MediaAgent {

    private CachedTagGuessingBuildService tagGuessingStrategy;

    public LocalTagGuessMediaAgent(CachedTagGuessingBuildService tagGuessingStrategy){
        this.tagGuessingStrategy = tagGuessingStrategy;
    }

    @Override
    public MediaMetadataDto fetchMetadata(MediaItem media) {

        ITagGuessingStrategy strategy = tagGuessingStrategy.get();

        MediaMetadataDto metadataDto = new MediaMetadataDto();

        Set<String> foundTags = strategy.guessTags(media);

        foundTags.addAll(findTags(media));

        for (String t : foundTags) {
            metadataDto.getGenres().add(t);
        }

        return metadataDto;
    }

    @Override
    public boolean canHandle(MediaItem media) {
        return true;
    }

    @Override
    public void refresh() {
        tagGuessingStrategy.refresh();
    }


    private Set<String> findTags(MediaItem media) {

        Set<String> tags = new HashSet<>();

        if (media.getType() == MediaType.IMAGE) {
            tags.add("image");
        }

        if (media.getType() == MediaType.MOVIE) {
            tags.add("video");
        }

        return tags;

    }


}
