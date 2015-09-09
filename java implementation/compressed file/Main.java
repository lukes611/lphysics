import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

//Java code by Luke Lincoln
public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//init objects
		P2i[] ov1 = new P2i[]{new P2i(-5, -7), new P2i(-4,3), new P2i(15, 2), new P2i(14, -6)};
		P2i[] ov2 = new P2i[]{new P2i(-2, 6), new P2i(4,6), new P2i(4, -6), new P2i(-2, -6)};
		P2i[] ov3 = new P2i[]{new P2i(0,0), new P2i(80, 0), new P2i(80, -10), new P2i(0, -10)};
		P2i[] ov4 = new P2i[]{new P2i(17,84), new P2i(74, 84), new P2i(88, 34), new P2i(45, 4), new P2i(2,34)};
		for(int i = 0; i <ov1.length; i++) ov1[i].scale(1.3);
		for(int i = 0; i <ov2.length; i++) ov2[i].scale(1.3);
		for(int i = 0; i <ov3.length; i++) ov3[i].scale(1.6);
		for(int i = 0; i <ov4.length; i++) ov4[i].scale(0.4);
		//var draw_list = [];

		
		LScene scene = new LScene();
		scene.add_object(ov1, false, new P2i(35, 30));
		scene.add_object(ov1, false, new P2i(86, 30), new P2i(5,-.5), 8/57.3);
		scene.add_object(ov4, false, new P2i(280, 95), new P2i(-1,-2), 8/57.3);
		scene.add_object(ov2, false, new P2i(380, 40), new P2i(-4.5,-2.5), 2/57.3);
		scene.add_object(ov4, false, new P2i(380, 100), new P2i(-3,-4.5), 2/57.3);
		scene.add_object(ov2, false, new P2i(230, 100), new P2i(3,-4.5), -1/57.3);
		scene.add_object(ov2, false, new P2i(66.66, 30), new P2i(0,0), -5.2/57.4);
		scene.add_object(ov2, false, new P2i(35, 160), new P2i(10,-2), -3/57.4);
		scene.add_object(ov2, false, new P2i(260, 185), new P2i(-10,-3.0), -3/57.4);
		scene.add_object(ov1, true, new P2i(22, 100));
		scene.add_object(ov1, true, new P2i(60, 100));
		scene.add_object(ov3, true, new P2i(360, 160), -45/57.3);
	
		
		//end
		
		JFrame frame = new JFrame();
		LGraphics g = new LGraphics(800,500);
		g.set_scene(scene);
		frame.setSize(800, 400);
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		frame.add(g);
		frame.setVisible(true);
		
		while(true)
		{
			scene.step(0.05);
			scene.apply_physics();
			
			g.repaint();
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}



class LShape {

	P2i[] points;
	public LShape(P2i pts[], boolean not_to_origin)
	{
		this.points = new P2i[pts.length];
		for(int i = 0; i < this.points.length; i++)
			this.points[i] = pts[i].cpy();
		if(!not_to_origin) this.to_origin();
	}
	
	public LShape(P2i pts[])
	{
		this.points = new P2i[pts.length];
		for(int i = 0; i < this.points.length; i++)
			this.points[i] = pts[i].cpy();
		this.to_origin();
	}
	
	public Bbox get_bbox()
	{
		P2i mx, mn;
		if(this.points.length >= 1)
		{
			mn = this.points[0].cpy();
			mx = mn.cpy();
		}else
		{
			mn = new P2i(0,0);
			mx = new P2i(0,0);
		}
		
		
		for(int i = 1; i < this.points.length; i++)
		{	
			if(mn.x > this.points[i].x) mn.x = this.points[i].x;
			if(mn.y > this.points[i].y) mn.y = this.points[i].y;
			if(mx.x < this.points[i].x) mx.x = this.points[i].x;
			if(mx.y < this.points[i].y) mx.y = this.points[i].y;
		}
		return new Bbox(mn, mx);
	}

