import java.util.ArrayList;


public class LShape {

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
