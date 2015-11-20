package org.stellar.sdk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import org.stellar.base.Keypair;

import java.lang.reflect.Type;

class PageDeserializer<E> implements JsonDeserializer<Page<E>> {
  private TypeToken<Page<E>> pageType;

  public PageDeserializer(TypeToken<Page<E>> pageType) {
    this.pageType = pageType;
  }

  @Override
  public Page<E> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    // Flatten the object so it has two fields `records` and `links`
    JsonObject newJson = new JsonObject();
    newJson.add("records", json.getAsJsonObject().get("_embedded").getAsJsonObject().get("records"));
    newJson.add("links", json.getAsJsonObject().get("_links"));

    // Create new Gson object with adapters needed in Page
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Keypair.class, new KeypairTypeAdapter().nullSafe())
            .create();

    return gson.fromJson(newJson, pageType.getType());
  }
}