	public double compute_mass()
	{
		Bbox bb = this.get_bbox();
		double s = Math.max(bb.mx.x - bb.mn.x, bb.mx.y-bb.mn.y);
		int q = 100;
		boolean d[] = new boolean[q*q];
		for(int i = 0; i < d.length; i++) d[i] = false;
		for(int i = 0; i < this.points.length; i++)
		{
			int j = (i+1) % this.points.length;
			P2i p1 = this.points[i].cpy().subtract(bb.mn).scale(q / s);
			P2i p2 = this.points[j].cpy().subtract(bb.mn).scale(q / s);
			P2i ray = p2.cpy().subtract(p1);
			double mag = ray.mag();
			ray.divide(mag);
			int m = (int)Math.round(mag);
			for(int h = 0; h < m; h++)
			{
				P2i p1c = p1.cpy().round();
				if(p1c.x >= 0 && p1c.y >= 0 && p1c.x < q && p1c.y < q)
				{
					d[(int)p1c.y*q + (int)p1c.x] = true;
				}
				p1.add(ray);
			}
		}
		boolean start = false;
		int tmp_count = 0;
		int count = 0;
		for(int y = 0; y < q; y++)
		{
			tmp_count = 0;
			start = false;
			for(int x = 0; x < q; x++)
			{
				boolean v = d[y*q + x];
				if(v)
				{
					if(start) //if already started
					{
						count += tmp_count;
						tmp_count = 0;
						start = false;
					}else
					{
						tmp_count = 0;
						start = true;
					}
					
				}
				if(start) tmp_count++;
			}
		}
		return (count / q) * s;
	}

	public LShape cpy()
	{
		return new LShape(this.points);
	}

	public void to_origin()
	{
		P2i avg = this.get_avg();
		for(int i = 0; i < this.points.length; i++) this.points[i].subtract(avg);
	}

	public P2i get_avg()
	{
		P2i rv = new P2i(0,0);
		for(int i = 0; i < this.points.length; i++)
			rv.add(this.points[i]);
		if(this.points.length != 0) rv.divide(this.points.length);
		return rv;
	}

	public double get_rad()
	{
		P2i avg = this.get_avg();
		double r = this.points[0].dist(avg);
		for(int i = 1; i < this.points.length; i++)
		{
			double tmp = this.points[i].dist(avg);
			r = (tmp > r) ? tmp : r;
		}
		return r;
	}
	
	private boolean collision_there(P2i p, LShape s2)
	{
		double mn1 = this.points[0].projection_scalar(p);
		double mx1 = mn1;
		double mn2 = s2.points[0].projection_scalar(p);
		double mx2 = mn2;
		for(int i = 1; i < this.points.length; i++)
		{
			double tmp = this.points[i].projection_scalar(p);
			if(tmp < mn1) mn1 = tmp;
			if(tmp > mx1) mx1 = tmp;
		}
		for(int i = 1; i < s2.points.length; i++)
		{
			double tmp = s2.points[i].projection_scalar(p);
			if(tmp < mn2) mn2 = tmp;
			if(tmp > mx2) mx2 = tmp;
		}
		if(mx1 < mn2 || mx2 < mn1) return false;
		return true;
	}

	//could be issue
	public boolean collision(LShape s2)
	{
		if(this.points.length == 0 || s2.points.length == 0) return false;
		ArrayList<P2i> normals_to_check = new ArrayList<P2i>();
		for(int i = 0; i < this.points.length; i++)
		{
			int j = (i+1) % this.points.length;
			P2i line = this.points[j].cpy().subtract(this.points[i]);
			normals_to_check.add(line.get_normal());
		}
		for(int i = 0; i < s2.points.length; i++)
		{
			int j = (i+1) % s2.points.length;
			P2i line = s2.points[j].cpy().subtract(s2.points[i]);
			normals_to_check.add(line.get_normal());
		}
		
		for(int i = 0; i < normals_to_check.size(); i++)
		{
			if(!collision_there(normals_to_check.get(i), s2)) return false;
		}
		
		return true;
	}


	public Collision surrounds_point(P2i p)
	{
		//need point, penetration & normal
		Collision rv = new Collision(true, 1000000000.0, p.cpy(), new P2i(0,0));
		boolean set = false;
		for(int i = 0; i < this.points.length; i++)
		{
			int j = (i+1) % this.points.length;
			P2i n = this.points[j].cpy().subtract(this.points[i]).get_normal();
			double D = n.cpy().inv().dot(this.points[i]);
			double v = n.dot(p) + D;
			if(v > 0.0)
			{
				rv.rv = false;
				return rv;
			}else if(!set || Math.abs(v) < rv.dist)
			{
				rv.rv = true;
				rv.dist = Math.abs(v);
				rv.norm = n.cpy();
				set = true;
			}
		}
		return rv;
	}

