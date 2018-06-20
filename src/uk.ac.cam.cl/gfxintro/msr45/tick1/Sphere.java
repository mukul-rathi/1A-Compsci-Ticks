package uk.ac.cam.cl.gfxintro.msr45.tick1;

public class Sphere extends SceneObject {

	// Sphere coefficients
	private final double SPHERE_KD = 0.8;
	private final double SPHERE_KS = 0.8;
	private final double SPHERE_ALPHA = 10;
	private final double SPHERE_REFLECTIVITY = 0.3;

	// The world-space position of the sphere
	private Vector3 position;

	public Vector3 getPosition() {
		return position;
	}

	// The radius of the sphere in world units
	private double radius;

	public Sphere(Vector3 position, double radius, ColorRGB colour) {
		this.position = position;
		this.radius = radius;
		this.colour = colour;

		this.phong_kD = SPHERE_KD;
		this.phong_kS = SPHERE_KS;
		this.phong_alpha = SPHERE_ALPHA;
		this.reflectivity = SPHERE_REFLECTIVITY;
	}

	/*
	 * Calculate intersection of the sphere with the ray. If the ray starts inside the sphere,
	 * intersection with the surface is also found.
	 */
	public RaycastHit intersectionWith(Ray ray) {

		// Get ray parameters
		Vector3 O = ray.getOrigin();
		Vector3 D = ray.getDirection();
		
		// Get sphere parameters
		Vector3 C = position;
		double r = radius;

		// Calculate quadratic coefficients
		double a = D.dot(D);
		double b = 2 * D.dot(O.subtract(C));
		double c = (O.subtract(C)).dot(O.subtract(C)) - Math.pow(r, 2);
		
		double discriminant = b*b -4*a*c;
		if(discriminant<0){
			return new RaycastHit();
		}
		double srootplus = (-b+Math.sqrt(discriminant))/(2*a);
		if(srootplus<0){
			return new RaycastHit();
		}
		double srootminus = (-b-Math.sqrt(discriminant))/(2*a);
		double sclosest;
		if(srootminus>=0){
			sclosest = srootminus;
		}
		else {
			sclosest = srootplus;
		}
		Vector3 intersectionVec = ray.evaluateAt(sclosest);
		Vector3 getNormalVec = getNormalAt(intersectionVec);
		return new RaycastHit(this,sclosest,intersectionVec,getNormalVec);
	}

	// Get normal to surface at position
	public Vector3 getNormalAt(Vector3 position) {
		return position.subtract(this.position).normalised();
	}
}
