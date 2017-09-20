package com.dabigjoe.obsidianAPI.render.part.bend;

import org.lwjgl.opengl.GL11;

import com.dabigjoe.obsidianAPI.MathHelper;
import com.dabigjoe.obsidianAPI.render.wavefront.Vertex;

import net.minecraft.util.math.Vec3d;

/**
 * Used for calculations involving cubic bezier curves.
 * The curve is generated using six points: a1,a2,b1,b2,c1 and c2.
 * Diagram: http://imgur.com/C5JOrRB
 * a1 to a2 and b1 to b2 are straight lines.
 * c1 and c2 are control points. c1 lies on the same line as
 * a1 and a2, c2, on the line containing b1 and b2.
 * The curve is actually drawn between a2 and b2.
 * c1 and c2 can be calculated from a1,a2,b1 and b2.
 * <p>
 * c1 and c2 are always the same distance from a2 (1.2*difference in a2.y and defaulty)
 * <p>
 * This also has a debugging feature, which renders a mesh between a,b2 and c1/c2.
 * Very useful to see where the control points are.
 */

public class BezierCurve
{

    private Vertex a1, a2, b1, b2, c1, c2, bendRotPoint;

    public BezierCurve(Vertex a1, Vertex a2, Vertex b1, Vertex b2, Vertex bendRotPoint)
    {
        this.a1 = a1;
        this.a2 = a2;
        this.b1 = b1;
        this.b2 = b2;
        this.bendRotPoint = bendRotPoint;
        setupControlVertices();
    }

    /**
     * Setup c1 and c2.
     */
    private void setupControlVertices()
    {
        //Line l1: v1 = p1 + t*d1.
        Vec3d p1 = new Vec3d(a1.x, a1.y, a1.z);
        Vec3d d1 = getDirectionVector(a1, a2);
        //Line l2: v2 = p2 + u*d2.
        Vec3d p2 = new Vec3d(b1.x, b1.y, b1.z);
        Vec3d d2 = getDirectionVector(b1, b2);
        //Rotation point
        Vec3d r = new Vec3d(bendRotPoint.x, bendRotPoint.y, bendRotPoint.z);
        
        //S is the scalar value that will be used to position the control vertices.
        double s = MathHelper.getLineScalarForClosestPoint(p1, d1, r);

        //Point on line1 = p1 + scalar*d1
        //Point on line2 = p2 + scalar*d2
        Vec3d cl1 = p1.addVector(d1.x * s, d1.y * s, d1.z * s);
        Vec3d cl2 = p2.addVector(d2.x * s, d2.y * s, d2.z * s);

        //Convert to vertex.
        c1 = new Vertex((float) cl1.x, (float) cl1.y, (float) cl1.z);
        c2 = new Vertex((float) cl2.x, (float) cl2.y, (float) cl2.z);
    }

    /**
     * Return a point on the curve based on the parameter t.
     *
     * @param t - A value between 0 and 1. 0 will give the point a, 1 gives the point b2, and anywhere inbetween gives a point of the curve.
     */
    public Vertex getVertexOnCurve(float t)
    {
        if (t >= 0 && t <= 1)
        {
            //(1-t)^3*a + 3(1-t)^2*t*c1 + 3(1-t)*t^2*c2 + t^3*b2
            float x = cube(1 - t) * a2.x + 3 * square(1 - t) * t * c1.x + 3 * (1 - t) * square(t) * c2.x + cube(t) * b2.x;
            float y = cube(1 - t) * a2.y + 3 * square(1 - t) * t * c1.y + 3 * (1 - t) * square(t) * c2.y + cube(t) * b2.y;
            float z = cube(1 - t) * a2.z + 3 * square(1 - t) * t * c1.z + 3 * (1 - t) * square(t) * c2.z + cube(t) * b2.z;

            return new Vertex(x, y, z);
        }
        throw new RuntimeException("Cannot get point on bezier curve for t value " + t + ". Outside of valid range (0 to 1).");
    }

    private float square(float f)
    {
        return f * f;
    }

    private float cube(float f)
    {
        return f * f * f;
    }

    /**
     * Debug purposes only
     */
    public void render()
    {
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GL11.glLineWidth(2.0f);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex3d(a1.x,a1.y,a1.z);
		GL11.glVertex3d(a2.x,a2.y,a2.z);
		GL11.glVertex3d(c1.x,c1.y,c1.z);
		GL11.glVertex3d(c2.x,c2.y,c2.z);
		GL11.glVertex3d(b2.x,b2.y,b2.z);
		GL11.glVertex3d(b1.x,b1.y,b1.z);
		GL11.glEnd();
		
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();	
    }

    /**
     * Get the direction vector from p to q.
     */
    private Vec3d getDirectionVector(Vertex p, Vertex q)
    {
        Vec3d pVec = new Vec3d(p.x, p.y, p.z);
        Vec3d qVec = new Vec3d(q.x, q.y, q.z);
        return pVec.subtract(qVec);
    }
}

