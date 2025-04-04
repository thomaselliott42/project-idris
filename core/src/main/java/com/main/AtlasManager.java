package com.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;
import java.util.Map;

public class AtlasManager {
    private static AtlasManager instance;

    // singelton that contains cases to return texture region
    private TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("atlas/terrain.atlas"));
    private TextureAtlas uiAtlas = new TextureAtlas(Gdx.files.internal("atlas/mapEditorUI.atlas"));

    private Map<String, TextureRegion> textureCache = new HashMap<>();

    public static AtlasManager getInstance() {
        if (instance == null) {
            instance = new AtlasManager();
        }
        return instance;
    }

    public TextureRegion getMapUItexture(String textureId) {
        return uiAtlas.findRegion(textureId);
    }

    public TextureRegion getTexture(String textureId) {
        textureId = textureId.toLowerCase();
        // Check if texture is already cached
        if (!textureCache.containsKey(textureId)) {
            TextureRegion texture = atlas.findRegion(textureId);
            textureCache.put(textureId, texture);
        }


        return textureCache.get(textureId);
    }

    public TextureRegion getTexture(String textureId, String baseTexture) {
//        if(!textureId.equals("s")){
//            textureId = baseTexture.toLowerCase()+"/"+textureId;
//        }else{
//            textureId = textureId;
//        }
        textureId = baseTexture.toLowerCase()+"/"+textureId.toLowerCase();

        // Check if texture is already cached
        if (!textureCache.containsKey(textureId)) {
            TextureRegion texture = atlas.findRegion(textureId);
            textureCache.put(textureId, texture);
        }


        return textureCache.get(textureId);
    }

    public void dispose() {
        if (atlas != null) {
            atlas.dispose();
            uiAtlas.dispose();
        }
        textureCache.clear(); // Clear references

    }



}