	public ArrayList<Collision> collision_detection(LShape s2)
	{
		ArrayList<Collision> rv = new ArrayList<Collision>();
		Bbox bb1 = this.get_bbox();
		Bbox bb2 = s2.get_bbox();
		if(!bb1.collision(bb2)) return rv;
		if(!this.collision(s2)) return rv;
		for(int i = 0; i < s2.points.length; i++)
		{
			Collision test = this.surrounds_point(s2.points[i]);
			if(test.rv)
			{
				test.norm.inv();
				rv.add(test);
			}
		}
		for(int i = 0; i < this.points.length; i++)
		{
			Collision test = s2.surrounds_point(this.points[i]);
			if(test.rv)
			{
				rv.add(test);
			}
		}
		return rv;
	}

	
}



class LScene {


	ArrayList<LObj> objects;
	int num_integrates;
	
	public LScene()
	{
		this.objects = new ArrayList<LObj>();
		this.num_integrates = 100;
	}
	
	public void add_object(P2i[] pnts, boolean is_fixed)
	{
		this.objects.add(new LObj(pnts, is_fixed));
	}
	
	public void add_object(P2i[] pnts, boolean is_fixed, P2i pos)
	{
		
		LObj ob = new LObj(pnts, is_fixed);
		ob.pos = pos.cpy();
		this.objects.add(ob);
	}
	
	public void add_object(P2i[] pnts, boolean is_fixed, P2i pos, P2i vel, double ang)
	{
		LObj ob = new LObj(pnts, is_fixed);
		ob.pos = pos.cpy();
		ob.vel = vel.cpy();
		ob.ang_vel = ang;
		this.objects.add(ob);
	}
	
	public void add_object(P2i[] pnts, boolean is_fixed, P2i pos, double orient)
	{
		LObj ob = new LObj(pnts, is_fixed);
		ob.pos = pos.cpy();
		ob.orient = orient;
		
		this.objects.add(ob);
	}
	
	public void step(double t)
	{
		double td = t / (double)this.num_integrates;
		for(int i = 0; i < this.objects.size(); i++)
			for(int j = 0; j < this.num_integrates; j++)
				this.objects.get(i).step(td);
	}
	
	public void apply_physics()
	{
		 for(int i = 0; i < this.objects.size()-1; i++)
		 {
			 for(int j = i+1; j < this.objects.size(); j++)
			 {
				LObj ob1 = this.objects.get(i);
				LObj ob2 = this.objects.get(j);
				if(ob1.fixed && ob2.fixed) continue;
				LShape s1 = ob1.get_shape();
				LShape s2 = ob2.get_shape();
				ArrayList<Collision> col = s1.collision_detection(s2);
				//for collision i need: 
				/*
					whether there was a collision:
					a list of collisions:
						the point of collision
						the penetration or distance
						the normal of the surface involved
				*/
				if(col.size() > 0)
				{
					double n = (double)col.size();
					for(int k = 0; k < n; k++)
					{
						LObj.ll_pos_correct(ob1, ob2, col.get(k).dist, col.get(k).norm.cpy().inv());
						LObj.ll_resolve_collision(ob1, ob2, col.get(k).norm.cpy().inv(), col.get(k).point.cpy(), n);
					}
					
				}
			 }
		 }
	}

	
}



class LObj {
	
	boolean fixed;
	LShape shape; 
	double computed_mass,
	mass,
	imass,
	inertia,
	iinertia,
	restitution,
	accel,
	orient,
	ang_vel,
	torque;
	P2i pos, vel;
	
	public LObj(P2i[] points, boolean fixed)
	{
		this.fixed = fixed;
		this.shape = new LShape(points);
		this.computed_mass = this.shape.compute_mass() * 0.4;
		if(this.fixed)
		{
			this.mass = 0;
			this.imass = 0;
			this.inertia = 0;
			this.iinertia = 0;
		}else
		{
			this.mass = this.computed_mass;
			this.imass = 1 / this.mass;
			this.inertia = this.mass * Math.pow(this.shape.get_rad(), 2);
			this.iinertia = 1 / this.inertia;
		}
		this.restitution = 0.2;
		this.pos = new P2i(0, 0);
		this.vel = new P2i(0,0);
		this.accel = 0;
		this.orient = 0;
		this.ang_vel = 0;
		this.torque = 0;
	}
	
