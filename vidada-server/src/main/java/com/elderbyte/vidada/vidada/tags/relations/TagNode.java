package com.elderbyte.vidada.vidada.tags.relations;

import com.elderbyte.vidada.vidada.tags.Tag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Tree-Node wrapper for a Tag
 * @author IsNull
 *
 */
class TagNode {
    private final Tag tag;
    private final Set<TagNode> specialisations = new HashSet<>(); // children
    private final Set<Tag> synonyms = new HashSet<>();

    /**
     * Creates a new tag-node for the given tag
     * * @param tag
     */
    public TagNode(Tag tag){
        this.tag = tag;
        this.synonyms.add(tag);
    }

    /**
     * Gets all synonyms of this tag
     * @return
     */
    public Set<Tag> getSynonyms() {
        return synonyms;
    }

    /**
     * Gets the underling tag of this node.
     * @return
     */
    public Tag getTag(){
        return tag;
    }

    /**
     * Gets all specialisaitons (childern) of this node.
     * @return
     */
    public Set<TagNode> getSpecialisations() {
        return specialisations;
    }


    public String toTreeString(TagRelationIndex tagRelationIndex) {
        StringBuilder sb = new StringBuilder();
        toTreeString(sb, true, tagRelationIndex);
        return sb.toString();
    }

    @Override
    public String toString(){
        return tag.getName();
    }

    /**
     * Prints this node and all its children as tree as string
     * @param prefix
     * @param isTail
     * @param tagRelationIndex
     */
    private void toTreeString(StringBuilder prefix, boolean isTail, TagRelationIndex tagRelationIndex) {
        StringBuilder sb = prefix;
        sb
                .append(isTail ? "└── " : "├── ")
                .append(toString())
                .append(equalTagsString(tag, tagRelationIndex))
                .append(System.lineSeparator());

        List<TagNode> childrenList = new ArrayList<>(specialisations);

        for (int i = 0; i < childrenList.size() - 1; i++) {
            TagNode child = childrenList.get(i);
            prefix.append(isTail ? "    " : "│   ");
            child.toTreeString(prefix, false, tagRelationIndex);
        }

        if (childrenList.size() >= 1) {
            prefix.append(isTail ? "    " : "│   ");
            TagNode child = childrenList.get(specialisations.size() - 1);
            child.toTreeString(prefix, true, tagRelationIndex);
        }
    }

    private String equalTagsString(Tag tag, TagRelationIndex tagRelationIndex){
        String str = "";
        Set<Tag> equals = tagRelationIndex.getAllEqualTags(tag);
        if(equals != null){
            for (Tag t : equals) {
                if(!t.equals(tag))
                    str += " = " + t;
            }
        }
        return str;
    }
}
