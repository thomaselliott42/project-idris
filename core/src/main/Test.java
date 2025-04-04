import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.transform.*;
import javafx.stage.Stage;
import javafx.animation.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import javafx.util.Duration;

public class Test extends Application {
    
    private static final int WIDTH = 1200;
    private static final int HEIGHT = 800;
    private static final double ROTATION_SPEED = 0.5;
    private static final double ZOOM_SPEED = 2.0;
    
    private double anchorX, anchorY;
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;
    private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    private double cameraDistance = -2000;
    
    private Group root3D;
    private PerspectiveCamera camera;
    private Timeline animation;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        // Create root 3D group and camera
        root3D = new Group();
        camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(100000.0);
        camera.setTranslateZ(cameraDistance);
        
        // Create scene
        Scene scene = new Scene(root3D, WIDTH, HEIGHT, true);
        scene.setFill(Color.rgb(30, 30, 40));
        scene.setCamera(camera);
        
        // Add mouse handlers for rotation and zoom
        initMouseControl(scene);
        
        // Add shapes to the scene
        create3DShapes();
        
        // Add lighting
        addLighting();
        
        // Add UI controls
        addControls(scene);
        
        // Set up stage
        primaryStage.setTitle("3D Shape Visualizer");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Start animation
        startAnimation();
    }
    
    private void create3DShapes() {
        // Create a sphere
        Sphere sphere = new Sphere(150);
        sphere.setTranslateX(-400);
        sphere.setTranslateY(-200);
        sphere.setTranslateZ(100);
        sphere.setMaterial(new PhongMaterial(Color.RED));
        
        // Create a box
        Box box = new Box(200, 200, 200);
        box.setTranslateX(0);
        box.setTranslateY(-200);
        box.setTranslateZ(100);
        box.setMaterial(new PhongMaterial(Color.BLUE));
        
        // Create a cylinder
        Cylinder cylinder = new Cylinder(100, 250);
        cylinder.setTranslateX(400);
        cylinder.setTranslateY(-200);
        cylinder.setTranslateZ(100);
        cylinder.setMaterial(new PhongMaterial(Color.GREEN));
        
        // Create a pyramid (using a mesh)
        MeshView pyramid = createPyramid(200, 300);
        pyramid.setTranslateX(-400);
        pyramid.setTranslateY(200);
        pyramid.setTranslateZ(100);
        pyramid.setMaterial(new PhongMaterial(Color.YELLOW));
        
        // Create a torus (using a mesh)
        MeshView torus = createTorus(100, 40, 64);
        torus.setTranslateX(0);
        torus.setTranslateY(200);
        torus.setTranslateZ(100);
        torus.setMaterial(new PhongMaterial(Color.PURPLE));
        
        // Create a complex shape (combination)
        Group complexShape = createComplexShape();
        complexShape.setTranslateX(400);
        complexShape.setTranslateY(200);
        complexShape.setTranslateZ(100);
        
        // Add all shapes to the scene
        root3D.getChildren().addAll(sphere, box, cylinder, pyramid, torus, complexShape);
    }
    
    private MeshView createPyramid(double size, double height) {
        float s = (float) size;
        float h = (float) height;
        
        float[] points = {
            0, -h/2, 0,    // Top point
            -s/2, h/2, -s/2, // Base point 1
            s/2, h/2, -s/2,  // Base point 2
            s/2, h/2, s/2,   // Base point 3
            -s/2, h/2, s/2    // Base point 4
        };
        
        int[] faces = {
            0, 0, 1, 0, 2, 0,    // Front face
            0, 0, 2, 0, 3, 0,    // Right face
            0, 0, 3, 0, 4, 0,    // Back face
            0, 0, 4, 0, 1, 0,    // Left face
            1, 0, 2, 0, 3, 0,    // Base triangle 1
            1, 0, 3, 0, 4, 0     // Base triangle 2
        };
        
        float[] texCoords = {
            0.5f, 0,    // Top
            0, 1,       // Base 1
            1, 1,        // Base 2
            1, 0,        // Base 3
            0, 0         // Base 4
        };
        
        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().addAll(points);
        mesh.getTexCoords().addAll(texCoords);
        mesh.getFaces().addAll(faces);
        
        return new MeshView(mesh);
    }
    
    private MeshView createTorus(double radius, double tubeRadius, int divisions) {
        int divR = divisions;
        int divT = divisions;
        
        TriangleMesh mesh = new TriangleMesh();
        
        // Create points
        for (int i = 0; i < divR; i++) {
            double r = 2 * Math.PI * i / divR;
            double cosR = Math.cos(r);
            double sinR = Math.sin(r);
            
            for (int j = 0; j < divT; j++) {
                double t = 2 * Math.PI * j / divT;
                double cosT = Math.cos(t);
                double sinT = Math.sin(t);
                
                double x = (radius + tubeRadius * cosT) * cosR;
                double y = (radius + tubeRadius * cosT) * sinR;
                double z = tubeRadius * sinT;
                
                mesh.getPoints().addAll((float)x, (float)y, (float)z);
            }
        }
        
        // Create texture coordinates
        for (int i = 0; i <= divR; i++) {
            for (int j = 0; j <= divT; j++) {
                mesh.getTexCoords().addAll((float)i / divR, (float)j / divT);
            }
        }
        
        // Create faces
        for (int i = 0; i < divR; i++) {
            for (int j = 0; j < divT; j++) {
                int i0 = i * divT + j;
                int i1 = (i + 1) * divT + j;
                int i2 = i * divT + (j + 1) % divT;
                int i3 = (i + 1) * divT + (j + 1) % divT;
                
                mesh.getFaces().addAll(
                    i0, i0, i1, i1, i2, i2,
                    i1, i1, i3, i3, i2, i2
                );
            }
        }
        
        return new MeshView(mesh);
    }
    
    private Group createComplexShape() {
        Group group = new Group();
        
        // Create a central sphere
        Sphere center = new Sphere(80);
        center.setMaterial(new PhongMaterial(Color.ORANGE));
        
        // Create surrounding cones
        for (int i = 0; i < 8; i++) {
            double angle = 2 * Math.PI * i / 8;
            double x = 200 * Math.cos(angle);
            double z = 200 * Math.sin(angle);
            
            Cylinder cone = new Cylinder(40, 120);
            cone.setTranslateX(x);
            cone.setTranslateZ(z);
            cone.setRotationAxis(new Point3D(z, 0, -x));
            cone.setRotate(90);
            cone.setMaterial(new PhongMaterial(Color.CYAN));
            
            group.getChildren().add(cone);
        }
        
        group.getChildren().add(center);
        return group;
    }
    
    private void addLighting() {
        // Ambient light
        AmbientLight ambientLight = new AmbientLight(Color.rgb(80, 80, 80));
        
        // Point lights
        PointLight pointLight1 = new PointLight(Color.WHITE);
        pointLight1.setTranslateX(-500);
        pointLight1.setTranslateY(-500);
        pointLight1.setTranslateZ(-1000);
        
        PointLight pointLight2 = new PointLight(Color.rgb(200, 200, 255));
        pointLight2.setTranslateX(500);
        pointLight2.setTranslateY(500);
        pointLight2.setTranslateZ(500);
        
        // Directional light
        DirectionalLight directionalLight = new DirectionalLight(Color.rgb(150, 150, 150));
        directionalLight.setDirection(new Point3D(0.5, -1, -0.5));
        
        root3D.getChildren().addAll(ambientLight, pointLight1, pointLight2, directionalLight);
    }
    
    private void initMouseControl(Scene scene) {
        scene.setOnMousePressed(event -> {
            anchorX = event.getSceneX();
            anchorY = event.getSceneY();
            anchorAngleX = rotateX.getAngle();
            anchorAngleY = rotateY.getAngle();
        });
        
        scene.setOnMouseDragged(event -> {
            rotateX.setAngle(anchorAngleX - (anchorY - event.getSceneY()));
            rotateY.setAngle(anchorAngleY + anchorX - event.getSceneX());
        });
        
        scene.setOnScroll(event -> {
            double delta = event.getDeltaY();
            cameraDistance += delta * ZOOM_SPEED;
            camera.setTranslateZ(cameraDistance);
        });
        
        // Add keyboard controls
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W:
                    rotateX.setAngle(rotateX.getAngle() - 5);
                    break;
                case S:
                    rotateX.setAngle(rotateX.getAngle() + 5);
                    break;
                case A:
                    rotateY.setAngle(rotateY.getAngle() - 5);
                    break;
                case D:
                    rotateY.setAngle(rotateY.getAngle() + 5);
                    break;
                case R:
                    resetCamera();
                    break;
            }
        });
        
        // Apply rotation transforms to the root group
        root3D.getTransforms().addAll(rotateX, rotateY);
    }
    
    private void resetCamera() {
        rotateX.setAngle(0);
        rotateY.setAngle(0);
        cameraDistance = -2000;
        camera.setTranslateZ(cameraDistance);
    }
    
    private void addControls(Scene scene) {
        VBox controls = new VBox(10);
        controls.setAlignment(Pos.TOP_LEFT);
        controls.setPadding(new Insets(10));
        controls.setPickOnBounds(false);
        
        Label title = new Label("3D Shape Visualizer");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        Label instructions = new Label(
            "Mouse drag: Rotate\n" +
            "Scroll: Zoom\n" +
            "W/S: Rotate up/down\n" +
            "A/D: Rotate left/right\n" +
            "R: Reset view"
        );
        instructions.setStyle("-fx-text-fill: white;");
        
        Button resetBtn = new Button("Reset View");
        resetBtn.setOnAction(e -> resetCamera());
        
        CheckBox animateCheck = new CheckBox("Enable Animation");
        animateCheck.setSelected(true);
        animateCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                animation.play();
            } else {
                animation.pause();
            }
        });
        
        controls.getChildren().addAll(title, instructions, resetBtn, animateCheck);
        
        // Add controls to the scene
        root3D.getChildren().add(controls);
    }
    
    private void startAnimation() {
        animation = new Timeline(
            new KeyFrame(Duration.millis(16), event -> {
                // Auto-rotate when not dragging
                if (!root3D.getOnMouseDragged()) {
                    rotateY.setAngle(rotateY.getAngle() + ROTATION_SPEED);
                }
            })
        );
        animation.setCycleCount(Animation.INDEFINITE);
        animation.play();
    }
    
    @Override
    public void stop() {
        if (animation != null) {
            animation.stop();
        }
    }
}