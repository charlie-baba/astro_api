package com.sankore.astro.repository.dataaccesslayer;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Obi on 20/05/2019
 */
public interface AstroDataAccessLayer {

    public EntityManager getEntityManager();

    public List<?> findAll(Class type);

    public List<?> findWithNativeQuery(String nativeQuery) throws Exception;

    public List<Object[]> findWithNativeQuery(String nativeQuery, int offset) throws Exception;

    public List<Object[]> findWithNativeQuery(String nativeQuery, int offset, int count) throws Exception;

    public List<?> findWithNativeNamedQuery(String nativeNamedQuery, Class<?> clazz) throws Exception;

    public List<?> findWithNativeNamedQuery(String nativeNamedQuery) throws Exception;

    public List<?> findWithNativeNamedQuery(String nativeNamedQuery, Map<String, Object> parameters, Class<?> clazz) throws Exception;

    public List<?> findWithNativeNamedQuery(String nativeNamedQuery, int limit, Class<?> clazz, String mapping) throws Exception;

    public List<?> findWithNativeNamedQuery(String nativeNamedQuery, Map<String, Object> parameters, int limit, Class<?> clazz) throws Exception;

    public List<Object[]> findWithNativeQuery(String nativeQuery, Map<String, Object> parameters, int count) throws Exception;

    public List<Object[]> findWithNativeQuery(String nativeQuery, Map<String, Object> parameters) throws Exception;

    public <T> T create(T t) throws Exception;

    public int deleteAll(String namedQuery) throws Exception;

    public <T> T find(Class<T> type, Object id) throws Exception;

    public void delete(Class<?> type, Object id)throws Exception;

    public <T> T update(T t)throws Exception;

    public List<?> findWithNamedQuery(String namedQueryName)throws Exception;

    public List<?> findWithNamedQuery(String namedQueryName, Map<String, Object> parameters)throws Exception;

    public int deleteWithNamedQuery(String namedQueryName, Map<String, Object> parameters)throws Exception;

    public List<?> findWithNamedQuery(String namedQueryName, Map<String, Object> params, int offset, int limit)throws Exception;

    public long countWithNamedQuery(String namedCountQuery, Map<String, Object> params)throws Exception;

    public List<?> findWithNamedQuery(String namedQueryName, int offset)throws Exception;

    public List<?> findWithNamedQuery(String namedQuery, int offset, int limit)throws Exception;

    public List<?> findWithNamedQuery(String namedQueryName, Map<String, Object> parameters, int resultLimit) throws Exception;

    public List<?> findWithDynamicJPAQueryString(String queryString, Map<String, Object> parameters) throws Exception;

    public List<?> findWithDynamicJPAQueryString(String queryString, Map<String, Object> parameters, int offset, int limit) throws Exception;

    public List<?> findWithDynamicJPAQueryString(String queryString, int offset, int limit) throws Exception;

    public long countWithDynamicJPAQueryString(String queryString, Map<String, Object> parameters) throws Exception;

    public Integer[] updateOrDeleteWithNativeQuery(List<String> queryList) throws Exception;

    public int updateOrDeleteWithNativeQuery(String queryString, HashMap<Integer, Object> paramMap) throws Exception;

    public void clearChilds(Class parantClass, Serializable parentId, String methods[]) throws Exception;

    public void addChildToParent(Class parantClass, Serializable parentId, Class childs[], Serializable childIds[], String childMethod[]) throws Exception;

    public void addChildToParent(Class parentClass,Serializable parentId, Class childClass, String childMethod, Object object) throws Exception;

    public void addChildsToParent(Class parantClass, Serializable parentId, Set<?> childs, String method, Class childValueType) throws Exception;

    public  boolean entityExists(String paramString, String[] paramArrayOfString, Object[] paramArrayOfObject) throws Exception;
}
