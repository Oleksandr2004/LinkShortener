package com.example.rest.lambda;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.lambda.runtime.Context;

import java.util.Random;

public class LinkAdderHandler {

    private static final Table LINKS;

    static {
        final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        final DynamoDB db = new DynamoDB(client);

        LINKS = db.getTable("Links");
    }

    public String addLink(final String url, final Context context) {
        int attempt = 0;
        while (attempt < 5) {
            final String id = getRandomId();
            final Item urlRecord = new Item()
                    .withPrimaryKey("id", id)
                    .withString("url", url);
            try {
                LINKS.putItem(
                        new PutItemSpec()
                                .withConditionExpression("attribute_not_exists(id)")
                                .withItem(urlRecord)
                );
                return id;
            } catch (final ConditionalCheckFailedException e) {
                //attempt to write failed
            }
            attempt++;
        }
        return null;
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
