
public class Bbox {

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
