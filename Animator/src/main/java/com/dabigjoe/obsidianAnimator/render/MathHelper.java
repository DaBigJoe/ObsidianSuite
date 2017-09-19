package com.dabigjoe.obsidianAnimator.render;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.lwjgl.util.vector.Quaternion;

import com.dabigjoe.obsidianAPI.render.wavefront.Face;

import net.minecraft.util.math.Vec3d;

public class MathHelper 
{

	private static final float rotationWheelWidth = 0.1F;
	public static final float rotationWheelRadius = 0.5F;

	public static Double rayIntersectsFace(RayTrace ray, Face f)
	{		
		Vec3d v0 = new Vec3d(f.vertices[0].x, f.vertices[0].y, f.vertices[0].z);
		Vec3d v1 = new Vec3d(f.vertices[1].x, f.vertices[1].y, f.vertices[1].z);
		Vec3d v2 = new Vec3d(f.vertices[2].x, f.vertices[2].y, f.vertices[2].z);
		Vec3d n = v0.subtract(v1).crossProduct(v0.subtract(v2));
		Vec3d pI = getRayPlaneIntersection(ray,v0,n);

		if(pI == null)
			return null;
		
		if(pointWithinTriangle(pI, v0, v1, v2))
			return ray.p0.distanceTo(pI);

		//Extra check if quad face
		if(f.vertices.length == 4)
		{
			Vec3d v4 = new Vec3d(f.vertices[3].x, f.vertices[3].y, f.vertices[3].z);
			
			//Two extra checks cover all bases of vertex order
			if(pointWithinTriangle(pI, v4, v1, v2))
				return ray.p0.distanceTo(pI);
			if(pointWithinTriangle(pI, v0, v4, v2))
				return ray.p0.distanceTo(pI);
		}
		
		return null;
	}
	
	/**
	 * Check if a point is within a triangle.
	 * Point is assumed to on same plane as triangle.
	 */
	private static boolean pointWithinTriangle(Vec3d p, Vec3d v0, Vec3d v1, Vec3d v2)
	{
		Vec3d u = v0.subtract(v1);
		Vec3d v = v0.subtract(v2);
		Vec3d w = v0.subtract(p);
		//System.out.println(u + " " + v + " " + w);
		double dn = u.dotProduct(v)*u.dotProduct(v)-u.dotProduct(u)*v.dotProduct(v);
		double s = (u.dotProduct(v)*w.dotProduct(v)-v.dotProduct(v)*w.dotProduct(u))/dn;
		double t = (u.dotProduct(v)*w.dotProduct(u)-u.dotProduct(u)*w.dotProduct(v))/dn;
		if(s>=0 && t>=0 && s+t<=1 && s != -0.0F && t != -0.0F)
			return true;
		return false;
	}

	/**
	 * Calculate the distance between the start of the ray and the rotation wheel.
	 * @param ray - Ray to test.
	 * @param p - Centre of rotation wheel.
	 * @param n - Normal to rotation wheel.
	 * @return - Distance or null if not intersection.
	 */
	public static Double rayIntersectsRotationWheel(RayTrace ray, Vec3d p, Vec3d n)
	{
		Vec3d pI = getRayPlaneIntersection(ray,p,n);
		if(pI == null)
			return null;
		double d = pI.distanceTo(p);
		if(d > rotationWheelRadius - rotationWheelWidth && d < rotationWheelRadius + rotationWheelWidth)
			return ray.p0.distanceTo(pI);
		return null;
	}
	
	/**
	 * Calculate the distance between the start of the ray and the axis slider.
	 * Assumes axis slider starts at origin.
	 * @param ray - Ray to test.
	 * @param p - End of slider.
	 * @param n - Normal to slider.
	 * @return - Distance or null if no intersection.
	 */
	public static Double rayIntersectsAxisSlider(RayTrace ray, Vec3d p, Vec3d n)
	{
		Vec3d pI = getRayPlaneIntersection(ray,p,n);
		if(pI == null)
			return null;
		double t = com.dabigjoe.obsidianAPI.MathHelper.getLineScalarForClosestPoint(new Vec3d(0, 0, 0), p, pI);
		double d;
		if(t < 0)
			d = pI.lengthVector();
		else if(t > 1.0F)
			d = pI.subtract(p).lengthVector();
		else
			d = pI.subtract(scale(p,t)).lengthVector();
		if(d < 0.05F)
			return ray.p0.distanceTo(pI);
		return null;
	}

	/**
	 * Calculate the point of intersection between a ray and a plane.
	 * @param ray - Ray to test.
	 * @param p - Point on plane.
	 * @param n - Normal to plane.
	 * @return - Point on plane where ray intersects, null if no intersection.
	 */
	public static Vec3d getRayPlaneIntersection(RayTrace ray, Vec3d p, Vec3d n)
	{
		//Vector in direction of line
		Vec3d l = ray.p1.subtract(ray.p0);

		double rd = n.dotProduct(l);
		if(rd == 0)
			return p;
		double r = n.dotProduct(p.subtract(ray.p1))/rd;
		return addVector(ray.p1, scale(l,r));
	}

