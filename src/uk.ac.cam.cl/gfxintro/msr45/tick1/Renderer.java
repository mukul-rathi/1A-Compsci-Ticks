package uk.ac.cam.cl.gfxintro.msr45.tick1;

import java.awt.image.BufferedImage;
import java.util.List;

public class Renderer {
	
	// The width and height of the image in pixels
	private int width, height;
	
	// Bias factor for reflected and shadow rays
	private final double EPSILON = 0.0001;

	// The number of times a ray can bounce for reflection
	private int bounces;
	
	// Background colour of the image
	private ColorRGB backgroundColor = new ColorRGB(0.1);

	public Renderer(int width, int height, int bounces) {
		this.width = width;
		this.height = height;
		this.bounces = bounces;
	}

	/*
	 * Trace the ray through the supplied scene, returning the colour to be rendered.
	 * The bouncesLeft parameter is for rendering reflective surfaces.
	 */
	protected ColorRGB trace(Scene scene, Ray ray, int bouncesLeft) {

		// Find closest intersection of ray in the scene
		RaycastHit closestHit = scene.findClosestIntersection(ray);

        // If no object has been hit, return a background colour
        SceneObject object = closestHit.getObjectHit();
        if (object == null){
            return backgroundColor;
        }
        
        // Otherwise calculate colour at intersection and return
        // Get properties of surface at intersection - location, surface normal
        Vector3 P = closestHit.getLocation();
        Vector3 N = closestHit.getNormal();
        Vector3 O = ray.getOrigin();

     	// Illuminate the surface

     	ColorRGB directIllumination = this.illuminate(scene, object, P, N, O);

		// Get reflectivity of object
		double reflectivity = object.getReflectivity();

		if (bouncesLeft == 0 || reflectivity == 0) {
			// Base case - if no bounces left or non-reflective surface
			return directIllumination;
		}else { // Recursive case
			ColorRGB reflectedIllumination;

			//TODO: Calculate the direction R of the bounced ray

			Vector3 origRayDirection = O.subtract(P).normalised();
			Vector3 R = origRayDirection.reflectIn(N.normalised()).normalised();
			//TODO: Spawn a reflectedRay with bias
			Ray reflectedRay = new Ray(P.add(R.scale(EPSILON)),R);
			//TODO: Calculate reflectedIllumination by tracing reflectedRay
			 reflectedIllumination = trace(scene,reflectedRay,bouncesLeft-1);
			// Scale direct and reflective illumination to conserve light
			directIllumination = directIllumination.scale(1.0 -reflectivity);
			reflectedIllumination = reflectedIllumination.scale(reflectivity);
			return directIllumination.add(reflectedIllumination);

		}

	}

	/*
	 * Illuminate a surface on and object in the scene at a given position P and surface normal N,
	 * relative to ray originating at O
	 */
	private ColorRGB illuminate(Scene scene, SceneObject object, Vector3 P, Vector3 N, Vector3 O) {
	   
		ColorRGB colourToReturn = new ColorRGB(0);

		ColorRGB I_a = scene.getAmbientLighting(); // Ambient illumination intensity

		ColorRGB C_diff = object.getColour(); // Diffuse colour defined by the object
		
		// Get Phong coefficients
		double k_d = object.getPhong_kD();
		double k_s = object.getPhong_kS();
		double alpha = object.getPhong_alpha();

		colourToReturn = C_diff.scale(I_a);

		// Loop over each point light source
		List<PointLight> pointLights = scene.getPointLights();
		for (int i = 0; i < pointLights.size(); i++) {
			PointLight light = pointLights.get(i); // Select point light
			
			// Calculate point light constants
			double distanceToLight = light.getPosition().subtract(P).magnitude();
			ColorRGB C_spec = light.getColour();
			ColorRGB I = light.getIlluminationAt(distanceToLight);


			Vector3 LHat = light.getPosition().subtract(P).normalised();
			Vector3 NHat = N.normalised();
			Vector3 VHat = O.subtract(P).normalised();
			Vector3 RHat = LHat.reflectIn(NHat).normalised();

			double maxDiffuse = NHat.dot(LHat);
			maxDiffuse = (maxDiffuse>0) ? maxDiffuse : 0;
			ColorRGB diffuse = C_diff.scale(k_d).scale(I).scale(maxDiffuse);

			double maxSpecular = Math.pow((RHat.dot(VHat)),alpha);
			 maxSpecular = (maxSpecular>0) ? maxSpecular : 0;
			ColorRGB specular = C_spec.scale(k_s).scale(I).scale(maxSpecular);

			Ray shadowRay = new Ray(P.add(LHat.scale(EPSILON)), LHat);

			RaycastHit intersect = scene.findClosestIntersection(shadowRay);


			if(intersect.getObjectHit()==null || intersect.getDistance()>distanceToLight){
				colourToReturn = colourToReturn.add(diffuse).add(specular);

			}



		}
		return colourToReturn;
	}

	// Render image from scene, with camera at origin
	public BufferedImage render(Scene scene) {
		
		// Set up image
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		// Set up camera
		Camera camera = new Camera(width, height);

		// Loop over all pixels
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				Ray ray = camera.castRay(x, y); // Cast ray through pixel
				ColorRGB pixel = trace(scene, ray, bounces); // Trace path of cast ray and determine colour
				image.setRGB(x, y, pixel.toRGB()); // Set image colour to traced colour
			}
			// Display progress
			System.out.println(String.format("%.2f", 100 * y / (float) (height - 1)) + "% completed");
		}
		return image;
	}
}
