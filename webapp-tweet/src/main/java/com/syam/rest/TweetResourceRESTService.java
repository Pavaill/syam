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
package com.syam.rest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.syam.data.TweetRepository;
import com.syam.model.Tweet;
import com.syam.service.TweetDeletion;
import com.syam.service.TweetModification;
import com.syam.service.TweetRegistration;
import org.json.JSONObject;

/**
 * JAX-RS Example
 * <p/>
 * This class produces a RESTful service to read/write the contents of the Tweets table.
 */
@Path("/tweet")
@RequestScoped
public class TweetResourceRESTService {

    private static final int tailleMaxTweet = 200;

    @Inject
    private Logger log;

    @Inject
    private Validator validator;

    @Inject
    private TweetRepository repository;

    @Inject
    TweetRegistration registration;

    @Inject
    TweetModification modification;

    @Inject
    TweetDeletion deletion;


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Tweet> listAllTweets() {
        return repository.findAllOrderedByName();
    }

    /**
     * Recuperation tweet à partir de son id
     * chemin : /webapp-tweet/rest/tweet/numeroID
     */
    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Tweet lookupTweetById(@PathParam("id") long id) {
        Tweet tweet = repository.findById(id);
        if (tweet == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return tweet;
    }

    /**
     * Recuperation de tous les tweets
     * chemin : /webapp-tweet/rest/tweet/alltweet
     */
    @GET
    @Path("/alltweet")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Tweet> lookupAllTweet() {
        List<Tweet> tweet = repository.findAllOrderedById();
        if (tweet == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return tweet;
    }


    /**
     * Creation d'un nouveau tweet
     * créer un Json avec le nom et le texte du tweet
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTweet(Tweet tweet) {

        Response.ResponseBuilder builder = null;

        try {
            // Validates tweet using bean validation
            validateTweet(tweet);

            //methode register permet d'enregistrer le tweet dans la base de données
            registration.register(tweet);

            //creation du Json
            //on retourne l'id du tweet
            JsonObject jsonFile = Json.createObjectBuilder()
                    .add("idTweet", tweet.getId())
                    .build();

            // Create an "ok" response
            builder = Response.ok(jsonFile);

        } catch (ConstraintViolationException ce) {
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (TooLongTweetException e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", "Tweet trop long");
            builder = Response.status(Response.Status.FORBIDDEN).entity(responseObj);
        } catch (NomVideException e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", "Nom vide");
            builder = Response.status(Response.Status.FORBIDDEN).entity(responseObj);
        } catch (TweetVideException e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", "Tweet vide");
            builder = Response.status(Response.Status.FORBIDDEN).entity(responseObj);
        } catch (Exception e) {
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }

        return builder.build();
    }

    /**
     * Modification tweet par id
     */
    @PUT
    @Path("/{id:[0-9][0-9]*}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifyTweet(@PathParam("id") long i,String inputJsonObj) {

        Response.ResponseBuilder builder = null;

        try {

            JSONObject innerJson = new JSONObject(inputJsonObj);
            String text= innerJson.getString("tweetText");
            modification.modify(i, text);

            //creation du Json
            //on retourne l'id du tweet
            JsonObject jsonFile = Json.createObjectBuilder()
                    .add("idTweet", i)
                    .build();

            // Create an "ok" response
            builder = Response.ok(jsonFile);

        } catch (ConstraintViolationException ce) {
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (TooLongTweetException e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", "Tweet trop long");
            builder = Response.status(Response.Status.FORBIDDEN).entity(responseObj);
        } catch (NomVideException e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", "Nom vide");
            builder = Response.status(Response.Status.FORBIDDEN).entity(responseObj);
        } catch (TweetVideException e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", "Tweet vide");
            builder = Response.status(Response.Status.FORBIDDEN).entity(responseObj);
        } catch (Exception e) {
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }

        return builder.build();
    }


    /**
     * Suppression de tweet à partir de son id
     * chemin : /webapp-tweet/rest/delete/numeroID
     */
    @DELETE
    @Path("/delete/{id:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTweet(@PathParam("id") long id) {

        Response.ResponseBuilder builder = null;

        try {

            //methode register permet d'enregistrer le tweet dans la base de données
            deletion.remove(id);

            //creation du Json
            //on retourne l'id du tweet
            JsonObject jsonFile = Json.createObjectBuilder()
                    .add("idTweet", id)
                    .build();

            // Create an "ok" response
            builder = Response.ok(jsonFile);

        } catch (ConstraintViolationException ce) {
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (TooLongTweetException e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", "Tweet trop long");
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        } catch (NomVideException e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", "Nom vide");
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        } catch (TweetVideException e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", "Tweet vide");
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        } catch (Exception e) {
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }

        return builder.build();
    }

    /*
    @POST
    @Path("/tweet/{login}/{text}")
    @Produces("application/json")
    public Response postTweetJSON(@PathParam("login") String login, @PathParam("text") String text) {
        System.out.println("login: " + login);

        JsonObject jsonFile = Json.createObjectBuilder()
                .add("login", login)
                .add("text", text)
                .build();

        return Response.ok(jsonFile).build();
    }
    $/

    /**
     * <p>
     * Validates the given Tweet variable and throws validation exceptions based on the type of error. If the error is standard
     * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.
     * </p>
     * <p>
     * If the error is caused because an existing tweet with the same email is registered it throws a regular validation
     * exception so that it can be interpreted separately.
     * </p>
     *
     * @param tweet Tweet to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws ValidationException          If tweet with the same email already exists
     */
    private void validateTweet(Tweet tweet) throws ConstraintViolationException, ValidationException, TooLongTweetException, NomVideException, TweetVideException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Tweet>> violations = validator.validate(tweet);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }

        //test si le tweet est inférieur à 200 caractères
        String tweetText = tweet.getTweetText();
        if (tweetText.length() > tailleMaxTweet) {
            throw new TooLongTweetException();
        }

        //test si le champ du nom n'est pas vide
        String tweetName = tweet.getName();
        if (tweetName.equals("")) {
            throw new NomVideException();
        }

        //test si le tweet n'est pas vide
        if (tweetText.equals("")) {
            throw new TweetVideException();
        }
    }

    /**
     * Creates a JAX-RS "Bad Request" response including a map of all violation fields, and their message. This can then be used
     * by clients to show violations.
     *
     * @param violations A set of violations that needs to be reported
     * @return JAX-RS response containing all violations
     */
    private Response.ResponseBuilder createViolationResponse(Set<ConstraintViolation<?>> violations) {
        log.fine("Validation completed. violations found: " + violations.size());

        Map<String, String> responseObj = new HashMap<>();

        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
    }

}