	public LObj(P2i[] points)
	{
		this.fixed = false;
		this.shape = new LShape(points);
		this.computed_mass = this.shape.compute_mass() * 0.4;
		if(this.fixed)
		{
			this.mass = 0;
			this.imass = 0;
			this.inertia = 0;
			this.iinertia = 0;
		}else
		{
			this.mass = this.computed_mass;
			this.imass = 1 / this.mass;
			this.inertia = this.mass * Math.pow(this.shape.get_rad(), 2);
			this.iinertia = 1 / this.inertia;
		}
		this.restitution = 0.2;
		this.pos = new P2i(0, 0);
		this.vel = new P2i(0,0);
		this.accel = 0;
		this.orient = 0;
		this.ang_vel = 0;
		this.torque = 0;
	}
	
	public void step(double t)
	{
		if(this.mass == 0) return;
		this.pos.add(this.vel.cpy().scale(t));
		this.vel.add_scalar(this.accel * t);
		this.orient += this.ang_vel * t;
		this.ang_vel += this.torque * t;
		P2i gravity = new P2i(0, 0.08);
		//this.vel.add(gravity.scale(this.imass).scale(t));
		this.vel.add(gravity.scale(t));
	}

	public LShape get_shape()
	{
		P2i pts[] = new P2i[this.shape.points.length];
		for(int i = 0; i < this.shape.points.length; i++)
		{
			P2i p = this.shape.points[i].cpy();
			p.rotate(this.orient);
			p.add(this.pos);
			pts[i] = p.cpy();
		}
		return new LShape(pts, true);
	}

	
	//correct position so object sinking does not occur
	public void apply_impulse(P2i impulse_vec, P2i contact_vec)
	{
		this.vel.add(impulse_vec.cpy().scale(this.imass));
		this.ang_vel += this.iinertia * P2i.ll_cross(contact_vec, impulse_vec);
	}
	
	public static void ll_pos_correct(LObj a, LObj b, double penetration, P2i normal)
	{
		double slop = 0.05;
		double percent_to_correct = 0.2;
		P2i correction = normal.cpy().scale((Math.max(penetration-slop,0) / (a.imass + b.imass)) * percent_to_correct);
		a.pos.subtract(correction.cpy().scale(a.imass));
		b.pos.add(correction.cpy().scale(b.imass));
	}


	
	
	//resolve a collision between object a and b, where point intersects
	public static void ll_resolve_collision(LObj a, LObj b, P2i normal, P2i point, double num_penetrations)
	{
		P2i ra = point.cpy().subtract(a.pos);
		P2i rb = point.cpy().subtract(b.pos);

		//get relative velocity
		P2i rv = b.vel.cpy().add(P2i.ll_cross(b.ang_vel, rb)).subtract(a.vel).subtract(P2i.ll_cross(a.ang_vel, ra));
		//rv along normal
		double contact_vel = rv.dot(normal);
		//if objects are moving away from eachother: do nothing
		if(contact_vel > 0)
		  return;

		double ra_cross_normal = P2i.ll_cross(ra,normal);
		double rb_cross_normal = P2i.ll_cross(rb,normal);
		double inv_mass_inertia = a.imass + b.imass + Math.pow(ra_cross_normal,2) * a.iinertia + Math.pow(rb_cross_normal,2) * b.iinertia;

		//create impulse
		double restitution = Math.min(a.restitution, b.restitution);
		double j = -(1 + restitution) * contact_vel;
		j /= inv_mass_inertia;
		j /= num_penetrations;

		//apply primary impulse
		P2i impulse = normal.cpy().scale(j);
		a.apply_impulse(impulse.cpy().inv(), ra.cpy());
		b.apply_impulse(impulse.cpy(), rb.cpy());

		//friction impulse
		rv = b.vel.cpy().add(P2i.ll_cross(b.ang_vel,rb)).subtract(a.vel).subtract(P2i.ll_cross(a.ang_vel,ra));

		P2i t = rv.cpy().subtract(normal.cpy().scale(rv.dot(normal)));
		t.unit();

		// j tangent magnitude
		double jt = -rv.dot(t);
		jt /= inv_mass_inertia;
		jt /= num_penetrations;

		//if friction is too small: do nothing
		if(Math.abs(jt) < 0.001) return;

		//coulumb's law: friction effects objects with more momentum... more
		P2i tangent_impulse;
		if(Math.abs(jt) < j * 0.5)
		  tangent_impulse = t.cpy().scale(jt);
		else
		  tangent_impulse = t.cpy().scale(-j * 0.3);

		//apply friction
		a.apply_impulse(tangent_impulse.cpy().inv(), ra.cpy());
		b.apply_impulse(tangent_impulse.cpy(), rb.cpy());
	}

}



