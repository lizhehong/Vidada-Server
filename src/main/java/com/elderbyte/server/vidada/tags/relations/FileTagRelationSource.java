package com.elderbyte.server.vidada.tags.relations;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;


public class FileTagRelationSource implements ITagRelationSource {

    private static final Logger logger = LogManager.getLogger(FileTagRelationSource.class.getName());


    private final File tagRelationFile;
    private final TagRelationDefinitionParser parser = new TagRelationDefinitionParser();

    public FileTagRelationSource(File tagRelationFile){
        this.tagRelationFile = tagRelationFile;
    }

    @Override
    public TagRelationDefinition buildTagRelation() {

        if(tagRelationFile != null && tagRelationFile.exists()){
            try {
                logger.info("Building tag-relation from file " + tagRelationFile);
                return parser.parse(tagRelationFile);
            } catch (IOException | TagRelationDefinitionParser.ParseException e) {
                logger.error("Failed to provide tag-relations from file '" + tagRelationFile + "'", e);
            }
        }else{
            logger.warn("Tag Relation File not found: '" + tagRelationFile + "'!");
        }

        return null;
    }
}
