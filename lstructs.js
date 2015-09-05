//JS code by Luke Lincoln

//2d point
function P2i(x,y)
{
	this.x = x;
	this.y = y;
}

function ll_cross(a, b)
{
	if(a instanceof P2i && !(b instanceof P2i))
	{//cross product type 1
		return new P2i(a.y * b, -a.x * b);
	}else if(!(a instanceof P2i) && b instanceof P2i)
	{
		return new P2i(-a * b.y, a * b.x);
	}else return a.x * b.y - a.y * b.x;
}

P2i.prototype.cross_product = function(p2)
{
	return this.x * p2.y - this.y * p2.x;
};

P2i.prototype.str = function()
{
	return '' + this.x + ', ' + this.y;
};

P2i.prototype.cross_product_scalar = function(s)
{
	return new P2i(-s * this.y , s * this.x);
};

P2i.prototype.round = function()
{
	this.x = Math.round(this.x);
	this.y = Math.round(this.y);
	return this;
};

P2i.prototype.projection_scalar = function(b)
{
	var m = b.mag();
	if(m == 0) return 0;
	return this.dot(b.clone().divide(m));
};

P2i.prototype.inv = function()
{
	this.x = -this.x;
	this.y = -this.y;
	return this;
};

P2i.prototype.project = function(b)
{
	var a1 = this.projection_scalar(b);
	this.set_as(b);
	this.unit();
	this.scale(a1);
	return this;
};

P2i.prototype.get_normal = function()
{
	var c = this.clone().unit();
	return new P2i(-c.y, c.x);
};

P2i.prototype.set_as = function(p2)
{
	this.x = p2.x;
	this.y = p2.y;
	return this;
};

P2i.prototype.dot = function(p2)
{
	return this.x * p2.x + this.y * p2.y;
};

P2i.prototype.mag = function()
{
	return Math.sqrt(this.x*this.x + this.y*this.y);
};

P2i.prototype.unit = function()
{
	var m = this.mag();
	if(m != 0.0) this.scale(1 / m);
	return this;
};

P2i.prototype.add = function(p2)
{
	this.x += p2.x;
	this.y += p2.y;
	return this;
};

P2i.prototype.subtract = function(p2)
{
	this.x -= p2.x;
	this.y -= p2.y;
	return this;
};

P2i.prototype.rotate = function(angle)
{
	//angle /= 57.3;
	var x = this.x;
	var y = this.y;
	this.x = Math.cos(angle) * x - Math.sin(angle) * y;
	this.y = Math.sin(angle) * x + Math.cos(angle) * y;
	return this;
};

P2i.prototype.clone = function()
{
	return new P2i(this.x, this.y);
}

P2i.prototype.dist = function(p2)
{
	var a = this.x - p2.x;
	var b = this.y - p2.y;
	return Math.sqrt(a*a + b*b);
};

P2i.prototype.scale = function(scalar)
{
	this.x *= scalar;
	this.y *= scalar;
	return this;
};

P2i.prototype.divide = function(scalar)
{
	this.x /= scalar;
	this.y /= scalar;
	return this;
};

P2i.prototype.add_scalar = function(scalar)
{
	this.x += scalar;
	this.y += scalar;
	return this;
};

//Bbox -> bounding box
function Bbox(top_left, bottom_right)
{
	this.mn = top_left;
	this.mx = bottom_right;
}

Bbox.prototype.collision = function(bb2)
{
	if(this.mn.x > bb2.mx.x || this.mx.x < bb2.mn.x) return false;
	if(this.mn.y > bb2.mx.y || this.mx.y < bb2.mn.y) return false;
	return true;
};

function LShape(points, not_to_origin)
{
	this.points = [];
	var i = 0;
	for(; i < points.length; i++)
		this.points.push(points[i].clone());
	if(not_to_origin == undefined) this.to_origin();
}

