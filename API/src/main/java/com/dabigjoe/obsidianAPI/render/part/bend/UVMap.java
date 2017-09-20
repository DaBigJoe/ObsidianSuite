package com.dabigjoe.obsidianAPI.render.part.bend;

import com.dabigjoe.obsidianAPI.render.wavefront.Face;
import com.dabigjoe.obsidianAPI.render.wavefront.TextureCoordinate;
import com.dabigjoe.obsidianAPI.render.wavefront.Vertex;

import net.minecraft.util.math.Vec3d;

public class UVMap {

	//Normal of face
	Vec3d normal;
	//Where the top left vertex of the face transforms to.
	Vec3d origin;
	//Transform to move face so it passes through the origin.
	Vec3d zAxisTranslation;
	//Rotation matrix to make face flat on xy plane.
	double[] rotationMatrix;
	//Translation to move top left vertex onto top left texture coordinate
	Vec3d topLeftTranslation;
	//Scalars to map vertices to texture coordinates.
	double horizontalScalar, verticalScalar;
	
	public UVMap(Face face) {
		this(
			new Vec3d(face.vertices[0].x, face.vertices[0].y, face.vertices[0].z),
			face.textureCoordinates[0],
			new Vec3d(face.vertices[2].x, face.vertices[2].y, face.vertices[2].z),
			face.textureCoordinates[2],
			new Vec3d(face.faceNormal.x, face.faceNormal.y, face.faceNormal.z));
	}
	
	private UVMap(Vec3d faceTopLeft, TextureCoordinate tcTopLeft, Vec3d faceBottomRight, TextureCoordinate tcBottomRight, Vec3d normal) {		
		this.normal = normal;
		
		//Convert to plane equation.
		double a = normal.x;
		double b = normal.y;
		double c = normal.z;
		double d = -(a*faceTopLeft.x+b*faceTopLeft.y+c*faceTopLeft.z);
		
		//Setup zAxisTransform
		this.zAxisTranslation = new Vec3d(0,0,c == 0 ? 0 : d/c);
				
		//Working value
		double w = Math.sqrt(a*a+b*b+c*c);
		
		//Translate faceTopLeft to origin - p_orig is point on the plane that goes through origin
		// and is orthogonal to n
		Vec3d p_origin = faceTopLeft.add(zAxisTranslation);	
		
		//Axis of rotation (u1, u2, 0)^T
		double u1 = b/w;
		double u2 = -a/w;
		
		//Make sure it's a unit vector
		double sU = (u1*u1+u2*u2);
		if(sU != 0) {
			double s = Math.sqrt(1/sU);
			u1 = u1*s;
			u2 = u2*s;
		}
				
		//Define trig functions of angle between normal and z axis
		double cosT = c/w;
		double sinT = Math.sqrt(1 - cosT*cosT);
		
		//Setup rotation matrix
		rotationMatrix = new double[9];
		rotationMatrix[0] = cosT + u1*u1*(1-cosT);
		rotationMatrix[1] = u1*u2*(1-cosT);
		rotationMatrix[2] = u2*sinT;
		rotationMatrix[3] = u1*u2*(1-cosT);
		rotationMatrix[4] = cosT + u2*u2*(1-cosT);
		rotationMatrix[5] = -u1*sinT;
		rotationMatrix[6] = -u2*sinT;
		rotationMatrix[7] = u1*sinT;
		rotationMatrix[8] = cosT;
		
		//Create rotated face
		Vec3d p_rotated = rotatePoint(p_origin);
		
		//Texture alignment
		this.topLeftTranslation = new Vec3d(tcTopLeft.u - p_rotated.x, tcTopLeft.v - p_rotated.y, 0);	
		
		//Setup origin
		origin = transformPointToUVMap(faceTopLeft, false);
		
		//Transform face bottom right to work out horizontal and vertical scalars.
		Vec3d faceBottomRightTransformed = transformPointToUVMap(faceBottomRight, false);
		double dx = (faceBottomRightTransformed.x-origin.x);
		double dy = (faceBottomRightTransformed.y-origin.y);
		horizontalScalar = (tcBottomRight.u-origin.x)/(dx == 0 ? 1 : dx);
		verticalScalar = (tcBottomRight.v-origin.y)/(dy == 0 ? 1 : dy);
		
	}
	
	/**
	 * Transform a point in the plane of the face onto the UV map.
	 */
	private Vec3d transformPointToUVMap(Vec3d p, boolean inverted) {
		Vec3d p1 = p.add(zAxisTranslation);
		Vec3d p2 = rotatePoint(p1);
		Vec3d p3 = p2.add(topLeftTranslation);
		return p3;
	}
	
	/**
	 * Get the equivalent texture coordinate of a point on the UV map.
	 */
	private TextureCoordinate getCoord(Vec3d p, boolean inverted) {
		Vec3d t = transformPointToUVMap(p, inverted);
		return new TextureCoordinate((float)(origin.x + horizontalScalar*(t.x-origin.x)), (float)(origin.y + verticalScalar*(t.y-origin.y)));
	}
	
	public TextureCoordinate getCoord(Vertex v, boolean inverted) {
		return getCoord(new Vec3d(v.x, v.y, v.z), inverted);
	}
	
	/**
	 * Rotate a point by the rotation matrix.
	 */
    private Vec3d rotatePoint(Vec3d p)
    {
        return new Vec3d(
				rotationMatrix[0]*p.x + rotationMatrix[1]*p.y + rotationMatrix[2]*p.z,
				rotationMatrix[3]*p.x + rotationMatrix[4]*p.y + rotationMatrix[5]*p.z,
				rotationMatrix[6]*p.x + rotationMatrix[7]*p.y + rotationMatrix[8]*p.z);
    }
    
	public static void main(String args[]) {
		Vec3d faceTopLeft = new Vec3d(-0.1875, 0.5625, -0.1875);
		TextureCoordinate tcTopLeft = new TextureCoordinate(0.5625F, 0.1875F);
		Vec3d faceBottomRight = new Vec3d(0.1875, 0.5625, 0.1875);
		TextureCoordinate tcBottomRight = new TextureCoordinate(0.65625F, 0.0F);
		Vec3d normal = new Vec3d(0, 0, 1);
		
		UVMap map = new UVMap(faceTopLeft, tcTopLeft, faceBottomRight, tcBottomRight, normal);
		
		TextureCoordinate tc = map.getCoord(new Vec3d(-0.1875, 0.5625, -0.1875), false);
		System.out.println(tc.u + " " + tc.v);
		tc = map.getCoord(new Vec3d(0.1875, 0.5625, -0.1875), false);
		System.out.println(tc.u + " " + tc.v);
		tc = map.getCoord(new Vec3d(0.1875, 0.5625, 0.1875), false);
		System.out.println(tc.u + " " + tc.v);
		tc = map.getCoord(new Vec3d(-0.1875, 0.5625, 0.1875), false);
		System.out.println(tc.u + " " + tc.v);
	}
	
	
}
