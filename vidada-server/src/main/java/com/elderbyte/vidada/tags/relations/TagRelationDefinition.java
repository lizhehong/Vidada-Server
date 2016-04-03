package com.elderbyte.vidada.tags.relations;

import com.elderbyte.vidada.tags.Tag;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.Map.Entry;

/**
 * Represents relations between Tags.
 *
 * Supported relations:
 *
 * --> Equality (synonyms)
 * --> Hierarchy < (ParentOf)
 *
 * See also @link{TagRelationOperator}
 *
 * @author IsNull
 *
 */
public class TagRelationDefinition {


	/***************************************************************************
	 *                                                                         *
	 * Private fields                                                          *
	 *                                                                         *
	 **************************************************************************/

    private static final Logger logger = LogManager.getLogger(TagRelationDefinition.class.getName());

	private Set<TagRelation> relations = new HashSet<>();
	private Map<TagRelationOperator, Set<TagRelation>> operatorCluster = new HashMap<>();
	private Map<String, Set<Tag>> namedGroups = new HashMap<>();


	transient private TagRelationIndex relationIndex = null;

	/***************************************************************************
	 *                                                                         *
	 * Constructor                                                             *
	 *                                                                         *
	 **************************************************************************/

	/**
	 * Creates an empty relation definition
	 */
	public TagRelationDefinition(){

	}


	/***************************************************************************
	 *                                                                         *
	 * Public API                                                              *
	 *                                                                         *
	 **************************************************************************/

	/**
	 *
	 * @param name If name is left null, the ANONYMOUS group will be used.
	 * @param tags
	 */
	public void addNamedGroupTags(String name, Set<Tag> tags){
		if(name == null) name = "ANONYMOUS";
		Set<Tag> group = namedGroups.get(name);
		if(group == null){
			group = new HashSet<>();
			namedGroups.put(name, group);
		}
		group.addAll(tags);
	}

	public void addRelation(TagRelation relation){
		relations.add(relation);
		getOperatorRelations(relation.getOperator()).add(relation);

		if(relationIndex != null){
			relationIndex.addRelation(relation);
		}
	}

	public void removeRelation(TagRelation relation){
		relations.remove(relation);
		getOperatorRelations(relation.getOperator()).remove(relation);

		relationIndex = null; // relation index is dirty
	}


	public Set<Tag> getAllRelatedTags(Tag tag){
		return getIndex().getAllRelatedTags(tag);
	}

	public boolean isSlaveTag(Tag tag) {
		return getIndex().isSlaveTag(tag);
	}


	public void merge(TagRelationDefinition relationDef) {
		relationIndex = null;
		for (TagRelation relation : relationDef.relations) {
			this.addRelation(relation);
		}
		for (Entry<String, Set<Tag>> namedGroup : relationDef.namedGroups.entrySet()) {
			this.addNamedGroupTags(namedGroup.getKey(), namedGroup.getValue());
		}
	}

	/**
	 * Returns a set of all tags in this definition
	 * @return
	 */
	public Set<Tag> getAllTags() {
		Set<Tag> allTags = new HashSet<Tag>();

		for (TagRelation relation : relations) {
			allTags.add(relation.getLeft());
			allTags.add(relation.getRight());
		}
		for (Set<Tag> tagGroup : namedGroups.values()) {
			allTags.addAll(tagGroup);
		}
		return allTags;
	}


	public String toTreeString(){
		return getIndex().toTreeString();
	}

	@Override
	public String toString(){
		String str = "";
		for (Tag t : getAllTags()) {
			str += t + ", ";
		}
		return str;
	}


	/***************************************************************************
	 *                                                                         *
	 * Private methods                                                         *
	 *                                                                         *
	 **************************************************************************/

	private TagRelationIndex getIndex(){
		if(relationIndex == null){
			relationIndex = buildTagRelationIndex();
		}
		return relationIndex;
	}

	private Set<TagRelation> getOperatorRelations(TagRelationOperator operator){
		Set<TagRelation> oprels = operatorCluster.get(operator);
		if(oprels == null){
			oprels = new HashSet<TagRelation>();
			operatorCluster.put(operator, oprels);
		}
		return oprels;
	}

	private TagRelationIndex buildTagRelationIndex(){
        logger.info("Building tag relation index...");
		TagRelationIndex index = new TagRelationIndex();

		Set<TagRelation> equalRelations = getOperatorRelations(TagRelationOperator.Equal);
		for (TagRelation tagRelation : equalRelations) {
			index.addRelation(tagRelation);
		}

		Set<TagRelation> hierarchyRelations = getOperatorRelations(TagRelationOperator.IsParentOf);
		for (TagRelation tagRelation : hierarchyRelations) {
			index.addRelation(tagRelation);
		}

		return index;
	}


    public Collection<Tag> getAllMasterTags() {
        return getIndex().getMasterTags();
    }
}