LShape.prototype.compute_mass = function()
{
	var bb = this.get_bbox();
	var s = Math.max(bb.mx.x - bb.mn.x, bb.mx.y-bb.mn.y);
	var q = 100;
	var d = new Array(q*q);
	var i = 0;
	for(; i < d.length; i++) d[i] = false;
	for(i = 0; i < this.points.length; i++)
	{
		var j = (i+1) % this.points.length;
		var p1 = this.points[i].clone().subtract(bb.mn).scale(q / s);
		var p2 = this.points[j].clone().subtract(bb.mn).scale(q / s);
		var ray = p2.clone().subtract(p1);
		var mag = ray.mag();
		ray.divide(mag);
		var m = Math.round(mag);
		var h = 0;
		for(; h < m; h++)
		{
			var p1c = p1.clone().round();;
			if(p1c.x >= 0 && p1c.y >= 0 && p1c.x < q && p1c.y < q)
			{
				d[p1c.y*q + p1c.x] = true;
			}
			p1.add(ray);
		}
	}
	var y = 0, x = 0;
	var start = false;
	var tmp_count = 0;
	var count = 0;
	for(; y < q; y++)
	{
		tmp_count = 0;
		start = false;
		for(x = 0; x < q; x++)
		{
			var v = d[y*q + x];
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
};

LShape.prototype.get_bbox = function()
{
	var i = 1, mx, mn;
	if(this.points.length >= 1)
	{
		mn = this.points[0].clone();
		mx = mn.clone();
	}else
	{
		mn = new P2i(0,0);
		mx = new P2i(0,0);
	}
	
	
	for(; i < this.points.length; i++)
	{	
		if(mn.x > this.points[i].x) mn.x = this.points[i].x;
		if(mn.y > this.points[i].y) mn.y = this.points[i].y;
		if(mx.x < this.points[i].x) mx.x = this.points[i].x;
		if(mx.y < this.points[i].y) mx.y = this.points[i].y;
	}
	return new Bbox(mn, mx);
};

LShape.prototype.clone = function(s2)
{
	return new LShape(s2.points);
};

LShape.prototype.to_origin = function()
{
	var avg = this.get_avg(), i = 0;
	for(; i < this.points.length; i++) this.points[i].subtract(avg);
};

LShape.prototype.get_avg = function()
{
	var i = 0;
	var rv = new P2i(0,0);
	for(; i < this.points.length; i++)
		rv.add(this.points[i]);
	if(this.points.length != 0) rv.divide(this.points.length);
	return rv;
};

LShape.prototype.get_rad = function()
{
	var avg = this.get_avg();
	var i = 1;
	var r = this.points[0].dist(avg);
	for(; i < this.points.length; i++)
	{
		var tmp = this.points[i].dist(avg);
		r = (tmp > r) ? tmp : r;
	}
	return r;
};

LShape.prototype.collision = function(s2)
{
	if(this.points.length == 0 || s2.points.length == 0) return false;
	var me = this;
	var collision_there = function(p)
	{
		
		var mn1 = me.points[0].projection_scalar(p);
		var mx1 = mn1;
		var mn2 = s2.points[0].projection_scalar(p);
		var mx2 = mn2;
		var i = 0;
		for(i = 1; i < me.points.length; i++)
		{
			var tmp = me.points[i].projection_scalar(p);
			if(tmp < mn1) mn1 = tmp;
			if(tmp > mx1) mx1 = tmp;
		}
		for(i = 1; i < s2.points.length; i++)
		{
			var tmp = s2.points[i].projection_scalar(p);
			if(tmp < mn2) mn2 = tmp;
			if(tmp > mx2) mx2 = tmp;
		}
		if(mx1 < mn2 || mx2 < mn1) return false;
		return true;
	};
	
	var normals_to_check = [];
	var i = 0;
	for(; i < this.points.length; i++)
	{
		var j = (i+1) % this.points.length;
		var line = this.points[j].clone().subtract(this.points[i]);
		normals_to_check.push(line.get_normal());
	}
	for(i = 0; i < s2.points.length; i++)
	{
		var j = (i+1) % s2.points.length;
		var line = s2.points[j].clone().subtract(s2.points[i]);
		normals_to_check.push(line.get_normal());
	}
	
	for(i = 0; i < normals_to_check.length; i++)
	{
		if(!collision_there(normals_to_check[i])) return false;
	}
	
	return true;
};


LShape.prototype.surrounds_point = function(p)
{
	var i = 0;
	//need point, penetration & normal
	var rv = {
		rv : true,
		dist : 100000000,
		norm : new P2i(0,0),
		point : p.clone()
	};
	var set = false;
	for(; i < this.points.length; i++)
	{
		var j = (i+1) % this.points.length;
		var n = this.points[j].clone().subtract(this.points[i]).get_normal();
		var D = n.clone().inv().dot(this.points[i]);
		var v = n.dot(p) + D;
		if(v > 0)
		{
			rv.rv = false;
			return rv;
		}else if(!set || Math.abs(v) < rv.dist)
		{
			rv.rv = true;
			rv.dist = Math.abs(v);
			rv.norm = n.clone();
			set = true;
		}
	}
	return rv;
};

LShape.prototype.collision_detection = function(s2)
{
	var rv = {
		collision : false,
		list : []
	};
	var bb1 = this.get_bbox();
	var bb2 = s2.get_bbox();
	if(!bb1.collision(bb2)) return rv;
	if(!this.collision(s2)) return rv;
	var set = false;
	var i = 0;
	for(; i < s2.points.length; i++)
	{
		var test = this.surrounds_point(s2.points[i]);
		if(test.rv)
		{
			delete test.rv;
			test.norm.inv();
			rv.collision = true;
			rv.list.push(test);
		}
	}
	for(i = 0; i < this.points.length; i++)
	{
		var test = s2.surrounds_point(this.points[i]);
		if(test.rv)
		{
			delete test.rv;
			rv.collision = true;
			rv.list.push(test);
		}
	}
	return rv;
};


function LObj(points, fixed, props)
{
	if(fixed == undefined) this.fixed = false;
	else this.fixed = fixed;
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
	if(props != undefined)
	{
		this.set_properties(props);
	}
	
};

LObj.prototype.set_properties = function(ob)
{
	if(ob.restitution)
	{
		this.restitution = ob.restitution;
	}
	if(ob.fixed != undefined)
	{
		if(ob.fixed == true)
		{
			this.mass = 0;
			this.imass = 0;
			this.inertia = 0;
			this.iinertia = 0;
			this.fixed = true;
		}else
		{
			this.mass = this.computed_mass;
			this.imass = 1 / this.mass;
			this.inertia = this.mass * Math.pow(this.shape.get_rad(), 2);
			this.iinertia = 1 / this.inertia;
		}
	}
	if(ob.torque)
	{
		this.torque = ob.torque;
	}
	if(ob.ang_vel)
	{
		this.ang_vel = ob.ang_vel;
	}
	if(ob.orient)
	{
		this.orient = ob.orient;
	}
	if(ob.accel)
	{
		this.accel = ob.accel;
	}
	if(ob.vel)
	{
		this.vel = ob.vel.clone();
	}
	if(ob.pos)
	{
		this.pos = ob.pos.clone();
	}
};

LObj.prototype.step = function(t)
{
	if(this.mass == 0) return;
	this.pos.add(this.vel.clone().scale(t));
	this.vel.add_scalar(this.accel * t);
	this.orient += this.ang_vel * t;
	this.ang_vel += this.torque * t;
	var gravity = new P2i(0, 0.08);
	//this.vel.add(gravity.scale(this.imass).scale(t));
	this.vel.add(gravity.scale(t));
};

LObj.prototype.get_shape = function()
{
	var rv = new LShape([]), i = 0;
	for(; i < this.shape.points.length; i++)
	{
		var p = this.shape.points[i].clone();
		p.rotate(this.orient);
		p.add(this.pos);
		rv.points.push(p);
	}
	return rv;
};

LObj.prototype.apply_impulse = function(impulse_vec, contact_vec)
{
	this.vel.add(impulse_vec.clone().scale(this.imass));
	this.ang_vel += this.iinertia * ll_cross(contact_vec, impulse_vec);
};

//correct position so object sinking does not occur
function ll_pos_correct(a, b, penetration, normal)
{
	var slop = 0.05;
	var percent_to_correct = 0.2;
	var correction = normal.clone().scale((Math.max(penetration-slop,0) / (a.imass + b.imass)) * percent_to_correct);
	a.pos.subtract(correction.clone().scale(a.imass));
	b.pos.add(correction.clone().scale(b.imass));
}

//resolve a collision between object a and b, where point intersects
function ll_resolve_collision(a, b, normal, point, num_penetrations)
{
	if(num_penetrations == undefined) num_penetrations = 1;
	var ra = point.clone().subtract(a.pos);
	var rb = point.clone().subtract(b.pos);

	//get relative velocity
	var rv = b.vel.clone().add(ll_cross(b.ang_vel, rb)).subtract(a.vel).subtract(ll_cross(a.ang_vel, ra));
	//rv along normal
	var contact_vel = rv.dot(normal);
	//if objects are moving away from eachother: do nothing
	if(contact_vel > 0)
	  return;

	var ra_cross_normal = ll_cross(ra,normal);
	var rb_cross_normal = ll_cross(rb,normal);
	var inv_mass_inertia = a.imass + b.imass + Math.pow(ra_cross_normal,2) * a.iinertia + Math.pow(rb_cross_normal,2) * b.iinertia;

	//create impulse
	var restitution = Math.min(a.restitution, b.restitution);
	var j = -(1 + restitution) * contact_vel;
	j /= inv_mass_inertia;
	j /= num_penetrations;

	//apply primary impulse
	var impulse = normal.clone().scale(j);
	a.apply_impulse(impulse.clone().inv(), ra.clone());
	b.apply_impulse(impulse.clone(), rb.clone());

	//friction impulse
	rv = b.vel.clone().add(ll_cross(b.ang_vel,rb)).subtract(a.vel).subtract(ll_cross(a.ang_vel,ra));

	var t = rv.clone().subtract(normal.clone().scale(rv.dot(normal)));
	t.unit();

	// j tangent magnitude
	var jt = -rv.dot(t);
	jt /= inv_mass_inertia;
	jt /= num_penetrations;

	//if friction is too small: do nothing
	if(Math.abs(jt) < 0.001) return;

	//coulumb's law: friction effects objects with more momentum... more
	var tangent_impulse;
	if(Math.abs(jt) < j * 0.5)
	  tangent_impulse = t.clone().scale(jt);
	else
	  tangent_impulse = t.clone().scale(-j * 0.3);

	//apply friction
	a.apply_impulse(tangent_impulse.clone().inv(), ra.clone());
	b.apply_impulse(tangent_impulse.clone(), rb.clone());
}







