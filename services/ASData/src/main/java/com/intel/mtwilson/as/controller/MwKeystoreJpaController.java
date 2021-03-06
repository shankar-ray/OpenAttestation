/*
 * Copyright (c) 2013, Intel Corporation. 
 * All rights reserved.
 * 
 * The contents of this file are released under the BSD license, you may not use this file except in compliance with the License.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of Intel Corporation nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.intel.mtwilson.as.controller;

import com.intel.mtwilson.as.controller.exceptions.NonexistentEntityException;
import com.intel.mtwilson.as.data.MwKeystore;
import com.intel.mtwilson.jpa.GenericJpaController;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.eclipse.persistence.config.CacheUsage;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

/**
 *
 * @author jbuhacoff
 */
public class MwKeystoreJpaController extends GenericJpaController<MwKeystore> implements Serializable {

    public MwKeystoreJpaController(EntityManagerFactory emf) {
        super(MwKeystore.class);
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    @Override
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(MwKeystore mwKeystore) {
        EntityManager em = getEntityManager();
        try {
            
            em.getTransaction().begin();
            em.persist(mwKeystore);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void edit(MwKeystore mwKeystore) throws NonexistentEntityException, Exception {
        EntityManager em = getEntityManager();
        try {
            
            em.getTransaction().begin();
            mwKeystore = em.merge(mwKeystore);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = mwKeystore.getId();
                if (findMwKeystore(id) == null) {
                    throw new NonexistentEntityException("The mwKeystore with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = getEntityManager();
        try {
            
            em.getTransaction().begin();
            MwKeystore mwKeystore;
            try {
                mwKeystore = em.getReference(MwKeystore.class, id);
                mwKeystore.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The mwKeystore with id " + id + " no longer exists.", enfe);
            }
            em.remove(mwKeystore);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public List<MwKeystore> findMwKeystoreEntities() {
        return findMwKeystoreEntities(true, -1, -1);
    }

    public List<MwKeystore> findMwKeystoreEntities(int maxResults, int firstResult) {
        return findMwKeystoreEntities(false, maxResults, firstResult);
    }

    private List<MwKeystore> findMwKeystoreEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(MwKeystore.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public MwKeystore findMwKeystore(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(MwKeystore.class, id);
        } finally {
            em.close();
        }
    }

    public int getMwKeystoreCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<MwKeystore> rt = cq.from(MwKeystore.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    /**
     *
     * @param name
     * @return the named MwKeystore, or null if it was not found
     */
    public MwKeystore findMwKeystoreByName(String name) {
        //HashMap<String,Object> parameters = new HashMap<String,Object>();
        //parameters.put("name", name);
        //List<MwKeystore> list = searchByNamedQuery("MwKeystore.findByName", parameters);
        //if( list.isEmpty() ) {
        //    return null;
        //}
        //return list.get(0);
        MwKeystore mwKeystoreObj = null;
        EntityManager em = getEntityManager();
        Query query = em.createNamedQuery("MwKeystore.findByName");
        query.setParameter("name", name);

        query.setHint(QueryHints.REFRESH, HintValues.TRUE);
        query.setHint(QueryHints.CACHE_USAGE, CacheUsage.DoNotCheckCache);
        try {
          mwKeystoreObj = (MwKeystore) query.getSingleResult();
        }
        catch(javax.persistence.NoResultException e) {
          mwKeystoreObj = null;           
        }       
        return mwKeystoreObj;
    }
}
