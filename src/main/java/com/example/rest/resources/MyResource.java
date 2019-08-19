package com.example.rest.resources;

import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Iterator;
import java.util.Random;

@Path("links")
public class MyResource {

    private static final MongoCollection<Document> LINKS_COLLECTION;

    static {
        final MongoClient mongoClient = new MongoClient();
        final MongoDatabase db = mongoClient.getDatabase("hexlet");
        LINKS_COLLECTION = db.getCollection("links");
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getUrlById(final @PathParam("id") String id) {
        if (id == null || id.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        final FindIterable<Document> iterable = LINKS_COLLECTION.find(new Document("id", id));
        final Iterator<Document> iterator = iterable.iterator();
        if (!iterator.hasNext()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        final String url = iterator.next().getString("url");
        if (url == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(url).build();
    }

    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public Response shortLink(final String url) {
        int attempt = 0;
        while (attempt < 5) {
            final String id = getRandomId();
            final Document newShortDoc = new Document("id", id);
            newShortDoc.put("url", url);
            try {
                LINKS_COLLECTION.insertOne(newShortDoc);
                return Response.ok(id).build();
            } catch (final MongoWriteException e) {
                //attempt to write failed
            }
            attempt++;
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    private String getRandomId() {
        final String possibleCharacters = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm0123456789";
        final StringBuilder idBuilder = new StringBuilder();
        final Random rnd = new Random();
        while (idBuilder.length() < 5) {
            int i = (int) (rnd.nextFloat() * possibleCharacters.length());
            idBuilder.append(possibleCharacters.charAt(i));
        }
        return idBuilder.toString();
    }

}