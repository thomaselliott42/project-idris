package com.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenu implements Screen {
    private Stage stage;
    private Skin skin;
    private SpriteBatch batch;

    private Texture backgroundTexture;
    private final Main game;


    // Buttons
    private ImageButton startButton;
    private ImageButton createButton;
    private ImageButton settingsButton;
    private ImageButton quitButton;
    private Map map;


    // Slide
    private Texture swipeSlideTexture;
    private float swipeWidth = 0f;
    private boolean playingSwipe = false;
    private final float SWIPE_SPEED = 2000f; // pixels per second

    public MainMenu(Main game) {
        this.game = game;
        swipeSlideTexture = new Texture(Gdx.files.internal("menu/swipeSlide.png")); // put the PNG in assets/menu/

        batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));
        backgroundTexture = new Texture(Gdx.files.internal("menu/mainMenuBackground1.png"));


        CameraManager.getInstance().getMapCamera().position.set(
            (100 * 32) / 2f,
            (100 * 32) / 2f,
            0
        );
        CameraManager.getInstance().getMapCamera().update();


        this.map = new Map(100, 100);
        map.generateMapSelected("S");
       //map.generateMapRandom();

        // Create and position buttons and add listeners
        createButtons();
        addListeners();

    }

    private void createButtons() {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("atlas/menuUi.atlas"));
        Skin skin = new Skin(atlas);

        float buttonWidth = 380;
        float buttonHeight = 80;
        float spacing = 20;
        float xBase = Gdx.graphics.getWidth() - 435;
        float yStart = Gdx.graphics.getHeight() / 2 ;

        float xShift = 31;

        ImageButton.ImageButtonStyle playStyle = new ImageButton.ImageButtonStyle();
        playStyle.imageUp = skin.getDrawable("play");
        playStyle.imageOver = skin.getDrawable("playHovered");
        playStyle.imageDown = skin.getDrawable("playClicked");
        startButton = new ImageButton(playStyle);
        startButton.setSize(buttonWidth, buttonHeight);
        startButton.setPosition(xBase, yStart);

        ImageButton.ImageButtonStyle createStyle = new ImageButton.ImageButtonStyle();
        createStyle.imageUp = skin.getDrawable("create");
        createStyle.imageOver = skin.getDrawable("createHovered");
        createStyle.imageDown = skin.getDrawable("createClicked");
        createButton = new ImageButton(createStyle);
        createButton.setSize(buttonWidth, buttonHeight);
        createButton.setPosition(xBase - xShift, yStart - (buttonHeight + spacing) * 1);

        ImageButton.ImageButtonStyle settingsStyle = new ImageButton.ImageButtonStyle();
        settingsStyle.imageUp = skin.getDrawable("settings");
        settingsStyle.imageOver = skin.getDrawable("settingsHovered");
        settingsStyle.imageDown = skin.getDrawable("settingsClicked");
        settingsButton = new ImageButton(settingsStyle);
        settingsButton.setSize(buttonWidth, buttonHeight);
        settingsButton.setPosition(xBase - xShift * 2, yStart - (buttonHeight + spacing) * 2);

        ImageButton.ImageButtonStyle quitStyle = new ImageButton.ImageButtonStyle();
        quitStyle.imageUp = skin.getDrawable("exit");
        quitStyle.imageOver = skin.getDrawable("exitHovered");
        quitStyle.imageDown = skin.getDrawable("exitClicked");
        quitButton = new ImageButton(quitStyle);
        quitButton.setSize(buttonWidth, buttonHeight);
        quitButton.setPosition(xBase - xShift * 3, yStart - (buttonHeight + spacing) * 3);


        // Add to stage
        stage.addActor(startButton);
        stage.addActor(createButton);
        stage.addActor(settingsButton);
        stage.addActor(quitButton);

    }


    private void addListeners() {
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        createButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
//                playingSwipe = true;
//                swipeWidth = 0f;
               game.setScreen(new LoadingScreen(game)); // Make sure LoadingScreen takes Game in its constructor
            }
        });
    }


    @Override
    public void show() {
        // Set input processor for the stage
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        map.render2DMap(32,batch,0,0,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

        if (playingSwipe) {
            swipeWidth += SWIPE_SPEED * delta;
            float height = Gdx.graphics.getHeight();
            batch.draw(swipeSlideTexture, -300, 0, swipeWidth, height);

            if (swipeWidth >= Gdx.graphics.getWidth() + 600) {
                playingSwipe = false;
                game.setScreen(new LoadingScreen(game)); // transition
            }
        }

        batch.end();

        if (!playingSwipe) {
            batch.begin();
            batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.end();
            stage.act(Math.min(delta, 1 / 30f));
            stage.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        // You can add any logic when the screen is hidden, but no additional code needed for this simple screen
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        // Dispose of all resources
        if (batch != null) {
            batch.dispose();
        }
        if (stage != null) {
            stage.dispose();
        }
        if (skin != null) {
            skin.dispose();
        }
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        if (swipeSlideTexture != null) {
            swipeSlideTexture.dispose();
        }
    }
}