class Collision {

	boolean rv;
	double dist;
	P2i point;
	P2i norm;
	Collision()
	{
		dist = 100000;
		rv = false;
		point = new P2i(0,0);
		norm = new P2i(1,0);
	}
	Collision(boolean rv, double dist, P2i p, P2i n)
	{
		this.rv = rv;
		this.dist = dist;
		point = p.cpy();
		norm = n.cpy();
	}
}


class P2i {

	double x, y;
	public P2i()
	{
		this.x = 0;
		this.y = 0;
	}
	public P2i(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public static P2i ll_cross(P2i a, double b)
	{
		return new P2i(a.y * b, -a.x * b);
	}
	
	public static P2i ll_cross(double a, P2i b)
	{
		return new P2i(-a * b.y, a * b.x);
	}
	
	public static double ll_cross(P2i a, P2i b)
	{
		return a.x * b.y - a.y * b.x;
	}

	public double cross_product(P2i p2)
	{
		return this.x * p2.y - this.y * p2.x;
	}

	public String str()
	{
		return "" + this.x + ", " + this.y;
	}

	public P2i cross_product_scalar(double s)
	{
		return new P2i(-s * this.y , s * this.x);
	}

	public P2i round()
	{
		this.x = Math.round(this.x);
		this.y = Math.round(this.y);
		return this;
	}

	public double projection_scalar(P2i b)
	{
		double m = b.mag();
		if(m == 0) return 0;
		return this.dot(b.cpy().divide(m));
	}

	public P2i inv()
	{
		this.x = -this.x;
		this.y = -this.y;
		return this;
	}

	public P2i project(P2i b)
	{
		double a1 = this.projection_scalar(b);
		this.set_as(b);
		this.unit();
		this.scale(a1);
		return this;
	}

	public P2i get_normal()
	{
		P2i c = this.cpy().unit();
		return new P2i(-c.y, c.x);
	}

	public P2i set_as(P2i p2)
	{
		this.x = p2.x;
		this.y = p2.y;
		return this;
	}

	public double dot(P2i p2)
	{
		return this.x * p2.x + this.y * p2.y;
	}

	public double mag()
	{
		return Math.sqrt(this.x*this.x + this.y*this.y);
	}

	public P2i unit()
	{
		double m = this.mag();
		if(m != 0.0) this.scale(1 / m);
		return this;
	}

	public P2i add(P2i p2)
	{
		this.x += p2.x;
		this.y += p2.y;
		return this;
	}

	public P2i subtract(P2i p2)
	{
		this.x -= p2.x;
		this.y -= p2.y;
		return this;
	}

	public P2i rotate(double angle)
	{
		double x = this.x;
		double y = this.y;
		this.x = Math.cos(angle) * x - Math.sin(angle) * y;
		this.y = Math.sin(angle) * x + Math.cos(angle) * y;
		return this;
	}

	public P2i cpy()
	{
		return new P2i(this.x, this.y);
	}

	public double dist(P2i p2)
	{
		double a = this.x - p2.x;
		double b = this.y - p2.y;
		return Math.sqrt(a*a + b*b);
	}

	public P2i scale(double scalar)
	{
		this.x *= scalar;
		this.y *= scalar;
		return this;
	}

	public P2i divide(double scalar)
	{
		this.x /= scalar;
		this.y /= scalar;
		return this;
	}

	public P2i add_scalar(double scalar)
	{
		this.x += scalar;
		this.y += scalar;
		return this;
	};
	
}


class Bbox {

