/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.syam.data;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

import com.syam.model.Tweet;

@ApplicationScoped
public class TweetRepository {

    @Inject
    private EntityManager em;

    @Inject
    private Event<Tweet> tweetEventSrc;

    public Tweet findById(Long id) {
        return em.find(Tweet.class, id);
    }

    public void deleteById(Long id) {
        em.remove(findById(id));
    }


    public List<Tweet> findAllOrderedByName() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tweet> criteria = cb.createQuery(Tweet.class);
        Root<Tweet> tweet = criteria.from(Tweet.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(tweet).orderBy(cb.asc(tweet.get(tweet_.name)));
        criteria.select(tweet).orderBy(cb.asc(tweet.get("name")));
        return em.createQuery(criteria).getResultList();
    }

    public List<Tweet> findAllOrderedById() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tweet> criteria = cb.createQuery(Tweet.class);
        Root<Tweet> tweet = criteria.from(Tweet.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(tweet).orderBy(cb.asc(tweet.get(tweet_.name)));
        criteria.select(tweet).orderBy(cb.asc(tweet.get("id")));
        return em.createQuery(criteria).getResultList();
    }

}
