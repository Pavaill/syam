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
package com.syam.service;

import com.syam.data.TweetRepository;
import com.syam.model.Tweet;
import com.syam.rest.IdInexistantException;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

// The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class TweetDeletion {

    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Tweet> tweetEventSrc;

    @Inject
    private TweetRepository repository;

    public void remove(Long id) throws Exception, IdInexistantException {
        log.info("Deleting tweet id:" + id);
        Tweet tweet = repository.findById(id);
        if (tweet == null) {
            throw new IdInexistantException();
        }
        em.remove(tweet);
    }
}
