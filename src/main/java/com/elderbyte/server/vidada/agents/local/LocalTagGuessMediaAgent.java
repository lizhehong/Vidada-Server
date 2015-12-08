package com.elderbyte.server.vidada.agents.local;

import com.elderbyte.server.vidada.agents.MediaAgent;
import com.elderbyte.server.vidada.agents.MediaMetadataDto;
import com.elderbyte.server.vidada.media.MediaItem;
import com.elderbyte.server.vidada.media.MediaType;
import com.elderbyte.server.vidada.tags.Tag;
import com.elderbyte.server.vidada.tags.autoTag.ITagGuessingStrategy;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Uses local data to guess tags for a media item.
 *
 */
public class LocalTagGuessMediaAgent implements MediaAgent {

    private Supplier<ITagGuessingStrategy> tagGuessingStrategy;

    public LocalTagGuessMediaAgent(Supplier<ITagGuessingStrategy> tagGuessingStrategy){
        this.tagGuessingStrategy = tagGuessingStrategy;
    }

    @Override
    public MediaMetadataDto fetchMetadata(MediaItem media) {

        ITagGuessingStrategy strategy = tagGuessingStrategy.get();

        MediaMetadataDto metadataDto = new MediaMetadataDto();

        Set<Tag> foundTags = strategy.guessTags(media);

        foundTags.addAll(findTags(media));

        for (Tag t : foundTags) {
            metadataDto.getGenres().add(t.getName());
        }

        return metadataDto;
    }

    @Override
    public boolean canHandle(MediaItem media) {
        return true;
    }


    private Set<Tag> findTags(MediaItem media){

        Set<Tag> tags = new HashSet<>();

        if(media.getType() == MediaType.IMAGE){
            tags.add(Tag.buildTag("image").get());
        }

        if(media.getType() == MediaType.MOVIE){
            tags.add(Tag.buildTag("video").get());
        }

        return tags;
    }
}
