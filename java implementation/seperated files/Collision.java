//Java code by Luke Lincoln
public class Collision {

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
