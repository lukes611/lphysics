
public class P2i {

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
