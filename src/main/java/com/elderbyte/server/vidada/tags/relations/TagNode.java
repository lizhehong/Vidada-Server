package com.elderbyte.server.vidada.tags.relations;

import archimedes.core.util.Lists;
import com.elderbyte.server.vidada.tags.Tag;

import java.util.Collection;
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
    private final Set<TagNode> children = new HashSet<>();
    private final Set<Tag> synonyms = new HashSet<>();

    public TagNode(Tag tag){
        this.tag = tag;
        this.addSynonym(tag);
    }

    public Set<Tag> getSynonyms() {
        return synonyms;
    }

    public Tag getTag(){
        return tag;
    }

    public Set<TagNode> getChildren() {
        return children;
    }

    public void addSynonym(Tag tag){
        synonyms.add(tag);
    }

    public void addSynonyms(Collection<Tag> synonyms) {
        synonyms.addAll(synonyms);
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

    private void toTreeString(StringBuilder prefix, boolean isTail, TagRelationIndex tagRelationIndex) {
        StringBuilder sb = prefix;
        sb
                .append(isTail ? "└── " : "├── ")
                .append(toString())
                .append(equalTagsString(tag, tagRelationIndex))
                .append(System.lineSeparator());

        List<TagNode> childrenList = Lists.toList(children);

        for (int i = 0; i < childrenList.size() - 1; i++) {
            TagNode child = childrenList.get(i);
            prefix.append(isTail ? "    " : "│   ");
            child.toTreeString(prefix, false, tagRelationIndex);
        }

        if (childrenList.size() >= 1) {
            prefix.append(isTail ? "    " : "│   ");
            TagNode child = childrenList.get(children.size() - 1);
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