	P2i mn, mx;
	public Bbox(P2i top_left, P2i bottom_right)
	{
		this.mn = top_left;
		this.mx = bottom_right;
	}

	public boolean collision(Bbox bb2)
	{
		if(this.mn.x > bb2.mx.x || this.mx.x < bb2.mn.x) return false;
		if(this.mn.y > bb2.mx.y || this.mx.y < bb2.mn.y) return false;
		return true;
	}
}



class LGraphics extends JPanel
{
	int width, height, offsetx, offsety;
	LScene scene;
	LGraphics(int w, int h)
	{
		scene = null;
		this.width = w;
		this.height = h;
		this.setDoubleBuffered(true);
	}
	
	void set_scene(LScene scene)
	{
		this.scene = scene;
	}
	
	public void paint(Graphics g)
	{
		super.paint(g);
		clear(g, Color.white);
		g.setColor(Color.black);
		g.drawString(new String("number of objects:" + scene.objects.size()), this.width-300, 20);
		//line(g, 0.0, 150.0, Math.random()*399.0, 150.0, Color.red);
		if(scene != null)
		{
			boolean ar[] = new boolean[scene.objects.size()];
			for(int i = 0; i < scene.objects.size(); i++)
			{
				ar[i] = this.draw_object(g, scene.objects.get(i), Color.blue, new Color(242,240,242));
			}
			for(int i = 0; i < scene.objects.size(); i++)
			{
				if(!ar[i])
				{
					scene.objects.remove(i);
					break;
				}
			}
		}
		repaint();
	}
	
	boolean in_range(double x, double y)
	{
		return x >= 0 && x < this.width && y >= 0 && y < this.height;
	}

	boolean in_rangev(P2i p)
	{
		return this.in_range(p.x, p.y);
	}

	boolean in_rangetd(double x, double y)
	{
		return x >= 0 && x < this.width && y < this.height;
	}

	boolean in_rangetdv(P2i p)
	{
		return this.in_rangetd(p.x, p.y);
	}

	void clear(Graphics g, Color c)
	{
		Graphics2D gd = (Graphics2D) g;
		gd.setPaint(c);
		gd.fillRect(0, 0, this.width, this.height);
	}


	void line(Graphics g, double x1, double y1, double x2, double y2, Color col)
	{
		Graphics2D gd = (Graphics2D) g;
		gd.setPaint(col);
		gd.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
	}
	
	void linev(Graphics g, P2i p1, P2i p2, Color col)
	{
		Graphics2D gd = (Graphics2D) g;
		gd.setPaint(col);
		gd.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
	}
	
	void rect(Graphics g, double x, double y, double w, double h, Color col)
	{
		Graphics2D gd = (Graphics2D) g;
		gd.setPaint(col);
		gd.drawRect((int)x, (int)y, (int)w, (int)h);
	}
	void rectf(Graphics g, double x, double y, double w, double h, Color col)
	{
		Graphics2D gd = (Graphics2D) g;
		gd.setPaint(col);
		gd.fillRect((int)x, (int)y, (int)w, (int)h);
	}
	
	public boolean draw_shape(Graphics g, LShape ob, Color col)
	{
		boolean rv = false;
		if(ob.points.length == 0) return rv;
		for(int i = 0; i < ob.points.length; i++)
		{
			int j = (i+1) % ob.points.length;
			this.line(g, ob.points[i].x, ob.points[i].y, ob.points[j].x, ob.points[j].y, col);
			rv = rv || this.in_rangetdv(ob.points[i]);
		}
		P2i avg = ob.get_avg();
		this.linev(g, avg, ob.points[0], col);
		return rv;
	}

	public boolean draw_object(Graphics g, LObj ob, Color col1, Color col2)
	{
		LShape shape = ob.get_shape();
		Bbox bbox = shape.get_bbox();
		this.draw_bbox(g, bbox, col2);
		return this.draw_shape(g, shape, col1);
	}

	public void draw_bbox(Graphics g, Bbox bb, Color col)
	{
		this.rect(g, bb.mn.x, bb.mn.y, bb.mx.x-bb.mn.x, bb.mx.y-bb.mn.y, col);
	}
	
	
}

