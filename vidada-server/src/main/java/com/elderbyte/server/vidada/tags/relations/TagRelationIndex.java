package com.elderbyte.server.vidada.tags.relations;

import com.elderbyte.common.NotSupportedException;
import com.elderbyte.server.vidada.tags.Tag;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Implementation of a Tag Relation Index.
 *
 * Tags can have a hierarchical relation towards each other, therefore
 * this implementation uses a Tree Structure for the Tag-Nodes.
 *
 *
 * @author IsNull
 *
 */
class TagRelationIndex {

    /***************************************************************************
     *                                                                         *
     * Private fields                                                          *
     *                                                                         *
     **************************************************************************/

    private static final Logger LOG = LogManager.getLogger(TagRelationIndex.class.getName());

    transient private final Map<Tag, TagNode> rootNodes = new HashMap<>(5000);


	/***************************************************************************
	 *                                                                         *
	 * Public API                                                              *
	 *                                                                         *
	 **************************************************************************/



	/**
	 * Add a relation between two tags.
	 * This will update this index accordingly.
	 *
	 * @param relation
	 */
	public void addRelation(TagRelation relation){
		switch (relation.getOperator()) {
		case Equal:
			setEqualityRelation(relation.getLeft(), relation.getRight());
			break;

		case IsParentOf:
			setParentRelation(relation.getLeft(), relation.getRight());
			break;

		default:
			throw new NotSupportedException("Unknown relation: " + relation);
		}
	}



	/**
	 * Returns all tags which are mutually related to the given tag.
	 * I.e. it will tags with synonymous meaning or tags
	 * which are specalisations of the given tag.
	 *
	 * @param tag
	 * @return
	 */
	public Set<Tag> getAllRelatedTags(Tag tag){
		Set<Tag> relatedTags = new HashSet<>();

		relatedTags.addAll(getAllEqualTags(tag));

		relatedTags.addAll(getSpecialisations(tag));

		relatedTags.add(tag);

		return relatedTags;
	}

	/**
	 * Returns all tags which as synonyms to the given tag
	 * @param tag
	 * @return
	 */
	Set<Tag> getAllEqualTags(Tag tag){
		TagNode node = findNode(tag);
		return node != null ? node.getSynonyms() : new HashSet<>();
	}

	/**
	 * Returns all tags which are hierarchical synonyms (specalisations)
	 * @param tag
	 * @return
	 */
	private Set<Tag> getSpecialisations(Tag tag){

		Set<Tag> specialisations = new HashSet<>();

		TagNode node = findNode(tag);

		if(node != null){
			for (TagNode child : node.getSpecialisations()) {
				specialisations.add(child.getTag());
				specialisations.addAll(getSpecialisations(child.getTag()));
			}
		}

		return specialisations;
	}


	/**
	 * Returns the master tag for the given tag.
	 * If there are two or more tags with an EQUAL relation, one tag is the master, the others are slaves (synonyms).
	 *
	 * @param tag
	 * @return
	 */
	public Tag getMasterTag(Tag tag){
		TagNode node = findNode(tag);
		return node != null ? node.getTag() : tag;
	}

	/**
	 * Checks if the given tag is a slave tag, that is, a synonym for a better alternative.
	 * @param tag
	 * @return
	 */
	public boolean isSlaveTag(Tag tag){
		Tag masterTag = getMasterTag(tag);
		return masterTag != null && !masterTag.equals(tag);
	}

    /**
     * Returns all master tags
     */
    public Collection<Tag> getMasterTags(){
        // Remove all synonyms from our tag list

        Set<Tag> masterTags = new HashSet<>(getAllTags());

        Iterator<Tag> allTagsIt = masterTags.iterator();
        while (allTagsIt.hasNext()) {
            if (isSlaveTag(allTagsIt.next())) {
                allTagsIt.remove();
            }
        }
        return masterTags;
    }


	public String toTreeString(){
		StringBuilder sb = new StringBuilder();
		Set<TagNode> addedNodes = new HashSet<>();

		for (Tag tag : rootNodes.keySet()) {
			TagNode node = findNode(tag);
			if(addedNodes.add(node)){ // ensure we print each node max once
				String tree = node.toTreeString( this );
				sb.append(tree);
			}
		}
		return sb.toString();
	}

	/***************************************************************************
	 *                                                                         *
	 * Private methods                                                         *
	 *                                                                         *
	 **************************************************************************/

	/**
	 * Introduce an equality relation between two tags
	 * This will in fact merge all the references of the two tags into the left one
	 * @param master
	 * @param slave
	 */
	private void setEqualityRelation(Tag master, Tag slave){
		TagNode masterNode = getNode(master);
		TagNode slaveNode = findNode(slave);

		if(slaveNode != null && !slaveNode.getSpecialisations().isEmpty()){
			mergeInto(masterNode, slaveNode);
		}
		rootNodes.put(slave, masterNode);
		masterNode.getSynonyms().add(slave);

		if(slaveNode != null){
			// Update the master-node with the now obsolete slave-node equality references
            masterNode.getSynonyms().addAll(slaveNode.getSynonyms());
		}
	}

	/**
	 * Merge the slave node into the master node
	 * @param master
	 * @param slave
	 */
	private void mergeInto(TagNode master, TagNode slave){
		Set<TagNode> masterChildren = master.getSpecialisations();
		masterChildren.addAll(slave.getSpecialisations());
	}

	private void setParentRelation(Tag parent, Tag child){
		TagNode parentNode = getNode(parent);
		TagNode childNode = getNode(child);
		parentNode.getSpecialisations().add(childNode);
	}

	/**
	 * Gets a TagNode for the given tag.
	 * This will create a new Node if no matching node is available
	 * @param tag
	 * @return
	 */
	private TagNode getNode(Tag tag){
		TagNode node = findNode(tag);
		if(node == null){
			node = new TagNode(tag);
			rootNodes.put(tag, node);
		}
		return node;
	}

	/**
	 * Finds a matching Node for the given Tag, but does not
	 * create a new Node.
	 * @param tag
	 * @return May return <code>null</code> if no matching {@link TagNode} exists
	 */
	private TagNode findNode(Tag tag){
		return rootNodes.get(tag);
	}

    private Collection<Tag> getAllTags(){
        return rootNodes.keySet();
    }

}