	public static double getAngleBetweenVectors(Vec3d v, Vec3d w, Vec3d n)
	{
		double angleDot =  Math.acos(v.dotProduct(w)/(v.lengthVector()*w.lengthVector()));
		Vec3d crossProduct = v.crossProduct(w);
		if(n.dotProduct(crossProduct) < 0)
			angleDot *= -1;
		return angleDot;
	}

	public static Vec3d addVector(Vec3d v, Vec3d w)
	{
		return new Vec3d(v.x + w.x, v.y + w.y, v.z + w.z);
	}

	public static Vec3d scale(Vec3d v, double scale)
	{
		return new Vec3d(v.x*scale, v.y*scale, v.z*scale);
	}

	public static float[] intToRGB(int color)
	{
		float[] rgb = new float[3];
		long blue = color%256;
		color = color/256;
		long green = color%256;
		color = color/256;
		long red = color%256;

		rgb[0] = red/255F;
		rgb[1] = green/255F;
		rgb[2] = blue/255F;

		return rgb;
	}
//	
//	public static int colorToInt(Color c)
//	{
//		int colour;
//		colour = (int) (c.getRed()*0xFF0000);
//		colour += (int) (c.getRed()*0x00FF00);
//		colour += (int) (b*0x0000FF);
//		return colour;
//	}

	public static FloatBuffer quarternionToMatrix(Quaternion q, FloatBuffer buffer)
	{		
		//System.out.println(q);
		//if(q.length() != 0)
		//	System.out.println("n:" + q.normalise());
		
//		System.out.println("Matrix before:");
//		System.out.println(String.format("%f %f %f %f", buffer.get(0), buffer.get(1), buffer.get(2), buffer.get(3)));
//		System.out.println(String.format("%f %f %f %f", buffer.get(4), buffer.get(5), buffer.get(6), buffer.get(7)));
//		System.out.println(String.format("%f %f %f %f", buffer.get(8), buffer.get(9), buffer.get(10), buffer.get(11)));
//		System.out.println(String.format("%f %f %f %f", buffer.get(12), buffer.get(13), buffer.get(14), buffer.get(15)));

		//System.out.println(q);
		
//		q = new Quaternion(0.5F, -0.1F, 0.4F, 0.8F);
//		q.normalise();
		
		FloatBuffer buffer2;
		float[] rotationMatrixF = new 
				float[]{1,0,0,0,
						0,1,0,0,
						0,0,1,0,
						0,0,0,1};
		ByteBuffer vbb = ByteBuffer.allocateDirect(rotationMatrixF.length*4);
		vbb.order(ByteOrder.nativeOrder());
		buffer2 = vbb.asFloatBuffer();
		buffer2.put(buffer);
		buffer.position(0);
		buffer2.position(0);
		
		float xx2=q.x*q.x*2;
		float yy2=q.y*q.y*2;
		float zz2=q.z*q.z*2;
		float xy2 = q.x*q.y*2;
		float xz2 = q.x*q.z*2;
		float yz2 = q.y*q.z*2;
		float wx2 = q.w*q.x*2;
		float wy2 = q.w*q.y*2;
		float wz2 = q.w*q.z*2;
		
//		buffer2.position(0);
//		buffer2.put(1-yy2-zz2);
//		buffer2.put(xy2+wz2);
//		buffer2.put(xz2-wy2);
//		buffer2.put(0);
//		buffer2.put(xy2-wz2);
//		buffer2.put(1-xx2-zz2);
//		buffer2.put(yz2+wx2);
//		buffer2.put(0);
//		buffer2.put(xz2+wy2);
//		buffer2.put(yz2-wx2);
//		buffer2.put(1-xx2-yy2);
//		buffer2.put(0);
//		buffer2.position(0);
		
		buffer2.position(0);
		buffer2.put(1-yy2-zz2);
		buffer2.put(xy2-wz2);
		buffer2.put(xz2+wy2);
		buffer2.put(0);
		buffer2.put(xy2+wz2);
		buffer2.put(1-xx2-zz2);
		buffer2.put(yz2-wx2);
		buffer2.put(0);
		buffer2.put(xz2-wy2);
		buffer2.put(yz2+wx2);
		buffer2.put(1-xx2-yy2);
		buffer2.put(0);
		buffer2.position(0);
		
//		System.out.println("Matrix after:");
//		System.out.println(String.format("%f %f %f %f", buffer2.get(0), buffer2.get(1), buffer2.get(2), buffer2.get(3)));
//		System.out.println(String.format("%f %f %f %f", buffer2.get(4), buffer2.get(5), buffer2.get(6), buffer2.get(7)));
//		System.out.println(String.format("%f %f %f %f", buffer2.get(8), buffer2.get(9), buffer2.get(10), buffer2.get(11)));
//		System.out.println(String.format("%f %f %f %f", buffer2.get(12), buffer2.get(13), buffer2.get(14), buffer2.get(15)));

		return buffer;
	}

}
