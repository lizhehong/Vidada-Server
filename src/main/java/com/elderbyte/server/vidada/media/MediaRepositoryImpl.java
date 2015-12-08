package com.elderbyte.server.vidada.media;

import archimedes.core.data.pagination.ListPage;
import archimedes.core.exceptions.NotImplementedException;
import archimedes.core.io.locations.ResourceLocation;
import com.elderbyte.server.vidada.media.libraries.MediaLibrary;
import com.elderbyte.server.vidada.tags.Tag;
import com.elderbyte.server.vidada.queries.JPQLExpressionCodeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.List;

/**
 * Created by IsNull on 19.04.15.
 */
public class MediaRepositoryImpl implements MediaRepositoryCustom {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private JPQLExpressionCodeGenerator jpqlCodeGenerator = new JPQLExpressionCodeGenerator();


    private final ObjectFactory<EntityManager> emProvider;

    @Autowired
    public MediaRepositoryImpl(ObjectFactory<EntityManager> emProvider){
        this.emProvider = emProvider;
    }


    @Override
    public ListPage<MediaItem> query(MediaExpressionQuery qry, int pageIndex, int maxPageSize) {

        long totalCount = queryCount(qry);

        TypedQuery<MediaItem> query = buildQuery(qry);
        query.setMaxResults(maxPageSize);
        query.setFirstResult(pageIndex * maxPageSize);
        List<MediaItem> pageItems = query.getResultList();

        return new ListPage<>(pageItems, totalCount, maxPageSize, pageIndex);
    }



    @Override
    public Collection<MediaItem> query(Tag tag) {
        String sQry = "SELECT m from MediaItem m WHERE '" + tag + "' MEMBER OF m.tags";
        TypedQuery<MediaItem> q = emProvider.getObject().createQuery(sQry, MediaItem.class);
        return q.getResultList();
    }

    @Override
    public List<MediaItem> queryByLibrary(MediaLibrary library) {
        TypedQuery<MediaItem> query = emProvider.getObject().createQuery(
            "SELECT distinct m from MediaItem m inner join m.sources s where s.parentLibrary = :library",
            MediaItem.class);
        query.setParameter("library", library);

        return query.getResultList();
    }

    @Override
    public List<MediaItem> query(Collection<MediaLibrary> libraries) {
        throw new NotImplementedException();
    }

    @Override
    public MediaItem queryByPath(ResourceLocation file, MediaLibrary library) {
        throw new NotImplementedException();
    }



    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/

    private long queryCount(MediaExpressionQuery qry){
        Query cQuery = emProvider.getObject().createQuery("SELECT COUNT(m) from MediaItem m WHERE " + buildMediaWhereQuery(qry));
        setQueryParams(cQuery, qry);
        Number result = (Number) cQuery.getSingleResult();
        return result.longValue();
    }

    private TypedQuery<MediaItem> buildQuery(MediaExpressionQuery qry){

        String sQry = "SELECT m from MediaItem m WHERE " + buildMediaWhereQuery(qry) + " " + buildMediaOrderByQuery(qry);

        logger.debug(sQry);

        TypedQuery<MediaItem> q = emProvider.getObject().createQuery(sQry, MediaItem.class);
        setQueryParams(q, qry);

        return q;
    }

    private void setQueryParams(Query q, MediaExpressionQuery qry){
        if(qry.hasKeyword()) q.setParameter("keywords", "%" + qry.getKeywords() + "%");
        if(qry.hasMediaType()) q.setParameter("type", MediaTypeUtil.findTypeByFilter(qry.getMediaType()));
        if(qry.hasAllowedLibraries()) q.setParameter("allowedLibraries", qry.getAllowedLibraries());
    }

    private String buildMediaOrderByQuery(MediaExpressionQuery qry){
        String orderBy = "";

        OrderProperty order = qry.getOrder();

        if(order != OrderProperty.NONE){

            String direction = "DESC";
            String reversedirection = "ASC";

            if(qry.isReverseOrder()){
                // Swap
                String tmp = direction;
                direction = reversedirection;
                reversedirection = tmp;
            }

            orderBy += "ORDER BY ";

            switch (order) {
                case TITLE:
                    // Ignore
                    break;
                default:
                    orderBy += "m." + order.getProperty() + " " + direction + ","; // By default desc order
            }

            orderBy += " m.title " + reversedirection;  // file name is asc order by default
        }

        return orderBy;
    }

    private String buildMediaWhereQuery(MediaExpressionQuery qry) {

        String where = "";

        if (qry.hasKeyword()) {
            String keywordPredicate = "(LOWER(m.title) LIKE LOWER(:keywords)) OR "

                // Search in the (relative) file path for a matching keyword
                + "EXISTS (SELECT s from MediaSource s WHERE s MEMBER OF m.sources AND LOWER(s.relativePathUri) LIKE LOWER(:keywords)) OR "
            /*
            Also search in tags of the media for this keyword
            This will not respect any hierarchical Tag information
             */
                + "EXISTS (SELECT t from Tag t WHERE t MEMBER OF m.tags AND t.name LIKE LOWER(:keywords))";
            where += "( "+keywordPredicate+" ) AND ";
        }

        if (qry.hasMediaType()) {
            where += "(m.type = :type) AND ";
        }

        if(!qry.getAllowedLibraries().isEmpty()){
            where += "EXISTS (SELECT s from MediaSource s WHERE s MEMBER OF m.sources AND s.parentLibrary IN (:allowedLibraries)) AND ";
        }


        if(qry.getTagsExpression() != null) {
            String tagExpr = jpqlCodeGenerator.generate(qry.getTagsExpression());

            logger.debug("Tag-Query: " + tagExpr);

            if (!tagExpr.trim().isEmpty()) {
                where += tagExpr + " AND ";
            }
        }

        where += "1=1";

        return where;
    }
}
