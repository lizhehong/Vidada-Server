package com.elderbyte.vidada.domain.queries;



import com.elderbyte.vidada.domain.tags.Tag;

import java.util.Collection;
import java.util.Set;

/**
 * Helps to build tag expressions
 */
public class TagExpressionBuilder {

    private Collection<Tag> requiredTags;
    private Collection<Tag> blockedTags;

    private ITagExpander tagExpander;
    private boolean expandTags = false;

    /**
     * Hidden Constructor (use create() factory)
     */
    private TagExpressionBuilder(){ }

    /**
     * Creates a new TagExpressionBuilder
     * @return
     */
    public static TagExpressionBuilder create() {
        return new TagExpressionBuilder();
    }


    public TagExpressionBuilder requiredTags(Collection<Tag> requiredTags){
        this.requiredTags = requiredTags;
        return this;
    }

    public TagExpressionBuilder blockedTags(Collection<Tag> blockedTags){
        this.blockedTags = blockedTags;
        return this;
    }

    /**
     * Expand each tag in the expression with similar tags.
     * @param tagService Used to find related tags for expansion.
     * @return
     */
    public TagExpressionBuilder expandTags(ITagExpander tagService){
        expandTags = true;
        this.tagExpander = tagService;
        return this;
    }


    /**
     * Build the tag expression
     * @return
     */
    public Expression<Tag> build(){

        Expression<Tag> required = createTagExpression(requiredTags, false);
        Expression<Tag> blocked = createTagExpression(blockedTags, true);

        Expression<Tag> tagExpression = Expressions.and(required, blocked);
        return tagExpression;
    }

    private Expression<Tag> createTagExpression(Collection<Tag> conjunction, boolean not){

        if(conjunction == null || conjunction.isEmpty()) return null;

        final VariableReferenceExpression<Tag> mediaTags = Expressions.varReference("m.tags");

        ListExpression<Tag> tagConjunction = ListExpression.createConjunction();

        for (Tag t : conjunction) {

            Expression<Tag> tagExpression;

            if(expandTags){
                tagExpression = isTagExpandedMemberOfExpression(t, mediaTags);
            }else{
                tagExpression = isTagMemberOfExpression(t, mediaTags);
            }

            tagConjunction.add(
                    not
                            ? Expressions.not(tagExpression)
                            : tagExpression);

        }

        return tagConjunction;
    }

    /**
     * Create an [tag] 'isMemberOf' [tags] expression, for all related tags of the given one.
     * Example:
     * For tag := 'car', the following expression may be generated:
     *
     *  ('car' isMemberOf myTags) OR ('auto' isMemberOf myTags)
     *
     * @param tag The tag to check if he is part of the collection. This tag will be expanded.
     * @param tagCollectionReference A reference to a collection of tags
     * @return
     */
    private Expression<Tag> isTagExpandedMemberOfExpression(Tag tag, VariableReferenceExpression<Tag> tagCollectionReference){
        ListExpression<Tag> tagDisjunction =  ListExpression.createDisjunction();
        Set<Tag> relatedTags = tagExpander.getAllRelatedTags(tag);
        for (LiteralValueExpression<String> relatedTag : Expressions.literalStrings(relatedTags)) {
            tagDisjunction.add(Expressions.memberOf(relatedTag, tagCollectionReference));
        }
        return tagDisjunction;
    }

    /**
     * Create an [tag] 'isMemberOf' [tags] expression.
     * @param tag The tag to check if he is part of the tagCollectionReference
     * @param tagCollectionReference A reference to a collection of tags
     * @return
     */
    private Expression<Tag> isTagMemberOfExpression(Tag tag, VariableReferenceExpression<Tag> tagCollectionReference){
        return Expressions.memberOf(Expressions.literalString(tag.toString()), tagCollectionReference);
    }


}
