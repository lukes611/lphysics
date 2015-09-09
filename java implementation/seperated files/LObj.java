//Java code by Luke Lincoln
public class LObj {
	
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
