package com.sankore.astro.repository.dataaccesslayer.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sankore.astro.generic.services.AstroLoggerService;
import com.sankore.astro.repository.dataaccesslayer.AstroDataAccessLayer;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Obi on 20/05/2019
 */
@Service
public class AstroDataAccessLayerImpl implements AstroDataAccessLayer {

    @Autowired
    AstroLoggerService log;

    private EntityManager entityManager;

    public EntityManager getEntityManager(){
        return this.entityManager;
    }

    @PersistenceContext(unitName="astroPU")
    public void setEntityManager(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    @Transactional(readOnly = true,value="txManage",rollbackFor=Exception.class)
    public List<?> findAll(Class type){
        return entityManager.createQuery("select c from "+type.getSimpleName()+" as c").getResultList();
    }

    @Transactional(readOnly = true,value="txManage",rollbackFor=Exception.class)
    public List<?> findWithNativeQuery(String nativeQuery) throws Exception {
        return entityManager.createNativeQuery(nativeQuery).getResultList();
    }

    @Transactional(readOnly = true,value="txManage",rollbackFor=Exception.class)
    public List<Object[]> findWithNativeQuery(String nativeQuery, int offset) throws Exception {
        return entityManager.createNativeQuery(nativeQuery).setFirstResult(offset).getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> findWithNativeQuery(String nativeQuery, int offset, int count)throws Exception {
        return entityManager.createNativeQuery(nativeQuery).setFirstResult(offset).setMaxResults(count).getResultList();
    }

    @Transactional(readOnly = true,value="txManage",rollbackFor=Exception.class)
    public List<?> findWithNativeNamedQuery(String nativeNamedQuery, Class<?> clazz) throws Exception {
        if(clazz != null)
            return entityManager.createNativeQuery(nativeNamedQuery, clazz).getResultList();
        else
            return entityManager.createNamedQuery(nativeNamedQuery).getResultList();
    }

    @Transactional(readOnly = true,value="txManage",rollbackFor=Exception.class)
    public List<Object[]> findWithNativeQuery(String nativeQuery, Map<String, Object> parameters, int count) throws Exception {
        Query query = entityManager.createNativeQuery(nativeQuery).setMaxResults(count);
        Set<Map.Entry<String, Object>> params = parameters.entrySet();
        for(Map.Entry<String, Object> entry : params) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.getResultList();
    }

    @Transactional(readOnly = true,value="txManage",rollbackFor=Exception.class)
    public List<Object[]> findWithNativeQuery(String nativeQuery, Map<String, Object> parameters) throws Exception {
        Query query = entityManager.createNativeQuery(nativeQuery);
        for(Map.Entry<String, Object> entry :  parameters.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.getResultList();
    }

    @Transactional(readOnly = false,value = "txManage", noRollbackFor = Exception.class)
    public int deleteWithNamedQuery(String namedQueryName, Map<String, Object> parameters)throws Exception {
        Query query = entityManager.createNamedQuery(namedQueryName);
        for(Map.Entry<String,Object> param : parameters.entrySet()){
            query.setParameter(param.getKey(),param.getValue());
        }
        return query.executeUpdate();
    }


    @Transactional(readOnly = true,value="txManage",rollbackFor=Exception.class)
    public List<?> findWithNativeNamedQuery(String nativeNamedQuery, Map<String, Object> parameters, Class<?> clazz) throws Exception {
        Query query = null;
        if (clazz != null) {
            query = entityManager.createNativeQuery(nativeNamedQuery, clazz);
        } else {
            query = entityManager.createNativeQuery(nativeNamedQuery);
        }

        Set<Map.Entry<String, Object>> params = parameters.entrySet();
        for (Map.Entry<String, Object> entry : params) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.getResultList();
    }

    @Transactional(readOnly = true,value="txManage",rollbackFor=Exception.class)
    public List<?> findWithNativeNamedQuery(String nativeNamedQuery, int limit, Class<?> clazz, String mapping){
        return entityManager.createNativeQuery(nativeNamedQuery, clazz).setMaxResults(limit).getResultList();
    }

    @Transactional(readOnly = true,value="txManage",rollbackFor=Exception.class)
    public List<?> findWithNativeNamedQuery(String nativeNamedQuery) throws Exception {
        return entityManager.createNamedQuery(nativeNamedQuery).getResultList();
    }

    @Transactional(readOnly = true,value="txManage",rollbackFor=Exception.class)
    public List<?> findWithNativeNamedQuery(String nativeNamedQuery, Map<String, Object> parameters, int limit, Class<?> clazz)throws Exception {
        Query query = null;
        if(clazz != null)
            query = entityManager.createNativeQuery(nativeNamedQuery, clazz);
        else
            query = entityManager.createNativeQuery(nativeNamedQuery);
        if(limit > 0)
            query.setMaxResults(limit);
        Set<Map.Entry<String, Object>> params = parameters.entrySet();
        for(Map.Entry<String, Object> entry : params) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.getResultList();
    }

    @Transactional(readOnly = false, value="txManage",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
    public <T> T create(T t) throws Exception {
        entityManager.persist(t);
        return t;
    }

    @Transactional(readOnly = false, value="txManage",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
    public int deleteAll(String namedQuery) throws Exception {
        return entityManager.createNamedQuery(namedQuery).executeUpdate();
    }

    @Transactional(readOnly = true,value="txManage",rollbackFor=Exception.class)
    public <T> T find(Class<T> type, Object id) throws Exception {
        return entityManager.find(type, id);
    }

    @Transactional(readOnly = false, value="txManage",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
    public void delete(Class<?> type, Object id) throws Exception {
        entityManager.remove(entityManager.getReference(type, id));
    }

    @Transactional(readOnly = false, value="txManage",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
    public <T> T update(T t) throws Exception {
        return entityManager.merge(t);
    }

    @Transactional(readOnly = true,value="txManage",rollbackFor=Exception.class)
    public List<?> findWithNamedQuery(String namedQueryName) throws Exception {
        return entityManager.createNamedQuery(namedQueryName).getResultList();
    }

    @Transactional(readOnly = true,value="txManage",rollbackFor=Exception.class)
    public List<?> findWithNamedQuery(String namedQueryName, Map<String, Object> parameters) throws Exception {
        Query query = entityManager.createNamedQuery(namedQueryName);
        for(String key : parameters.keySet()){
            query.setParameter(key, parameters.get(key));
        }
        return query.getResultList();
    }

    @Transactional(readOnly = true,value="txManage",rollbackFor=Exception.class)
    public List<?> findWithNamedQuery(String namedQueryName, Map<String, Object> params, int offset, int limit) throws Exception {
        Query query = entityManager.createNamedQuery(namedQueryName);
        for(String key : params.keySet()){
            query.setParameter(key, params.get(key));
        }
        return query.setFirstResult(offset).setMaxResults(limit).getResultList();
    }

    @Transactional(readOnly = true,value="txManage",rollbackFor=Exception.class)
    public long countWithNamedQuery(String namedCountQuery, Map<String, Object> params) throws Exception {
        Query query = entityManager.createNamedQuery(namedCountQuery);
        for(String key : params.keySet()){
            query.setParameter(key, params.get(key));
        }
        return ((Long) query.getSingleResult()).longValue();
    }


    @Transactional(readOnly = true,value="txManage",rollbackFor=Exception.class)
    public List<?> findWithNamedQuery(String namedQueryName, int offset) throws Exception {
        return entityManager.createNamedQuery(namedQueryName).setFirstResult(offset).getResultList();
    }

    @Transactional(readOnly = true,value="txManage",rollbackFor=Exception.class)
    public List<?> findWithNamedQuery(String namedQuery, int offset, int limit) throws Exception {
        return entityManager.createNamedQuery(namedQuery).setFirstResult(offset).setMaxResults(limit).getResultList();
    }

    @Transactional(readOnly = true,value="txManage",rollbackFor=Exception.class)
    public List<?> findWithNamedQuery(String namedQueryName, Map<String, Object> parameters, int resultLimit) throws Exception {
        Query query = this.entityManager.createNamedQuery(namedQueryName);
        query.setMaxResults(resultLimit);
        Set<Map.Entry<String, Object>> params = parameters.entrySet();
        for(Map.Entry<String, Object> entry : params) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.getResultList();
    }

    @Transactional(readOnly = true,value="txManage",rollbackFor=Exception.class)
    public List<?> findWithDynamicJPAQueryString(String queryString, Map<String,Object> parameters) throws Exception {
        Query jpaQuery = this.entityManager.createQuery(queryString);
        for(Map.Entry<String,Object> parameter : parameters.entrySet()){
            jpaQuery.setParameter(parameter.getKey(),parameter.getValue());
        }
        return jpaQuery.getResultList();
    }

    @Transactional(readOnly = true,value="txManage",rollbackFor=Exception.class)
    public List<?> findWithDynamicJPAQueryString(String queryString, Map<String, Object> parameters, int offset, int limit) throws Exception {
        Query jpaQuery = this.entityManager.createQuery(queryString);
        for(Map.Entry<String,Object> parameter : parameters.entrySet()){
            jpaQuery.setParameter(parameter.getKey(),parameter.getValue());
        }
        return jpaQuery.setFirstResult(offset).setMaxResults(limit).getResultList();
    }

    @Transactional(readOnly = true,value="txManage",rollbackFor=Exception.class)
    public List<?> findWithDynamicJPAQueryString(String queryString, int offset, int limit) throws Exception {
        Query jpaQuery = this.entityManager.createQuery(queryString);
        return jpaQuery.setFirstResult(offset).setMaxResults(limit).getResultList();
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED,value="txManage",rollbackFor=Exception.class)
    public void addChildToParent(Class parantClass, Serializable parentId, Class childs[], Serializable childIds[], String childMethod[]) throws Exception {
        Object parentObject = this.entityManager.find(parantClass, parentId);
        if (parentObject != null) {
            if (childs.length == childIds.length && childs.length == childMethod.length) {
                for (int i = 0; i < childs.length; i++) {
                    Method mth = parantClass.getDeclaredMethod(childMethod[i], childs[i]);
                    if (mth != null) {
                        mth.invoke(parentObject, this.entityManager.find(childs[i], childIds[i]));
                    }
                }
            }
        }
    }
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED,value="txManage",rollbackFor=Exception.class)
    public void clearChilds(Class parantClass, Serializable parentId, String methods[]) throws Exception {
        Object parentObject = this.entityManager.find(parantClass,parentId);
        if(parentObject !=null){
            for(int i=0; i< methods.length; i++){
                Method mth = parantClass.getDeclaredMethod(methods[i]);
                if(mth !=null){
                    mth.invoke(parentObject);
                }
            }

        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED,value="txManage",rollbackFor=Exception.class)
    public void addChildToParent(Class parentClass,Serializable parentId, Class childClass, String childMethod, Object childObject) throws Exception{
        Object parentObject = this.entityManager.find(parentClass,parentId);
        if(parentObject !=null){
            Method mth = parentClass.getDeclaredMethod(childMethod, childClass);
            if(mth !=null){
                mth.invoke(parentClass,childObject);
            }
        }
    }
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED,value="txManage",rollbackFor=Exception.class)
    public void addChildsToParent(Class parantClass, Serializable parentId, Set<?> childs, String method, Class childValueType) throws Exception {
        Object parentObject = this.entityManager.find(parantClass,parentId);
        if(parentObject !=null){
            Method mth = parantClass.getDeclaredMethod(method, childValueType);
            if(mth !=null){
                mth.invoke(parentObject,childs);
            }

        }
    }



    @Transactional(readOnly = false, propagation = Propagation.REQUIRED,value="txManage",rollbackFor=Exception.class)
    public Integer[] updateOrDeleteWithNativeQuery(List<String> queryList) throws Exception {
        List<Integer> operationResult = new ArrayList<Integer>();
        queryList.stream().forEach(query ->{
            operationResult.add(entityManager.createNativeQuery(query).executeUpdate());
        });
        return operationResult.toArray(new Integer[operationResult.size()]);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED,value="txManage",rollbackFor=Exception.class)
    public int updateOrDeleteWithNativeQuery(String queryString, HashMap<Integer,Object> paramMap) throws Exception {
        Query query = entityManager.createNativeQuery(queryString);
        paramMap.forEach((key,value) ->{
            query.setParameter(key.intValue(),value);
        });

        return query.executeUpdate();
    }

    @Transactional(readOnly = true,value="txManage",rollbackFor=Exception.class)
    public long countWithDynamicJPAQueryString(String queryString, Map<String, Object> parameters) throws Exception {
        Query jpaQuery = this.entityManager.createQuery(queryString);
        for(Map.Entry<String,Object> parameter : parameters.entrySet()){
            jpaQuery.setParameter(parameter.getKey(),parameter.getValue());
        }
        return ((Long) jpaQuery.getSingleResult()).longValue();
    }

    @Transactional(readOnly=true, propagation = Propagation.REQUIRED,value="txManage", rollbackFor={Exception.class})
    public boolean entityExists(String className, String[] attr, Object[] data)
            throws Exception
    {
        String query = "select c from " + className + " as c where ";
        for (int i = 0; i < attr.length; i++) {
            if (i == 0) {
                //query = query + "c." + attr[i] + "='" + dataacess[i] + "'";
                if (attr[i].equals("name")) {
                    query = query + "lower(c." + attr[i] + ")" + "=:param"+i+"";
                } else {
                    query = query + ((data[i] instanceof String)?"lower(c."+attr[i]+")":"c."+attr[i]) + "=:param"+i+"";
                }
            } else {
                if (attr[i].equals("name")) {
                    query = query + " and lower(c." + attr[i] + ")" + "=:param"+i+"";
                } else {
                    query = query + " and " + ((data[i] instanceof String)?"lower(c."+attr[i]+")":"c."+attr[i]) + "=:param"+i+"";
                }
            }
        }

        Query jpaQuery = this.entityManager.createQuery(query);
        for(int i=0; i< data.length; i++){
            if (attr.length > i && attr[i].equals("name")) {
                jpaQuery.setParameter("param" + i, String.valueOf(data[i]).toLowerCase());
            } else {
                jpaQuery.setParameter("param"+i,(data[i] instanceof String)?String.valueOf(data[i]).toLowerCase():data[i]);
            }
        }
        return !jpaQuery.getResultList().isEmpty();
    }
}
